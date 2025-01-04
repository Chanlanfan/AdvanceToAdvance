package org.chanlanfan.advancetoadvance.events;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class AtAEvents implements Listener {


    public static PersistentDataContainer timer;
    private final Set<UUID> processingDamage = new HashSet<>(); // Prevents recursive damage handling


    private AdvanceToAdvance plugin;

    public AtAEvents(AdvanceToAdvance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId());

        if (partnerId != null) {
            Player partner = Bukkit.getPlayer(partnerId);
            if (partner != null) {
                partner.getInventory().setContents(player.getInventory().getContents());
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncInventories(player, partner), 1L);
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        boolean adjusted = adjustEnchantments(item);







    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        Bukkit.broadcastMessage("This is a test version of Chanlanfan's AdvanceToAdvancePaired Plugin :)");

        if (event.getEntity() instanceof Player player) { // 1. Check if the entity that picked up the item is a player
            UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId()); // 2. Get the partner's UUID from the pairedPlayers map based on the player's UUID
            if (partnerId == null) return; // 3. If there is no partner for this player, exit the method

            Player partner = Bukkit.getPlayer(partnerId); // 4. Get the partner player from their UUID
            if (partner == null || !partner.isOnline()) return; // 5. If the partner player is offline or null, exit the method

            // 6. Schedule a task to sync the inventories of the player and their partner after 1 tick
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncInventories(player, partner), 1L);
        }
    }
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer(); // Get the player who dropped the item
        UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId()); // Get the player's partner

        if (partnerId == null) return; // If the player has no partner, exit the method

        Player partner = Bukkit.getPlayer(partnerId); // Get the partner player
        if (partner == null || !partner.isOnline()) return; // If the partner is offline, exit the method

        // Schedule a task to sync inventories after the drop event
        Bukkit.getScheduler().runTaskLater(plugin, () -> syncInventories(player, partner), 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId());
            if (partnerId == null) return;

            Player partner = Bukkit.getPlayer(partnerId);
            if (partner == null || !partner.isOnline()) return;

            syncInventories(player, partner);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId());
        if (partnerId == null) return;

        Player partner = Bukkit.getPlayer(partnerId);
        if (partner == null || !partner.isOnline()) return;

        // Ensure health is synced bidirectionally
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double primaryHealth = player.getHealth();
            double partnerHealth = partner.getHealth();

            // Update partner's health to match primary
            syncHealth(player, partner);

            // Also update primary's health if the secondary is lower
            if (partnerHealth < primaryHealth) {
                syncHealth(partner, player);
            }


        }, 1L);
        player.sendMessage("Test");
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        UUID partnerId = plugin.getPairedPlayers().get(player.getUniqueId());
        if (partnerId == null) return;

        Player partner = Bukkit.getPlayer(partnerId);
        if (partner == null || !partner.isOnline()) return;

        // Sync health after a healing event
        Bukkit.getScheduler().runTaskLater(plugin, () -> syncHealth(player, partner), 1L);
    }


    private void syncInventories(Player player, Player partner) {
        try {
            Inventory playerInventory = player.getInventory();
            Inventory partnerInventory = partner.getInventory();

            for (int i = 0; i < playerInventory.getSize(); i++) {
                partnerInventory.setItem(i, playerInventory.getItem(i));
            }

            partner.updateInventory(); // Force the client to refresh inventory
        } catch (Exception e) {
            e.printStackTrace(); // Log any exception
        }
    }

    private void syncHealth(Player source, Player target) {
        double sourceHealth = source.getHealth();
        double sourceMaxHealth = source.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();

        // Ensure target's max health matches source's max health
        if (target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue() != sourceMaxHealth) {
            target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(sourceMaxHealth);
        }

        // Sync target's health to match the source's health
        target.setHealth(Math.min(sourceHealth, target.getHealthScale())); // Prevent exceeding target's max health
    }


    private void syncAllPairedInventories() {
        plugin.getPairedPlayers().forEach((playerId, partnerId) -> {
            Player player = Bukkit.getPlayer(playerId);
            Player partner = Bukkit.getPlayer(partnerId);

            if (player != null && partner != null && player.isOnline() && partner.isOnline()) {
                syncInventories(player, partner);
            }
        });
    }
    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        Map<Enchantment, Integer> enchantments = event.getEnchantsToAdd();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            boolean adjusted = false;
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if (entry.getValue() > 1) {
                    item.addEnchantment(entry.getKey(), 1); // Set level to 1
                    adjusted = true;
                }
            }
        }, 1L); // Adjust after the enchantment is applied
    }
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack result = event.getResult();

        if (result != null) {
            boolean adjusted = adjustEnchantments(result);
            if (adjusted) {
                event.setResult(result); // Update the result
            }
        }
    }




    private boolean adjustEnchantments(ItemStack item) {
        boolean adjusted = false;

        // Handle normal items
        if (item.getItemMeta() != null && !item.getType().toString().contains("BOOK")) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                if (entry.getValue() > 1) {
                    item.removeEnchantment(entry.getKey());
                    item.addEnchantment(entry.getKey(), 1);
                    adjusted = true;
                }
            }
        }

        // Handle enchanted books
        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            Map<Enchantment, Integer> storedEnchantments = meta.getStoredEnchants();
            for (Map.Entry<Enchantment, Integer> entry : storedEnchantments.entrySet()) {
                if (entry.getValue() > 1) {
                    meta.removeStoredEnchant(entry.getKey());
                    meta.addStoredEnchant(entry.getKey(), 1, true);
                    adjusted = true;
                }
            }
            item.setItemMeta(meta); // Update the item's metadata
        }

        return adjusted;
    }





}
