package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.CaterpillarEntity;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class CaterpillarSpawnerBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public CaterpillarSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CATERPILLAR_SPAWNER_BE, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CaterpillarSpawnerBlockEntity be) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        if (++be.scanCooldown < 40) return;
        be.scanCooldown = 0;

        List<CaterpillarEntity> existing = serverWorld.getEntitiesOfClass(
                CaterpillarEntity.class, new AABB(pos).inflate(32), c -> true);
        if (!existing.isEmpty()) return;

        List<ServerPlayer> nearby = serverWorld.getEntitiesOfClass(
                ServerPlayer.class, new AABB(pos).inflate(16), p -> true);

        boolean shouldSpawn = false;
        for (ServerPlayer player : nearby) {
            if (!ModComponents.PROGRESSION_COMPONENT.get(player).hasKilledCaterpillar()) {
                shouldSpawn = true;
                break;
            }
        }
        if (!shouldSpawn) return;

        CaterpillarEntity caterpillar = ModEntities.CATERPILLAR.create(serverWorld);
        if (caterpillar == null) return;

        float yaw = serverWorld.random.nextFloat() * 360F;
        caterpillar.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, yaw, 0);
        caterpillar.setYBodyRot(yaw);
        caterpillar.setYHeadRot(yaw);
        caterpillar.setHome(pos);
        serverWorld.addFreshEntity(caterpillar);
    }
}