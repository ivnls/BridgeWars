package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class denyAllowBuild implements Listener {

    private final BridgeWars plugin;

    public denyAllowBuild(BridgeWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player p = event.getPlayer();
        String world_name = p.getWorld().getName();

        if (world_name.startsWith("battle_2v2_") || world_name.startsWith("battle_4v4_")) {
            event.setCancelled(true);
            p.sendMessage("Construção não está habilitada!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        String world_name = p.getWorld().getName();

        if (world_name.startsWith("battle_2v2_") || world_name.startsWith("battle_4v4_")) {
            event.setCancelled(true);
            p.sendMessage("Quebra de Blocos não está habilitada!");
        }
    }
}
