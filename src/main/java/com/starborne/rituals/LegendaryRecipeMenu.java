
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegendaryRecipeMenu implements Listener {

    private static final int MENU_SIZE = 54;

    private static final String MENU_TITLE =
            ChatColor.DARK_PURPLE + "Legendary Recipes";

    private static final String MATERIALS_TITLE =
            ChatColor.DARK_PURPLE + "Recipe Materials";

    private final Starborne plugin;
    private final LegendaryRecipeManager recipeManager;
    private final LegendaryIngredientChecker ingredientChecker;
    private final LegendaryRitualConfirmationMenu confirmationMenu;

    private final NamespacedKey recipeIdKey;
    private final NamespacedKey actionKey;

    public LegendaryRecipeMenu(
            Starborne plugin,
            LegendaryRecipeManager recipeManager,
            LegendaryRitualConfirmationMenu confirmationMenu
    ) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
        this.ingredientChecker = new LegendaryIngredientChecker();
        this.confirmationMenu = confirmationMenu;

        this.recipeIdKey = new NamespacedKey(
                plugin,
                "legendary_recipe_id"
        );

        this.actionKey = new NamespacedKey(
                plugin,
                "legendary_menu_action"
        );
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(
                null,
                MENU_SIZE,
                MENU_TITLE
        );

        fillBackground(inventory);

        if (!recipeManager.hasRecipes()) {
            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,
                            ChatColor.RED + "No Legendary Recipes Yet",
                            ChatColor.GRAY
                                    + "Recipes will appear here once registered."
                    )
            );
        } else {
            int[] slots = {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34,
                    37, 38, 39, 40, 41, 42, 43
            };

            int index = 0;

            for (LegendaryRecipe recipe : recipeManager.getRecipes()) {
                if (index >= slots.length) {
                    break;
                }

                ItemStack icon = createItem(
                        recipe.getIcon(),
                        ChatColor.LIGHT_PURPLE
                                + recipe.getDisplayName(),
                        ChatColor.GRAY + "Click to view materials",
                        ChatColor.DARK_PURPLE + "Legendary Recipe"
                );

                ItemMeta meta = icon.getItemMeta();

                if (meta != null) {
                    meta.getPersistentDataContainer().set(
                            recipeIdKey,
                            PersistentDataType.STRING,
                            recipe.getId()
                    );

                    icon.setItemMeta(meta);
                }

                inventory.setItem(slots[index], icon);
                index++;
            }
        }

        player.openInventory(inventory);
    }

    private void openMaterials(Player player, LegendaryRecipe recipe) {
        Inventory inventory = Bukkit.createInventory(
                null,
                MENU_SIZE,
                MATERIALS_TITLE
        );

        fillBackground(inventory);

        inventory.setItem(
                4,
                createItem(
                        recipe.getIcon(),
                        ChatColor.LIGHT_PURPLE + recipe.getDisplayName(),
                        ChatColor.GRAY + "Required materials are listed below."
                )
        );

        int[] slots = {
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        int index = 0;

        for (Map.Entry<Material, Integer> entry
                : recipe.getIngredients().entrySet()) {

            if (index >= slots.length) {
                break;
            }

            Material material = entry.getKey();
            int required = entry.getValue();

            int missing = ingredientChecker.getMissingAmount(
                    player,
                    recipe,
                    material
            );

            int available = required - missing;

            ItemStack ingredient = new ItemStack(
                    material,
                    Math.min(required, material.getMaxStackSize())
            );

            ItemMeta meta = ingredient.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(
                        ChatColor.WHITE + formatMaterialName(material)
                );

                List<String> lore = new ArrayList<>();

                lore.add(
                        ChatColor.GRAY + "Required: "
                                + ChatColor.WHITE + required
                );

                lore.add(
                        ChatColor.GRAY + "You have: "
                                + (missing == 0
                                ? ChatColor.GREEN
                                : ChatColor.RED)
                                + available
                );

                lore.add(
                        missing == 0
                                ? ChatColor.GREEN + "Enough materials"
                                : ChatColor.RED + "Missing: " + missing
                );

                meta.setLore(lore);
                ingredient.setItemMeta(meta);
            }

            inventory.setItem(slots[index], ingredient);
            index++;
        }

        boolean hasAll = ingredientChecker.hasIngredients(
                player,
                recipe
        );

        inventory.setItem(
                49,
                createActionItem(
                        Material.ARROW,
                        ChatColor.YELLOW + "Back to Recipes",
                        "back"
                )
        );

        inventory.setItem(
                53,
                createActionItem(
                        hasAll ? Material.LIME_CONCRETE : Material.RED_CONCRETE,
                        hasAll
                                ? ChatColor.GREEN + "Review & Confirm"
                                : ChatColor.RED + "Missing Materials",
                        "confirm:" + recipe.getId(),
                        hasAll
                                ? ChatColor.GRAY
                                    + "Continue to ritual confirmation."
                                : ChatColor.GRAY
                                    + "Gather the missing ingredients first."
                )
        );

        player.openInventory(inventory);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (!MENU_TITLE.equals(title)
                && !MATERIALS_TITLE.equals(title)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || event.getClickedInventory()
                        != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();

        if (MENU_TITLE.equals(title)) {
            String recipeId = meta.getPersistentDataContainer().get(
                    recipeIdKey,
                    PersistentDataType.STRING
            );

            if (recipeId == null) {
                return;
            }

            LegendaryRecipe recipe = recipeManager.getRecipe(recipeId);

            if (recipe != null) {
                openMaterials(player, recipe);
            }

            return;
        }

        String action = meta.getPersistentDataContainer().get(
                actionKey,
                PersistentDataType.STRING
        );

        if ("back".equals(action)) {
            open(player);
            return;
        }

        if (action != null && action.startsWith("confirm:")) {
            String recipeId = action.substring("confirm:".length());

            LegendaryRecipe recipe = recipeManager.getRecipe(recipeId);

            if (recipe == null) {
                player.sendMessage(
                        ChatColor.RED + "That recipe is no longer available."
                );
                open(player);
                return;
            }

            if (!ingredientChecker.hasIngredients(player, recipe)) {
                player.sendMessage(
                        ChatColor.RED
                                + "You no longer have all the required materials."
                );
                openMaterials(player, recipe);
                return;
            }

            confirmationMenu.open(player, recipe);
        }
    }

    private void fillBackground(Inventory inventory) {
        ItemStack filler = createItem(
                Material.PURPLE_STAINED_GLASS_PANE,
                ChatColor.DARK_PURPLE + " "
        );

        for (int slot = 0; slot < MENU_SIZE; slot++) {
            inventory.setItem(slot, filler);
        }
    }

    private ItemStack createActionItem(
            Material material,
            String name,
            String action,
            String... lore
    ) {
        ItemStack item = createItem(material, name, lore);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    actionKey,
                    PersistentDataType.STRING,
                    action
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createItem(
            Material material,
            String name,
            String... lore
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    private String formatMaterialName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) {
                result.append(' ');
            }

            result.append(
                    Character.toUpperCase(word.charAt(0))
            ).append(word.substring(1));
        }

        return result.toString();
    }
}
