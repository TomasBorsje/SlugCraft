package io.github.tomasborsje.slugcraft.core;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.blocks.SimpleBlock;
import io.github.tomasborsje.slugcraft.effect.RottingEffect;
import io.github.tomasborsje.slugcraft.items.HostWand;
import io.github.tomasborsje.slugcraft.items.HunterSoul;
import io.github.tomasborsje.slugcraft.items.RivuletSoul;
import io.github.tomasborsje.slugcraft.quickfire.IQuickfireCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    public static Item.Properties ITEM_PROPERTIES = new Item.Properties();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlugCraft.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlugCraft.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SlugCraft.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SlugCraft.MODID);

    public static final RegistryObject<SimpleBlock> SIMPLE_BLOCK = BLOCKS.register("simple_block", SimpleBlock::new);
    public static final RegistryObject<Item> SIMPLE_BLOCK_ITEM = ITEMS.register("simple_block", () -> new BlockItem(SIMPLE_BLOCK.get(), new Item.Properties()));

    // Capabilities
    public static Capability<IQuickfireCapability> QUICKFIRE_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    // Items
    public static final RegistryObject<HostWand> HOST_WAND = ITEMS.register("host_wand", HostWand::new);

    // Souls
    public static final RegistryObject<RivuletSoul> RIVULET_SOUL = ITEMS.register("rivulet_soul", RivuletSoul::new);
    public static final RegistryObject<HunterSoul> HUNTER_SOUL = ITEMS.register("hunter_soul", HunterSoul::new);
    // Effects
    public static final RegistryObject<MobEffect> ROTTING = EFFECTS.register("rotting", RottingEffect::new);

    /**
     * Add items to the creative tab.
     */
    static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(SIMPLE_BLOCK_ITEM);
            event.accept(RIVULET_SOUL);
            event.accept(HUNTER_SOUL);
            event.accept(HOST_WAND);
        }
    }

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        EFFECTS.register(modEventBus);
    }
}