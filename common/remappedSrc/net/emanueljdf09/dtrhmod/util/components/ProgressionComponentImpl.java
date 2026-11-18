package net.emanueljdf09.dtrhmod.util.components;

import net.emanueljdf09.dtrhmod.fabric.util.ModComponents;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ProgressionComponentImpl implements net.emanueljdf09.dtrhmod.util.components.ProgressionComponent, AutoSyncedComponent {

    private final Player player;
    private boolean exteriorDone = false;
    private BlockPos wonderlandSpawn = null;
    private boolean openedExtChest = false;
    private boolean openedExtGrownChest = false;
    private boolean doorOpened = false;
    private boolean wonderlandMirrorUnlocked = false;
    private int completedStages = 0;
    private boolean metWhiteRabbit = false;
    private boolean metInOverworld = false;

    private boolean metMadHatter = false;
    private boolean wonMadHatterTrivia = false;
    private boolean killedMadHatter = false;
    private boolean killedSingingFlowers = false;
    private boolean killedCaterpillar = false;


    public ProgressionComponentImpl(Player player) {
        this.player = player;
    }

    @Override
    public boolean hasDoneExterior() {
        return exteriorDone;
    }

    @Override
    public void setExteriorDone(boolean done) {
        this.exteriorDone = done;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasOpenedExtChest() {
        return openedExtChest;
    }

    @Override
    public void setOpenedExtChest(boolean openedExtChest) {
        this.openedExtChest = openedExtChest;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasOpenedExtGrownChest() {
        return openedExtGrownChest;
    }

    @Override
    public void setOpenedExtGrownChest(boolean openedExtGrownChest) {
        this.openedExtGrownChest = openedExtGrownChest;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }

    }

    @Override
    public boolean hasOpenedExtDoor() {
        return doorOpened;
    }

    @Override
    public void setOpenedExtDoor(boolean openedExtDoor) {
        this.doorOpened = openedExtDoor;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean isWonderlandMirrorUnlocked() {
        return this.wonderlandMirrorUnlocked;
    }

    @Override
    public void setWonderlandMirrorUnlocked(boolean unlocked) {
        this.wonderlandMirrorUnlocked = unlocked;

    }

    @Override
    public int getCompletedStages() {
        return this.completedStages;
    }

    @Override
    public void setCompletedStages(int stages) {

        this.completedStages = stages;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasMetWhiteRabbit() {
        return this.metWhiteRabbit;
    }

    @Override
    public void setMetWhiteRabbit(boolean met) {
        this.metWhiteRabbit = met;
    }

    @Override
    public boolean hasMetInOverworld() { return this.metInOverworld; }
    @Override
    public void setMetInOverworld(boolean met) { this.metInOverworld = met; }

    @Override
    public void setWonderlandSpawn(BlockPos pos) {
        this.wonderlandSpawn = pos;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public BlockPos getWonderlandSpawn() {
        return this.wonderlandSpawn;
    }


    @Override
    public boolean hasMetMadHatter() {
        return this.metMadHatter;
    }

    @Override
    public void setMetMadHatter(boolean met) {
        this.metMadHatter = met;
        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasWonMadHatterTrivia() {
        return this.wonMadHatterTrivia;
    }

    @Override
    public void setWonMadHatterTrivia(boolean won) {
        this.wonMadHatterTrivia = won;
        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasKilledMadHatter() {
        return this.killedMadHatter;
    }

    @Override
    public void setKilledMadHatter(boolean killed) {
        this.killedMadHatter = killed;
        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public boolean hasKilledSingingFlowers() {
        return killedSingingFlowers;
    }

    @Override
    public void setKilledSingingFlowers(boolean killed) {
        this.killedSingingFlowers = killed;
        if (player instanceof  ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }

    }

    @Override
    public boolean hasKilledCaterpillar() {
        return killedCaterpillar;
    }

    @Override
    public void setKilledCaterpillar(boolean killed) {
        this.killedCaterpillar = killed;
        if (player instanceof  ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public void reset() {
        this.exteriorDone = false;
        this.wonderlandSpawn = null;
        this.openedExtChest = false;
        this.openedExtGrownChest = false;
        this.doorOpened = false;
        this.wonderlandMirrorUnlocked = false;
        this.completedStages = 0;
        this.metWhiteRabbit = false;
        this.metInOverworld = false;
        this.metMadHatter = false;
        this.wonMadHatterTrivia = false;
        this.killedMadHatter = false;
        this.killedSingingFlowers = false;
        this.killedCaterpillar = false;

        if (player instanceof ServerPlayer serverPlayer) {
            ModComponents.PROGRESSION_COMPONENT.sync(serverPlayer);
        }
    }

    @Override
    public void readFromNbt(CompoundTag nbtCompound, HolderLookup.Provider registryLookup) {
        exteriorDone = nbtCompound.getBoolean("ExteriorDone");
        openedExtChest = nbtCompound.getBoolean("OpenedExtChest");
        openedExtGrownChest = nbtCompound.getBoolean("OpenedExtGrownChest");
        doorOpened = nbtCompound.getBoolean("DoorOpened");
        wonderlandMirrorUnlocked = nbtCompound.getBoolean("WonderlandMirrorUnlocked");
        completedStages = nbtCompound.getInt("CompletedStages");
        this.metWhiteRabbit = nbtCompound.getBoolean("MetWhiteRabbit");
        this.metInOverworld = nbtCompound.getBoolean("MetInOverworld");
        this.metMadHatter = nbtCompound.getBoolean("MetMadHatter");
        this.wonMadHatterTrivia = nbtCompound.getBoolean("WonMadHatterTrivia");
        this.killedMadHatter = nbtCompound.getBoolean("KilledMadHatter");
        this.killedSingingFlowers = nbtCompound.getBoolean("KilledSingingFlowers");
        this.killedCaterpillar = nbtCompound.getBoolean("KilledCaterpillar");

        this.wonderlandSpawn = NbtUtils.readBlockPos(nbtCompound, "wonderlandSpawn").orElse(null);
    }

    @Override
    public void writeToNbt(CompoundTag nbtCompound, HolderLookup.Provider registryLookup) {
        nbtCompound.putBoolean("ExteriorDone", exteriorDone);
        nbtCompound.putBoolean("DoorOpened", doorOpened);
        nbtCompound.putBoolean("OpenedExtChest", openedExtChest);
        nbtCompound.putBoolean("OpenedExtGrownChest", openedExtGrownChest);
        nbtCompound.putBoolean("WonderlandMirrorUnlocked", this.wonderlandMirrorUnlocked);
        nbtCompound.putInt("CompletedStages", this.completedStages);
        nbtCompound.putBoolean("MetWhiteRabbit", this.metWhiteRabbit);
        nbtCompound.putBoolean("MetInOverworld", this.metInOverworld);
        nbtCompound.putBoolean("MetMadHatter", this.metMadHatter);
        nbtCompound.putBoolean("WonMadHatterTrivia", this.wonMadHatterTrivia);
        nbtCompound.putBoolean("KilledMadHatter", this.killedMadHatter);
        nbtCompound.putBoolean("KilledSingingFlowers", this.killedSingingFlowers);
        nbtCompound.putBoolean("KilledCaterpillar", this.killedCaterpillar);

        if (this.wonderlandSpawn != null) {
            nbtCompound.put("wonderlandSpawn", NbtUtils.writeBlockPos(this.wonderlandSpawn));
        }
    }
}
