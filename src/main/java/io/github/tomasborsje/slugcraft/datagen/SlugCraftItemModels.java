package io.github.tomasborsje.slugcraft.datagen;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SlugCraftItemModels extends ItemModelProvider {

    public SlugCraftItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SlugCraft.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.SIMPLE_BLOCK.getId().getPath(), modLoc("block/simple_block"));
        basicItem(Registration.RIVULET_SOUL.get());
        basicItem(Registration.HUNTER_SOUL.get());
    }
}
