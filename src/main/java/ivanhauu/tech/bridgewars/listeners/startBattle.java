package ivanhauu.tech.bridgewars.listeners;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.GenerateChest;
import ivanhauu.tech.bridgewars.worldedit.LoadSchematic;
import ivanhauu.tech.bridgewars.worldedit.PasteSchematic;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class startBattle implements Listener {

    private final BridgeWars plugin;
    private final GenerateChest generateChest;

    public startBattle(BridgeWars plugin, GenerateChest generateChest) {
        this.plugin = plugin;
        this.generateChest = generateChest;
    }

    // Aqui é feita a lógica de inicialização de uma partida 2v2 e 4v4, além de gerar as pontes depois de
    // 15 segundos para partidas 4v4. (Para partidas 2v2 as pontes são geradas em um Listener onPlayerKill, pois
    // as pontes devem ser geradas se houver um determinado número de jogadores no mundo).
    @EventHandler
    public void startBattle(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World toWorld = player.getWorld();
        plugin.updatePlayersVisualization(player);

        if (toWorld.getPlayers().size() == 2 && toWorld.getName().startsWith("battle_2v2_")) {

            plugin.is2v2BattleStart(toWorld.getName(), true);

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            // Criar 8 equipes
            for (int i = 1; i <= 2; i++) {
                Team team = board.registerNewTeam("GRUPO " + i);
                team.setPrefix("GRUPO " + i + " ");
            }

            /*Possíveis localizações dos baús nas quatro ilhas

            -----> FAZER A GERAÇÃO DE BAÚS DA PARTIDA 2V2 !!!!!! <-----

            List<Location> overworld = List.of(
                    new Location(toWorld, -6, 7, -12),
                    new Location(toWorld, -15, 6, -5),
                    new Location(toWorld, -1, 6, -7),
                    new Location(toWorld, -14, 7, 8),
                    new Location(toWorld, -14, 6, 13),
                    new Location(toWorld, 6, 6, 14),
                    new Location(toWorld, 5, 6, 4),
                    new Location(toWorld, 13, 10, -12)
            );

            List<Location> nether = List.of(
                    new Location(toWorld, -79, 16, 3),
                    new Location(toWorld, -51, 8, 13),
                    new Location(toWorld, -61, 8, 2),
                    new Location(toWorld, -76, 6, -11),
                    new Location(toWorld, -77, 7, 12),
                    new Location(toWorld, -59, 7, -14),
                    new Location(toWorld, -78, 15, -16),
                    new Location(toWorld, -49, 7, 7)
            );

            List<Location> theEnd = List.of(
                    new Location(toWorld, -79, 6, 49),
                    new Location(toWorld, -62, 6, 53),
                    new Location(toWorld, -51, 6, 76),
                    new Location(toWorld, -69, 7, 75),
                    new Location(toWorld, -77, 12, 66),
                    new Location(toWorld, -68, 7, 74),
                    new Location(toWorld, -61, 5, 73),
                    new Location(toWorld, -66, 6, 59)
            );

            List<Location> ancientCity = List.of(
                    new Location(toWorld, -9, 8, 73),
                    new Location(toWorld, -11, 9, 59),
                    new Location(toWorld, 8, 14, 52),
                    new Location(toWorld, 11, 8, 73),
                    new Location(toWorld, -1, 7, 63),
                    new Location(toWorld, 3, 6, 48),
                    new Location(toWorld, 2, 8, 75),
                    new Location(toWorld, 0, 8, 66)
            );

            List<List<Location>> bausNasIlhas = List.of(overworld, nether, theEnd, ancientCity);

            // Geração dos baús, utilizando um sistema de aleatoriedade --> FUTURAMENTE ADICIONAR LIMITE DE BAÚS POR ILHA!
            Random rand = new Random();

            for (List<Location> ilha : bausNasIlhas) {
                for (Location loc : ilha) {
                    boolean isChest = rand.nextBoolean();
                    if (isChest) {
                        generateChest.generateChest(loc);
                    }
                }
            }

             */

            // Atribuir cada jogador a uma equipe
            int teamNumber = 1;
            for (Player p : toWorld.getPlayers()) {
                Team team = board.getTeam("GRUPO " + teamNumber);
                team.addEntry(p.getName());
                p.setScoreboard(board);

                teamNumber++;
                if (teamNumber > 2) {
                    teamNumber = 1;
                }

                int y = 0;
                int x = 0;
                int z = 0;

                int group = teamNumber;

                Random rand = new Random();

                //Localizações de spawn em batalhas 2v2
                switch (group) {
                    case 1:
                        x = rand.nextInt(-65 - (-79) + 1) + (-79);
                        y = 6;
                        z = rand.nextInt(46 - 17 + 1) + 17;
                        break;
                    case 2:
                        x = rand.nextInt(14 - 0 + 1) + 0;
                        y = 6;
                        z = rand.nextInt(46 - 17 + 1) + 17;
                        break;
                }

                Location location = new Location(p.getWorld(), x, y, z);

                p.teleport(location);

            }
            plugin.isBattleStarting(toWorld.getName(), true);

            BukkitTask task = new BukkitRunnable() {
                int currentCount = 5;

                @Override
                public void run() {
                    if (currentCount == 0) {
                        for (Player p : toWorld.getPlayers()) {
                            p.sendTitle("A partida começou!", "Boa sorte!");
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 10, 1);
                            plugin.isBattleStarting(toWorld.getName(), false);
                        }
                        cancel();
                    } else {
                        plugin.isBattleStarting(toWorld.getName(), true);

                        for (Player p : toWorld.getPlayers()) {
                            p.sendTitle("A partida vai começar!", "em " + currentCount, 10, 70, 20);
                            p.playSound(p.getLocation(), Sound.BLOCK_CRAFTER_CRAFT, 10, 1);
                        }
                        currentCount--;
                    }
                }
            }.runTaskTimer(plugin, 0, 20L);

            List<String> listCaos = new ArrayList<>();
            listCaos.add("Creeper");
            listCaos.add("Blaze");
            listCaos.add("Wither_Skeleton");
            listCaos.add("Zoglin");
            listCaos.add("Magma_Cube");

            new BukkitRunnable() {
                //pos3 = -32, 18, 28

                int carac0 = 1;
                int carac1 = 2;
                int carac2 = 5;


                @Override
                public void run() {

                    if (carac0 == 0 && carac1 == 0 && carac2 < 0) {
                        cancel();
                        return;
                    }

                    int oldnum0 = carac0 + 1;
                    int oldnum1 = carac1 + 1;

                    if (carac2 < 0) {
                        carac1 --;
                        carac2 = 9;
                    }

                    if (carac1 < 0) {
                        carac0 --;
                        carac1 = 9;
                    }

                    if (carac0 != oldnum0) {

                        File num0 = new File(plugin.getDataFolder() + "/numbers/num" + carac0 + ".schem");
                        Clipboard clipboard = LoadSchematic.loadSchematic(num0, toWorld);
                        Location pasteLocation1 = new Location(toWorld, -31, 18, 38);
                        PasteSchematic.pasteSchematic(clipboard, pasteLocation1, false);

                        Location pasteLocation2 = new Location(toWorld, -34, 18, 25);
                        PasteSchematic.pasteSchematic(clipboard, pasteLocation2, true);

                    }

                    if (carac1 != oldnum1) {

                        File num1 = new File(plugin.getDataFolder() + "/numbers/num" + carac1 + ".schem");
                        Clipboard clipboard = LoadSchematic.loadSchematic(num1, toWorld);
                        Location pasteLocation1 = new Location(toWorld, -31, 18, 33);
                        PasteSchematic.pasteSchematic(clipboard, pasteLocation1, false);

                        Location pasteLocation2 = new Location(toWorld, -34, 18, 30);
                        PasteSchematic.pasteSchematic(clipboard, pasteLocation2, true);

                    }


                    File num2 = new File(plugin.getDataFolder() + "/numbers/num" + carac2 + ".schem");
                    Clipboard clipboard = LoadSchematic.loadSchematic(num2, toWorld);

                    Location pasteLocation1 = new Location(toWorld, -31, 18, 28);
                    PasteSchematic.pasteSchematic(clipboard, pasteLocation1, false);

                    Location pasteLocation2 = new Location(toWorld, -34, 18, 35);
                    PasteSchematic.pasteSchematic(clipboard, pasteLocation2, true);

                    Location soundLoc = new Location(toWorld, -32, 18, 28);

                    if (carac0 == 0 && carac1 == 0 && carac2 <=10 && carac2 != 0) {
                        toWorld.playSound(soundLoc, Sound.BLOCK_NOTE_BLOCK_BASS, 10, 10);

                    } else if (carac0 == 1 && carac1 == 0 && carac2 == 0) {

                        if (toWorld.getPlayers().size() == 2) {
                            for (Player p : toWorld.getPlayers()) {
                                p.sendMessage("As pontes serão abertas!");
                            }

                            //Carregamento da schematic para a ponte

                            Location explosion_location = new Location(toWorld, -32, 6, 32);

                            //Pontes gpoint (Green Start Point)

                            List<List<Integer>> listaPrincipal = new ArrayList<>();

                            List<Integer> gpoint1 = new ArrayList<>();

                            gpoint1.add(-15); // X
                            gpoint1.add(6); //   Y
                            gpoint1.add(18);//   Z

                            listaPrincipal.add(gpoint1);

                            List<Integer> gpoint2 = new ArrayList<>();

                            gpoint2.add(-15);
                            gpoint2.add(6);
                            gpoint2.add(42);

                            listaPrincipal.add(gpoint2);

                            for (int i = 0; i <= 1; i++) {

                                final int index = i;

                                int x2v2 = listaPrincipal.get(i).get(0);
                                int y2v2 = listaPrincipal.get(i).get(1);
                                int z2v2 = listaPrincipal.get(i).get(2);

                                int finalI = i + 1;
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                                    File bridgeSchematic = new File(plugin.getDataFolder() + "/schematics/2v2/" + "default_2v2" + ".schem");
                                    Clipboard clipboardo = LoadSchematic.loadSchematic(bridgeSchematic, toWorld);
                                    Location pasteLocation = new Location(toWorld, x2v2, y2v2, z2v2);
                                    PasteSchematic.pasteSchematic(clipboardo, pasteLocation, false);


                                    toWorld.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                                    for (Player p : toWorld.getPlayers()) {
                                        p.sendMessage(finalI + "º ponte aberta!");
                                    }

                                }, index * 40L);
                            }
                        }

                    } else if (carac0 == 0 && carac1 == 0 && carac2 == 0) {

                        Collections.shuffle(listCaos);

                        EntityType mobToSpawn = EntityType.valueOf(listCaos.get(0).toUpperCase());

                        for (Player p : toWorld.getPlayers()) {
                            p.sendTitle("O tempo acabou!", "§4Começando o modo caos! com " + listCaos.get(0));
                        }

                        Random rand = new Random();

                        int contador = 40;

                        while (contador >= 0) {

                            int min1 = -80;
                            int max1 = 15;

                            int min2 = 16;
                            int max2 = 47;

                            int randomX = rand.nextInt((max1 - min1) + 1) + min1;
                            int randomY = 26;
                            int randomZ = rand.nextInt((max2 - min2) + 1) + min2;

                            Location location = new Location(toWorld, randomX, randomY, randomZ);

                            toWorld.spawnEntity(location, mobToSpawn);
                            contador--;
                        }


                    } else {
                        toWorld.playSound(soundLoc, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 10, 10);
                    }
                    carac2 --;
                }
            }.runTaskTimer(plugin, 0, 20);


            for (Player p : toWorld.getPlayers()) {
                p.sendMessage("A partida começou!");
            }

            // Deletar o prefixo do grupo ao jogador entrar no spawn
        } else if (toWorld.getName().equals("world")) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            player.setScoreboard(board);

        } else if (toWorld.getPlayers().size() == 4 && toWorld.getName().startsWith("battle_4v4_")) {
            plugin.is4v4BattleStart(toWorld.getName(), true);

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            for (int i = 1; i <= 4; i++) {
                Team team = board.registerNewTeam("GRUPO " + i);
                team.setPrefix("GRUPO " + i + " ");
            }

            List<Location> overworld = new ArrayList<>();
            overworld.add(new Location(toWorld, -6, 7, -12));
            overworld.add(new Location(toWorld, -15, 6, -5));
            overworld.add(new Location(toWorld, -1, 6, -7));
            overworld.add(new Location(toWorld, -14, 7, 8));
            overworld.add(new Location(toWorld, -14, 6, 13));
            overworld.add(new Location(toWorld, 6, 6, 14));
            overworld.add(new Location(toWorld, 5, 6, 4));
            overworld.add(new Location(toWorld, 13, 10, -12));
            Collections.shuffle(overworld);

            List<Location> nether = new ArrayList<>();
            nether.add(new Location(toWorld, -79, 16, 3));
            nether.add(new Location(toWorld, -51, 8, 13));
            nether.add(new Location(toWorld, -61, 8, 2));
            nether.add(new Location(toWorld, -76, 6, -11));
            nether.add(new Location(toWorld, -77, 7, 12));
            nether.add(new Location(toWorld, -59, 7, -14));
            nether.add(new Location(toWorld, -78, 15, -16));
            nether.add(new Location(toWorld, -49, 7, 7));
            Collections.shuffle(nether);

            List<Location> theEnd = new ArrayList<>();
            theEnd.add(new Location(toWorld, -79, 6, 49));
            theEnd.add(new Location(toWorld, -62, 6, 53));
            theEnd.add(new Location(toWorld, -51, 6, 76));
            theEnd.add(new Location(toWorld, -69, 7, 75));
            theEnd.add(new Location(toWorld, -77, 12, 66));
            theEnd.add(new Location(toWorld, -68, 7, 74));
            theEnd.add(new Location(toWorld, -61, 5, 73));
            theEnd.add(new Location(toWorld, -66, 6, 59));
            Collections.shuffle(theEnd);

            List<Location> ancientCity = new ArrayList<>();
            ancientCity.add(new Location(toWorld, -9, 8, 73));
            ancientCity.add(new Location(toWorld, -11, 9, 59));
            ancientCity.add(new Location(toWorld, 8, 14, 52));
            ancientCity.add(new Location(toWorld, 11, 8, 73));
            ancientCity.add(new Location(toWorld, -1, 7, 63));
            ancientCity.add(new Location(toWorld, 3, 6, 48));
            ancientCity.add(new Location(toWorld, 2, 8, 75));
            ancientCity.add(new Location(toWorld, 0, 8, 66));
            Collections.shuffle(ancientCity);


            List<List<Location>> bausNasIlhas = List.of(overworld, nether, theEnd, ancientCity);

            for (List<Location> ilha : bausNasIlhas) {
                for (int i = 0; i <= 3; i++) {
                    Location loc = ilha.get(i);
                    generateChest.generateChest(loc);
                }
            }

            int teamNumber = 1;
            for (Player p : toWorld.getPlayers()) {
                Team team = board.getTeam("GRUPO " + teamNumber);
                if (team != null) {
                    team.addEntry(p.getName());
                    p.setScoreboard(board);
                }

                int x = 0, y = 0, z = 0;
                switch (teamNumber) {
                    case 1:
                        x = 13;
                        y = 6;
                        z = 3;
                        break;
                    case 2:
                        x = -51;
                        y = 8;
                        z = -14;
                        break;
                    case 3:
                        x = -52;
                        y = 6;
                        z = 52;
                        break;
                    case 4:
                        x = -14;
                        y = 6;
                        z = 70;
                        break;
                }

                Location location = new Location(p.getWorld(), x, y, z);
                p.teleport(location);

                teamNumber++;
                if (teamNumber > 4) {
                    teamNumber = 1;
                }
            }

            plugin.isBattleStarting(toWorld.getName(), true);

            BukkitTask task = new BukkitRunnable() {
                int currentCount = 5;

                @Override
                public void run() {
                    if (currentCount == 0) {
                        for (Player p : toWorld.getPlayers()) {
                            p.sendTitle("A partida começou!", "Boa sorte!");
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 10, 1);
                            plugin.isBattleStarting(toWorld.getName(), false);
                        }
                        cancel();
                    } else {
                        plugin.isBattleStarting(toWorld.getName(), true);

                        for (Player p : toWorld.getPlayers()) {
                            p.sendTitle("A partida vai começar!", "em " + currentCount, 10, 70, 20);
                            p.playSound(p.getLocation(), Sound.BLOCK_CRAFTER_CRAFT, 10, 1);
                        }
                        currentCount--;
                    }
                }
            }.runTaskTimer(plugin, 0, 20L);

            String schematicsFolder4v4 = "4v4";

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (toWorld.getPlayers().size() == 4) {
                    for (Player p : toWorld.getPlayers()) {
                        p.sendMessage("As pontes serão abertas!");
                    }

                    //Carregamento da schematic para a ponte

                    Location explosion_location = new Location(toWorld, -32, 6, 32);

                    //Pontes gpoint (Green Start Point)

                    List<List<Integer>> listaPrincipal = new ArrayList<>();

                    List<Integer> gpoint1 = new ArrayList<>();

                    gpoint1.add(-15); // X
                    gpoint1.add(6); //   Y
                    gpoint1.add(-2);//   Z

                    listaPrincipal.add(gpoint1);

                    List<Integer> gpoint2 = new ArrayList<>();

                    gpoint2.add(-67);
                    gpoint2.add(6);
                    gpoint2.add(14);

                    listaPrincipal.add(gpoint2);

                    List<Integer> gpoint3 = new ArrayList<>();

                    gpoint3.add(-50);
                    gpoint3.add(6);
                    gpoint3.add(65);

                    listaPrincipal.add(gpoint3);

                    List<Integer> gpoint4 = new ArrayList<>();

                    gpoint4.add(2);
                    gpoint4.add(6);
                    gpoint4.add(49);

                    listaPrincipal.add(gpoint4);

                    for (int i = 0; i <= 3; i++) {

                        final int index = i;

                        int x4v4 = listaPrincipal.get(i).get(0);
                        int y4v4 = listaPrincipal.get(i).get(1);
                        int z4v4 = listaPrincipal.get(i).get(2);

                        int finalI = i + 1;
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {


                            File bridgeSchematic = new File(plugin.getDataFolder() + "/schematics/" + schematicsFolder4v4 + "/" + finalI + ".schem");
                            Clipboard clipboard = LoadSchematic.loadSchematic(bridgeSchematic, toWorld);
                            Location pasteLocation = new Location(toWorld, x4v4, y4v4, z4v4);
                            PasteSchematic.pasteSchematic(clipboard, pasteLocation, false);


                            toWorld.playSound(explosion_location, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                            for (Player p : toWorld.getPlayers()) {
                                p.sendMessage(finalI + "º ponte aberta!");
                            }

                        }, index * 40L);
                    }
                }
            }, 640L);
        }
        if (!toWorld.getName().equals("world"))
            player.sendMessage("Você entrou na partida: " + toWorld.getName());
        else {
            player.sendMessage("Você voltou ao spawn!");
        }
    }

}
