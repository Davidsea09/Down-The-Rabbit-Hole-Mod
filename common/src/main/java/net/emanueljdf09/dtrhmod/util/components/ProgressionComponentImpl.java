package net.emanueljdf09.dtrhmod.util.components;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class ProgressionComponentImpl implements ProgressionComponent {

    private final Runnable onChanged;

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

    public ProgressionComponentImpl(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    private void changed() {
        onChanged.run();
    }

    @Override public boolean hasDoneExterior() { return exteriorDone; }
    @Override public void setExteriorDone(boolean done) { this.exteriorDone = done; changed(); }

    @Override public boolean hasOpenedExtChest() { return openedExtChest; }
    @Override public void setOpenedExtChest(boolean done) { this.openedExtChest = done; changed(); }

    @Override public boolean hasOpenedExtGrownChest() { return openedExtGrownChest; }
    @Override public void setOpenedExtGrownChest(boolean done) { this.openedExtGrownChest = done; changed(); }

    @Override public boolean hasOpenedExtDoor() { return doorOpened; }
    @Override public void setOpenedExtDoor(boolean done) { this.doorOpened = done; changed(); }

    @Override public boolean isWonderlandMirrorUnlocked() { return wonderlandMirrorUnlocked; }
    @Override public void setWonderlandMirrorUnlocked(boolean unlocked) { this.wonderlandMirrorUnlocked = unlocked; changed(); }

    @Override public int getCompletedStages() { return completedStages; }
    @Override public void setCompletedStages(int stages) { this.completedStages = stages; changed(); }

    @Override public boolean hasMetWhiteRabbit() { return metWhiteRabbit; }
    @Override public void setMetWhiteRabbit(boolean met) { this.metWhiteRabbit = met; changed(); }

    @Override public boolean hasMetInOverworld() { return metInOverworld; }
    @Override public void setMetInOverworld(boolean met) { this.metInOverworld = met; changed(); }

    @Override public void setWonderlandSpawn(BlockPos pos) { this.wonderlandSpawn = pos; changed(); }
    @Override public BlockPos getWonderlandSpawn() { return wonderlandSpawn; }

    @Override public boolean hasMetMadHatter() { return metMadHatter; }
    @Override public void setMetMadHatter(boolean met) { this.metMadHatter = met; changed(); }

    @Override public boolean hasWonMadHatterTrivia() { return wonMadHatterTrivia; }
    @Override public void setWonMadHatterTrivia(boolean won) { this.wonMadHatterTrivia = won; changed(); }

    @Override public boolean hasKilledMadHatter() { return killedMadHatter; }
    @Override public void setKilledMadHatter(boolean killed) { this.killedMadHatter = killed; changed(); }

    @Override public boolean hasKilledSingingFlowers() { return killedSingingFlowers; }
    @Override public void setKilledSingingFlowers(boolean killed) { this.killedSingingFlowers = killed; changed(); }

    @Override public boolean hasKilledCaterpillar() { return killedCaterpillar; }
    @Override public void setKilledCaterpillar(boolean killed) { this.killedCaterpillar = killed; changed(); }

    @Override
    public void reset() {
        exteriorDone = false;
        wonderlandSpawn = null;
        openedExtChest = false;
        openedExtGrownChest = false;
        doorOpened = false;
        wonderlandMirrorUnlocked = false;
        completedStages = 0;
        metWhiteRabbit = false;
        metInOverworld = false;
        metMadHatter = false;
        wonMadHatterTrivia = false;
        killedMadHatter = false;
        killedSingingFlowers = false;
        killedCaterpillar = false;
        changed();
    }

   public void load(CompoundTag tag) {
        exteriorDone = tag.getBoolean("ExteriorDone");
        openedExtChest = tag.getBoolean("OpenedExtChest");
        openedExtGrownChest = tag.getBoolean("OpenedExtGrownChest");
        doorOpened = tag.getBoolean("DoorOpened");
        wonderlandMirrorUnlocked = tag.getBoolean("WonderlandMirrorUnlocked");
        completedStages = tag.getInt("CompletedStages");
        metWhiteRabbit = tag.getBoolean("MetWhiteRabbit");
        metInOverworld = tag.getBoolean("MetInOverworld");
        metMadHatter = tag.getBoolean("MetMadHatter");
        wonMadHatterTrivia = tag.getBoolean("WonMadHatterTrivia");
        killedMadHatter = tag.getBoolean("KilledMadHatter");
        killedSingingFlowers = tag.getBoolean("KilledSingingFlowers");
        killedCaterpillar = tag.getBoolean("KilledCaterpillar");
        wonderlandSpawn = NbtUtils.readBlockPos(tag, "wonderlandSpawn").orElse(null);
    }

    public void save(CompoundTag tag) {
        tag.putBoolean("ExteriorDone", exteriorDone);
        tag.putBoolean("DoorOpened", doorOpened);
        tag.putBoolean("OpenedExtChest", openedExtChest);
        tag.putBoolean("OpenedExtGrownChest", openedExtGrownChest);
        tag.putBoolean("WonderlandMirrorUnlocked", wonderlandMirrorUnlocked);
        tag.putInt("CompletedStages", completedStages);
        tag.putBoolean("MetWhiteRabbit", metWhiteRabbit);
        tag.putBoolean("MetInOverworld", metInOverworld);
        tag.putBoolean("MetMadHatter", metMadHatter);
        tag.putBoolean("WonMadHatterTrivia", wonMadHatterTrivia);
        tag.putBoolean("KilledMadHatter", killedMadHatter);
        tag.putBoolean("KilledSingingFlowers", killedSingingFlowers);
        tag.putBoolean("KilledCaterpillar", killedCaterpillar);
        if (wonderlandSpawn != null) {
            tag.put("wonderlandSpawn", NbtUtils.writeBlockPos(wonderlandSpawn));
        }
    }
}
