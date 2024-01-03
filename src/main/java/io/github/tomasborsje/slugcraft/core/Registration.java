package io.github.tomasborsje.slugcraft.core;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.blocks.SimpleBlock;
import io.github.tomasborsje.slugcraft.effect.RottingEffect;
import io.github.tomasborsje.slugcraft.entities.ThrownExplosiveSpear;
import io.github.tomasborsje.slugcraft.entities.ThrownNeedle;
import io.github.tomasborsje.slugcraft.entities.ThrownSpear;
import io.github.tomasborsje.slugcraft.models.ExplosiveSpearModel;
import io.github.tomasborsje.slugcraft.models.NeedleModel;
import io.github.tomasborsje.slugcraft.models.SpearModel;
import io.github.tomasborsje.slugcraft.renderers.ThrownExplosiveSpearRenderer;
import io.github.tomasborsje.slugcraft.renderers.ThrownNeedleRenderer;
import io.github.tomasborsje.slugcraft.items.*;
import io.github.tomasborsje.slugcraft.quickfire.IQuickfireCapability;
import io.github.tomasborsje.slugcraft.renderers.ThrownSpearRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    public static Item.Properties ITEM_PROPERTIES = new Item.Properties();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlugCraft.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlugCraft.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SlugCraft.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SlugCraft.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SlugCraft.MODID);
    // Entities
    public static final RegistryObject<EntityType<ThrownNeedle>> THROWN_NEEDLE = ENTITIES.register("thrown_needle",
            () -> EntityType.Builder.<ThrownNeedle>of(ThrownNeedle::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .updateInterval(5)
                    .build("slugcraft:thrown_needle"));

    public static final RegistryObject<EntityType<ThrownSpear>> THROWN_SPEAR = ENTITIES.register("thrown_spear",
            () -> EntityType.Builder.<ThrownSpear>of(ThrownSpear::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .updateInterval(5)
                    .build("slugcraft:thrown_spear"));

    public static final RegistryObject<EntityType<ThrownExplosiveSpear>> THROWN_EXPLOSIVE_SPEAR = ENTITIES.register("thrown_explosive_spear",
            () -> EntityType.Builder.<ThrownExplosiveSpear>of(ThrownExplosiveSpear::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .updateInterval(5)
                    .build("slugcraft:thrown_explosive_spear"));



    // Blocks
    public static final RegistryObject<SimpleBlock> SIMPLE_BLOCK = BLOCKS.register("simple_block", SimpleBlock::new);
    public static final RegistryObject<Item> SIMPLE_BLOCK_ITEM = ITEMS.register("simple_block", () -> new BlockItem(SIMPLE_BLOCK.get(), new Item.Properties()));

    // Capabilities
    public static Capability<IQuickfireCapability> QUICKFIRE_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    // Items
    public static final RegistryObject<HostWand> HOST_WAND = ITEMS.register("host_wand", HostWand::new);

    // Souls
    public static final RegistryObject<RivuletSoul> RIVULET_SOUL = ITEMS.register("rivulet_soul", RivuletSoul::new);
    public static final RegistryObject<HunterSoul> HUNTER_SOUL = ITEMS.register("hunter_soul", HunterSoul::new);
    public static final RegistryObject<SpearmasterSoul> SPEARMASTER_SOUL = ITEMS.register("spearmaster_soul", SpearmasterSoul::new);
    public static final RegistryObject<Needle> NEEDLE = ITEMS.register("needle", Needle::new);
    // Spears
    public static final RegistryObject<Spear> SPEAR = ITEMS.register("spear", Spear::new);
    public static final RegistryObject<ExplosiveSpear> EXPLOSIVE_SPEAR = ITEMS.register("explosive_spear", ExplosiveSpear::new);

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

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        SlugCraft.LOGGER.info("Registering Slugcraft entity renderers");
        event.registerEntityRenderer(THROWN_NEEDLE.get(), ThrownNeedleRenderer::new);
        event.registerEntityRenderer(THROWN_EXPLOSIVE_SPEAR.get(), ThrownExplosiveSpearRenderer::new);
        event.registerEntityRenderer(THROWN_SPEAR.get(), ThrownSpearRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        SlugCraft.LOGGER.info("Registering Slugcraft layer definitions");
        event.registerLayerDefinition(NeedleModel.LAYER_LOCATION, NeedleModel::createBodyLayer);
        event.registerLayerDefinition(ExplosiveSpearModel.LAYER_LOCATION, ExplosiveSpearModel::createBodyLayer);
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
    }

    public static void init(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        EFFECTS.register(modEventBus);
    }

}