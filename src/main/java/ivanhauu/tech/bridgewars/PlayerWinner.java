package ivanhauu.tech.bridgewars;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class PlayerWinner {

    private final BridgeWars plugin;
    private final WorldManager worldManager;

    public PlayerWinner(BridgeWars plugin, WorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
    }

    public void playerWinner(World world, boolean pass_check) {
        if (getAlivePlayers(world) == 1 || pass_check) {
            // player_killer.sendMessage("Você Ganhou a partida!");
            Player winner = null;
            for (Player p : world.getPlayers()) {
                if (!p.isDead() && p.getGameMode() == GameMode.SURVIVAL) {
                    Location winner_loc = new Location(world, p.getX(), p.getY(), p.getZ());
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendTitle("§2Você Ganhou a partida!", "");
                    spawnFirework(world, winner_loc);
                    winner = p;
                }
            }

            if (winner != null) {
                if (world.getName().startsWith("battle_2v2_")) {
                    int currentWins = plugin.getPlayerConfig().getInt("players." + winner.getName() + ".2v2wins", 0);
                    int newWins = currentWins + 1;
                    plugin.getPlayerConfig().set("players." + winner.getName() + ".2v2wins", newWins);
                    plugin.savePlayerConfig();
                } else if (world.getName().startsWith("battle_4v4_")) {
                    int currentWins = plugin.getPlayerConfig().getInt("players." + winner.getName() + ".4v4wins", 0);
                    int newWins = currentWins + 1;
                    plugin.getPlayerConfig().set("players." + winner.getName() + ".4v4wins", newWins);
                    plugin.savePlayerConfig();
                }

                Player finalWinner = winner;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location spawn = new Location(Bukkit.getWorld("world"), 8, 0, 8);
                    for (Player p : world.getPlayers()) {
                        p.getInventory().clear();
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.setGameMode(GameMode.SURVIVAL);
                        p.teleport(spawn);
                    }

                }, 80L);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getLogger().info("Mundo apagado, pois a partida acabou!");
                    worldManager.deleteWorldIfEmpty(world.getName());
                }, 100L);
            }
        } else {
            plugin.getLogger().info("Ninguém ganhou ainda!");
        }
    }

    public int getAlivePlayers(World world) {
        int aliveCount = 0;
        for (Player player : world.getPlayers()) {
            if (!player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                aliveCount++;
            }
        }
        return aliveCount;
    }

    public void spawnFirework(World world, Location location) {
        // Cria a entidade do foguete na localização especificada
        Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK_ROCKET);

        // Obtém o meta dos fogos de artifício para personalização
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        // Cria um efeito de fogos de artifício
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED)
                .withFade(Color.ORANGE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withFlicker()
                .withTrail()
                .build();

        // Define o efeito no meta dos fogos de artifício
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1); // Define o poder do foguete (altura de explosão)
        firework.setFireworkMeta(fireworkMeta);

        // Inicia o foguete
        firework.detonate(); // O foguete detona imediatamente após ser spawnado
    }
}
