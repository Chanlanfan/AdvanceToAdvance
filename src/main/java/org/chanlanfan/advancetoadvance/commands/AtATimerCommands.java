package org.chanlanfan.advancetoadvance.commands;


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

public class AtATimerCommands implements CommandExecutor {

public static Boolean isPaused = true ;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("settime")) {
            Player player = (Player) sender;
            PersistentDataContainer timePDC = player.getPersistentDataContainer();
            int value = Integer.parseInt(args[0]);


            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);


        }
        if (cmd.getName().equalsIgnoreCase("pause")){
            if (args.length == 0) {
                if (isPaused) {
                    isPaused = false;
                }
                if (!isPaused) {
                    isPaused = true;
                }
            }
            if (args.length == 1){
                isPaused = Boolean.parseBoolean(args[0]);
            }
        }

        return true;
    }


}