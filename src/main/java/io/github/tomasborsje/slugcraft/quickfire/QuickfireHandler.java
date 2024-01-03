package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

public class QuickfireHandler {
    private static LazyOptional<IQuickfireCapability> quickfireData;
    @SubscribeEvent
    public static void OnAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
        if(!(event.getObject() instanceof Level)) { return; }

        QuickfireCapability backend = new QuickfireCapability();
        LazyOptional<IQuickfireCapability> optionalStorage = LazyOptional.of(() -> backend);

        ICapabilityProvider provider = new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
                if (cap == Registration.QUICKFIRE_HANDLER) {
                    return optionalStorage.cast();
                }
                return LazyOptional.empty();
            }
        };
        SlugCraft.LOGGER.info("Attached Quickfire Level capability.");
        event.addCapability(new ResourceLocation(SlugCraft.MODID, "quickfire_handler_cap"), provider);
    }

    @SubscribeEvent
    public static void OnWorldTick(TickEvent.LevelTickEvent event) {
        if(event.side == LogicalSide.CLIENT) {
            return;
        }
        // Only tick world events on the server
        ServerLevel level = (ServerLevel) event.level;

        // Get or create capability
        if (quickfireData == null) {
            quickfireData = level.getCapability(Registration.QUICKFIRE_HANDLER);
            quickfireData.addListener(self -> quickfireData = null);
        }

        // If quickfire data exists, run logic
        quickfireData.ifPresent(quickfire -> {
            // Start phase
            if(event.phase == TickEvent.Phase.START) {
                quickfire.tickWorld(level);
            }

            // End phase
            if(event.phase == TickEvent.Phase.END) {

            }
        });
    }
}
