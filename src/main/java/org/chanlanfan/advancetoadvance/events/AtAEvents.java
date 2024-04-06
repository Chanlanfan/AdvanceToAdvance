package org.chanlanfan.advancetoadvance.events;

import com.google.gson.Gson;
import io.papermc.paper.advancement.AdvancementDisplay;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.chanlanfan.advancetoadvance.AdvanceToAdvance;

import java.io.*;
import java.util.Objects;


public class AtAEvents implements Listener {


    public static PersistentDataContainer timer;
    public AtAEvents() throws FileNotFoundException {
    }



    Gson gson = new Gson();
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws IOException {

    }

    @EventHandler
public void onAchievement(PlayerAdvancementDoneEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer timePDC = player.getPersistentDataContainer();
        Advancement advancement = event.getAdvancement();
    AdvancementDisplay.Frame frameTypeFrame = Objects.requireNonNull(advancement.getDisplay()).frame();
    String frameType = String.valueOf(frameTypeFrame);


        Integer value = timePDC.get(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER);
if (frameType == "TASK"){
    value = value + 300;
    timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
    player.sendMessage("You completed a task! [+5min]");
}
        if (frameType == "GOAL"){
            value = value + 600;
            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
            player.sendMessage("You completed a goal! [+10min]");
        }
        if (frameType == "CHALLENGE"){
            value = value + 900;
            timePDC.set(new NamespacedKey(AdvanceToAdvance.getPlugin(AdvanceToAdvance.class), "timer"), PersistentDataType.INTEGER, value);
            player.sendMessage("You completed a challenge! [+15min]");
        }

    }



}
