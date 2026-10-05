package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CheshireCatSpawnerBlockEntity extends BlockEntity {
    private int checkCooldown = 80;
    private int spawnCooldownTimer = 0;
    private boolean hasSpawnedCat = false;

    private static final int TEN_MINUTES_IN_TICKS = 12000;

    public CheshireCatSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHESHIRE_CAT_SPAWNER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CheshireCatSpawnerBlockEntity blockEntity) {
        if (world.isClient()) return;

        if (blockEntity.spawnCooldownTimer > 0) {
            blockEntity.spawnCooldownTimer--;
            return;
        }

        blockEntity.checkCooldown++;
        if (blockEntity.checkCooldown >= 80) {
            blockEntity.checkCooldown = 0;

            Box scanArea = new Box(pos).expand(80.0);

            if (blockEntity.hasSpawnedCat) {
                List<CheshireCatEntity> existingCats = world.getEntitiesByClass(CheshireCatEntity.class, scanArea, c -> true);
                if (existingCats.isEmpty()) {
                    blockEntity.hasSpawnedCat = false;
                    blockEntity.spawnCooldownTimer = TEN_MINUTES_IN_TICKS;
                }
                return;
            }

            List<ServerPlayerEntity> players = world.getEntitiesByClass(
                    ServerPlayerEntity.class,
                    scanArea,
                    player -> true
            );

            if (!players.isEmpty()) {
                List<CheshireCatEntity> globalCats = world.getEntitiesByClass(CheshireCatEntity.class, scanArea, c -> true);

                if (globalCats.isEmpty()) {
                    spawnServerCat((ServerWorld) world, pos);
                    blockEntity.hasSpawnedCat = true;
                }
            }
        }
    }

    private static void spawnServerCat(ServerWorld world, BlockPos pos) {
        CheshireCatEntity cat = ModEntities.CHESHIRE_CAT.create(world);
        if (cat != null) {
            cat.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            cat.initialize(world, world.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
            world.spawnEntity(cat);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("HasSpawnedCat", this.hasSpawnedCat);
        nbt.putInt("SpawnCooldownTimer", this.spawnCooldownTimer);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("HasSpawnedCat")) {
            this.hasSpawnedCat = nbt.getBoolean("HasSpawnedCat");
        }
        if (nbt.contains("SpawnCooldownTimer")) {
            this.spawnCooldownTimer = nbt.getInt("SpawnCooldownTimer");
        }
        if (this.spawnCooldownTimer <= 0) {
            this.checkCooldown = 80;
        }
    }
}