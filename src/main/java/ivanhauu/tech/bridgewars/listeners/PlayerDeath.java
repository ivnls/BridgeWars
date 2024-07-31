package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.PlayerWinner;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class PlayerDeath implements Listener {

    private final PlayerWinner playerWinner;
    private final JavaPlugin plugin;

    public PlayerDeath(JavaPlugin plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player playerKilled = event.getPlayer();
        World eventWorld = playerKilled.getWorld();

        //Aqui está a geração das pontes para a partida 2v2 e parte da lógica de morte
        if (eventWorld.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_") || eventWorld.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {

            ItemStack[] itensFromPlayer = playerKilled.getInventory().getContents();
            event.setCancelled(true);
            for (ItemStack item : itensFromPlayer) {
                if (item != null) {
                    playerKilled.getWorld().dropItem(playerKilled.getLocation(), item);
                }
            }
            playerKilled.sendTitle("§4Você morreu!","§4Assista a partida, ou saia com /spawn");
            playerKilled.setGameMode(GameMode.SPECTATOR);

            for (Player p : eventWorld.getPlayers()) { p.sendMessage(Color.RED + "O player " + playerKilled.getName() + " foi de arrasta!"); }

                //Para ambas as partidas quando sobrar 1 player no mundo, ele ganhará --> SEPARAR VITÓRIA 2v2 DE 4V4!
            if (event.getEntity().getKiller() != null || playerKilled.getLastDamageCause() != null) {
                playerWinner.playerWinner(eventWorld, false);
            }

        }

    }

}
