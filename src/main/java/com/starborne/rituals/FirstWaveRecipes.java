
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class FirstWaveRecipes {

    private FirstWaveRecipes() {
    }

    public static void registerAll(
            Starborne plugin,
            LegendaryRecipeManager manager
    ) {
        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Starborne plugin cannot be null."
            );
        }

        if (manager == null) {
            throw new IllegalArgumentException(
                    "Recipe manager cannot be null."
            );
        }

        register(manager, "resistance_chestplate",
                "Resistance Chestplate", Material.NETHERITE_CHESTPLATE,
                potion(Material.SPLASH_POTION, 4,
                        "Splash Turtle Master Potion",
                        PotionType.TURTLE_MASTER),
                req(Material.ENCHANTED_GOLDEN_APPLE, 8,
                        "Enchanted Golden Apple"),
                req(Material.TURTLE_HELMET, 8, "Turtle Helmet"),
                req(Material.GOLDEN_APPLE, 128, "Golden Apple"),
                req(Material.NETHERITE_CHESTPLATE, 1,
                        "Netherite Chestplate"),
                req(Material.NETHER_STAR, 1, "Soul Star"));

        registerHealthPiece(manager, "health_chestplate",
                "Health Chestplate", Material.NETHERITE_CHESTPLATE);
        registerHealthPiece(manager, "health_leggings",
                "Health Leggings", Material.NETHERITE_LEGGINGS);
        registerHealthPiece(manager, "health_helmet",
                "Health Helmet", Material.NETHERITE_HELMET);
        registerHealthPiece(manager, "health_boots",
                "Health Boots", Material.NETHERITE_BOOTS);

        register(manager, "axe_of_greed",
                "Axe of Greed", Material.NETHERITE_AXE,
                req(Material.GOLD_BLOCK, 32, "Gold Block"),
                req(Material.NETHERITE_AXE, 1, "Netherite Axe"),
                req(Material.GOLDEN_APPLE, 64, "Golden Apple"),
                req(Material.ENCHANTED_GOLDEN_APPLE, 2,
                        "Enchanted Golden Apple"));

        register(manager, "villager_talisman",
                "Villager Talisman", Material.EMERALD,
                req(Material.EMERALD_BLOCK, 64, "Emerald Block"),
                req(Material.DIAMOND, 8, "Diamond"));

        register(manager, "cactus_leggings",
                "Cactus Leggings", Material.NETHERITE_LEGGINGS,
                req(Material.CACTUS, 192, "Cactus"),
                req(Material.TNT, 16, "TNT"),
                req(Material.NETHERITE_LEGGINGS, 1,
                        "Netherite Leggings"),
                enchantedBook(Material.ENCHANTED_BOOK, 8,
                        "Thorns III Enchanted Book",
                        Enchantment.THORNS, 3),
                req(Material.GREEN_DYE, 64, "Green Dye"));

        register(manager, "breezy_boots",
                "Breezy Boots", Material.NETHERITE_BOOTS,
                req(Material.BREEZE_ROD, 64, "Breeze Rod"),
                req(Material.NETHERITE_BOOTS, 1, "Netherite Boots"),
                req(Material.HEAVY_CORE, 1, "Heavy Core"));

        register(manager, "thousand_arrow_crossbow",
                "Thousand Arrow Crossbow", Material.CROSSBOW,
                matching(Material.FIREWORK_ROCKET, 256,
                        "Flight Duration 3 Rocket",
                        IngredientMatchers.fireworkPower(3)),
                req(Material.CROSSBOW, 16, "Crossbow"),
                req(Material.ARROW, 256, "Arrow"),
                req(Material.NETHERITE_INGOT, 1, "Netherite Ingot"),
                req(Material.DIAMOND_BLOCK, 18, "Diamond Block"));

        register(manager, "warden_helmet",
                "Warden Helmet", Material.NETHERITE_HELMET,
                req(Material.NETHERITE_HELMET, 1, "Netherite Helmet"),
                req(Material.SCULK_CATALYST, 32, "Sculk Catalyst"),
                req(Material.SCULK_SHRIEKER, 16, "Sculk Shrieker"),
                req(Material.SCULK_SENSOR, 32, "Sculk Sensor"),
                req(Material.RECOVERY_COMPASS, 1, "Recovery Compass"),
                req(Material.MUSIC_DISC_5, 1, "Music Disc 5"),
                req(Material.ENCHANTED_GOLDEN_APPLE, 4,
                        "Enchanted Golden Apple"));

        register(manager, "warden_battleaxe",
                "Warden Battleaxe", Material.NETHERITE_AXE,
                req(Material.NETHERITE_AXE, 1, "Netherite Axe"),
                req(Material.SCULK_CATALYST, 32, "Sculk Catalyst"),
                req(Material.SCULK_SHRIEKER, 16, "Sculk Shrieker"),
                req(Material.SCULK_SENSOR, 32, "Sculk Sensor"),
                req(Material.RECOVERY_COMPASS, 1, "Recovery Compass"),
                req(Material.MUSIC_DISC_5, 1, "Music Disc 5"),
                req(Material.ENCHANTED_GOLDEN_APPLE, 4,
                        "Enchanted Golden Apple"));

        register(manager, "torrential_trident",
                "Torrential Trident", Material.TRIDENT,
                req(Material.TRIDENT, 4, "Trident"),
                req(Material.HEART_OF_THE_SEA, 4, "Heart of the Sea"),
                req(Material.NAUTILUS_SHELL, 32, "Nautilus Shell"),
                req(Material.GOLD_BLOCK, 16, "Gold Block"));

        register(manager, "ice_talisman",
                "Ice Talisman", Material.PACKED_ICE,
                req(Material.PACKED_ICE, 256, "Packed Ice"),
                req(Material.BLUE_ICE, 256, "Blue Ice"),
                req(Material.POWDER_SNOW_BUCKET, 8,
                        "Powder Snow Bucket"),
                req(Material.NETHERITE_INGOT, 1, "Netherite Ingot"),
                req(Material.GOLD_BLOCK, 8, "Gold Block"));

        register(manager, "eternal_golden_apple",
                "Eternal Golden Apple", Material.GOLDEN_APPLE,
                req(Material.GOLDEN_APPLE, 128, "Golden Apple"));

        register(manager, "fruitful_spear",
                "Fruitful Spear", Material.TRIDENT,
                potion(Material.POTION, 12,
                        "Regeneration II Potion",
                        PotionType.STRONG_REGENERATION),
                potion(Material.POTION, 12,
                        "Instant Health II Potion",
                        PotionType.STRONG_HEALING),
                req(Material.GOLDEN_APPLE, 192, "Golden Apple"),
                req(Material.ENCHANTED_GOLDEN_APPLE, 8,
                        "Enchanted Golden Apple"));

        register(manager, "gravity_bow",
                "Gravity Bow", Material.BOW,
                matching(Material.BOW, 12, "Power V Flame Bow",
                        IngredientMatchers.enchantedItem(
                                Enchantment.POWER, 5,
                                Enchantment.FLAME, 1)),
                req(Material.ENDER_PEARL, 64, "Ender Pearl"),
                req(Material.TNT, 32, "TNT"),
                req(Material.IRON_BLOCK, 8, "Iron Block"));

        register(manager, "regeneration_bucket",
                "Regeneration Bucket", Material.WATER_BUCKET,
                potion(Material.POTION, 18,
                        "Regeneration II Potion",
                        PotionType.STRONG_REGENERATION),
                req(Material.IRON_BLOCK, 16, "Iron Block"),
                req(Material.WATER_BUCKET, 4, "Water Bucket"),
                req(Material.MILK_BUCKET, 1, "Milk Bucket"));

        register(manager, "poison_sword",
                "Poison Sword", Material.NETHERITE_SWORD,
                potion(Material.SPLASH_POTION, 6,
                        "Poison II Splash Potion",
                        PotionType.STRONG_POISON),
                potion(Material.SPLASH_POTION, 6,
                        "Poison Splash Potion",
                        PotionType.POISON),
                req(Material.NETHERITE_SWORD, 1, "Netherite Sword"),
                potion(Material.POTION, 12,
                        "Instant Damage II Potion",
                        PotionType.STRONG_HARMING));
    }

    private static void registerHealthPiece(
            LegendaryRecipeManager manager,
            String id,
            String name,
            Material armor
    ) {
        register(manager, id, name, armor,
                potion(Material.SPLASH_POTION, 4,
                        "Splash Regeneration II Potion",
                        PotionType.STRONG_REGENERATION),
                potion(Material.SPLASH_POTION, 4,
                        "Splash Instant Health II Potion",
                        PotionType.STRONG_HEALING),
                req(Material.GOLDEN_APPLE, 16, "Golden Apple"),
                req(armor, 1, armor.name()));
    }

    private static IngredientRequirement req(
            Material material,
            int amount,
            String description
    ) {
        return new IngredientRequirement(material, amount, description);
    }

    private static IngredientRequirement potion(
            Material material,
            int amount,
            String description,
            PotionType type
    ) {
        return matching(
                material,
                amount,
                description,
                IngredientMatchers.potion(type)
        );
    }

    private static IngredientRequirement enchantedBook(
            Material material,
            int amount,
            String description,
            Enchantment enchantment,
            int level
    ) {
        return matching(
                material,
                amount,
                description,
                IngredientMatchers.enchantedBook(enchantment, level)
        );
    }

    private static IngredientRequirement matching(
            Material material,
            int amount,
            String description,
            Predicate<ItemStack> matcher
    ) {
        return new IngredientRequirement(
                material, amount, description, matcher
        );
    }

    private static void register(
            LegendaryRecipeManager manager,
            String id,
            String name,
            Material icon,
            IngredientRequirement... ingredients
    ) {
        List<IngredientRequirement> requirements =
                new ArrayList<>(List.of(ingredients));

        LegendaryRecipe recipe = new LegendaryRecipe(
                id, name, icon, requirements
        );

        if (!manager.registerRecipe(recipe)) {
            throw new IllegalStateException(
                    "Could not register recipe: " + id
            );
        }
    }
}
