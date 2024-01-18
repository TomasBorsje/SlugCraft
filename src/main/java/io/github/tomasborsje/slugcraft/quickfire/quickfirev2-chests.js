// priority: 0

console.info('Loading Quickfire v2 Loot Tables!')

LootJS.modifiers((event) => {
    event
        //.addLootTableModifier(/(.*:chests?\/.*)|(structory:.+)/)
        .addLootTableModifier("minecraft:chests/village/village_weaponsmith")

        // Remove obsidian from the pool
        .removeLoot("minecraft:obsidian")
        // Remove oak saplings
        .removeLoot("minecraft:oak_sapling")

		// Random food, 30% chance
		.pool((pool) => {
            pool.randomChance(0.30);
            pool.addWeightedLoot([1], [
				Item.of("alexsmobs:cooked_catfish").withChance(5),
				Item.of("alexsmobs:boiled_emu_egg").withChance(5),
				Item.of("create:sweet_roll").withChance(5),
				Item.of("twilightforest:cooked_meef").withChance(5),
			]);
			pool.limitCount([3], [6]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
		})

		// Random arrows, 25% chance
        .pool((pool) => {
            pool.randomChance(0.25);
            pool.addWeightedLoot([1], [
				Item.of("archers_paradox:quartz_arrow").withChance(5),
				Item.of("archers_paradox:explosive_arrow").withChance(5),
				Item.of("archers_paradox:lightning_arrow").withChance(5),
				Item.of("archers_paradox:ender_arrow").withChance(5),
				Item.of("archers_paradox:phantasmal_arrow").withChance(5),
				Item.of("archers_paradox:blaze_arrow").withChance(5),
			]);
			pool.limitCount([5], [10]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })

		// Bow, 15% chance
		.pool((pool) => {
            pool.randomChance(0.15);
            pool.addWeightedLoot([1], [
				Item.of("minecraft:bow").withChance(5),
			]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })

		// Rain world items, 25% chance
		.pool((pool) => {
            pool.randomChance(0.25);
            pool.addWeightedLoot([1, 2], [
				Item.of("slugcraft:spore_puff").withChance(30),
				Item.of("slugcraft:spear").withChance(20),
				Item.of("slugcraft:explosive_spear").withChance(4),
			]);
		    pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })

		// Singularity grenade, 0.5% chance
		.pool((pool) => {
            pool.randomChance(0.005);
            pool.addLoot("slugcraft:singularity_grenade")
			pool.limitCount([1], [1]);
		    pool.addLore(Text.gold("LEGENDARY LOOT").bold().underlined());
        })

        // 35% chance for 1-2 random pieces of armor
        .pool((pool) => {
            pool.randomChance(0.35); // 35% chance
            pool.addWeightedLoot([1, 2], [ // 1-2 items
                Item.of("magistuarmory:rustedchainmail_helmet").withWeight(5),
                Item.of("magistuarmory:rustedchainmail_chestplate").withWeight(5),
                Item.of("magistuarmory:rustedchainmail_leggings").withWeight(5),
                Item.of("magistuarmory:rustedchainmail_boots").withWeight(5),
                Item.of("immersive_armors:slime_helmet").withWeight(5),
                Item.of("immersive_armors:slime_chestplate").withWeight(5),
                Item.of("immersive_armors:slime_slime_leggings").withWeight(5),
                Item.of("immersive_armors:slime_boots").withWeight(5),
                Item.of("minecraft:iron_helmet").withWeight(4),
                Item.of("minecraft:iron_chestplate").withWeight(4),
                Item.of("minecraft:iron_leggings").withWeight(4),
                Item.of("minecraft:iron_boots").withWeight(4),
                Item.of("immersive_armors:heavy_helmet").withWeight(4),
                Item.of("immersive_armors:heavy_chestplate").withWeight(4),
                Item.of("immersive_armors:heavy_leggings").withWeight(4),
                Item.of("immersive_armors:heavy_boots").withWeight(4),
                Item.of("immersive_armors:divine_helmet").withWeight(3),
                Item.of("immersive_armors:divine_chestplate").withWeight(3),
                Item.of("immersive_armors:divine_leggings").withWeight(3),
                Item.of("immersive_armors:divine_boots").withWeight(3),
                Item.of("immersive_armors:prismarine_helmet").withWeight(3),
                Item.of("immersive_armors:prismarine_chestplate").withWeight(3),
                Item.of("immersive_armors:prismarine_leggings").withWeight(3),
                Item.of("immersive_armors:prismarine_boots").withWeight(3),
                Item.of("botania:terrasteel_helmet").withWeight(2),
                Item.of("botania:terrasteel_chestplate").withWeight(2),
                Item.of("botania:terrasteel_leggings").withWeight(2),
                Item.of("botania:terrasteel_boots").withWeight(2),
                Item.of("mekanismtools:osmium_helmet").withWeight(2),
                Item.of("mekanismtools:osmium_chestplate").withWeight(2),
                Item.of("mekanismtools:osmium_leggings").withWeight(2),
                Item.of("mekanismtools:osmium_boots").withWeight(2),
                Item.of("cyclic:crystal_chestplate").withWeight(1),
            ]);
            pool.limitCount([1], [1]);
            pool.addLore(Text.blue("RARE LOOT").bold());
        })


        // 30% chance for 1-2 random weapons or tools
        .pool((pool) => {
            pool.randomChance(0.30); // 30% chance
            pool.addWeightedLoot([1, 2], [ // 1-2 items
                Item.of("mekanismtools:lapis_lazuli_sword").withWeight(5),
                Item.of("cyclic:netherbrick_sword").withWeight(5),
                Item.of("botania:manasteel_sword").withWeight(5),
                Item.of("botania:elementium_sword").withWeight(5),
                Item.of("mekanismtools:steel_sword").withWeight(4),
                Item.of("twilightforest:knightmetal_sword").withWeight(4),
                Item.of("cyclic:boomerang_damage").withWeight(4),
                Item.of("twilightforest:ice_sword").withWeight(3),
                Item.of("twilightforest:fiery_sword").withWeight(3),
                Item.of("magistuarmory:diamond_bastardsword").withWeight(3),
                Item.of("cyclic:emerald_sword").withWeight(3),
                Item.of("magistuarmory:iron_flamebladedsword").withWeight(2),
                Item.of("magistuarmory:bronze_flamebladedsword").withWeight(2),
            ]);
            pool.limitCount([1], [1]);
            pool.addLore(Text.blue("RARE LOOT").bold());
        })


        // 10% chance for 1-3 random grenades from Thermal
        .pool((pool) => {
            pool.randomChance(0.1);
            pool.addWeightedLoot([1, 3], [
                Item.of("thermal:earth_grenade").withChance(100),
                Item.of("thermal:ice_grenade").withChance(100),
                Item.of("thermal:lightning_grenade").withChance(100),
                Item.of("thermal:nuke_grenade").withChance(1),
                Item.of("cgm:stun_grenade").withChance(20),
            ]);
            pool.limitCount([1]);
            pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })

        // Epic Loot - 10%
        .pool((pool) => {
            pool.randomChance(0.10); // 10% chance
            pool.addWeightedLoot([1], [ // 1 item
                Item.of("cyclic:evoker_fang").withWeight(3),
                Item.of("cyclic:lightning_scepter").withWeight(3),
                Item.of("cyclic:fire_scepter").withWeight(2),
                Item.of("twilightforest:zombie_scepter").withWeight(2),
                Item.of("botania:star_sword").withWeight(2),
                Item.of("twilightforest:twilight_scepter").withWeight(1),
                Item.of("supplementaties:bubble_blower").withWeight(1),
            ]);
            pool.limitCount([1], [1]);
            pool.addLore(Text.lightPurple("EPIC LOOT").bold());
        })

        // Legendary Loot - 5%
        .pool((pool) => {
            pool.randomChance(0.05); // 5% chance
            pool.triggerLightningStrike(false);
            pool.addWeightedLoot([1], [ // 1 item
                Item.of("twilightforest:glass_sword").withWeight(2),
                Item.of("cataclysm:the_incinerator").withWeight(2),
                Item.of("cataclysm:tidal_claws").withWeight(2),
                Item.of("cataclysm:wither_assault_shoulder_weapon").withWeight(2),
                Item.of("cataclysm:laser_gatling").withWeight(2),
                Item.of("cataclysm:meat_shredder").withWeight(1),
                Item.of("cataclysm:void_assault_shoulder_weapon").withWeight(1),
                Item.of("cataclysm:gauntlet_of_guard").withWeight(1),
                Item.of("twilightforest:cicada").withWeight(1),
            ]);
            pool.limitCount([1], [1]);
            pool.addLore(Text.gold("LEGENDARY LOOT").bold());
        })


		
	// --- GLOBAL LOOT ---
	// Low level random enchants 50% of the time
	event.addLootTableModifier("minecraft:chests/village/village_weaponsmith")
		.randomChance(0.6)
		.functions(!ItemFilter.ENCHANTED, (a) => {
			a.enchantWithLevels([1, 5]);
		});
	
	// 70% chance for degraded durability
	event.addLootTableModifier("minecraft:chests/village/village_weaponsmith")
		.randomChance(0.7)
		.functions(ItemFilter.DAMAGEABLE, (a) => {
			a.damage([0.1, 0.45]);
		});
		
});