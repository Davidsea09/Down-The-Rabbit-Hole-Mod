package net.emanueljdf09.dtrhmod.world.features.tree.sapling;

import net.emanueljdf09.dtrhmod.world.features.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;
import java.util.Optional;

public class ModSaplingsGenerators {
    public static final TreeGrower BB_SAPLING = new TreeGrower(
            "bb",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.BB_TREE_SELECTOR),
            Optional.empty()
    );

    public static final TreeGrower TH_SAPLING = new TreeGrower(
            "th",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.TH_TREE_SELECTOR),
            Optional.empty()
    );

    public static final TreeGrower WW_SAPLING = new TreeGrower(
            "ww",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.WW_TREE_SELECTOR),
            Optional.empty()
    );

    public static final TreeGrower HH_SAPLING = new TreeGrower(
            "hh",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.HH_TREE_SELECTOR),
            Optional.empty()
    );
}
