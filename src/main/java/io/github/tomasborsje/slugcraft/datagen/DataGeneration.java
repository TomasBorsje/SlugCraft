package io.github.tomasborsje.slugcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGeneration {

    public static void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Block States, Item Models, Language Providers
        generator.addProvider(event.includeClient(), new SlugCraftBlockStates(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new SlugCraftItemModels(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new SlugCraftLanguageProvider(packOutput, "en_us"));

        // Block Tags, Item Tags
        SlugCraftBlockTags blockTags = new SlugCraftBlockTags(packOutput, lookupProvider, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new SlugCraftItemTags(packOutput, lookupProvider, blockTags, event.getExistingFileHelper()));

        // Recipes, Loot Tables
        generator.addProvider(event.includeServer(), new SlugCraftRecipes(packOutput));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(SlugCraftLootTables::new, LootContextParamSets.BLOCK))));
    }
}
