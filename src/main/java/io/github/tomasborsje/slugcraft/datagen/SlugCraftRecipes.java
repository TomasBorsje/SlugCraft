package io.github.tomasborsje.slugcraft.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class SlugCraftRecipes extends RecipeProvider {

    public SlugCraftRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // https://www.mcjty.eu/docs/1.20/ep2#data-generation
    }
}
