package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.PlayerWinner;
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
    private final PlayerWinner playerWinner;

    private List<Double> playerF2v2;
    private List<Double> playerS2v2;
    private List<Double> playerT2v2;
    private List<Double> playerF4v4;
    private List<Double> playerS4v4;
    private List<Double> playerT4v4;
    private String nameSpawnWorld;
    private World spawnWorld;

    private Location pod1_4v4;
    private Location pod2_4v4;
    private Location pod3_4v4;
    private Location pod1_2v2;
    private Location pod2_2v2;
    private Location pod3_2v2;

    private String sectionsBaseFolder;
    private String subFolder2v2;
    private String subFolder4v4;
    private String battleWorldName2v2;
    private String battleWorldName4v4;

    public PlayerQuitJoin(BridgeWars plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;

        this.playerF2v2 = plugin.getFirst2v2();
        this.playerS2v2 = plugin.getSecond2v2();
        this.playerT2v2 = plugin.getThird2v2();
        this.playerF4v4 = plugin.getFirst4v4();
        this.playerS4v4 = plugin.getSecond4v4();
        this.playerT4v4 = plugin.getThird4v4();
        this.nameSpawnWorld = plugin.getSpawnWorld();
        this.spawnWorld = Bukkit.getWorld(nameSpawnWorld);

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.battleWorldName2v2 = plugin.getBattleWorldName2v2();
        this.battleWorldName4v4 = plugin.getBattleWorldName4v4();

        initializeLocations();
    }

    private void initializeLocations() {
        World world = Bukkit.getWorld(nameSpawnWorld);

        if (world == null) {
            plugin.getLogger().warning("World '" + nameSpawnWorld + "' is not loaded!");
            return;
        }

        // Verifica se as listas contêm os elementos necessários
        if (playerF4v4.size() >= 3) {
            pod1_4v4 = new Location(world, playerF4v4.get(0), playerF4v4.get(1), playerF4v4.get(2));
        }
        if (playerS4v4.size() >= 3) {
            pod2_4v4 = new Location(world, playerS4v4.get(0), playerS4v4.get(1), playerS4v4.get(2));
        }
        if (playerT4v4.size() >= 3) {
            pod3_4v4 = new Location(world, playerT4v4.get(0), playerT4v4.get(1), playerT4v4.get(2));
        }
        if (playerF2v2.size() >= 3) {
            pod1_2v2 = new Location(world, playerF2v2.get(0), playerF2v2.get(1), playerF2v2.get(2));
        }
        if (playerS2v2.size() >= 3) {
            pod2_2v2 = new Location(world, playerS2v2.get(0), playerS2v2.get(1), playerS2v2.get(2));
        }
        if (playerT2v2.size() >= 3) {
            pod3_2v2 = new Location(world, playerT2v2.get(0), playerT2v2.get(1), playerT2v2.get(2));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World mundo = player.getWorld();

        if (mundo.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2) || mundo.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {
            Location spawn = new Location(spawnWorld, 8, 0, 8);
            player.teleport(spawn);
        }

        if (player.getWorld().getName().equals("world")) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.clearActivePotionEffects();
            player.setGameMode(GameMode.SURVIVAL);
            Location spawn = new Location(spawnWorld, 8, 0, 8);
            player.teleport(spawn);
        }

        plugin.updatePlayersVisualization(player);

        event.setJoinMessage(null);

        if (spawnWorld.getName().equals("world")) {
            for (Entity e : spawnWorld.getEntities()) {
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
        World eventWorld = player.getWorld();
        int playersOnWorld = eventWorld.getPlayers().size() - 1;

        event.setQuitMessage(null);

        if (eventWorld.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2)) {
            boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + eventWorld.getName() + ".is4v4BattleStarted");
            if (is4v4BattleStarted) {
                if (playersOnWorld <= 4) {
                    for (Player p : eventWorld.getPlayers()) {
                        if (p != player && p.getGameMode() == GameMode.SURVIVAL) {
                            playerWinner.playerWinner(eventWorld, true);
                            plugin.getLogger().info("O player " + p.getName() + " ganhou poie só ele está no mundo!");
                            break;
                        }
                    }
                }
            }
        } else if (eventWorld.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {
            boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + eventWorld.getName() + ".is2v2BattleStarted");
            if (is2v2BattleStarted) {
                if (eventWorld.getPlayers().size() <= 2) {
                    for (Player p : eventWorld.getPlayers()) {
                        if (p != player && p.getGameMode() == GameMode.SURVIVAL) {
                            playerWinner.playerWinner(eventWorld, true);
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

        Set<String> players = plugin.getPlayerConfig().getConfigurationSection("players").getKeys(false);

        List<Map.Entry<String, Integer>> playerWins = new ArrayList<>();

        for (String player : players) {
            int wins = 0;

            if (mode == "4v4") {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".4v4wins");
            } else if (mode == "2v2") {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".2v2wins");
            }

            playerWins.add(new AbstractMap.SimpleEntry<>(player, wins));
        }

        playerWins.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

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

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(top);
            skull.setItemMeta(meta);
            armorStand.setHelmet(skull);

            armorStand.setCustomName(top + " - " + wins + " vitórias");
            armorStand.setCustomNameVisible(true);
        }
    }
}
