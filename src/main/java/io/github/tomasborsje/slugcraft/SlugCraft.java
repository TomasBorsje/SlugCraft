package io.github.tomasborsje.slugcraft;

import com.mojang.logging.LogUtils;
import io.github.tomasborsje.slugcraft.core.Config;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.datagen.DataGeneration;
import io.github.tomasborsje.slugcraft.quickfire.QuickfireEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SlugCraft.MODID)
public class SlugCraft
{
    public static final String MODID = "slugcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SlugCraft()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Registration.init(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::CommonSetup);
        modEventBus.addListener(DataGeneration::generateData);
        //modEventBus.addListener(QuickfireHandler::OnWorldTick);
        MinecraftForge.EVENT_BUS.register(QuickfireEvents.class);
        MinecraftForge.EVENT_BUS.register(Registration.class);
        modEventBus.addListener(Registration::registerEntityRenderers);
        modEventBus.addListener(Registration::registerLayerDefinitions);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void CommonSetup(final FMLCommonSetupEvent event)
    {
    }
}
