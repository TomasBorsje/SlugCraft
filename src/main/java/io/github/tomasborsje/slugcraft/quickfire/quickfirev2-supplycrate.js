// priority: 0

console.info('Loading Quickfire v2 Loot Tables!')

LootJS.modifiers((event) => {
    event
        //.addLootTableModifier(/(.*:chests?\/.*)|(structory:.+)/)
        .addLootTableModifier("minecraft:chests/woodland_mansion")

        // Remove obsidian from the pool
        .removeLoot(Ingredient.all)

		// Random food, 30% chance
		.pool((pool) => {
            pool.randomChance(1.0);
            pool.addWeightedLoot([15], [
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
			pool.limitCount([5], [10]);
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

        // 25% chance for 1-2 random pieces of either iron, chainmail, or diamond armour
        .pool((pool) => {
            pool.randomChance(0.25);
            pool.addWeightedLoot([1, 2], [
                Item.of("minecraft:iron_helmet").withChance(1),
                Item.of("minecraft:iron_chestplate").withChance(1),
                Item.of("minecraft:iron_leggings").withChance(1),
                Item.of("minecraft:iron_boots").withChance(1),
                Item.of("minecraft:chainmail_helmet").withChance(1),
                Item.of("minecraft:chainmail_chestplate").withChance(1),
                Item.of("minecraft:chainmail_leggings").withChance(1),
                Item.of("minecraft:chainmail_boots").withChance(1),
                Item.of("minecraft:diamond_helmet").withChance(1),
                Item.of("minecraft:diamond_chestplate").withChance(1),
                Item.of("minecraft:diamond_leggings").withChance(1),
                Item.of("minecraft:diamond_boots").withChance(1)
            ]);
            pool.limitCount([1], [1]);
            pool.addLore(Text.green("UNCOMMON LOOT").bold());
        })

        // 25% chance for random sword, axe or pickaxe of either iron or diamond
        .pool((pool) => {
            pool.randomChance(0.25);
            pool.addWeightedLoot([1], [
                Item.of("minecraft:iron_sword").withChance(1),
                Item.of("minecraft:iron_axe").withChance(1),
                Item.of("minecraft:iron_pickaxe").withChance(1),
                Item.of("minecraft:diamond_sword").withChance(1),
                Item.of("minecraft:diamond_axe").withChance(1),
                Item.of("minecraft:diamond_pickaxe").withChance(1)
            ]);
            pool.limitCount([1]);
            pool.addLore(Text.green("UNCOMMON LOOT").bold());
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

        // Legendary loot, 5% chance
        .pool((pool) => {
            pool.randomChance(0.05);
            // Strike lightning on the chest
            pool.triggerLightningStrike(false);
            pool.addWeightedLoot([1], [
                Item.of("minecraft:elytra").withChance(1),
                Item.of("minecraft:totem_of_undying").withChance(1),
                Item.of("minecraft:trident").withChance(1),
                Item.of("minecraft:crossbow").withChance(1),
            ]);
            pool.limitCount([1]);
            pool.addLore(Text.gold("LEGENDARY LOOT").bold().underlined());
        })
		
});