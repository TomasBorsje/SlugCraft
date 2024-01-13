package io.github.tomasborsje.slugcraft.datagen;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class SlugCraftLanguageProvider extends LanguageProvider {
    public SlugCraftLanguageProvider(PackOutput output, String locale) {
        super(output, SlugCraft.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.SIMPLE_BLOCK.get(), "Simple Block");
        // System messages
        add("message.slugcraft.quickfire.no_toss", "§cYou can't drop your soul!");
        // Host wand
        add(Registration.HOST_WAND.get(), "§bHost Wand");
        add("slugcraft.host_wand_description", "Right click to start a round of Quickfire!");
        // Rivulet
        add(Registration.RIVULET_SOUL.get(), "§b§lSoul of the Rivulet");
        add("slugcraft.rivulet_power", "Gain increased movement speed, swim speed, and jump height.");
        add("slugcraft.rivulet_power_two", "You can breathe underwater.");
        add("slugcraft.rivulet_haiku_one", "§8§oWith speed and with flow");
        add("slugcraft.rivulet_haiku_two", "§8§oRivulet makes the journey");
        add("slugcraft.rivulet_haiku_three", "§8§oFrom rot to ocean");
        // Hunter
        add(Registration.HUNTER_SOUL.get(), "§c§lSoul of the Hunter");
        add("slugcraft.hunter_power", "Your melee attacks deal an extra 6 hearts of damage.");
        add("slugcraft.hunter_power_two", "§4You lose one heart of max health every 3 minutes.");
        add("slugcraft.hunter_haiku_one", "§8§oA fierce warrior");
        add("slugcraft.hunter_haiku_two", "§8§oA grievous affliction");
        add("slugcraft.hunter_haiku_three", "§8§oTime is running out");
        // Spearmaster
        add(Registration.SPEARMASTER_SOUL.get(), "§5§lSoul of the Spearmaster");
        add(Registration.NEEDLE.get(), "§5§lNeedle");
        add("item.slugcraft.needle.hit", "§5§lYou struck %s§5§l with a needle!");
        add("item.slugcraft.needle.receive_hit", "§5§lYou were struck by a needle!");
        add("slugcraft.needle_description", "Throw at an enemy to slow them, gaining speed and an absorption shield.");
        add("message.slugcraft.quickfire.needle_gained", "§5§lYou created a needle!");
        add("slugcraft.spearmaster_power", "Gain a throwable needle every 30 seconds, up to three.");
        add("slugcraft.spearmaster_power_two", "Hitting a player with a needle grants speed, an absorption shield, and fills your food.");
        add("slugcraft.spearmaster_haiku_one", "§8§oA resourceful being");
        add("slugcraft.spearmaster_haiku_two", "§8§oCreate your own vicious weapons");
        add("slugcraft.spearmaster_haiku_three", "§8§oFeed on their lifeforce");
        // Artificer
        add(Registration.ARTIFICER_SOUL.get(), "§c§lSoul of the Artificer");
        add("slugcraft.artificer_power", "Press space while in the air to launch yourself.");
        add("slugcraft.artificer_power_two", "Any spears you pick up automatically become §cexplosive.");
        add("slugcraft.artificer_haiku_one", "§8§oA burning vengeance");
        add("slugcraft.artificer_haiku_two", "§8§oBringing destruction and death");
        add("slugcraft.artificer_haiku_three", "§8§oDon't get in her way");
        // Saint
        add(Registration.SAINT_SOUL.get(), "§2§lSoul of the Saint");
        add("slugcraft.saint_power", "Gain a karma level every minute you go without damaging a living creature.");
        add("slugcraft.saint_power_two", "At max karma you §2§lAscend§r, gaining bursts of flight and permanent regeneration.");
        add("slugcraft.karma_level_reminder", "\uE000  \uE001  \uE002  \uE003  \uE004  \uE005  \uE006  \uE007  \uE008  \uE009");
        add("message.slugcraft.quickfire.saint_hurt", "§r§f\uE000  §2§lYou  hurt  something!  Your  karma  level  has  droppedq.§r§f  \uE000");
        add("message.slugcraft.quickfire.saint_ascend", "§2You have ascended!");
        add("message.slugcraft.quickfire.player_ascend", "§r§f\uE009  §2§l%s  has  ascended!§r§f  \uE009");
        add("slugcraft.saint_haiku_one", "§8§oAnchored to the world");
        add("slugcraft.saint_haiku_two", "§8§oThe beginning and the end");
        add("slugcraft.saint_haiku_three", "§8§oApotheosis");
        // Gourmand
        add(Registration.GOURMAND_SOUL.get(), "§6§lSoul of the Gourmand");
        add("slugcraft.gourmand_power", "Crouch to eat your currently held item if it can't stack.");
        add("slugcraft.gourmand_power_two", "Eat two items to craft a random item.");
        add("slugcraft.gourmand_haiku_one", "§8§oA hunger for flesh");
        add("slugcraft.gourmand_haiku_two", "§8§oA hunger for the living");
        add("slugcraft.gourmand_haiku_three", "§8§oA hunger for life");
        add("message.slugcraft.gourmand.item_eaten", "§6§lYou ate %s§6§l!");
        add("message.slugcraft.gourmand.item_craft", "§6§lYou crafted %s§6§l!");
        // Spore Puff
        add(Registration.SPORE_PUFF.get(), "Spore Puff");
        add("slugcraft.spore_puff_description", "Throw to create a puff of spores, blinding and poisoning anyone caught in the cloud.");
        // Spears
        add(Registration.EXPLOSIVE_SPEAR.get(), "§cExplosive Spear");
        add("slugcraft.explosive_spear_description", "A spear that explodes on impact.");
        add(Registration.SPEAR.get(), "Spear");
        add("slugcraft.spear_description", "A spear that can be thrown.");
        // Creative tab
        add("itemGroup.slugcraft", "SlugCraft");
        // Effects
        add(Registration.ROTTING.get(), "Rotting");
        add("message.slugcraft.rotting", "§8You are rotting away...");
        add("message.slugcraft.rotting_final", "§8§lYou're out of time...");
        // Karma Levels
        add("message.slugcraft.karma_level.1", "\uE000");
        add("message.slugcraft.karma_level.2", "\uE001");
        add("message.slugcraft.karma_level.3", "\uE002");
        add("message.slugcraft.karma_level.4", "\uE003");
        add("message.slugcraft.karma_level.5", "\uE004");
        add("message.slugcraft.karma_level.6", "\uE005");
        add("message.slugcraft.karma_level.7", "\uE006");
        add("message.slugcraft.karma_level.8", "\uE007");
        add("message.slugcraft.karma_level.9", "\uE008");
        add("message.slugcraft.karma_level.10", "\uE009");
        // Quickfire
        add("message.slugcraft.quickfire.round_start", "§b§lThe round has begun!");
        add("message.slugcraft.quickfire.round_end", "§b§lRound over! §b§l%s won!");
        add("message.slugcraft.quickfire.player_died", "§b§l%s has died!");
    }
}
