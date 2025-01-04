package org.chanlanfan.advancetoadvance.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;

import java.util.UUID;

public class AtAUnpairCommands implements CommandExecutor {

    private final AdvanceToAdvance plugin;

    public AtAUnpairCommands(AdvanceToAdvance plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Command to unpair everyone
        if (command.getName().equalsIgnoreCase("unpairall")) {
            if (!sender.hasPermission("ata.unpairall")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            // Unpair all players
            plugin.getPairedPlayers().clear();
            Bukkit.broadcastMessage("All players have been unpaired.");
            return true;
        }

        // Command for players to unpair from their partner
        if (command.getName().equalsIgnoreCase("unpair")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            UUID playerId = player.getUniqueId();
            UUID partnerId = plugin.getPairedPlayers().get(playerId);

            if (partnerId == null) {
                player.sendMessage("You are not paired with anyone.");
                return true;
            }

            // Unpair the player
            plugin.getPairedPlayers().remove(playerId);
            plugin.getPairedPlayers().remove(partnerId);

            player.sendMessage("You have been unpaired.");
            Player partner = Bukkit.getPlayer(partnerId);
            if (partner != null && partner.isOnline()) {
                partner.sendMessage(player.getName() + " has unpaired from you.");
            }
            return true;
        }

        return false;
    }
}
