package org.chanlanfan.advancetoadvance;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.chanlanfan.advancetoadvance.commands.AtAMainCommands;
import org.chanlanfan.advancetoadvance.commands.AtAPairCommands;
import org.chanlanfan.advancetoadvance.commands.AtATimerCommands;
import org.chanlanfan.advancetoadvance.commands.AtAUnpairCommands;
import org.chanlanfan.advancetoadvance.events.AtAEvents;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


import static org.chanlanfan.advancetoadvance.commands.AtATimerCommands.isPaused;

public final class AdvanceToAdvance extends JavaPlugin implements Listener {


//  FETCHING CONFIG

    Integer startTime = getConfig().getInt("start_time");

    Boolean achievement_bonuses_enabled = getConfig().getBoolean("achievement_bonuses_enabled");

    Integer taskTime = getConfig().getInt("task_time");
    Integer goalTime = getConfig().getInt("goal_time");
    Integer challenegeTime = getConfig().getInt("challenge_time");

    Integer deathTime = getConfig().getInt("death_time");

    Integer greenTime = getConfig().getInt("green_time");
    Integer yellowTime = getConfig().getInt("yellow_time");
    Integer goldTime = getConfig().getInt("gold_time");
    Integer redTime = getConfig().getInt("red_time");

    private HashMap<UUID, UUID> pairedPlayers = new HashMap<>();

    public HashMap<UUID, UUID> getPairedPlayers() {
        return pairedPlayers;
    }


