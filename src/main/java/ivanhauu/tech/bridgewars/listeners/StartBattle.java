package ivanhauu.tech.bridgewars.listeners;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.GenerateChest;
import ivanhauu.tech.bridgewars.worldedit.ManageSchematics;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;

public class StartBattle implements Listener {

    private final BridgeWars plugin;
    private final GenerateChest generateChest;

    public StartBattle(BridgeWars plugin, GenerateChest generateChest) {
        this.plugin = plugin;
        this.generateChest = generateChest;
    }

    Random rand = new Random();

    String groupPrefix = "GRUPO ";

    // Aqui é feita a lógica de inicialização de uma partida 2v2 e 4v4, além de gerar as pontes depois de
    // 15 segundos para partidas 4v4. (Para partidas 2v2 as pontes são geradas em um Listener PlayerDeath, pois
    // as pontes devem ser geradas se houver um determinado número de jogadores no mundo).
    @EventHandler
    public void startBattle(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World toWorld = player.getWorld();
        plugin.updatePlayersVisualization(player);
        String worldName = toWorld.getName();

        //>>>>>>>>>>>>>>>>>> PARTIDA 2V2 <<<<<<<<<<<<<<<<<<<<<<<<

        if (toWorld.getPlayers().size() == 2 && toWorld.getName().startsWith(plugin.getDataFolder() + "/sections/2v2/battle_2v2_")) {
            plugin.is2v2BattleStart(toWorld.getName(), true);

            preparePlayers2v2(toWorld);

            // Deletar o prefixo do grupo ao jogador entrar no spawn
        } else if (toWorld.getName().equals("world")) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            player.setScoreboard(board);

            //>>>>>>>>>>>>>>>>>> PARTIDA 4V4 <<<<<<<<<<<<<<<<<<<<<<<<

        } else if (toWorld.getPlayers().size() == 4 && toWorld.getName().startsWith(plugin.getDataFolder() + "/sections/4v4/battle_4v4_")) {
            plugin.is4v4BattleStart(toWorld.getName(), true);

            preparePlayers4v4(toWorld);
        }

