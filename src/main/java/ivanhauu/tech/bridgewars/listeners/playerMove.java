package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class playerMove implements Listener {

    private final BridgeWars plugin;

    public playerMove(BridgeWars plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String eventWorldName = player.getWorld().getName();

        if (eventWorldName.startsWith("battle_4v4_") || eventWorldName.startsWith("battle_2v2_")) {
            boolean isBattleStarting = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".isBattleStarting");
            if (isBattleStarting) {
                player.sendMessage("A partida está iniciando, você não pode se mover!");
                event.setCancelled(true);
            }
        }
    }
}
