package net.emanueljdf09.dtrhmod.world.features.tree.deco;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class ModTreeDeco {
    public static final TreeDecoratorType<WwTreeDecorator> WW_TREE_DECORATOR =
            Registry.register(
                    BuiltInRegistries.TREE_DECORATOR_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "ww_tree_decorator"),
                    new TreeDecoratorType<>(WwTreeDecorator.CODEC)
            );

    public static final TreeDecoratorType<HangingLeavesTreeDeco> HANGING_LEAVES_DECORATOR =
            Registry.register(
                    BuiltInRegistries.TREE_DECORATOR_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hanging_leaves_decorator"),
                    new TreeDecoratorType<>(HangingLeavesTreeDeco.CODEC)
            );

    public static final TreeDecoratorType<HangFromTreeDeco> HANG_FROM_TREE_DECORATOR =
            Registry.register(
                    BuiltInRegistries.TREE_DECORATOR_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hang_from_tree_decorator"),
                    new TreeDecoratorType<>(HangFromTreeDeco.CODEC)
            );


    public static void register() {

    }
}
