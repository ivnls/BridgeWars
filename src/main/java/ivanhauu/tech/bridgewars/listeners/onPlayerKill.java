package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.PlayerWinner;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;


public class onPlayerKill implements Listener {

    private final BridgeWars plugin;
    private final PlayerWinner playerWinner;

    public onPlayerKill(BridgeWars plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player_killed = event.getPlayer();
        World event_world = player_killed.getWorld();

        //Aqui está a geração das pontes para a partida 2v2 e parte da lógica de morte
        if (event_world.getName().startsWith("battle_2v2_") || event_world.getName().startsWith("battle_4v4_")) {

            ItemStack[] itensFromPlayer = player_killed.getInventory().getContents();
            event.setCancelled(true);
            for (ItemStack item : itensFromPlayer) {
                if (item != null) {
                    player_killed.getWorld().dropItem(player_killed.getLocation(), item);
                }
            }
            player_killed.sendTitle("§4Você morreu!","§4Assista a partida, ou saia com /spawn");
            player_killed.setGameMode(GameMode.SPECTATOR);

            for (Player p : event_world.getPlayers()) { p.sendMessage("O player " + player_killed.getName() + " foi de arrasta!"); }

                //Para ambas as partidas quando sobrar 1 player no mundo, ele ganhará --> SEPARAR VITÓRIA 2v2 DE 4V4!
            if (event.getEntity().getKiller() != null || player_killed.getLastDamageCause() != null) {
                playerWinner.playerWinner(event_world, false);
            }

        }

    }

}
