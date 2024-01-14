package io.github.tomasborsje.slugcraft.core;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.blocks.SimpleBlock;
import io.github.tomasborsje.slugcraft.effect.RottingEffect;
import io.github.tomasborsje.slugcraft.entities.*;
import io.github.tomasborsje.slugcraft.models.ExplosiveSpearModel;
import io.github.tomasborsje.slugcraft.models.NeedleModel;
import io.github.tomasborsje.slugcraft.models.SpearModel;
import io.github.tomasborsje.slugcraft.quickfire.StartRoundCommand;
import io.github.tomasborsje.slugcraft.renderers.ThrownExplosiveSpearRenderer;
import io.github.tomasborsje.slugcraft.renderers.ThrownNeedleRenderer;
import io.github.tomasborsje.slugcraft.items.*;
import io.github.tomasborsje.slugcraft.quickfire.IQuickfireCapability;
import io.github.tomasborsje.slugcraft.renderers.ThrownSpearRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Registration {

    public static Item.Properties ITEM_PROPERTIES = new Item.Properties();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlugCraft.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlugCraft.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SlugCraft.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SlugCraft.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SlugCraft.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlugCraft.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SlugCraft.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SlugCraft.MODID);
    public static final List<Supplier<? extends ItemLike>> SLUGCRAFT_TAB_ITEMS = new ArrayList<>();
    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike) {
        SLUGCRAFT_TAB_ITEMS.add(itemLike);
        return itemLike;
    }

    // Sounds
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(SlugCraft.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    public static final RegistryObject<SoundEvent> GAIN_KARMA = registerSoundEvent("gain_karma");
    public static final RegistryObject<SoundEvent> THREAT_GARBAGE_WASTES = registerSoundEvent("threat_garbage_wastes");
    public static final RegistryObject<SoundEvent> THREAT_INDUSTRIAL_COMPLEX = registerSoundEvent("threat_industrial_complex");
    public static final RegistryObject<SoundEvent> THREAT_OUTSKIRTS = registerSoundEvent("threat_outskirts");
    public static final RegistryObject<SoundEvent>[] THREAT_MUSICS = new RegistryObject[] {
            THREAT_GARBAGE_WASTES,
            THREAT_INDUSTRIAL_COMPLEX,
            THREAT_OUTSKIRTS
    };

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
    public static final RegistryObject<EntityType<ThrownSporePuff>> THROWN_SPORE_PUFF = ENTITIES.register("thrown_spore_puff",
            () -> EntityType.Builder.<ThrownSporePuff>of(ThrownSporePuff::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .updateInterval(5)
                    .build("slugcraft:thrown_spore_puff"));

    public static final RegistryObject<EntityType<ThrownSingularityGrenade>> THROWN_SINGULARITY_GRENADE = ENTITIES.register("thrown_singularity_grenade",
            () -> EntityType.Builder.<ThrownSingularityGrenade>of(ThrownSingularityGrenade::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .updateInterval(5)
                    .build("slugcraft:thrown_singularity_grenade"));

    // Blocks
    public static final RegistryObject<SimpleBlock> SIMPLE_BLOCK = BLOCKS.register("simple_block", SimpleBlock::new);
    public static final RegistryObject<Item> SIMPLE_BLOCK_ITEM = ITEMS.register("simple_block", () -> new BlockItem(SIMPLE_BLOCK.get(), new Item.Properties()));

    // Capabilities
    public static Capability<IQuickfireCapability> QUICKFIRE_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    // Items
    public static final RegistryObject<HostWand> HOST_WAND = addToTab(ITEMS.register("host_wand", HostWand::new));
    public static final RegistryObject<SporePuff> SPORE_PUFF = addToTab(ITEMS.register("spore_puff", SporePuff::new));
    public static final RegistryObject<SingularityGrenade> SINGULARITY_GRENADE = addToTab(ITEMS.register("singularity_grenade", SingularityGrenade::new));
    // Souls
    public static final RegistryObject<RivuletSoul> RIVULET_SOUL = addToTab(ITEMS.register("rivulet_soul", RivuletSoul::new));
    public static final RegistryObject<HunterSoul> HUNTER_SOUL = addToTab(ITEMS.register("hunter_soul", HunterSoul::new));
    public static final RegistryObject<SpearmasterSoul> SPEARMASTER_SOUL = addToTab(ITEMS.register("spearmaster_soul", SpearmasterSoul::new));
    public static final RegistryObject<ArtificerSoul> ARTIFICER_SOUL = addToTab(ITEMS.register("artificer_soul", ArtificerSoul::new));
    public static final RegistryObject<SaintSoul> SAINT_SOUL = addToTab(ITEMS.register("saint_soul", SaintSoul::new));
    public static final RegistryObject<GourmandSoul> GOURMAND_SOUL = addToTab(ITEMS.register("gourmand_soul", GourmandSoul::new));
    public static final RegistryObject<EnotSoul> ENOT_SOUL = addToTab(ITEMS.register("enot_soul", EnotSoul::new));
    public static final RegistryObject<Item>[] SLUGCAT_SOULS = new RegistryObject[] {
            RIVULET_SOUL,
            HUNTER_SOUL,
            SPEARMASTER_SOUL,
            ARTIFICER_SOUL,
            SAINT_SOUL,
            GOURMAND_SOUL,
            ENOT_SOUL
    };

    // Spears
    public static final RegistryObject<Needle> NEEDLE = addToTab(ITEMS.register("needle", Needle::new));
    public static final RegistryObject<Spear> SPEAR = addToTab(ITEMS.register("spear", Spear::new));
    public static final RegistryObject<ExplosiveSpear> EXPLOSIVE_SPEAR = addToTab(ITEMS.register("explosive_spear", ExplosiveSpear::new));

    // Creative mode tab
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("slugcraft_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.slugcraft"))
                    .icon(RIVULET_SOUL.get()::getDefaultInstance)
                    .displayItems((displayParams, output) ->
                            SLUGCRAFT_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
                    .withSearchBar()
                    .build()
    );

    // Effects
    public static final RegistryObject<MobEffect> ROTTING = EFFECTS.register("rotting", RottingEffect::new);

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        SlugCraft.LOGGER.info("Registering Slugcraft entity renderers");
        event.registerEntityRenderer(THROWN_NEEDLE.get(), ThrownNeedleRenderer::new);
        event.registerEntityRenderer(THROWN_EXPLOSIVE_SPEAR.get(), ThrownExplosiveSpearRenderer::new);
        event.registerEntityRenderer(THROWN_SPEAR.get(), ThrownSpearRenderer::new);
        // Register spore puff
        event.registerEntityRenderer(THROWN_SPORE_PUFF.get(), ThrownItemRenderer::new);
        // Register singularity grenade
        event.registerEntityRenderer(THROWN_SINGULARITY_GRENADE.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        SlugCraft.LOGGER.info("Registering Slugcraft layer definitions");
        event.registerLayerDefinition(NeedleModel.LAYER_LOCATION, NeedleModel::createBodyLayer);
        event.registerLayerDefinition(ExplosiveSpearModel.LAYER_LOCATION, ExplosiveSpearModel::createBodyLayer);
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SlugCraft.LOGGER.info("Registering Slugcraft commands");
        StartRoundCommand.register(event.getDispatcher());
    }

    public static void init(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        EFFECTS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
    }
}