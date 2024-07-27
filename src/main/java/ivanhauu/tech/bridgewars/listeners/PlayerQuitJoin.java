package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
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

public class PlayerQuitJoin implements Listener {

    private final BridgeWars plugin;

    public PlayerQuitJoin(BridgeWars plugin) {
        this.plugin = plugin;
    }

    String spawnWorld = "world";

    Location pod1_4v4 = new Location(Bukkit.getWorld(spawnWorld), 8.5, 0, -12.5);
    Location pod2_4v4 = new Location(Bukkit.getWorld(spawnWorld), 12.5, -1, -12.5);
    Location pod3_4v4 = new Location(Bukkit.getWorld(spawnWorld), 4.5, -2, -12.5);
    Location pod1_2v2 = new Location(Bukkit.getWorld(spawnWorld), 8.5, 0, 29.5);
    Location pod2_2v2 = new Location(Bukkit.getWorld(spawnWorld), 12.5, -1, 29.5);
    Location pod3_2v2 = new Location(Bukkit.getWorld(spawnWorld), 4.5, -2, 29.5);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World mundo = player.getWorld();
        World spawn_world = Bukkit.getWorld("world");

        if (mundo.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_") || mundo.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
            Location spawn = new Location(spawn_world, 8, 0, 8);
            player.teleport(spawn);
        }

        if (player.getWorld().getName().equals("world")) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.clearActivePotionEffects();
            player.setGameMode(GameMode.SURVIVAL);
            Location spawn = new Location(spawn_world, 8, 0, 8);
            player.teleport(spawn);
        }

        plugin.updatePlayersVisualization(player);

        event.setJoinMessage(null);

        if (spawn_world.getName().equals("world")) {
            for (Entity e : spawn_world.getEntities()) {
                if (e.getType().equals(EntityType.ARMOR_STAND)) {
                    e.remove();
                }
            }
        }

        playerPodium(pod1_4v4, pod2_4v4, pod3_4v4, "4v4");
        playerPodium(pod1_2v2, pod2_2v2, pod3_2v2, "2v2");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World event_world = player.getWorld();

        event.setQuitMessage(null);

        if (event_world.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
            boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + event_world.getName() + ".is4v4BattleStarted");
            if (is4v4BattleStarted) {
                if (event_world.getPlayers().size() <= 2) {
                    for (Player p : event_world.getPlayers()) {
                        if (p != player) {
                            plugin.getPlayerWinner().playerWinner(event_world, true);
                            plugin.getLogger().info("O player " + player.getName() + " ganhou pois só ele está no mundo!");
                        }
                    }
                }
            }
        } else if (event_world.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_")) {
            boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + event_world.getName() + ".is2v2BattleStarted");
            if (is2v2BattleStarted) {
                if (event_world.getPlayers().size() <= 2) {
                    for (Player p : event_world.getPlayers()) {
                        if (p != player) {
                            plugin.getPlayerWinner().playerWinner(event_world, true);
                            plugin.getLogger().info("O player " + player.getName() + " ganhou pois só ele está no mundo!");
                        }
                    }
                }
            }
        }

        plugin.updatePlayersVisualization(event.getPlayer());
    }

    public void playerPodium(Location pod1, Location pod2, Location pod3, String mode) {

        World world1 = pod1.getWorld();

        // Obter todos os jogadores do arquivo de configuração

        Set<String> players = plugin.getPlayerConfig().getConfigurationSection("players").getKeys(false);

        // Lista para armazenar jogadores e suas vitórias
        List<Map.Entry<String, Integer>> playerWins = new ArrayList<>();

        // Iterar sobre os jogadores e adicionar seus wins à lista
        for (String player : players) {
            int wins = 0;

            if (mode == "4v4") {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".4v4wins");
            } else if (mode == "2v2") {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".2v2wins");
            }

            playerWins.add(new AbstractMap.SimpleEntry<>(player, wins));
        }

        // Ordenar a lista pelo número de vitórias em ordem decrescente
        playerWins.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Pegar os top 3 jogadores
        List<Map.Entry<String, Integer>> topPlayers = playerWins.subList(0, Math.min(3, playerWins.size()));

        Location[] podiumLocations = { pod1, pod2, pod3 };

        for (int i = 0;topPlayers.size() > i; i++) {
            String top = topPlayers.get(i).getKey();
            int wins = topPlayers.get(i).getValue();

            ArmorStand armorStand = (ArmorStand) world1.spawnEntity(podiumLocations[i], EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setVisible(false);

            if (mode.equals("4v4")) {
                armorStand.setRotation(0, 0);
            } else if (mode.equals("2v2")) {
                armorStand.setRotation(180, 0);
            }

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
