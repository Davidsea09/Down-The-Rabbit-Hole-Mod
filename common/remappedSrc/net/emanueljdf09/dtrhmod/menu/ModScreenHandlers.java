package net.emanueljdf09.dtrhmod.menu;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.menu.handler.TeapotScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers {
    public static final MenuType<TeapotScreenHandler> TEAPOT_SCREEN_HANDLER =
            Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot_screen"),
                    new ExtendedScreenHandlerType<>(TeapotScreenHandler::new, BlockPos.STREAM_CODEC));

    public static void registerScreenHandlers() {
        DownTheRabbitHole.LOGGER.info("Registering Screen Handlers for " + DownTheRabbitHole.MOD_ID);
    }
}
