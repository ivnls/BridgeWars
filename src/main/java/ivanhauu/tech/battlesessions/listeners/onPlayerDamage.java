package ivanhauu.tech.battlesessions.listeners;

import ivanhauu.tech.battlesessions.BattleSessions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class onPlayerDamage implements Listener {

    private final BattleSessions plugin;

    public onPlayerDamage(BattleSessions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        String eventWorldName = event.getEntity().getWorld().getName();
        Player player = (Player) event.getEntity();
        if (event.getEntity() instanceof Player) {
            if (eventWorldName.startsWith("battle_4v4_") || eventWorldName.startsWith("battle_8v8_")) {
                Location iSpawn = new Location(player.getWorld(), -32, 50, 32);
                boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".is4v4BattleStarted");

                if (!is4v4BattleStarted) {
                    event.setCancelled(true);
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        for (Player p : player.getWorld().getPlayers()) {
                            p.sendMessage("O player " + player.getName() + " foi de arrasta!");
                        }
                    }
                    player.teleport(iSpawn);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendTitle("§4Você morreu!","§4Assista a partida, ou saia com /spawn");
                }

            }
        }
    }
}
