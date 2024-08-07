package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.PlayerWinner;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerDeath implements Listener {

    private final PlayerWinner playerWinner;
    private final BridgeWars plugin;

    private String sectionsBaseFolder;
    private String subFolder2v2;
    private String subFolder4v4;
    private String battleWorldName2v2;
    private String battleWorldName4v4;

    public PlayerDeath(BridgeWars plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.battleWorldName2v2 = plugin.getBattleWorldName2v2();
        this.battleWorldName4v4 = plugin.getBattleWorldName4v4();
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player playerKilled = event.getPlayer();
        World eventWorld = playerKilled.getWorld();

        //Aqui está a geração das pontes para a partida 2v2 e parte da lógica de morte
        if (eventWorld.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2) || eventWorld.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {

            ItemStack[] itensFromPlayer = playerKilled.getInventory().getContents();
            event.setCancelled(true);
            for (ItemStack item : itensFromPlayer) {
                if (item != null) {
                    playerKilled.getWorld().dropItem(playerKilled.getLocation(), item);
                }
            }
            playerKilled.sendTitle("§4Você morreu!","§4Assista a partida, ou saia com /spawn");
            playerKilled.setGameMode(GameMode.SPECTATOR);

            String deathMessage = "§cO jogador §6" + playerKilled.getName() + "§c foi de arrasta!";

            for (Player p : eventWorld.getPlayers()) {
                p.sendMessage(deathMessage);
            }

                //Para ambas as partidas quando sobrar 1 player no mundo, ele ganhará --> SEPARAR VITÓRIA 2v2 DE 4V4!
            if (event.getEntity().getKiller() != null || playerKilled.getLastDamageCause() != null) {
                playerWinner.playerWinner(eventWorld, false);
            }

        }

    }

}
