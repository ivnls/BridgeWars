package ivanhauu.tech.battlesessions.listeners;

import ivanhauu.tech.battlesessions.BattleSessions;
import ivanhauu.tech.battlesessions.PlayerWinner;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class onPlayerKill implements Listener {

    private final BattleSessions plugin;
    private final PlayerWinner playerWinner;

    public onPlayerKill(BattleSessions plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player_killed = event.getPlayer();
        World event_world = player_killed.getWorld();

        //Aqui está a geração das pontes para a partida 8v8 e parte da lógica de morte
        if (event_world.getName().startsWith("battle_8v8_") || event_world.getName().startsWith("battle_4v4_")) {

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

            if (event_world.getPlayers().size() == 5) {
                for (Player p : event_world.getPlayers()) {
                    p.sendMessage("As pontes serão abertas!");
                }
                Location explosion_location = new Location(event_world, -32, 6, 32);

                //Pontes gpoint (Green Start Point)

                List<List<Integer>> listaPrincipal = new ArrayList<>();

                List<Integer> gpoint1 = new ArrayList<>();

                //Aqui aumentar o X 32

                gpoint1.add(-16); // X
                gpoint1.add(5); //   Y
                gpoint1.add(-2);//   Z

                listaPrincipal.add(gpoint1);

                List<Integer> gpoint2 = new ArrayList<>();

                //Aqui aumentar o Z 32

                gpoint2.add(-66);
                gpoint2.add(5);
                gpoint2.add(15);

                listaPrincipal.add(gpoint2);

                List<Integer> gpoint3 = new ArrayList<>();

                //Aqui abixar o X 32

                gpoint3.add(-49);
                gpoint3.add(5);
                gpoint3.add(65);

                listaPrincipal.add(gpoint3);

                List<Integer> gpoint4 = new ArrayList<>();

                //Aqui abaixar o Z 32

                gpoint4.add(1);
                gpoint4.add(5);
                gpoint4.add(48);

                listaPrincipal.add(gpoint4);

                for (int i = 0; i <= 3; i++) {

                    final int index = i;

                    int x = listaPrincipal.get(i).get(0);
                    int y = listaPrincipal.get(i).get(1);
                    int z = listaPrincipal.get(i).get(2);

                    //Aqui aumentar o X 32
                    // Aumentar Z 4
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (index == 0) {
                            int cordx = x;
                            int cordz = z;
                            for (int b = 1; b <= 4; b++) {
                                for (int c = 1; c <= 32; c++) {
                                    cordx--;
                                    if (c <= 16) {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.GRASS_BLOCK);
                                        if (c == 16) {
                                            Location lightning = new Location(event_world, cordx, y, cordz);
                                            event_world.strikeLightning(lightning);
                                        }
                                    } else {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.NETHERRACK);
                                    }
                                }
                                cordx = x;
                                cordz++;
                            }
                            event_world.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                            for (Player p : event_world.getPlayers()) {
                                p.sendMessage("Primeira ponte aberta!");
                            }
                        } else if (index == 1) {
                            int cordx = x;
                            int cordz = z;
                            for (int b = 1; b <= 4; b++) {
                                for (int c = 1; c <= 32; c++) {
                                    cordz++;
                                    if (c <= 16) {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.NETHERRACK);
                                        if (c == 16) {
                                            Location lightning = new Location(event_world, cordx, y, cordz);
                                            event_world.strikeLightning(lightning);
                                        }
                                    } else {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.END_STONE);
                                    }
                                }
                                cordz = z;
                                cordx++;
                            }
                            event_world.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                            for (Player p : event_world.getPlayers()) {
                                p.sendMessage("Segunda ponte aberta!");
                            }
                        } else if (index == 2) {
                            int cordx = x;
                            int cordz = z;
                            for (int b = 1; b <= 4; b++) {
                                for (int c = 1; c <= 32; c++) {
                                    cordx++;
                                    if (c <= 16) {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.END_STONE);
                                        if (c == 16) {
                                            Location lightning = new Location(event_world, cordx, y, cordz);
                                            event_world.strikeLightning(lightning);
                                        }
                                    } else {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.DEEPSLATE);
                                    }
                                }
                                cordx = x;
                                cordz--;
                            }
                            event_world.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                            for (Player p : event_world.getPlayers()) {
                                p.sendMessage("Terceira ponte aberta!");
                            }
                        } else if (index == 3) {
                            int cordx = x;
                            int cordz = z;
                            for (int b = 1; b <= 4; b++) {
                                for (int c = 1; c <= 32; c++) {
                                    cordz--;
                                    if (c <= 16) {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.DEEPSLATE);
                                        if (c == 16) {
                                            Location lightning = new Location(event_world, cordx, y, cordz);
                                            event_world.strikeLightning(lightning);
                                        }
                                    } else {
                                        event_world.getBlockAt(cordx, y, cordz).setType(Material.GRASS_BLOCK);
                                    }
                                }
                                cordz = z;
                                cordx--;
                            }
                            event_world.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                            for (Player p : event_world.getPlayers()) {
                                p.sendMessage("Quarta ponte aberta!");
                            }
                        }
                    }, index * 40L);
                }

                //Para ambas as partidas quando sobrar 1 player no mundo, ele ganhará --> SEPARAR VITÓRIA 8V8 DE 4V4!
            } else if (event.getEntity().getKiller() != null) {
                plugin.getPlayerWinner().playerWinner(event_world, player_killed);
            }

        }

    }

}