    @Override
    public void onEnable() {
        saveDefaultConfig();
        // LOAD CONFIG
        saveResource("config.yml", /* replace */ false);
        // LOAD EVENTS
        getServer().getPluginManager().registerEvents(new AtAEvents(this), this);

        Bukkit.getPluginManager().registerEvents(this, this);

        // ****************** //
        // **** Expiring **** //
        // ****************** //

        LocalDate expirationDate = LocalDate.of(2025, 1, 30);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Check if the plugin has expired
        if (currentDate.isAfter(expirationDate)) {
            Bukkit.getLogger().severe("This plugin has expired and is no longer usable.");
            Bukkit.broadcastMessage("AtA membership has expired, please contact an admin. If you are an admin, please contact Chanlanfan");
            // Disable the plugin
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // ******************* //
        // ** Expiring Over ** //
        // ******************* //


//        LOAD COMMANDS
        AtAMainCommands mainCommands = new AtAMainCommands();
        AtATimerCommands timerCommands = new AtATimerCommands();



        this.getCommand("unpairall").setExecutor(new AtAUnpairCommands(this));
        this.getCommand("unpair").setExecutor(new AtAUnpairCommands(this));

        Bukkit.broadcastMessage("AtA LOADED SUCCESSFULLY");
        timer();

        if (getCommand("info") != null) {
            getCommand("info").setExecutor(mainCommands);
        } else {
            getLogger().severe("Command 'info' is not defined in plugin.yml!");
        }

        if (getCommand("settime") != null) {
            getCommand("settime").setExecutor(timerCommands);
        } else {
            getLogger().severe("Command 'settime' is not defined in plugin.yml!");
        }

        if (getCommand("pause") != null) {
            getCommand("pause").setExecutor(timerCommands);
        } else {
            getLogger().severe("Command 'pause' is not defined in plugin.yml!");
        }


        //Shared Inv.

        AtAPairCommands pairCommands = new AtAPairCommands(this);

        getCommand("pair").setExecutor(pairCommands);

        pairedPlayers = new HashMap<>(); // Initialize the map


        Bukkit.getScheduler().runTaskTimer(this, () ->
                        Bukkit.broadcastMessage("This is a test version of Chanlanfan's AdvanceToAdvancePaired Plugin :)"),
                0L, 600L // 600 ticks = 30 seconds
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage("(AtA has shutdown successfully)");
        pairedPlayers.clear();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer timePDC = player.getPersistentDataContainer();
        Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);
if (value == null){
    value = -1;
}

        if (!player.hasPlayedBefore()) {
            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, startTime);
        } else {
            net.kyori.adventure.text.TextComponent joinMessage = net.kyori.adventure.text.Component.text("Welcome back ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(player.getName())
                            .color(NamedTextColor.BLUE))
                    .append(Component.text("!\n" +
                            "You have ")
                            .color(NamedTextColor.GREEN))
                    .append(Component.text(value)
                            .color(NamedTextColor.GOLD))
                    .append(Component.text(" seconds remaining!\n" +
                                    "Hold ")
                            .color(NamedTextColor.GREEN))
                    .append(Component.keybind("key.playerlist")
                            .color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" to view timer status")
                            .color(NamedTextColor.GREEN));

            player.sendMessage(joinMessage);




            if (isPaused){
                Component header = Component.text("The timer is currently")
                        .color(NamedTextColor.YELLOW)
                        .append(Component.text(" OFF")
                                .color(NamedTextColor.DARK_RED));
                Component footer = Component.text("Advance to Advance plugin by Chanlanfan (Check out my GitHub)", NamedTextColor.BLUE, TextDecoration.BOLD)
                        ;
                player.sendPlayerListHeaderAndFooter(header, footer);
            }
            else {
                Component header = Component.text("The timer is currently")
                        .color(NamedTextColor.YELLOW)
                        .append(Component.text(" ON")
                                .color(NamedTextColor.GREEN));
                Component footer = Component.text("Advance to Advance plugin by Chanlanfan (Check out my GitHub)", NamedTextColor.BLUE, TextDecoration.BOLD);
                player.sendPlayerListHeaderAndFooter(header, footer);
            }



        }

    }

    private void timer() {


        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), new Runnable() {

            public void run() {
                if (!isPaused) {
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        PersistentDataContainer timePDC = player.getPersistentDataContainer();
                        Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);

                        if (value >= 0) {
                            value = value - 1;
                            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);


                            if (player.getGameMode() == GameMode.SPECTATOR) {
                                player.setGameMode(GameMode.SURVIVAL);
                            }
                        }
                        if (value == 0) {
                            for (Player onPlayer : Bukkit.getOnlinePlayers()) {
                                net.kyori.adventure.text.TextComponent playerOut = net.kyori.adventure.text.Component.text(player.getName()).color(NamedTextColor.DARK_RED).append(Component.text(" is out!").color(NamedTextColor.GOLD));
                                onPlayer.sendMessage(playerOut);
                            }

                        }
                        if (value <= 0 && player.getGameMode() == GameMode.SURVIVAL) {

                            player.setGameMode(GameMode.SPECTATOR);
                        }
                        displayTimer(value, player);

                    }

                }
                if (isPaused) {
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
        net.kyori.adventure.text.TextComponent playerTime;

        int hours = (int) Math.floor(time / 3600);
        int minutes = (int) Math.floor((time / 60)- (hours*60));
        int seconds = time % 60;


        if (time >= greenTime){

           if (time >= 3600){
               playerTime = net.kyori.adventure.text.Component.text(hours + ":" + minutes + ":" + seconds).color(NamedTextColor.GREEN);
           }
           else {
               playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.GREEN);
           }
        } else if (time < greenTime && time >= yellowTime) {
         playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.YELLOW);
        } else if (time < yellowTime && time >= goldTime) {
       playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.GOLD);
        }
        else if (time < goldTime && time >= redTime) {
            playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.RED);
        }
        else if (time < redTime && time >= 1) {
            playerTime = net.kyori.adventure.text.Component.text(minutes + ":" + seconds).color(NamedTextColor.DARK_RED);
        }
        else if (time <= 0){
            playerTime = net.kyori.adventure.text.Component.text("You are out!").color(NamedTextColor.DARK_RED);
        }
        else {
            playerTime = net.kyori.adventure.text.Component.text("Issue! Contact admin!").color(NamedTextColor.DARK_RED);
        }


        playerCur.sendActionBar((ComponentLike) playerTime);

    }

    @EventHandler
    public void onAchievement(PlayerAdvancementDoneEvent event) {


        Player player = event.getPlayer();
        PersistentDataContainer timePDC = player.getPersistentDataContainer();
        Advancement advancement = event.getAdvancement();
        AdvancementDisplay.Frame frameTypeFrame = Objects.requireNonNull(advancement.getDisplay()).frame();
        String frameType = String.valueOf(frameTypeFrame);

if (achievement_bonuses_enabled == true) {
    Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);
    if (frameType == "TASK") {
        value = value + taskTime;
        timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
        player.sendMessage("You completed a task! [+5min]");
    }
    if (frameType == "GOAL") {
        value = value + goalTime;
        timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
        player.sendMessage("You completed a goal! [+10min]");
    }
    if (frameType == "CHALLENGE") {
        value = value + challenegeTime;
        timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
        player.sendMessage("You completed a challenge! [+15min]");
    }
}

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer timePDC = player.getPersistentDataContainer();
        Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);
        value = value + deathTime;
        timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
    }

    public void pairPlayers(Player player1, Player player2) {
        pairedPlayers.put(player1.getUniqueId(), player2.getUniqueId());
        pairedPlayers.put(player2.getUniqueId(), player1.getUniqueId());

        player1.sendMessage(ChatColor.GREEN + "You are now paired with " + player2.getName() + "!");
        player2.sendMessage(ChatColor.GREEN + "You are now paired with " + player1.getName() + "!");
    }

    public void unpairPlayer(Player player) {
        UUID partnerId = pairedPlayers.remove(player.getUniqueId());
        if (partnerId != null) {
            pairedPlayers.remove(partnerId);
            Player partner = getServer().getPlayer(partnerId);
            if (partner != null) {
                partner.sendMessage(ChatColor.RED + "You have been unpaired from " + player.getName() + ".");
            }
        }
    }


}










