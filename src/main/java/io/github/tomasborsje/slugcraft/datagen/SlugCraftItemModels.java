package io.github.tomasborsje.slugcraft.datagen;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SlugCraftItemModels extends ItemModelProvider {

    public SlugCraftItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SlugCraft.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.SIMPLE_BLOCK.getId().getPath(), modLoc("block/simple_block"));
        basicItem(Registration.HOST_WAND.get());
        basicItem(Registration.RIVULET_SOUL.get());
        basicItem(Registration.HUNTER_SOUL.get());
        basicItem(Registration.SPEARMASTER_SOUL.get());
        basicItem(Registration.ARTIFICER_SOUL.get());
        basicItem(Registration.SAINT_SOUL.get());
        basicItem(Registration.GOURMAND_SOUL.get());
        basicItem(Registration.SPORE_PUFF.get());
    }
}
