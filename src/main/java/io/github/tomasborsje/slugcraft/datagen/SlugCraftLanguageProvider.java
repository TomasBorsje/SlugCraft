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
        add("slugcraft.spearmaster_power", "Gain a throwable needle every 30 seconds.");
        add("slugcraft.spearmaster_power_two", "Hitting a player with a needle grants speed and an absorption shield.");
        add("slugcraft.spearmaster_haiku_one", "§8§oA master of spears");
        add("slugcraft.spearmaster_haiku_two", "§8§oA master of the hunt");
        add("slugcraft.spearmaster_haiku_three", "§8§oA master of time");
        // Spears
        add(Registration.EXPLOSIVE_SPEAR.get(), "§cExplosive Spear");
        add("slugcraft.explosive_spear_description", "A spear that explodes on impact.");
        add(Registration.SPEAR.get(), "Spear");
        add("slugcraft.spear_description", "A spear that can be thrown.");

        // Effects
        add(Registration.ROTTING.get(), "Rotting");
        add("message.slugcraft.rotting", "§8You are rotting away...");
        add("message.slugcraft.rotting_final", "§8§lYou're out of time...");
    }
}
