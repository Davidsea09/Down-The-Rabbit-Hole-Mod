package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity.AttackType;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.*;

public class SingingFlowersSpawnerBlockEntity extends BlockEntity {
    public static final int FLOWER_COUNT = 5;
    private static final int START_RADIUS = 16;
    private static final int ARENA_RADIUS = 40;
    private static final int EMPTY_RESET_TICKS = 200;
    private static final double RING_RADIUS = 5.0;

    private static final int FIRST_ROUND_DELAY = 80;
    private static final int WINDUP_TICKS = 12;
    private static final int CHOMP_STAGGER = 10;
    private static final int VINE_STAGGER = 14;
    private static final int MAX_VINES_PER_ROUND = 2;
    private static final float VINE_CHANCE = 0.4f;


    private boolean active = false;
    private int kills = 0;
    private final List<UUID> flowerIds = new ArrayList<>();
    private final Set<UUID> participants = new HashSet<>();

    private int scanCooldown = 0;
    private int emptyTicks = 0;
    private int roundCooldown = FIRST_ROUND_DELAY;

    private final ServerBossEvent bossBar = (ServerBossEvent) new ServerBossEvent(
            Component.translatable("entity.dtrhmod.singing_flowers"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

    public SingingFlowersSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SINGING_FLOWERS_SPAWNER_BE.get(), pos, state);
    }

    public void forceReset(ServerLevel world) {
        resetFight(world);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SingingFlowersSpawnerBlockEntity be) {
        if (!(world instanceof ServerLevel serverWorld)) return;


        if (be.active) {
            be.tickActive(serverWorld);
        } else {
            be.tickIdle(serverWorld);
        }
    }

    private void tickIdle(ServerLevel world) {
        if (++scanCooldown < 20) return;
        scanCooldown = 0;

        List<ServerPlayer> nearby = world.getEntitiesOfClass(
                ServerPlayer.class,
                new AABB(worldPosition).inflate(START_RADIUS),
                p -> p.isAlive() && !p.isSpectator());

        MinecraftServer server = world.getServer();
        if (server == null) return;

        for (ServerPlayer player : nearby) {
            ProgressionComponent component = PlayerProgressionManager.get(server).get(player.getUUID());
            if (!component.hasKilledSingingFlowers()) {
                startFight(world);
                return;
            }
        }
    }

    private void tickActive(ServerLevel world) {
        List<ServerPlayer> players = world.getEntitiesOfClass(
                ServerPlayer.class,
                new AABB(worldPosition).inflate(ARENA_RADIUS),
                p -> p.isAlive() && !p.isSpectator());

        syncBossBar(world, players);

        if (players.isEmpty()) {
            if (++emptyTicks >= EMPTY_RESET_TICKS) resetFight(world);
        } else {
            emptyTicks = 0;
            conduct(world, players);
        }
    }

    private void conduct(ServerLevel world, List<ServerPlayer> players) {
        if (roundCooldown > 0) {
            roundCooldown--;
            return;
        }

        List<SingingFlowerEntity> ready = new ArrayList<>();
        for (UUID id : flowerIds) {
            if (!(world.getEntity(id) instanceof SingingFlowerEntity f) || !f.isAlive()) continue;

            if (f.hasPendingAttack() || f.hasActiveVine()) return;
            if (f.isReadyToAttack()) ready.add(f);
        }
        if (ready.isEmpty()) return;

        ServerPlayer target = players.get(world.getRandom().nextInt(players.size()));

        AttackType type = AttackType.CHOMP;
        List<SingingFlowerEntity> group = ready;
        if (getPhase() >= 2 && world.getRandom().nextFloat() < VINE_CHANCE) {
            List<SingingFlowerEntity> inRange = new ArrayList<>();
            for (SingingFlowerEntity f : ready) {
                if (f.canVineTarget(target)) inRange.add(f);
            }
            if (!inRange.isEmpty()) {
                type = AttackType.VINE;
                group = inRange;
            }
        }

        final int sweep = world.getRandom().nextBoolean() ? 1 : -1;
        final Vec3 right = SingingFlowerEntity.rightOf(target);
        final ServerPlayer t = target;
        group.sort(Comparator.comparingDouble((SingingFlowerEntity f) ->
                sweep * ((f.getX() - t.getX()) * right.x + (f.getZ() - t.getZ()) * right.z)));

        if (type == AttackType.VINE && group.size() > MAX_VINES_PER_ROUND) {
            group = new ArrayList<>(group.subList(0, MAX_VINES_PER_ROUND));
        }

        int stagger = type == AttackType.VINE ? VINE_STAGGER : CHOMP_STAGGER;
        int delay = WINDUP_TICKS;
        for (SingingFlowerEntity f : group) {
            f.queueAttack(type, delay, sweep, target);
            delay += stagger;
        }

        int gap = switch (getPhase()) {
            case 1 -> 70;
            case 2 -> 55;
            default -> 45;
        };
        roundCooldown = delay + gap + world.getRandom().nextInt(20);
    }

    private void syncBossBar(ServerLevel world, List<ServerPlayer> players) {
        for (ServerPlayer p : new ArrayList<>(bossBar.getPlayers())) {
            if (!players.contains(p)) bossBar.removePlayer(p);
        }
        for (ServerPlayer p : players) {
            bossBar.addPlayer(p);
        }

        float health = 0f;
        for (UUID id : flowerIds) {
            if (world.getEntity(id) instanceof SingingFlowerEntity flower && flower.isAlive()) {
                health += flower.getHealth();
            }
        }
        bossBar.setProgress(Math.min(1f, health / (FLOWER_COUNT * SingingFlowerEntity.MAX_HEALTH)));
    }

