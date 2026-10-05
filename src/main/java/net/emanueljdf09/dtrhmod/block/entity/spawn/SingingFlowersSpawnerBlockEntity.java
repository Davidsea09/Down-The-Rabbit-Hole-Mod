package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SingingFlowersSpawnerBlockEntity extends BlockEntity {
    public static final int FLOWER_COUNT = 5;
    private static final int START_RADIUS = 16;
    private static final int ARENA_RADIUS = 40;
    private static final int EMPTY_RESET_TICKS = 200;
    private static final double RING_RADIUS = 5.0;

    private boolean active = false;
    private int kills = 0;
    private final List<UUID> flowerIds = new ArrayList<>();
    private final Set<UUID> participants = new HashSet<>();

    private int scanCooldown = 0;
    private int emptyTicks = 0;

    private final ServerBossBar bossBar = new ServerBossBar(
            Text.translatable("entity.dtrhmod.singing_flowers"),
            BossBar.Color.PINK,
            BossBar.Style.PROGRESS);

    public SingingFlowersSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SINGING_FLOWERS_SPAWNER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SingingFlowersSpawnerBlockEntity be) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (be.active) {
            be.tickActive(serverWorld);
        } else {
            be.tickIdle(serverWorld);
        }
    }

    private void tickIdle(ServerWorld world) {
        if (++scanCooldown < 20) return;
        scanCooldown = 0;

        List<ServerPlayerEntity> nearby = world.getEntitiesByClass(
                ServerPlayerEntity.class,
                new Box(pos).expand(START_RADIUS),
                p -> p.isAlive() && !p.isSpectator());

        for (ServerPlayerEntity player : nearby) {
            ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
            if (!component.hasKilledSingingFlowers()) {
                startFight(world);
                return;
            }
        }
    }

    private void tickActive(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getEntitiesByClass(
                ServerPlayerEntity.class,
                new Box(pos).expand(ARENA_RADIUS),
                p -> p.isAlive() && !p.isSpectator());

        syncBossBar(world, players);

        if (players.isEmpty()) {
            if (++emptyTicks >= EMPTY_RESET_TICKS) resetFight(world);
        } else {
            emptyTicks = 0;
        }
    }

    private void syncBossBar(ServerWorld world, List<ServerPlayerEntity> players) {
        for (ServerPlayerEntity p : new ArrayList<>(bossBar.getPlayers())) {
            if (!players.contains(p)) bossBar.removePlayer(p);
        }
        for (ServerPlayerEntity p : players) {
            bossBar.addPlayer(p);
        }

        float health = 0f;
        for (UUID id : flowerIds) {
            if (world.getEntity(id) instanceof SingingFlowerEntity flower && flower.isAlive()) {
                health += flower.getHealth();
            }
        }
        bossBar.setPercent(Math.min(1f, health / (FLOWER_COUNT * SingingFlowerEntity.MAX_HEALTH)));
    }

    private void startFight(ServerWorld world) {
        flowerIds.clear();
        participants.clear();
        kills = 0;
        emptyTicks = 0;

        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        double y = pos.getY() + 1.0;

        for (int i = 0; i < FLOWER_COUNT; i++) {
            SingingFlowerEntity flower = ModEntities.SINGING_FLOWER.create(world);
            if (flower == null) continue;

            double angle = (Math.PI * 2.0 / FLOWER_COUNT) * i;
            double dx = Math.cos(angle) * RING_RADIUS;
            double dz = Math.sin(angle) * RING_RADIUS;
            float yaw = (float) Math.toDegrees(Math.atan2(-dz, -dx)) - 90.0F;

            flower.refreshPositionAndAngles(cx + dx, y, cz + dz, yaw, 0.0F);
            flower.setVariant(i);
            flower.setHome(pos);
            flower.setPhase(1);
            flower.startEmerging();
            world.spawnEntity(flower);
            flowerIds.add(flower.getUuid());
        }

        active = true;
        markDirty();
    }

    public int getPhase() {
        if (kills < 2) return 1;
        if (kills < 4) return 2;
        return 3;
    }

    public boolean isTracking(UUID flowerId) {
        return active && flowerIds.contains(flowerId);
    }

    public void addParticipant(ServerPlayerEntity player) {
        if (active && participants.add(player.getUuid())) markDirty();
    }

    public void onFlowerKilled(SingingFlowerEntity flower, DamageSource source) {
        if (!active || !(getWorld() instanceof ServerWorld world)) return;
        if (!flowerIds.remove(flower.getUuid())) return;

        if (source.getAttacker() instanceof ServerPlayerEntity player) {
            participants.add(player.getUuid());
        }
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
        markDirty();
    }

    private void completeFight(ServerWorld world) {
        for (UUID id : participants) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(id);
            if (player == null) continue;

            ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
            if (!component.hasKilledSingingFlowers()) {
                component.setKilledSingingFlowers(true);
                // TODO: grant advancement (AdvancementUtil.grant), WonderlandProgressionUtil.triggerMilestone, loot
            }
        }

        bossBar.clearPlayers();
        active = false;
        flowerIds.clear();
        participants.clear();
        kills = 0;
        scanCooldown = -400;
        markDirty();
    }

    private void resetFight(ServerWorld world) {
        for (UUID id : flowerIds) {
            var entity = world.getEntity(id);
            if (entity != null) entity.discard();
        }

        bossBar.clearPlayers();
        active = false;
        flowerIds.clear();
        participants.clear();
        kills = 0;
        emptyTicks = 0;
        scanCooldown = -100;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("Active", active);
        nbt.putInt("Kills", kills);

        NbtList flowers = new NbtList();
        for (UUID id : flowerIds) flowers.add(NbtHelper.fromUuid(id));
        nbt.put("Flowers", flowers);

        NbtList players = new NbtList();
        for (UUID id : participants) players.add(NbtHelper.fromUuid(id));
        nbt.put("Participants", players);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        active = nbt.getBoolean("Active");
        kills = nbt.getInt("Kills");

        flowerIds.clear();
        for (NbtElement e : nbt.getList("Flowers", NbtElement.INT_ARRAY_TYPE)) {
            flowerIds.add(NbtHelper.toUuid(e));
        }

        participants.clear();
        for (NbtElement e : nbt.getList("Participants", NbtElement.INT_ARRAY_TYPE)) {
            participants.add(NbtHelper.toUuid(e));
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        bossBar.clearPlayers();
    }
}