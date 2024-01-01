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
        // Effects
        add(Registration.ROTTING.get(), "Rotting");
    }
}
