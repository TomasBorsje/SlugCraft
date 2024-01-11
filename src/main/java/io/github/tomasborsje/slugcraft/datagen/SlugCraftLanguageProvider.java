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
        add("slugcraft.saint_power", "You can grapple by right clicking while crouching.");
        add("slugcraft.saint_power_two", "After 15 minutes you §2§lAscend§r, gaining bursts of flight and permanent regeneration.");
        add("slugcraft.saint_haiku_one", "§8§oAnchored to this world");
        add("slugcraft.saint_haiku_two", "§8§oThe beginning and the end");
        add("slugcraft.saint_haiku_three", "§8§oApotheosis");
        // Gourmand
        add(Registration.GOURMAND_SOUL.get(), "§6§lSoul of the Gourmand");
        add("slugcraft.gourmand_power", "You can eat rotten flesh without getting hungry.");
        add("slugcraft.gourmand_power_two", "You can eat any food instantly.");
        add("slugcraft.gourmand_haiku_one", "§8§oA hunger for flesh");
        add("slugcraft.gourmand_haiku_two", "§8§oA hunger for the living");
        add("slugcraft.gourmand_haiku_three", "§8§oA hunger for life");
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
        // Quickfire
        add("message.slugcraft.quickfire.round_start", "§b§lThe round has begun!");
        add("message.slugcraft.quickfire.round_end", "§b§lRound over! §b§l%s won!");
    }
}
