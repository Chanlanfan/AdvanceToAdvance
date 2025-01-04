package org.chanlanfan.advancetoadvance.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class AtAPairCommands implements CommandExecutor {

    private final AdvanceToAdvance plugin;

    public AtAPairCommands(AdvanceToAdvance plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);




        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pair <player>, <player> (optional)");
            return true;
        }



        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(ChatColor.RED + "You cannot pair with yourself.");
            return true;
        }

        if (plugin.getPairedPlayers().containsKey(player.getUniqueId()) || plugin.getPairedPlayers().containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "One of the players is already paired.");
            return true;
        }
        if (args.length == 2) {
            Player target2 = Bukkit.getPlayer(args[1]);
            plugin.pairPlayers(player, target);
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncInventories(target, target2), 1L);
            return true;
        }

            plugin.pairPlayers(player, target);
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncInventories(player, target), 1L);
            return true;



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
}