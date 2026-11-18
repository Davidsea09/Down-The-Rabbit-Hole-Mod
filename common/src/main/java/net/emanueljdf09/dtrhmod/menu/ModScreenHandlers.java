package net.emanueljdf09.dtrhmod.menu;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.menu.handler.TeapotScreenHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;

public class ModScreenHandlers {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<TeapotScreenHandler>> TEAPOT_SCREEN_HANDLER =
            MENUS.register("teapot_screen", () -> MenuRegistry.ofExtended(TeapotScreenHandler::new));

    public static void registerScreenHandlers() {
        MENUS.register();
        DownTheRabbitHole.LOGGER.info("Registering Screen Handlers for " + DownTheRabbitHole.MOD_ID);
    }
}