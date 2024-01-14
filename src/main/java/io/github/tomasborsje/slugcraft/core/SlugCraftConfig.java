package io.github.tomasborsje.slugcraft.core;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SlugCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlugCraftConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue WORLD_BORDER_START_SIZE = BUILDER
            .comment("The start size of the world border.")
            .defineInRange("worldBorderStartSize", 225, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue WORLD_BORDER_END_SIZE = BUILDER
            .comment("The end size of the world border.")
            .defineInRange("worldBorderEndSize", 30, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue QUICKFIRE_ROUND_TIME = BUILDER
            .comment("The time in seconds for a round of Quickfire.")
            .defineInRange("quickfireRoundTime", 600, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue SAINT_SECONDS_PER_KARMA_LEVEL = BUILDER
            .comment("The time in seconds for Saint to gain a karma level.")
            .defineInRange("saintSecondsPerKarmaLevel", 60, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue QUICKFIRE_GRACE_PERIOD_TIME = BUILDER
            .comment("The time in seconds until the grace period ends.")
            .defineInRange("quickfireGracePeriodTime", 20, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue QUICKFIRE_HARD_RAIN_DELAY = BUILDER
            .comment("Time until the hard rain starts after the world border has closed.")
            .defineInRange("quickfireHardRainDelay", 30, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int worldBorderStartSize;
    public static int worldBorderEndSize;
    public static int quickfireRoundTime;
    public static int quickfireGracePeriodTime;
    public static int saintSecondsPerKarmaLevel;
    public static int quickfireHardRainDelay;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        SlugCraft.LOGGER.debug("Loading SlugCraft's config file...");
        worldBorderStartSize = WORLD_BORDER_START_SIZE.get();
        worldBorderEndSize = WORLD_BORDER_END_SIZE.get();
        quickfireRoundTime = QUICKFIRE_ROUND_TIME.get();
        quickfireGracePeriodTime = QUICKFIRE_GRACE_PERIOD_TIME.get();
        saintSecondsPerKarmaLevel = SAINT_SECONDS_PER_KARMA_LEVEL.get();
        quickfireHardRainDelay = QUICKFIRE_HARD_RAIN_DELAY.get();
    }
}
