package net.emanueljdf09.dtrhmod.util.components;

import net.minecraft.core.BlockPos;

public interface ProgressionComponent {

    boolean hasDoneExterior();
    void setExteriorDone(boolean done);

    void setWonderlandSpawn(BlockPos pos);
    BlockPos getWonderlandSpawn();

    boolean hasOpenedExtChest();
    void setOpenedExtChest(boolean done);
    boolean hasOpenedExtGrownChest();
    void setOpenedExtGrownChest(boolean done);

    boolean hasOpenedExtDoor();
    void setOpenedExtDoor(boolean done);

    boolean isWonderlandMirrorUnlocked();
    void setWonderlandMirrorUnlocked(boolean unlocked);

    int getCompletedStages();
    void setCompletedStages(int stages);

    boolean hasMetWhiteRabbit();
    void setMetWhiteRabbit(boolean met);

    boolean hasMetInOverworld();
    void setMetInOverworld(boolean met);

    boolean hasMetMadHatter();
    void setMetMadHatter(boolean met);

    boolean hasWonMadHatterTrivia();
    void setWonMadHatterTrivia(boolean won);

    boolean hasKilledMadHatter();
    void setKilledMadHatter(boolean killed);

    boolean hasKilledSingingFlowers();
    void setKilledSingingFlowers(boolean killed);

    boolean hasKilledCaterpillar();
    void setKilledCaterpillar(boolean killed);

    void reset();
}