        if (!toWorld.getName().equals("world") && !worldName.isEmpty()) {
            char lastChar = worldName.charAt(worldName.length() - 1);
            player.sendMessage("&bVocê entrou na partida: " + lastChar);
        } else {
            player.sendMessage("&bVocê voltou ao spawn!");
        }
    }

    private void run2v2Clock(World toWorld) {

        countdown(toWorld);

        new BukkitRunnable() {
            //pos3 = -32, 18, 28

            int carac0 = 1;
            int carac1 = 2;
            int carac2 = 5;


            @Override
            public void run() {

                if (toWorld.getPlayers().size() >= 2) {
                    if (carac0 == 0 && carac1 == 0 && carac2 < 0) {
                        cancel();
                        return;
                    }

                    int oldnum0 = carac0 + 1;
                    int oldnum1 = carac1 + 1;
                    int oldnum2 = carac2 + 1;

                    if (carac2 < 0) {
                        carac1--;
                        carac2 = 9;
                    }

                    if (carac1 < 0) {
                        carac0--;
                        carac1 = 9;
                    }

                    Location paste0Location1 = new Location(toWorld, -31, 18, 38);
                    Location paste0Location2 = new Location(toWorld, -34, 18, 25);
                    genNumClock2v2(carac0, oldnum0, paste0Location1, paste0Location2);

                    Location paste1Location1 = new Location(toWorld, -31, 18, 33);
                    Location paste1Location2 = new Location(toWorld, -34, 18, 30);
                    genNumClock2v2(carac1, oldnum1, paste1Location1, paste1Location2);

                    Location paste2Location1 = new Location(toWorld, -31, 18, 28);
                    Location paste2Location2 = new Location(toWorld, -34, 18, 35);
                    genNumClock2v2(carac2, oldnum2, paste2Location1, paste2Location2);

                    controllerClock2v2(carac0, carac1, carac2, toWorld, this);

                    carac2--;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);

    }

    private void run4v4Clock(World toWorld) {

        countdown(toWorld);

        String schematicsFolder4v4 = "4v4";

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (toWorld.getPlayers().size() == 4) {
                for (Player p : toWorld.getPlayers()) {
                    p.sendMessage("As pontes serão abertas!");
                }

                //Carregamento da schematic para a ponte

                Location explosionLocation = new Location(toWorld, -32, 6, 32);

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

                        File baseDir = new File(plugin.getDataFolder() + "schematics");
                        File subDir = new File(baseDir + schematicsFolder4v4);
                        File bridgeSchematic = new File(subDir, finalI + ".schem");

                        Clipboard clipboard = ManageSchematics.loadSchematic(bridgeSchematic);
                        Location pasteLocation = new Location(toWorld, x4v4, y4v4, z4v4);
                        ManageSchematics.pasteSchematic(clipboard, pasteLocation, false);


                        toWorld.playSound(explosionLocation, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                        for (Player p : toWorld.getPlayers()) {
                            p.sendMessage(finalI + "º ponte aberta!");
                        }

                    }, index * 40L);
                }
            }
        }, 640L);
    }

    private void countdown(World toWorld) {
        new BukkitRunnable() {
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
    }

    private void generateBridge2v2(World toWorld) {

        if (toWorld.getPlayers().size() == 2) {
            for (Player p : toWorld.getPlayers()) {
                p.sendMessage("§2As pontes serão abertas!");
            }

            //Carregamento da schematic para a ponte

            Location explosionLocation = new Location(toWorld, -32, 6, 32);

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
                    Clipboard clipboardo = ManageSchematics.loadSchematic(bridgeSchematic);
                    Location pasteLocation = new Location(toWorld, x2v2, y2v2, z2v2);
                    ManageSchematics.pasteSchematic(clipboardo, pasteLocation, false);


                    toWorld.playSound(explosionLocation, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
                    for (Player p : toWorld.getPlayers()) {
                        p.sendMessage(finalI + "º ponte aberta!");
                    }

                }, index * 40L);
            }
        }
    }

    private void preparePlayers2v2(World toWorld) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();


        // Criar 2 equipes
        for (int i = 1; i <= 2; i++) {
            Team team = board.registerNewTeam(groupPrefix + i);
            team.setPrefix(groupPrefix + i + " ");
        }

        // Atribuir cada jogador a uma equipe
        int teamNumber = 1;
        for (Player p : toWorld.getPlayers()) {
            Team team = board.getTeam(groupPrefix + teamNumber);
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
                default:
                    x = 0;
                    y = 6;
                    z = 0;
                    plugin.getLogger().warning("Erro ao gerar a localização 2v2 de algum player!");
                    break;
            }

            Location location = new Location(p.getWorld(), x, y, z);

            p.teleport(location);

            List<ItemStack> chestItems = Arrays.asList(
                    new ItemStack(Material.NETHERITE_HELMET, 1),
                    new ItemStack(Material.NETHERITE_CHESTPLATE, 1),
                    new ItemStack(Material.NETHERITE_LEGGINGS, 1),
                    new ItemStack(Material.NETHERITE_BOOTS, 1),
                    new ItemStack(Material.GOLDEN_APPLE, 12),
                    new ItemStack(Material.WIND_CHARGE, 24),
                    new ItemStack(Material.BOW, 1),
                    new ItemStack(Material.DIAMOND_SWORD,1),
                    new ItemStack(Material.MACE, 1),
                    new ItemStack(Material.SPECTRAL_ARROW, 64)
            );

            p.getInventory().clear();

            int i = 1;
            for (ItemStack item : chestItems) {
                p.getInventory().setItem(i, item);
                i++;
            }
        }
        plugin.isBattleStarting(toWorld.getName(), true);

        run2v2Clock(toWorld);
    }

    private void preparePlayers4v4(World toWorld) {

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

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        for (int i = 1; i <= 4; i++) {
            Team team = board.registerNewTeam(groupPrefix + i);
            team.setPrefix(groupPrefix + i + " ");
        }

        for (List<Location> ilha : bausNasIlhas) {
            for (int i = 0; i <= 7; i++) {
                Location loc = ilha.get(i);
                Block bloco = loc.getBlock();
                bloco.setType(Material.AIR);

                if (i <= 3) {
                    generateChest.generateChest(loc);
                }
            }
        }

        int teamNumber = 1;
        for (Player p : toWorld.getPlayers()) {
            Team team = board.getTeam(groupPrefix + teamNumber);
            if (team != null) {
                team.addEntry(p.getName());
                p.setScoreboard(board);
            }

            int x = 0;
            int y = 0;
            int z = 0;

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
                default:
                    plugin.getLogger().warning("Erro no sistema de distribuição de players!");
            }

            Location location = new Location(p.getWorld(), x, y, z);
            p.teleport(location);

            teamNumber++;
            if (teamNumber > 4) {
                teamNumber = 1;
            }
        }

        plugin.isBattleStarting(toWorld.getName(), true);

        run4v4Clock(toWorld);
    }

    private void startChaosMode(World toWorld) {

        List<String> listCaos = new ArrayList<>();
        listCaos.add("Blaze");
        listCaos.add("Wither_Skeleton");
        listCaos.add("Zoglin");
        listCaos.add("Magma_Cube");
        listCaos.add("Vex");
        listCaos.add("Pillager");
        listCaos.add("Breeze");

        Collections.shuffle(listCaos);

        EntityType mobToSpawn = EntityType.valueOf(listCaos.get(0).toUpperCase());

        for (Player p : toWorld.getPlayers()) {
            p.sendTitle("O tempo acabou!", "§4Começando o modo caos! com " + listCaos.get(0).replace("_", " "));
        }

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
    }

    private void controllerClock2v2(int carac0, int carac1, int carac2, World toWorld, BukkitRunnable bukkitRunnable) {
        Location soundLoc = new Location(toWorld, -32, 18, 28);

        if (carac0 == 0 && carac1 == 0 && carac2 <= 10 && carac2 != 0) {
            toWorld.playSound(soundLoc, Sound.BLOCK_NOTE_BLOCK_BASS, 10, 10);
        } else if (carac0 == 1 && carac1 == 0 && carac2 == 0) {
            generateBridge2v2(toWorld);
        } else if (carac0 == 0 && carac1 == 0 && carac2 == 0) {
            startChaosMode(toWorld);
            bukkitRunnable.cancel();
        } else {
            toWorld.playSound(soundLoc, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 10, 10);
        }
    }

    private void genNumClock2v2(int carac,int oldnum, Location pasteLocation1, Location pasteLocation2){

        String folder = "/numbers/num";
        String extension = ".schem";

        if (carac != oldnum) {
            File num = new File(plugin.getDataFolder() + folder + carac + extension);
            Clipboard clipboard = ManageSchematics.loadSchematic(num);
            ManageSchematics.pasteSchematic(clipboard, pasteLocation1, false);

            ManageSchematics.pasteSchematic(clipboard, pasteLocation2, true);
        }
    }
}
