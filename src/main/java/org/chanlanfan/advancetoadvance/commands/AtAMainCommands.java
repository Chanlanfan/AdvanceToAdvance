package org.chanlanfan.advancetoadvance.commands;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;


import java.util.Map;

import static org.bukkit.Bukkit.broadcastMessage;
import static org.bukkit.Bukkit.getServer;
import static org.chanlanfan.advancetoadvance.AdvanceToAdvance.*;


public class AtAMainCommands implements CommandExecutor {




    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;





        if (cmd.getName().equalsIgnoreCase("info")) {


            TextComponent infoTC = Component.text("━━━━━━━━━━━━━━━━━━━━")
                    .color(NamedTextColor.BLUE)
                    .append(Component.text("\n"))
                    .append(Component.text("Welcome to")
                            .color(NamedTextColor.GREEN))
                    .append(Component.text("Chanlanfan's")
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("Advance to Advance")
                            .color(NamedTextColor.AQUA))
                    .append(Component.text("plugin")
                            .color(NamedTextColor.GREEN))
                    ;



            player.sendMessage((ComponentLike) infoTC);

        }

        return true;
    }
}