    private void startFight(ServerLevel world) {
        flowerIds.clear();
        participants.clear();
        kills = 0;
        emptyTicks = 0;
        roundCooldown = FIRST_ROUND_DELAY;

        double cx = worldPosition.getX() + 0.5;
        double cz = worldPosition.getZ() + 0.5;
        double y = worldPosition.getY() + 1.0;

        for (int i = 0; i < FLOWER_COUNT; i++) {
            SingingFlowerEntity flower = ModEntities.SINGING_FLOWER.get().create(world);
            if (flower == null) continue;

            double angle = (Math.PI * 2.0 / FLOWER_COUNT) * i;
            double dx = Math.cos(angle) * RING_RADIUS;
            double dz = Math.sin(angle) * RING_RADIUS;
            float yaw = (float) Math.toDegrees(Math.atan2(-dz, -dx)) - 90.0F;

            flower.moveTo(cx + dx, y, cz + dz, yaw, 0.0F);
            flower.setVariant(i);
            flower.setHome(worldPosition);
            flower.setPhase(1);
            flower.startEmerging();
            world.addFreshEntity(flower);
            flowerIds.add(flower.getUUID());
        }

        world.playSound(null, worldPosition, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 2.0F, 0.8F);

        active = true;
        setChanged();
    }

    public int getPhase() {
        if (kills < 2) return 1;
        if (kills < 4) return 2;
        return 3;
    }

    public boolean isTracking(UUID flowerId) {
        return active && flowerIds.contains(flowerId);
    }

    public void addParticipant(ServerPlayer player) {
        if (active && participants.add(player.getUUID())) setChanged();
    }

    public void onFlowerKilled(SingingFlowerEntity flower, DamageSource source) {
        if (!active || !(getLevel() instanceof ServerLevel world)) return;
        if (!flowerIds.remove(flower.getUUID())) return;

        if (source.getEntity() instanceof ServerPlayer player) {
            participants.add(player.getUUID());
        }
        int phaseBefore = getPhase();
        kills++;

        if (kills >= FLOWER_COUNT) {
            completeFight(world);
            return;
        }

        int phase = getPhase();
        for (UUID id : flowerIds) {
            if (world.getEntity(id) instanceof SingingFlowerEntity alive) {
                alive.setPhase(phase);
                if (phase == 3) alive.beginFinalForm();
            }
        }

        if (phase != phaseBefore) {
            world.playSound(null, worldPosition, SoundEvents.EVOKER_CELEBRATE, SoundSource.HOSTILE, 2.0F, 0.7F);
        }
        setChanged();
    }

    private void completeFight(ServerLevel world) {
        MinecraftServer server = world.getServer();
        if (server == null) return;


        for (UUID id : participants) {
            ServerPlayer player = world.getServer().getPlayerList().getPlayer(id);
            if (player == null) continue;

            ProgressionComponent component = PlayerProgressionManager.get(server).get(player.getUUID());
            if (!component.hasKilledSingingFlowers()) {
                component.setKilledSingingFlowers(true);
                component.setWonderlandMirrorUnlocked(true);
                grantAdvancement(player);

                ItemEntity lassoDrop = new ItemEntity(
                        world,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1.0,
                        worldPosition.getZ() + 0.5,
                        new ItemStack(ModItems.VINE_LASSO_ITEM)
                );
                world.addFreshEntity(lassoDrop);
            }
        }

        ItemEntity lassoDrop = new ItemEntity(
                world,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.0,
                worldPosition.getZ() + 0.5,
                new ItemStack(ModItems.EAT_ME)
        );
        world.addFreshEntity(lassoDrop);

        ExperienceOrb.award(world, Vec3.atCenterOf(worldPosition), 30);

        world.playSound(null, worldPosition, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.HOSTILE, 1.0F, 1.0F);

        bossBar.removeAllPlayers();
        active = false;
        flowerIds.clear();
        participants.clear();
        kills = 0;
        scanCooldown = -400;
        setChanged();
    }

    private void grantAdvancement(ServerPlayer player) {
        AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "boss_root"));
        AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID,"killed_flowers"));
    }

    private void resetFight(ServerLevel world) {
        for (UUID id : flowerIds) {
            var entity = world.getEntity(id);
            if (entity != null) entity.discard();
        }

        bossBar.removeAllPlayers();
        active = false;
        flowerIds.clear();
        participants.clear();
        kills = 0;
        emptyTicks = 0;
        scanCooldown = -100;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putBoolean("Active", active);
        nbt.putInt("Kills", kills);

        ListTag flowers = new ListTag();
        for (UUID id : flowerIds) flowers.add(NbtUtils.createUUID(id));
        nbt.put("Flowers", flowers);

        ListTag players = new ListTag();
        for (UUID id : participants) players.add(NbtUtils.createUUID(id));
        nbt.put("Participants", players);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        active = nbt.getBoolean("Active");
        kills = nbt.getInt("Kills");

        flowerIds.clear();
        for (Tag e : nbt.getList("Flowers", Tag.TAG_INT_ARRAY)) {
            flowerIds.add(NbtUtils.loadUUID(e));
        }

        participants.clear();
        for (Tag e : nbt.getList("Participants", Tag.TAG_INT_ARRAY)) {
            participants.add(NbtUtils.loadUUID(e));
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        bossBar.removeAllPlayers();
    }
}