
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

import java.util.List;

public class LegendaryRitualConfirmationMenu implements Listener {

    private static final String TITLE =
            ChatColor.DARK_PURPLE + "Confirm Legendary Ritual";

    private final Starborne plugin;
    private final LegendaryRecipeManager recipeManager;
    private final LegendaryIngredientChecker ingredientChecker;
    private final NamespacedKey recipeIdKey;
    private final NamespacedKey actionKey;

    public LegendaryRitualConfirmationMenu(
            Starborne plugin,
            LegendaryRecipeManager recipeManager
    ) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
        this.ingredientChecker = new LegendaryIngredientChecker();

        this.recipeIdKey = new NamespacedKey(
                plugin,
                "legendary_recipe_id"
        );

        this.actionKey = new NamespacedKey(
                plugin,
                "legendary_confirmation_action"
        );
    }

    public void open(Player player, LegendaryRecipe recipe) {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        ItemStack filler = createItem(
                Material.PURPLE_STAINED_GLASS_PANE,
                ChatColor.DARK_PURPLE + " "
        );

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }

        boolean hasIngredients = ingredientChecker.hasIngredients(
                player,
                recipe
        );

        inventory.setItem(
                13,
                createItem(
                        recipe.getIcon(),
                        ChatColor.LIGHT_PURPLE + recipe.getDisplayName(),
                        ChatColor.GRAY + "Review this Legendary ritual.",
                        ChatColor.GRAY + "Required materials: "
                                + recipe.getIngredients().size()
                                + " ingredient types",
                        hasIngredients
                                ? ChatColor.GREEN + "You have all materials."
                                : ChatColor.RED + "You are missing materials."
                )
        );

        inventory.setItem(
                11,
                createActionItem(
                        Material.LIME_CONCRETE,
                        ChatColor.GREEN + "Confirm Ritual",
                        "confirm",
                        ChatColor.GRAY + "Attempt to begin this ritual."
                )
        );

        inventory.setItem(
                15,
                createActionItem(
                        Material.RED_CONCRETE,
                        ChatColor.RED + "Cancel",
                        "cancel",
                        ChatColor.GRAY + "Return to the recipe menu."
                )
        );

        ItemMeta recipeMeta = inventory.getItem(13).getItemMeta();

        if (recipeMeta != null) {
            recipeMeta.getPersistentDataContainer().set(
                    recipeIdKey,
                    PersistentDataType.STRING,
                    recipe.getId()
            );

            inventory.getItem(13).setItemMeta(recipeMeta);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onConfirmationClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!TITLE.equals(event.getView().getTitle())) {
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

        String action = clicked.getItemMeta()
                .getPersistentDataContainer()
                .get(actionKey, PersistentDataType.STRING);

        if (action == null) {
            return;
        }

        if ("cancel".equals(action)) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.GRAY + "Legendary ritual cancelled."
            );
            return;
        }

        if (!"confirm".equals(action)) {
            return;
        }

        ItemStack recipeIcon = event.getView()
                .getTopInventory()
                .getItem(13);

        if (recipeIcon == null || !recipeIcon.hasItemMeta()) {
            player.closeInventory();
            return;
        }

        String recipeId = recipeIcon.getItemMeta()
                .getPersistentDataContainer()
                .get(recipeIdKey, PersistentDataType.STRING);

        LegendaryRecipe recipe = recipeManager.getRecipe(recipeId);

        if (recipe == null) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED + "That recipe is no longer available."
            );
            return;
        }

        if (!ingredientChecker.hasIngredients(player, recipe)) {
            player.sendMessage(
                    ChatColor.RED
                            + "You no longer have all required materials."
            );
            player.closeInventory();
            return;
        }

        player.sendMessage(
                ChatColor.YELLOW
                        + "Confirmation received! Ritual startup will be "
                        + "connected in the next step."
        );
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
}
