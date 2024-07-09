package ivanhauu.tech.battlesessions.listeners;

import ivanhauu.tech.battlesessions.BattleSessions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class denyAllowBuild implements Listener {

    private final BattleSessions plugin;

    public denyAllowBuild(BattleSessions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player p = event.getPlayer();
        String world_name = p.getWorld().getName();

        if (world_name.startsWith("battle_8v8_") || world_name.startsWith("battle_4v4_")) {
            event.setCancelled(true);
            p.sendMessage("Construção não está habilitada!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        String world_name = p.getWorld().getName();

        if (world_name.startsWith("battle_8v8_") || world_name.startsWith("battle_4v4_")) {
            event.setCancelled(true);
            p.sendMessage("Quebra de Blocos não está habilitada!");
        }
    }
}
