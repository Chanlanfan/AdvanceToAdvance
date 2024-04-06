package org.chanlanfan.advancetoadvance;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.chanlanfan.advancetoadvance.commands.AtAMainCommands;
import org.chanlanfan.advancetoadvance.commands.AtATimerCommands;
import org.chanlanfan.advancetoadvance.events.AtAEvents;

import java.io.FileNotFoundException;

public final class AdvanceToAdvance extends JavaPlugin implements Listener {


    public int n;
    public Integer timeInSec;
    public Player playerCur;


    @Override
    public void onEnable() {
        // LOAD CONFIG
        saveResource("config.yml", /* replace */ false);
        // LOAD EVENTS
        try {
            Bukkit.getPluginManager().registerEvents(new AtAEvents(), this);
            Bukkit.getPluginManager().registerEvents(this, this);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

//        LOAD COMMANDS
        AtAMainCommands mainCommands = new AtAMainCommands();
        AtATimerCommands timerCommands = new AtATimerCommands();

        getCommand("info").setExecutor(mainCommands);
        getCommand("reloadata").setExecutor(mainCommands);
        getCommand("test").setExecutor(mainCommands);
        getCommand("settime").setExecutor(timerCommands);
        getCommand("pause").setExecutor(timerCommands);


        Bukkit.broadcastMessage("AtA LOADED SUCCESSFULLY");


        timer();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage("(AtA has shutdown successfully)");

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer timePDC = player.getPersistentDataContainer();
        Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);


        if (!player.hasPlayedBefore()) {
            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, 116);
        } else {

            player.sendMessage(String.valueOf(value));
        }

    }

    private void timer() {


        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), new Runnable() {

            public void run() {
                if (!AtATimerCommands.isPaused){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PersistentDataContainer timePDC = player.getPersistentDataContainer();
                    Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);

                    if (value > 0) {
                        value = value - 1;
                        timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
//                    player.sendMessage(String.valueOf(value));
                        displayTimer(value, player);
                        if (player.getGameMode() == GameMode.SPECTATOR){
                            player.setGameMode(GameMode.SURVIVAL);
                        }
                    }
                    if (value == 0){
                        player.sendMessage("URRR OUT PAL!!");
                        value = -1;
                    }
                    if (value <= 0){

                        player.setGameMode(GameMode.SPECTATOR);
                    }


                }

                }
if (AtATimerCommands.isPaused) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        net.kyori.adventure.text.TextComponent playerPaused = net.kyori.adventure.text.Component.text("PAUSED").color(NamedTextColor.DARK_RED);
                        player.sendActionBar(playerPaused);

                    }

                }
//

            }

        }, 20L, 20);




    }

    public void displayTimer(Integer time, Player playerCur) {


        int minutes = (int) Math.floor(time / 60);
        int seconds = time % 60;


        net.kyori.adventure.text.TextComponent playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.BLUE);


        playerCur.sendActionBar((ComponentLike) playerTime);

    }


}










