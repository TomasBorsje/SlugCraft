// priority: 0

console.info('Loading Quickfire v2 Loot Tables!')

LootJS.modifiers((event) => {
    event
        .addLootTableModifier(/(.*:chests?\/.*)|(structory:.+)/)
		
		// Uncommon Food, 40% chance
        .pool((pool) => {
            pool.randomChance(0.4)
            pool.addWeightedLoot([1, 2], [
				Item.of("alexsmobs:cooked_moose_ribs").withChance(5),
				Item.of("alexsmobs:cooked_lobster_tail").withChance(5),
				Item.of("alexsmobs:cooked_kangaroo_meat").withChance(5),
				Item.of("twilightforest:cooked_meef").withChance(5),
				Item.of("twilightforest:cooked_venison").withChance(5),
			]);
			pool.limitCount([3, 3], [4, 5]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })
		
		// Misc Bulk Uncommon Loot, 30% chance
		.pool((pool) => {
            pool.randomChance(0.3)
            pool.addWeightedLoot([1, 2], [
				Item.of("cyclic:fireball").withChance(5),
				Item.of("cyclic:dark_fireball").withChance(2),
				Item.of("minecraft:tnt").withChance(5),
				Item.of("minecraft:arrow").withChance(10),
				Item.of('minecraft:tipped_arrow', '{Potion:"rottencreatures:strong_freeze"}').withChance(1),
				Item.of('create:experience_nugget').withChance(3),
				Item.of('minecraft:cobweb').withChance(5),
			]);
			pool.limitCount([4, 6], [7, 11])
			pool.modifyLoot(Item.of("minecraft:arrow"), (itemStack) => {
				itemStack.setCount(24);
				return itemStack;
			});
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })
		
		// 10% 10-20 random arrows
		.pool((pool) => {
            pool.randomChance(0.1)
            pool.addWeightedLoot([1, 1], [
				Item.of("archers_paradox:blaze_arrow").withChance(3),
				Item.of("archers_paradox:shulker_arrow").withChance(2),
				Item.of("archers_paradox:frost_arrow").withChance(3),
				Item.of("archers_paradox:lightning_arrow").withChance(3),
				Item.of("archers_paradox:phantasmal_arrow").withChance(4),
				Item.of("archers_paradox:diamond_arrow").withChance(2),
				Item.of("archers_paradox:explosive_arrow").withChance(1),
				Item.of("archers_paradox:quartz_arrow").withChance(3),
			]);
			pool.limitCount([20, 21], [25, 27]);
		    pool.addLore(Text.blue("RARE LOOT").bold());
        })
		
		// Misc Singular Uncommon Loot, 50% chance
		.pool((pool) => {
            pool.randomChance(0.5)
            pool.addWeightedLoot([1, 2], [
				Item.of("quark:pickarang").withChance(5),
				Item.of("minecraft:fishing_rod").withChance(5),
				Item.of("minecraft:bow").withChance(10),
				Item.of("minecraft:iron_ingot", 8).withChance(10),
				Item.of("minecraft:diamond", 6).withChance(5),
				Item.of("minecraft:crossbow").withChance(10),
				Item.of("byg:pendorite_pickaxe").withChance(3),
				Item.of("alexsmobs:skelewag_sword").withChance(3),
				Item.of("rottencreatures:treasure_chest").withChance(1),
				Item.of("twilightforest:uncrafting_table").withChance(1),
				Item.of("twilightforest:lamp_of_cinders").withChance(1),
				Item.of("minecraft:iron_helmet").withChance(2),
				Item.of("minecraft:iron_chestplate").withChance(2),
				Item.of("minecraft:iron_leggings").withChance(2),
				Item.of("minecraft:iron_boots").withChance(2),
				Item.of("minecraft:chainmail_helmet").withChance(3),
				Item.of("minecraft:chainmail_chestplate").withChance(3),
				Item.of("minecraft:chainmail_leggings").withChance(3),
				Item.of("minecraft:chainmail_boots").withChance(3),
				Item.of("alexsmobs:roadrunner_boots").withChance(3),
				Item.of("alexsmobs:frontier_cap").withChance(3),
				Item.of("minecraft:leather_helmet").withChance(3),
				Item.of("minecraft:leather_chestplate").withChance(3),
				Item.of("minecraft:leather_leggings").withChance(3),
				Item.of("minecraft:leather_boots").withChance(3),
				Item.of("minecraft:flint_and_steel").withChance(20),
			]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })
		
		// 1 Rare Weapon, 15% Chance
		.pool((pool) => {
            pool.randomChance(0.15)
            pool.addWeightedLoot([1, 1], [
				Item.of("dungeons_gear:dagger"),
				Item.of("dungeons_gear:fang_of_frost"),
				Item.of("dungeons_gear:moon_dagger"),
				Item.of("dungeons_gear:shear_dagger"),
				Item.of("dungeons_gear:sickle"),
				Item.of("dungeons_gear:nightmares_bite"),
				Item.of("dungeons_gear:the_last_laugh"),
				Item.of("dungeons_gear:rapier"),
				Item.of("dungeons_gear:bee_stinger"),
				Item.of("dungeons_gear:freezing_foil"),
				Item.of("dungeons_gear:soul_scythe"),
				Item.of("dungeons_gear:frozen_scythe"),
				Item.of("dungeons_gear:jailors_scythe"),
				Item.of("dungeons_gear:cutlass"),
				Item.of("dungeons_gear:dancers_sword"),
				Item.of("dungeons_gear:nameless_blade"),
				Item.of("dungeons_gear:sparkler"),
				Item.of("dungeons_gear:sword"),
				Item.of("dungeons_gear:hawkbrand"),
				Item.of("alexsmobs:tendon_whip"),
				Item.of("dungeons_gear:sinister_sword"),
				Item.of("dungeons_gear:battlestaff"),
				Item.of("dungeons_gear:battlestaff_of_terror"),
				Item.of("dungeons_gear:growing_staff"),
				Item.of("dungeons_gear:firebrand"),
				Item.of("dungeons_gear:highland_axe"),
				Item.of("dungeons_gear:double_axe"),
				Item.of("dungeons_gear:cursed_axe"),
				Item.of("dungeons_gear:whirlwind"),
				Item.of("dungeons_gear:mace"),
				Item.of("dungeons_gear:flail"),
				Item.of("dungeons_gear:suns_grace"),
				Item.of("dungeons_gear:great_hammer"),
				Item.of("dungeons_gear:hammer_of_gravity"),
				Item.of("dungeons_gear:stormlander"),
				Item.of("dungeons_gear:katana"),
				Item.of("dungeons_gear:dark_katana"),
				Item.of("dungeons_gear:masters_katana"),
				Item.of("dungeons_gear:soul_knife"),
				Item.of("dungeons_gear:eternal_knife"),
				Item.of("dungeons_gear:truthseeker"),
				Item.of("dungeons_gear:claymore"),
				Item.of("dungeons_gear:broadsword"),
				Item.of("dungeons_gear:heartstealer"),
				Item.of("dungeons_gear:great_axeblade"),
				Item.of("dungeons_gear:frost_slayer"),
				Item.of("dungeons_gear:spear"),
				Item.of("dungeons_gear:fortune_spear"),
				Item.of("dungeons_gear:whispering_spear"),
				Item.of("dungeons_gear:glaive"),
				Item.of("dungeons_gear:grave_bane"),
				Item.of("dungeons_gear:venom_glaive"),
				Item.of("dungeons_gear:whip"),
				Item.of("dungeons_gear:vine_whip"),
				Item.of("dungeons_gear:tempest_knife"),
				Item.of("dungeons_gear:resolute_tempest_knife"),
				Item.of("dungeons_gear:chill_gale_knife"),
				Item.of("dungeons_gear:boneclub"),
				Item.of("dungeons_gear:bone_cudgel"),
				Item.of("dungeons_gear:anchor"),
				Item.of("dungeons_gear:encrusted_anchor"),
				Item.of("dungeons_gear:sabrewing"),
				Item.of("dungeons_gear:twin_bow"),
				Item.of("dungeons_gear:haunted_bow"),
				Item.of("dungeons_gear:soul_bow"),
				Item.of("dungeons_gear:bow_of_lost_souls"),
				Item.of("dungeons_gear:nocturnal_bow"),
				Item.of("dungeons_gear:shivering_bow"),
				Item.of("dungeons_gear:power_bow"),
				Item.of("dungeons_gear:elite_power_bow"),
				Item.of("dungeons_gear:bonebow"),
				Item.of("dungeons_gear:longbow"),
				Item.of("dungeons_gear:guardian_bow"),
				Item.of("dungeons_gear:red_snake"),
				Item.of("dungeons_gear:hunting_bow"),
				Item.of("dungeons_gear:hunters_promise"),
				Item.of("dungeons_gear:masters_bow"),
				Item.of("dungeons_gear:ancient_bow"),
				Item.of("dungeons_gear:shortbow"),
				Item.of("dungeons_gear:mechanical_shortbow"),
				Item.of("dungeons_gear:purple_storm"),
				Item.of("dungeons_gear:love_spell_bow"),
				Item.of("dungeons_gear:trickbow"),
				Item.of("dungeons_gear:the_green_menace"),
				Item.of("dungeons_gear:the_pink_scoundrel"),
				Item.of("dungeons_gear:sugar_rush"),
				Item.of("dungeons_gear:snow_bow"),
				Item.of("dungeons_gear:winters_touch"),
				Item.of("dungeons_gear:wind_bow"),
				Item.of("dungeons_gear:burst_gale_bow"),
				Item.of("dungeons_gear:echo_of_the_valley"),
				Item.of("dungeons_gear:rapid_crossbow"),
				Item.of("dungeons_gear:butterfly_crossbow"),
				Item.of("dungeons_gear:auto_crossbow"),
				Item.of("dungeons_gear:azure_seeker"),
				Item.of("dungeons_gear:the_slicer"),
				Item.of("dungeons_gear:heavy_crossbow"),
				Item.of("dungeons_gear:doom_crossbow"),
				Item.of("dungeons_gear:slayer_crossbow"),
				Item.of("dungeons_gear:soul_crossbow"),
				Item.of("dungeons_gear:feral_soul_crossbow"),
				Item.of("dungeons_gear:voidcaller"),
				Item.of("dungeons_gear:scatter_crossbow"),
				Item.of("dungeons_gear:harp_crossbow"),
				Item.of("dungeons_gear:lightning_harp_crossbow"),
				Item.of("dungeons_gear:exploding_crossbow"),
				Item.of("dungeons_gear:firebolt_thrower"),
				Item.of("dungeons_gear:imploding_crossbow"),
				Item.of("dungeons_gear:burst_crossbow"),
				Item.of("dungeons_gear:corrupted_crossbow"),
				Item.of("dungeons_gear:soul_hunter_crossbow"),
				Item.of("dungeons_gear:dual_crossbow"),
				Item.of("dungeons_gear:baby_crossbow"),
				Item.of("dungeons_gear:harpoon_crossbow"),
				Item.of("dungeons_gear:nautical_crossbow"),
			]);
		    pool.addLore(Text.lightPurple("EPIC LOOT").bold());
        })		
		
		// 1-2 Rare Armor, 15% Chance
		.pool((pool) => {
            pool.randomChance(0.15)
            pool.addWeightedLoot([1, 2], [
				Item.of("dungeons_gear:hunters_helmet"),
				Item.of("dungeons_gear:hunters_chestplate"),
				Item.of("dungeons_gear:hunters_leggings"),
				Item.of("dungeons_gear:hunters_boots"),
				Item.of("dungeons_gear:archers_helmet"),
				Item.of("dungeons_gear:archers_chestplate"),
				Item.of("dungeons_gear:archers_leggings"),
				Item.of("dungeons_gear:archers_boots"),
				Item.of("dungeons_gear:battle_robes_helmet"),
				Item.of("dungeons_gear:battle_robes_chestplate"),
				Item.of("dungeons_gear:battle_robes_leggings"),
				Item.of("dungeons_gear:battle_robes_boots"),
				Item.of("dungeons_gear:champions_helmet"),
				Item.of("dungeons_gear:champions_chestplate"),
				Item.of("dungeons_gear:champions_leggings"),
				Item.of("dungeons_gear:champions_boots"),
				Item.of("dungeons_gear:heros_helmet"),
				Item.of("dungeons_gear:heros_chestplate"),
				Item.of("dungeons_gear:heros_leggings"),
				Item.of("dungeons_gear:heros_boots"),
				Item.of("dungeons_gear:dark_helmet"),
				Item.of("dungeons_gear:dark_chestplate"),
				Item.of("dungeons_gear:dark_leggings"),
				Item.of("dungeons_gear:dark_boots"),
				Item.of("dungeons_gear:titans_shroud_helmet"),
				Item.of("dungeons_gear:titans_shroud_chestplate"),
				Item.of("dungeons_gear:titans_shroud_leggings"),
				Item.of("dungeons_gear:titans_shroud_boots"),
				Item.of("dungeons_gear:evocation_robes_helmet"),
				Item.of("dungeons_gear:evocation_robes_chestplate"),
				Item.of("dungeons_gear:evocation_robes_leggings"),
				Item.of("dungeons_gear:evocation_robes_boots"),
				Item.of("dungeons_gear:ember_robes_helmet"),
				Item.of("dungeons_gear:ember_robes_chestplate"),
				Item.of("dungeons_gear:ember_robes_leggings"),
				Item.of("dungeons_gear:ember_robes_boots"),
				Item.of("dungeons_gear:grim_helmet"),
				Item.of("dungeons_gear:grim_chestplate"),
				Item.of("dungeons_gear:grim_leggings"),
				Item.of("dungeons_gear:grim_boots"),
				Item.of("dungeons_gear:wither_helmet"),
				Item.of("dungeons_gear:wither_chestplate"),
				Item.of("dungeons_gear:wither_leggings"),
				Item.of("dungeons_gear:wither_boots"),
				Item.of("dungeons_gear:guards_helmet"),
				Item.of("dungeons_gear:guards_chestplate"),
				Item.of("dungeons_gear:guards_leggings"),
				Item.of("dungeons_gear:guards_boots"),
				Item.of("dungeons_gear:curious_helmet"),
				Item.of("dungeons_gear:curious_chestplate"),
				Item.of("dungeons_gear:curious_leggings"),
				Item.of("dungeons_gear:curious_boots"),
				Item.of("dungeons_gear:mercenary_helmet"),
				Item.of("dungeons_gear:mercenary_chestplate"),
				Item.of("dungeons_gear:mercenary_leggings"),
				Item.of("dungeons_gear:mercenary_boots"),
				Item.of("dungeons_gear:renegade_helmet"),
				Item.of("dungeons_gear:renegade_chestplate"),
				Item.of("dungeons_gear:renegade_leggings"),
				Item.of("dungeons_gear:renegade_boots"),
				Item.of("dungeons_gear:hungry_horror_helmet"),
				Item.of("dungeons_gear:hungry_horror_chestplate"),
				Item.of("dungeons_gear:hungry_horror_leggings"),
				Item.of("dungeons_gear:hungry_horror_boots"),
				Item.of("dungeons_gear:ocelot_helmet"),
				Item.of("dungeons_gear:ocelot_chestplate"),
				Item.of("dungeons_gear:ocelot_leggings"),
				Item.of("dungeons_gear:ocelot_boots"),
				Item.of("dungeons_gear:shadow_walker_helmet"),
				Item.of("dungeons_gear:shadow_walker_chestplate"),
				Item.of("dungeons_gear:shadow_walker_leggings"),
				Item.of("dungeons_gear:shadow_walker_boots"),
				Item.of("dungeons_gear:phantom_helmet"),
				Item.of("dungeons_gear:phantom_chestplate"),
				Item.of("dungeons_gear:phantom_leggings"),
				Item.of("dungeons_gear:phantom_boots"),
				Item.of("dungeons_gear:frost_bite_helmet"),
				Item.of("dungeons_gear:frost_bite_chestplate"),
				Item.of("dungeons_gear:frost_bite_leggings"),
				Item.of("dungeons_gear:frost_bite_boots"),
				Item.of("dungeons_gear:plate_helmet"),
				Item.of("dungeons_gear:plate_chestplate"),
				Item.of("dungeons_gear:plate_leggings"),
				Item.of("dungeons_gear:plate_boots"),
				Item.of("dungeons_gear:full_metal_helmet"),
				Item.of("dungeons_gear:full_metal_chestplate"),
				Item.of("dungeons_gear:full_metal_leggings"),
				Item.of("dungeons_gear:full_metal_boots"),
				Item.of("dungeons_gear:reinforced_mail_helmet"),
				Item.of("dungeons_gear:reinforced_mail_chestplate"),
				Item.of("dungeons_gear:reinforced_mail_leggings"),
				Item.of("dungeons_gear:reinforced_mail_boots"),
				Item.of("dungeons_gear:stalwart_helmet"),
				Item.of("dungeons_gear:stalwart_chestplate"),
				Item.of("dungeons_gear:stalwart_leggings"),
				Item.of("dungeons_gear:stalwart_boots"),
				Item.of("dungeons_gear:scale_mail_helmet"),
				Item.of("dungeons_gear:scale_mail_chestplate"),
				Item.of("dungeons_gear:scale_mail_leggings"),
				Item.of("dungeons_gear:scale_mail_boots"),
				Item.of("dungeons_gear:highland_helmet"),
				Item.of("dungeons_gear:highland_chestplate"),
				Item.of("dungeons_gear:highland_leggings"),
				Item.of("dungeons_gear:highland_boots"),
				Item.of("dungeons_gear:snow_helmet"),
				Item.of("dungeons_gear:snow_chestplate"),
				Item.of("dungeons_gear:snow_leggings"),
				Item.of("dungeons_gear:snow_boots"),
				Item.of("dungeons_gear:frost_helmet"),
				Item.of("dungeons_gear:frost_chestplate"),
				Item.of("dungeons_gear:frost_leggings"),
				Item.of("dungeons_gear:frost_boots"),
				Item.of("dungeons_gear:soul_helmet"),
				Item.of("dungeons_gear:soul_chestplate"),
				Item.of("dungeons_gear:soul_leggings"),
				Item.of("dungeons_gear:soul_boots"),
				Item.of("dungeons_gear:souldancer_helmet"),
				Item.of("dungeons_gear:souldancer_chestplate"),
				Item.of("dungeons_gear:souldancer_leggings"),
				Item.of("dungeons_gear:souldancer_boots"),
				Item.of("dungeons_gear:spelunker_helmet"),
				Item.of("dungeons_gear:spelunker_chestplate"),
				Item.of("dungeons_gear:spelunker_leggings"),
				Item.of("dungeons_gear:spelunker_boots"),
				Item.of("dungeons_gear:cave_crawler_helmet"),
				Item.of("dungeons_gear:cave_crawler_chestplate"),
				Item.of("dungeons_gear:cave_crawler_leggings"),
				Item.of("dungeons_gear:cave_crawler_boots"),
				Item.of("dungeons_gear:thief_helmet"),
				Item.of("dungeons_gear:thief_chestplate"),
				Item.of("dungeons_gear:thief_leggings"),
				Item.of("dungeons_gear:thief_boots"),
				Item.of("dungeons_gear:spider_helmet"),
				Item.of("dungeons_gear:spider_chestplate"),
				Item.of("dungeons_gear:spider_leggings"),
				Item.of("dungeons_gear:spider_boots"),
				Item.of("dungeons_gear:wolf_helmet"),
				Item.of("dungeons_gear:wolf_chestplate"),
				Item.of("dungeons_gear:wolf_leggings"),
				Item.of("dungeons_gear:wolf_boots"),
				Item.of("dungeons_gear:fox_helmet"),
				Item.of("dungeons_gear:fox_chestplate"),
				Item.of("dungeons_gear:fox_leggings"),
				Item.of("dungeons_gear:fox_boots"),
				Item.of("dungeons_gear:arctic_fox_helmet"),
				Item.of("dungeons_gear:arctic_fox_chestplate"),
				Item.of("dungeons_gear:arctic_fox_leggings"),
				Item.of("dungeons_gear:arctic_fox_boots"),
				Item.of("dungeons_gear:climbing_helmet"),
				Item.of("dungeons_gear:climbing_chestplate"),
				Item.of("dungeons_gear:climbing_leggings"),
				Item.of("dungeons_gear:climbing_boots"),
				Item.of("dungeons_gear:rugged_climbing_helmet"),
				Item.of("dungeons_gear:rugged_climbing_chestplate"),
				Item.of("dungeons_gear:rugged_climbing_leggings"),
				Item.of("dungeons_gear:rugged_climbing_boots"),
				Item.of("dungeons_gear:goat_helmet"),
				Item.of("dungeons_gear:goat_chestplate"),
				Item.of("dungeons_gear:goat_leggings"),
				Item.of("dungeons_gear:goat_boots"),
				Item.of("dungeons_gear:emerald_helmet"),
				Item.of("dungeons_gear:emerald_chestplate"),
				Item.of("dungeons_gear:emerald_leggings"),
				Item.of("dungeons_gear:emerald_boots"),
				Item.of("dungeons_gear:gilded_glory_helmet"),
				Item.of("dungeons_gear:gilded_glory_chestplate"),
				Item.of("dungeons_gear:gilded_glory_leggings"),
				Item.of("dungeons_gear:gilded_glory_boots"),
				Item.of("dungeons_gear:opulent_helmet"),
				Item.of("dungeons_gear:opulent_chestplate"),
				Item.of("dungeons_gear:opulent_leggings"),
				Item.of("dungeons_gear:opulent_boots"),
				Item.of("dungeons_gear:beenest_helmet"),
				Item.of("dungeons_gear:beenest_chestplate"),
				Item.of("dungeons_gear:beenest_leggings"),
				Item.of("dungeons_gear:beenest_boots"),
				Item.of("dungeons_gear:beehive_helmet"),
				Item.of("dungeons_gear:beehive_chestplate"),
				Item.of("dungeons_gear:beehive_leggings"),
				Item.of("dungeons_gear:beehive_boots"),
			]);
		    pool.addLore(Text.lightPurple("EPIC LOOT").bold());
        })	
		
		// 1 Rare Artifact, 10% Chance
		.pool((pool) => {
            pool.randomChance(0.10)
            pool.addWeightedLoot([1, 1], [
				Item.of("dungeons_gear:boots_of_swiftness"),
				Item.of("dungeons_gear:death_cap_mushroom"),
				Item.of("dungeons_gear:golem_kit"),
				Item.of("dungeons_gear:tasty_bone"),
				Item.of("dungeons_gear:wonderful_wheat"),
				Item.of("dungeons_gear:gong_of_weakening"),
				Item.of("dungeons_gear:lightning_rod"),
				Item.of("dungeons_gear:iron_hide_amulet"),
				Item.of("dungeons_gear:love_medallion"),
				Item.of("dungeons_gear:ghost_cloak"),
				Item.of("dungeons_gear:harvester"),
				Item.of("dungeons_gear:shock_powder"),
				Item.of("dungeons_gear:corrupted_seeds"),
				Item.of("dungeons_gear:ice_wand"),
				Item.of("dungeons_gear:wind_horn"),
				Item.of("dungeons_gear:soul_healer"),
				Item.of("dungeons_gear:light_feather"),
				Item.of("dungeons_gear:flaming_quiver"),
				Item.of("dungeons_gear:torment_quiver"),
				Item.of("dungeons_gear:totem_of_regeneration"),
				Item.of("dungeons_gear:totem_of_shielding"),
				Item.of("dungeons_gear:totem_of_soul_protection"),
				Item.of("dungeons_gear:corrupted_beacon"),
				Item.of("dungeons_gear:buzzy_nest"),
				Item.of("dungeons_gear:enchanted_grass"),
				Item.of("dungeons_gear:corrupted_pumpkin"),
				Item.of("dungeons_gear:thundering_quiver"),
				Item.of("dungeons_gear:harpoon_quiver"),
				Item.of("dungeons_gear:stachel_of_elixirs"),
				Item.of("dungeons_gear:satchel_of_snacks"),
				Item.of("dungeons_gear:satchel_of_elements"),
				Item.of("dungeons_gear:powershaker"),
				Item.of("dungeons_gear:updraft_tome"),
				Item.of("dungeons_gear:eye_of_the_guardian"),
				Item.of("dungeons_gear:fireworks_display"),
				Item.of("dungeons_gear:soul_lantern"),
			]);
		    pool.addLore(Text.lightPurple("EPIC LOOT").bold());
        })		
		
		// --- POTIONS ---
		// Blindness Potion - 3.5% chance
		// /give @a minecraft:splash_potion{CustomPotionEffects:[{Id:15,Amplifier:1,Duration:600}]}
		.pool((pool) => {
            pool.randomChance(0.07)
			pool.addLoot("minecraft:splash_potion")
			pool.addNBT({CustomPotionEffects:[{Id:15,Amplifier:1,Duration:900}]})
			pool.setName(Text.blue("Splash Potion of Blindness"))
			pool.addLore(Text.gray("Try not to hit yourself..."))
		    pool.addLore(Text.blue("RARE LOOT").bold());
        })
		// Freezing Potion - 3.5% chance
		// /give @a minecraft:splash_potion{CustomPotionEffects:[{Id:0,Amplifier:1,Duration:600}]}
		.pool((pool) => {
            pool.randomChance(0.035)
			pool.addLoot(Item.of('minecraft:splash_potion', '{Potion:"rottencreatures:strong_freeze"}'))
			//pool.addNBT({CustomPotionEffects:[{Id:0,Amplifier:2,Duration:450}]})
			//pool.setName(Text.blue("Splash Potion of Freezing"))
			pool.addLore(Text.gray("Absolute chills"))
		    pool.addLore(Text.blue("RARE LOOT").bold());
        })
		
		// Cyclic Apples - 10% chance
		.pool((pool) => {
            pool.randomChance(0.1)
            pool.addWeightedLoot([1, 2], [
				Item.of("cyclic:apple_chorus").withChance(5),
				Item.of("cyclic:apple_bone").withChance(5),
				Item.of("cyclic:apple_prismarine").withChance(5),
				Item.of("cyclic:apple_lapis").withChance(5),
				Item.of("cyclic:apple_iron").withChance(2),
				Item.of("cyclic:apple_diamond").withChance(1),
				Item.of("cyclic:apple_emerald").withChance(5),
			]);
			pool.limitCount([1, 2], [3, 4]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })
		
		// Rare Loot - 32% chance
        .pool((pool) => {
            pool.randomChance(0.32)
			pool.triggerExplosion(1, false, false)
            pool.addWeightedLoot([2, 4], [
				Item.of("artifacts:umbrella").withChance(5),
				Item.of("artifacts:plastic_drinking_hat").withChance(5),
				Item.of("artifacts:snorkel").withChance(5),
				Item.of("artifacts:night_vision_goggles").withChance(5),
				Item.of("artifacts:superstitious_hat").withChance(5),
				Item.of("artifacts:helium_flamingo").withChance(5),
				Item.of("artifacts:steadfast_spikes").withChance(5),
				Item.of("artifacts:lucky_scarf").withChance(5),
				Item.of("artifacts:cloud_in_a_bottle").withChance(5),
				Item.of("artifacts:obsidian_skull").withChance(5),
				Item.of("minecraft:iron_ingot", 32).withChance(8),
				Item.of("minecraft:diamond", 12).withChance(5),
				Item.of("artifacts:panic_necklace").withChance(5),
				Item.of("artifacts:shock_pendant").withChance(5),
				Item.of("artifacts:flame_pendant").withChance(5),
				Item.of("artifacts:thorn_pendant").withChance(5),
				Item.of("artifacts:feral_claws").withChance(5),
				Item.of("artifacts:scarf_of_invisibility").withChance(5),
				Item.of("artifacts:aqua_dashers").withChance(5),
				Item.of("cyclic:charm_world").withChance(5),
				Item.of("cyclic:chorus_flight").withChance(5),
				Item.of("cyclic:chorus_spectral").withChance(5),
				Item.of("minecraft:trident").withChance(5),
				Item.of("cyclic:heart").withChance(5),
				Item.of("cyclic:charm_wing").withChance(5),
				Item.of("cyclic:quiver_damage").withChance(5),
				Item.of("dungeons_gear:frost_scythe").withChance(5),
				Item.of("artifacts:bunny_hoppers").withChance(5),
				Item.of("artifacts:running_shoes").withChance(5),
				Item.of("artifacts:steadfast_spikes").withChance(5),
				Item.of("artifacts:flippers").withChance(5),
				Item.of("twilightforest:lifedrain_scepter").withChance(4),
				Item.of("twilightforest:zombie_scepter").withChance(3),
				Item.of("supplementaries:slingshot").withChance(5),
				Item.of("supplementaries:bubble_blower").withChance(2),
				Item.of("alexsmobs:rocky_chestplate").withChance(2),
				Item.of("twilightforest:fiery_helmet").withChance(2),
				Item.of("twilightforest:fiery_chestplate").withChance(2),
				Item.of("twilightforest:fiery_leggings").withChance(2),
				Item.of("twilightforest:fiery_boots").withChance(2),
				Item.of("twilightforest:steeleaf_sword").withChance(3),
				Item.of("twilightforest:naga_leggings").withChance(4),
				Item.of("twilightforest:naga_chestplate").withChance(4),
				Item.of("alexsmobs:shield_of_the_deep").withChance(5),
				Item.of("alexsmobs:squid_grapple").withChance(5),
				Item.of("alexsmobs:centipede_leggings").withChance(5),
				Item.of("alexsmobs:crocodile_chestplate").withChance(5),
				Item.of("alexsmobs:blood_sprayer").withChance(3),
			]);
		    pool.addLore(Text.blue("RARE LOOT").bold());
        })
		
		// Bulk Rare Loot, 15% chance
		.pool((pool) => {
            pool.randomChance(0.15)
            pool.addWeightedLoot([1, 2], [
				Item.of("supplementaries:bomb").withChance(5),
				Item.of("twilightforest:ice_bomb").withChance(5),
				Item.of('supplementaries:bamboo_spikes_tipped', '{Damage:0,Potion:"rottencreatures:freeze"}').withChance(4),
				Item.of("minecraft:ender_pearl").withChance(3),
			]);
			pool.limitCount([3, 4], [6, 7]);
		    pool.addLore(Text.blue("RARE LOOT").bold());
        })
		
		// Legendary Loot, 7.5% chance
        .pool((pool) => {
            pool.randomChance(0.075)
			pool.triggerLightningStrike(false)
            pool.addWeightedLoot([1, 2], [
				Item.of("artifacts:crystal_heart").withChance(50),
				Item.of("artifacts:power_glove").withChance(50),
				Item.of("artifacts:fire_gauntlet").withChance(50),
				Item.of("artifacts:eternal_steak").withChance(50),
				Item.of("artifacts:cross_necklace").withChance(50),
				Item.of("byg:ametrine_helmet").withChance(50),
				Item.of("byg:ametrine_chestplate").withChance(50),
				Item.of("byg:ametrine_leggings").withChance(50),
				Item.of("byg:ametrine_boots").withChance(50),
				Item.of("minecraft:enchanted_golden_apple").withChance(50),
				Item.of("minecraft:totem_of_undying").withChance(50),
				Item.of("twilightforest:giant_sword").withChance(30),
				Item.of("twilightforest:giant_pickaxe").withChance(20),
				Item.of("twilightforest:glass_sword").withChance(3),
			]);
			pool.enchantWithLevels([15, 30])
		    pool.addLore(Text.gold("LEGENDARY LOOT").bold().underlined());
        })
		
		// Elytra, 1.69% chance
        .pool((pool) => {
            pool.randomChance(0.0169)
			pool.triggerLightningStrike(false)
            pool.addLoot("minecraft:elytra")
			pool.addLoot("minecraft:firework_rocket")
			.modifyLoot(Item.of("minecraft:firework_rocket"), (itemStack) => {
				itemStack.setCount(45);
				return itemStack;
			});
			
		    pool.addLore(Text.gold("LEGENDARY LOOT").bold().underlined());
        })
		
		// Potato Cannon, 3% chance
        .pool((pool) => {
            pool.randomChance(0.03)
			pool.triggerLightningStrike(false)
            pool.addLoot("minecraft:potato")
			pool.addLoot("create:potato_cannon")
			.modifyLoot(Item.of("minecraft:potato"), (itemStack) => {
				itemStack.setCount(32);
				return itemStack;
			});
			
		    pool.addLore(Text.gold("LEGENDARY LOOT").bold().underlined());
        })
		
	// --- GLOBAL LOOT ---
	// Low level random enchants 50% of the time
	event.addLootTableModifier(/(.*:chests?\/.*)|(structory:.+)/)
		.randomChance(0.8)
		.functions(!ItemFilter.ENCHANTED, (a) => {
			a.enchantWithLevels([1, 6]);
		});
	
	// 70% chance for degraded durability
	event.addLootTableModifier(/(.*:chests?\/.*)|(structory:.+)/)
		.randomChance(0.7)
		.functions(ItemFilter.DAMAGEABLE, (a) => {
			a.damage([0.1, 0.45]);
		});
		
});