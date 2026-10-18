
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LegendaryRitualConfirmationMenu implements Listener {

    private static final String TITLE =
            ChatColor.DARK_PURPLE + "Confirm Legendary Ritual";

    private final Starborne plugin;
    private final LegendaryRecipeManager recipeManager;
    private final LegendaryIngredientChecker ingredientChecker;
    private final LegendaryIngredientConsumer ingredientConsumer;

    private final NamespacedKey recipeIdKey;
    private final NamespacedKey actionKey;

    private final Map<UUID, Location> selectedTables = new HashMap<>();

    public LegendaryRitualConfirmationMenu(
            Starborne plugin,
            LegendaryRecipeManager recipeManager
    ) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
        this.ingredientChecker = new LegendaryIngredientChecker();
        this.ingredientConsumer = new LegendaryIngredientConsumer();

        this.recipeIdKey = new NamespacedKey(
                plugin,
                "legendary_recipe_id"
        );

        this.actionKey = new NamespacedKey(
                plugin,
                "legendary_confirmation_action"
        );
    }

    public void open(
            Player player,
            LegendaryRecipe recipe,
            Location tableLocation
    ) {
        if (tableLocation == null || tableLocation.getWorld() == null) {
            player.sendMessage(
                    ChatColor.RED + "Invalid crafting table location."
            );
            return;
        }

        selectedTables.put(
                player.getUniqueId(),
                tableLocation.clone()
        );

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
                createRecipeItem(recipe, hasIngredients)
        );

        inventory.setItem(
                11,
                createActionItem(
                        hasIngredients
                                ? Material.LIME_CONCRETE
                                : Material.RED_CONCRETE,
                        hasIngredients
                                ? ChatColor.GREEN + "Confirm Ritual"
                                : ChatColor.RED + "Missing Materials",
                        "confirm",
                        hasIngredients
                                ? ChatColor.GRAY
                                    + "Consume materials and begin the ritual."
                                : ChatColor.GRAY
                                    + "Gather the required materials first."
                )
        );

        inventory.setItem(
                15,
                createActionItem(
                        Material.RED_CONCRETE,
                        ChatColor.RED + "Cancel",
                        "cancel",
                        ChatColor.GRAY + "Close this confirmation screen."
                )
        );

        player.openInventory(inventory);
    }

    private ItemStack createRecipeItem(
            LegendaryRecipe recipe,
            boolean hasIngredients
    ) {
        ItemStack item = createItem(
                recipe.getIcon(),
                ChatColor.LIGHT_PURPLE + recipe.getDisplayName(),
                ChatColor.GRAY + "Review this Legendary ritual.",
                ChatColor.GRAY + "Required materials: "
                        + recipe.getIngredients().size()
                        + " ingredient types",
                hasIngredients
                        ? ChatColor.GREEN + "You have all materials."
                        : ChatColor.RED + "You are missing materials."
        );

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    recipeIdKey,
                    PersistentDataType.STRING,
                    recipe.getId()
            );

            item.setItemMeta(meta);
        }

        return item;
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
            selectedTables.remove(player.getUniqueId());
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

        Location tableLocation = selectedTables.get(
                player.getUniqueId()
        );

        if (tableLocation == null || tableLocation.getWorld() == null) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "Please reopen the menu at a Starborne Crafting Table."
            );
            return;
        }

        if (!plugin.getCraftingTableManager()
                .isRegisteredTable(tableLocation)) {
            selectedTables.remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "That Starborne Crafting Table is no longer registered."
            );
            return;
        }

        Block tableBlock = tableLocation.getBlock();

        if (tableBlock.getType() != Material.CRAFTING_TABLE) {
            selectedTables.remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED + "The crafting table no longer exists."
            );
            return;
        }

        if (!plugin.getLegendaryRitualManager().areRitualsEnabled()) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "Legendary crafting or rituals are currently disabled."
            );
            return;
        }

        if (plugin.getLegendaryRitualManager()
                .isRitualActive(tableLocation)) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "A ritual is already active at this table."
            );
            return;
        }

        if (!ingredientChecker.hasIngredients(player, recipe)) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "You no longer have all required materials."
            );
            return;
        }

        // Start first. Do not consume materials if startup fails.
        boolean started = plugin.getLegendaryRitualManager().startRitual(
                tableLocation,
                player.getUniqueId(),
                recipe.getId()
        );

        if (!started) {
            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "The ritual could not start. Your materials were not consumed."
            );
            return;
        }

        // Recheck and consume after successful startup.
        // If inventory contents changed unexpectedly, cancel the ritual.
        if (!ingredientChecker.hasIngredients(player, recipe)) {
            plugin.getLegendaryRitualManager().getRitualAt(tableLocation);
            var ritual = plugin.getLegendaryRitualManager()
                    .getRitualAt(tableLocation);

            if (ritual != null
                    && ritual.getCreator().equals(player.getUniqueId())) {
                plugin.getLegendaryRitualManager().removeRitual(
                        ritual.getId()
                );
            }

            player.closeInventory();
            player.sendMessage(
                    ChatColor.RED
                            + "Your materials changed. The ritual was cancelled."
            );
            return;
        }

        ingredientConsumer.consumeIngredients(player, recipe);

        selectedTables.remove(player.getUniqueId());
        player.closeInventory();

        player.sendMessage(
                ChatColor.LIGHT_PURPLE + "✦ Legendary ritual started!"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "The ritual is taking place at your selected table."
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
