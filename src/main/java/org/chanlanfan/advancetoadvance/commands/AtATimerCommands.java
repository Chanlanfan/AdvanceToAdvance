package org.chanlanfan.advancetoadvance.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;
import org.jetbrains.annotations.NotNull;

import javax.naming.Name;

public class AtATimerCommands implements CommandExecutor {

    public static Boolean isPaused = true;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("settime")) {
            Player player = (Player) sender;


            if (args.length == 0) {
                player.sendMessage("Please enter a time!");
            } else if (args.length == 1) {
                int value = Integer.parseInt(args[0]);

                PersistentDataContainer timePDC = player.getPersistentDataContainer();
                timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
                net.kyori.adventure.text.TextComponent platerSettime = net.kyori.adventure.text.Component.text(player.getName()).color(NamedTextColor.BLUE).append(Component.text(" has updated their time to ").color(NamedTextColor.DARK_RED).append(Component.text(value).color(NamedTextColor.GOLD)).append(Component.text(" seconds!").color(NamedTextColor.DARK_RED)));

                player.sendMessage(platerSettime);
            } else if (args.length == 2) {
                int value = Integer.parseInt(args[0]);
                Player target = Bukkit.getPlayerExact(args[1]);
                PersistentDataContainer timePDC = target.getPersistentDataContainer();
                timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
                net.kyori.adventure.text.TextComponent platerSettime = net.kyori.adventure.text.Component.text(player.getName())
                        .color(NamedTextColor.BLUE)
                        .append(Component.text(" has updated the time of ")
                                .color(NamedTextColor.DARK_RED))
                        .append(Component.text(target.getName())
                                .color(NamedTextColor.GREEN))
                        .append(Component.text(" to ")
                                .color(NamedTextColor.DARK_RED))
                        .append(Component.text(value)
                                .color(NamedTextColor.GREEN))
                        .append(Component.text(" seconds!")
                                .color(NamedTextColor.DARK_RED))
                        ;

                player.sendMessage(platerSettime);
            } else {
                player.sendMessage("Failed to execute /settime command");
            }


        }
        if (cmd.getName().equalsIgnoreCase("pause")) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (!isPaused) {
                    isPaused = true;
                    for (Player onPlayer : Bukkit.getOnlinePlayers()) {
                        net.kyori.adventure.text.TextComponent gamePaused = net.kyori.adventure.text.Component.text("The timer has ").color(NamedTextColor.YELLOW).append(Component.text("PAUSED").color(NamedTextColor.DARK_RED));
                        onPlayer.sendMessage(gamePaused);
                    }
                    Component header = Component.text("The timer is currently")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.text(" OFF")
                                    .color(NamedTextColor.DARK_RED));
                    Component footer = Component.text("Advance to Advance plugin by Chanlanfan (Check out my GitHub)", NamedTextColor.BLUE, TextDecoration.BOLD);
                    player.sendPlayerListHeaderAndFooter(header, footer);
                } else {
                    isPaused = false;
                    for (Player onPlayer : Bukkit.getOnlinePlayers()) {
                        net.kyori.adventure.text.TextComponent gameUnPaused = net.kyori.adventure.text.Component.text("The timer has ").color(NamedTextColor.YELLOW).append(Component.text("RESUMED").color(NamedTextColor.GREEN));
                        onPlayer.sendMessage(gameUnPaused);

                    }
                    Component header = Component.text("The timer is currently")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.text(" ON")
                                    .color(NamedTextColor.GREEN));
                    Component footer = Component.text("Advance to Advance plugin by Chanlanfan (Check out my GitHub)", NamedTextColor.BLUE, TextDecoration.BOLD);
                    player.sendPlayerListHeaderAndFooter(header, footer);
                }
            }
            if (args.length == 1) {
                isPaused = Boolean.parseBoolean(args[0]);
            }
        }

        return true;
    }


}