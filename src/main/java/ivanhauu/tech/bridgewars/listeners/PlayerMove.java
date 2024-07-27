package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    private final BridgeWars plugin;

    public PlayerMove(BridgeWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String eventWorldName = player.getWorld().getName();

        if (eventWorldName.startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_") || eventWorldName.startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
            boolean isBattleStarting = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".isBattleStarting");
            if (isBattleStarting) {
                player.sendMessage("A partida está iniciando, você não pode se mover!");
                event.setCancelled(true);
            }
        }
    }
}
