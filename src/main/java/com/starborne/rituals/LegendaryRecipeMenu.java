
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LegendaryRecipeMenu implements Listener {

    private static final int MENU_SIZE = 54;
    private static final String MENU_TITLE =
            ChatColor.DARK_PURPLE + "Legendary Recipes";

    private final Starborne plugin;
    private final LegendaryRecipeManager recipeManager;

    public LegendaryRecipeMenu(
            Starborne plugin,
            LegendaryRecipeManager recipeManager
    ) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(
                null,
                MENU_SIZE,
                MENU_TITLE
        );

        ItemStack filler = createItem(
                Material.PURPLE_STAINED_GLASS_PANE,
                ChatColor.DARK_PURPLE + " "
        );

        for (int slot = 0; slot < MENU_SIZE; slot++) {
            inventory.setItem(slot, filler);
        }

        if (!recipeManager.hasRecipes()) {
            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,
                            ChatColor.RED + "No Legendary Recipes Yet",
                            ChatColor.GRAY
                                    + "Legendary recipes will appear here",
                            ChatColor.GRAY
                                    + "once they have been registered."
                    )
            );
        } else {
            int slot = 10;

            for (LegendaryRecipe recipe : recipeManager.getRecipes()) {
                if (slot >= 44) {
                    break;
                }

                if (slot % 9 == 8) {
                    slot += 2;
                }

                ItemStack icon = createItem(
                        recipe.getIcon(),
                        ChatColor.LIGHT_PURPLE
                                + recipe.getDisplayName(),
                        ChatColor.GRAY + "Click to view ingredients",
                        ChatColor.DARK_PURPLE
                                + "Legendary Recipe"
                );

                ItemMeta meta = icon.getItemMeta();

                if (meta != null) {
                    meta.setCustomModelData(0);
                    meta.getPersistentDataContainer().set(
                            new org.bukkit.NamespacedKey(
                                    plugin,
                                    "legendary_recipe_id"
                            ),
                            org.bukkit.persistence.PersistentDataType.STRING,
                            recipe.getId()
                    );
                    icon.setItemMeta(meta);
                }

                inventory.setItem(slot, icon);
                slot++;
            }
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!MENU_TITLE.equals(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        org.bukkit.NamespacedKey recipeKey =
                new org.bukkit.NamespacedKey(
                        plugin,
                        "legendary_recipe_id"
                );

        String recipeId = clicked.getItemMeta()
                .getPersistentDataContainer()
                .get(
                        recipeKey,
                        org.bukkit.persistence.PersistentDataType.STRING
                );

        if (recipeId == null) {
            return;
        }

        LegendaryRecipe recipe = recipeManager.getRecipe(recipeId);

        if (recipe == null) {
            player.sendMessage(
                    ChatColor.RED + "That recipe is no longer available."
            );
            return;
        }

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "Selected Legendary: "
                        + recipe.getDisplayName()
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "The ingredient details screen is coming next."
        );
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

            List<String> loreLines = new ArrayList<>();
            for (String line : lore) {
                loreLines.add(line);
            }

            meta.setLore(loreLines);
            item.setItemMeta(meta);
        }

        return item;
    }
}
