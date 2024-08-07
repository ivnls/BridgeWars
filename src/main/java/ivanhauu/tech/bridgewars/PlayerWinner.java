package ivanhauu.tech.bridgewars;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class PlayerWinner {

    private final BridgeWars plugin;

    public PlayerWinner(BridgeWars plugin) {
        this.plugin = plugin;
    }

    public void playerWinner(World world, boolean pass_check) {
        if (getAlivePlayers(world) == 1 || pass_check) {
            Player winner = null;
            for (Player p : world.getPlayers()) {
                if (!p.isDead() && p.getGameMode() == GameMode.SURVIVAL) {
                    Location winner_loc = new Location(world, p.getX(), p.getY(), p.getZ());
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendTitle("§2Você Ganhou a partida!", "");
                    spawnFirework(world, winner_loc);
                    winner = p;
                    break;
                }
            }

            plugin.getLogger().info("winner: " + winner.getName());

            if (winner != null) {
                if (world.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_")) {
                    int currentWins = plugin.getPlayerConfig().getInt("players." + winner.getName() + ".2v2wins", 0);
                    int newWins = currentWins + 1;
                    plugin.getPlayerConfig().set("players." + winner.getName() + ".2v2wins", newWins);
                    plugin.savePlayerConfig();
                    plugin.is2v2BattleStart(world.getName(), false);
                    plugin.isBattleStarting(world.getName(), false);
                } else if (world.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
                    int currentWins = plugin.getPlayerConfig().getInt("players." + winner.getName() + ".4v4wins", 0);
                    int newWins = currentWins + 1;
                    plugin.getPlayerConfig().set("players." + winner.getName() + ".4v4wins", newWins);
                    plugin.savePlayerConfig();
                    plugin.is4v4BattleStart(world.getName(), false);
                    plugin.isBattleStarting(world.getName(), false);
                }

                Player finalWinner = winner;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location spawn = new Location(Bukkit.getWorld("world"), 8, 0, 8);
                    for (Player p : world.getPlayers()) {
                        p.getInventory().clear();
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.clearActivePotionEffects();
                        p.setGameMode(GameMode.SURVIVAL);
                        p.teleport(spawn);
                    }

                }, 80L);

                if (world.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_")) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (Entity e : world.getEntities()) {
                            e.remove();
                        }

                        Location corner01 = new Location(world, -16, 7, 42);
                        Location corner02 = new Location(world, -49, 0, 45);

                        Location corner11 = new Location(world, -16, 7, 18);
                        Location corner12 = new Location(world, -49, 0, 21);

                        plugin.setBlocksToAir(corner01, corner02);
                        plugin.setBlocksToAir(corner11, corner12);
                    }, 100L);
                } else if (world.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (Entity e : world.getEntities()) {
                            e.remove();
                        }

                        Location corner01 = new Location(world, -16, 7, -2);
                        Location corner02 = new Location(world, -49, 0, 1);

                        Location corner11 = new Location(world, -67, 7, 15);
                        Location corner12 = new Location(world, -64, 0, 48);

                        Location corner21 = new Location(world, -49, 7, 65);
                        Location corner22 = new Location(world, -16, 0, 62);

                        Location corner31 = new Location(world, 2, 7, 48);
                        Location corner32 = new Location(world, -1, 0, 15);

                        plugin.setBlocksToAir(corner01, corner02);
                        plugin.setBlocksToAir(corner11, corner12);
                        plugin.setBlocksToAir(corner21, corner22);
                        plugin.setBlocksToAir(corner31, corner32);

                    }, 100L);
                }

            }
        } else {
            plugin.getLogger().info("Ninguém ganhou ainda!");
        }
    }

    public int getAlivePlayers(World world) {
        if (world == null) {
            plugin.getLogger().warning("World is null, cannot count alive players.");
            return 0;
        }

        int aliveCount = 0;
        for (Player player : world.getPlayers()) {
            if (player != null && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                aliveCount++;
            }
        }

        plugin.getLogger().info("Há " + aliveCount + " players vivos!");
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
