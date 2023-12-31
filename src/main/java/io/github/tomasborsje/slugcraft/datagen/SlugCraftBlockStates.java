package io.github.tomasborsje.slugcraft.datagen;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SlugCraftBlockStates extends BlockStateProvider {
    public SlugCraftBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SlugCraft.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.SIMPLE_BLOCK.get());
    }
}
