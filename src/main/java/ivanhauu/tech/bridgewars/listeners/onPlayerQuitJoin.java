package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.WorldManager;
import ivanhauu.tech.bridgewars.utils.GetPlayerRank;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class onPlayerQuitJoin implements Listener {

    private final BridgeWars plugin;
    private final WorldManager worldManager;
    private final GetPlayerRank getPlayerRank;

    public onPlayerQuitJoin(BridgeWars plugin, WorldManager worldManager, GetPlayerRank getPlayerRank) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.getPlayerRank = getPlayerRank;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World mundo = player.getWorld();
        World spawn_world = Bukkit.getWorld("world");

        if (mundo.getName().startsWith("battle_2v2_") || mundo.getName().startsWith("battle_4v4_")) {
            Location spawn = new Location(spawn_world, 8, 0, 8);
            player.teleport(spawn);
        }

        if (player.getWorld().getName().equals("world")) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
        }

        plugin.updatePlayersVisualization(player);

        event.setJoinMessage(null);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (mundo == p.getWorld()) {
                p.sendMessage(ChatColor.GREEN + player.getName() + " entrou no jogo!");
            }
        }

        Location pod1 = new Location(spawn_world, 8.5, 0, -12.5);
        Location pod2 = new Location(spawn_world, 12.5, -1, -12.5);
        Location pod3 = new Location(spawn_world, 4.5, -2, -12.5);

        playerPodium(pod1, pod2, pod3);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World event_world = player.getWorld();

        if (event_world.getName().startsWith("battle_4v4_") || event_world.getName().startsWith("battle_2v2_")) {
            boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + event_world.getName() + ".is4v4BattleStarted");
            plugin.getLogger().info("1 O player ganhou pois só ele está no mundo!");
            if (is4v4BattleStarted) {
                plugin.getLogger().info("2 O player ganhou pois só ele está no mundo!");
                if (event_world.getPlayers().size() <= 2) {
                    for (Player p : event_world.getPlayers()) {
                        if (p != player) {
                            plugin.getPlayerWinner().playerWinner(event_world, true);
                            plugin.getLogger().info("3 O player ganhou pois só ele está no mundo!");
                        }
                    }
                }
            }
        }

        plugin.updatePlayersVisualization(event.getPlayer());
        getServer().getScheduler().runTaskLater(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                worldManager.deleteWorldIfEmpty(world.getName());
            }
        }, 20L);
    }

    public void playerPodium(Location pod1, Location pod2, Location pod3) {

        World world1 = pod1.getWorld();

        // Obter todos os jogadores do arquivo de configuração
        Set<String> players = plugin.getPlayerConfig().getConfigurationSection("players").getKeys(false);

        // Lista para armazenar jogadores e suas vitórias
        List<Map.Entry<String, Integer>> playerWins = new ArrayList<>();

        // Iterar sobre os jogadores e adicionar seus wins à lista
        for (String player : players) {
            int wins = plugin.getPlayerConfig().getInt("players." + player + ".4v4wins");
            playerWins.add(new AbstractMap.SimpleEntry<>(player, wins));
        }

        // Ordenar a lista pelo número de vitórias em ordem decrescente
        playerWins.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Pegar os top 3 jogadores
        List<Map.Entry<String, Integer>> topPlayers = playerWins.subList(0, Math.min(3, playerWins.size()));

        for (Entity e : world1.getEntities()) {
            if (e.getType().equals(EntityType.ARMOR_STAND)) {
                e.remove();
            }
        }

        Location[] podiumLocations = { pod1, pod2, pod3 };

        for (int i = 0;topPlayers.size() > i; i++) {
            String top = topPlayers.get(i).getKey();
            int wins = topPlayers.get(i).getValue();

            ArmorStand armorStand = (ArmorStand) world1.spawnEntity(podiumLocations[i], EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setRotation(0, 180);

            // Definir a cabeça do armor stand para a skin do jogador
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(top);
            skull.setItemMeta(meta);
            armorStand.setHelmet(skull);

            // Definir o nome do jogador e o número de vitórias como a etiqueta do armor stand
            armorStand.setCustomName(top + " - " + wins + " vitórias");
            armorStand.setCustomNameVisible(true);
        }
    }
}
