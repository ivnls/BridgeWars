package ivanhauu.tech.battlesessions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class onAdvancement implements Listener {

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("");
        }
    }
}
