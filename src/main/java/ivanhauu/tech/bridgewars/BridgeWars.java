package ivanhauu.tech.bridgewars;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import ivanhauu.tech.bridgewars.commands.*;
import ivanhauu.tech.bridgewars.listeners.*;
import ivanhauu.tech.bridgewars.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class BridgeWars extends JavaPlugin {

    private GetPlayerRank getPlayerRank;
    private WorldManager worldManager;
    private GenerateChest generateChest;
    private PlayerWinner playerWinner;
    private JoinSession joinSession;

    private PlayerDamage playerDamage;
    private PlayerDeath playerDeath;
    private PlayerQuitJoin playerQuitJoin;
    private PlayerChat playerChat;
    private PlayerMove playerMove;
    private StartBattle startBattle;

    private File battleFile;
    private FileConfiguration battleConfig;
    private File playerFile;
    private FileConfiguration playerConfig;

    private String spawnWorld;
    private String sectionsBaseFolder;
    private String subfolder2v2;
    private String subfolder4v4;
    private String battleWorldName2v2;
    private String battleWorldName4v4;
    private String schemBaseFolder;
    private String schemSub2v2;
    private String schemSub4v4;
    private String TemName2v2;
    private String TemName4v4;
    private String group2v2Prefix;
    private String group4v4Prefix;
    private int clockTime2v2;
    private boolean allow2v2;
    private boolean allow4v4;
    private int battleStartingTime;
    /*
    private boolean allowClock2v2;
    private boolean joinMessages;
    private boolean quitMessages;
    private boolean allowSpectateCommand;

     */
    private String serverPrefix;
    private List<String> ranksPrefix;
    private List<Double> playerF2v2;
    private List<Double> playerS2v2;
    private List<Double> playerT2v2;
    private List<Double> playerF4v4;
    private List<Double> playerS4v4;
    private List<Double> playerT4v4;

    @Override
    public void onEnable() {
        getLogger().info("O plugin BridgeWars foi iniciado!");

        saveResource("config.yml", false);
        saveDefaultConfig();
        loadConfigurations();
        createBattleConfig();
        createPlayerConfig();

        worldManager = new WorldManager(this);
        playerWinner = new PlayerWinner(this);
        generateChest = new GenerateChest(this);
        getPlayerRank = new GetPlayerRank(this);
        joinSession = new JoinSession(this);

        playerChat = new PlayerChat(getPlayerRank);
        playerDamage = new PlayerDamage(this, playerWinner);
        playerDeath = new PlayerDeath(this, playerWinner);
        playerMove = new PlayerMove(this);
        playerQuitJoin = new PlayerQuitJoin(this, playerWinner);
        startBattle = new StartBattle(this, generateChest);

        //Registradores de eventos
        getServer().getPluginManager().registerEvents(playerChat, this);
        getServer().getPluginManager().registerEvents(playerDamage, this);
        getServer().getPluginManager().registerEvents(playerDeath, this);
        getServer().getPluginManager().registerEvents(playerMove, this);
        getServer().getPluginManager().registerEvents(playerQuitJoin, this);
        getServer().getPluginManager().registerEvents(startBattle, this);

        // Executors dos comandos:
        getCommand("fight").setExecutor(new Fight(joinSession, this));
        getCommand("spawn").setExecutor(new Spawn(this));
        getCommand("ptop").setExecutor(new Ptop(this));
        getCommand("battles").setExecutor(new Battles(this));
        getCommand("spectate").setExecutor(new Spectate(this));

        //Carregando as sections
        Bukkit.getConsoleSender().sendMessage("§6[INICIANDO O CARREGAMENTO DAS SECTIONS]");

        for (int i = 0; i <= 9; i++) {
            String worldName = getDataFolder() + "/sections/2v2/battle_2v2_" + i;
            worldManager.loadWorld("2v2", i);
            is2v2BattleStart(worldName, false);
            isBattleStarting(worldName, false);
            protectWorld(Bukkit.getWorld(worldName));
            Bukkit.getConsoleSender().sendMessage("§2[2v2 SECTION "+ i +" CARREGADA!]");
        }

        for (int i = 0; i <= 9; i++) {
            String worldName = getDataFolder() + "/sections/4v4/battle_4v4_" + i;
            worldManager.loadWorld("4v4", i);
            is4v4BattleStart(worldName, false);
            isBattleStarting(worldName, false);
            protectWorld(Bukkit.getWorld(worldName));
            Bukkit.getConsoleSender().sendMessage("§2[4v4 SECTION "+ i +" CARREGADA!]");
        }
    }

    @Override
    public void onDisable() {

        //Descarregando as sections
        Bukkit.getConsoleSender().sendMessage("§6[INICIANDO O DESCARREGAMENTO DAS SECTIONS]");

        for (int i = 0; i <= 9; i++) {
            String worldName = getDataFolder() + "/sections/2v2/battle_2v2_" + i;
            worldManager.unloadWorld(worldName);
            is2v2BattleStart(worldName, false);
            isBattleStarting(worldName, false);
            Bukkit.getConsoleSender().sendMessage("§2[2v2 SECTION "+ i +" DESCARREGADA!]");
        }

        for (int i = 0; i <= 9; i++) {
            String worldName = getDataFolder() + "/sections/4v4/battle_4v4_" + i;
            worldManager.unloadWorld(worldName);
            is4v4BattleStart(worldName, false);
            isBattleStarting(worldName, false);
            Bukkit.getConsoleSender().sendMessage("§2[4v4 SECTION "+ i +" DESCARREGADA!]");
        }

        saveBattleConfig();
        savePlayerConfig();
        saveDefaultConfig();
        getLogger().info("O plugin BridgeWars foi encerrado!");
    }

    // Criar o arquivo de database battle.yml
    private void createBattleConfig() {
        battleFile = new File(getDataFolder(), "battle.yml");
        if (!battleFile.exists()) {
            battleFile.getParentFile().mkdirs();
            saveResource("battle.yml", false);
        }
        battleConfig = YamlConfiguration.loadConfiguration(battleFile);
    }

    // Criar o arquivo de database player.yml
    private void createPlayerConfig() {
        playerFile = new File(getDataFolder(), "player.yml");
        if (!playerFile.exists()) {
            playerFile.getParentFile().mkdirs();
            saveResource("player.yml", false);
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void saveBattleConfig() {
        try {
            battleConfig.save(battleFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao salvar o arquivo BattleConfig: " + e.getMessage());
        }
    }

    // Salvar o arquivo player.yml
    public void savePlayerConfig() {
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao salvar o arquivo PlayerConfig: " + e.getMessage());
        }
    }

    // Método para setar uma partida 2v2 como iniciada(true) terminada(false)
    public void is2v2BattleStart(String battleName, boolean is2v2BattleStarted) {
        battleConfig.set("worlds." + battleName + ".is2v2BattleStarted", is2v2BattleStarted);
        try {
            battleConfig.save(battleFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao modificar --> salvar o arquivo battleConfig para partida 2v2: " + e.getMessage());
        }
    }

    // Método para setar uma partida 4v4 como iniciada(true) terminada(false)
    public void is4v4BattleStart(String battleName, boolean is4v4BattleStarted) {
        battleConfig.set("worlds." + battleName + ".is4v4BattleStarted", is4v4BattleStarted);
        try {
            battleConfig.save(battleFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao modificar --> salvar o arquivo battleConfig para partida 4v4: " + e.getMessage());
        }
    }

    // Método que é definido para true quando começa a contagem da partida.
    public void isBattleStarting(String battleName, boolean isDamagePermitted) {
        battleConfig.set("worlds." + battleName + ".isBattleStarting", isDamagePermitted);
        try {
            battleConfig.save(battleFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao modificar --> salvar o arquivo battleConfig para partida 4v4: " + e.getMessage());
        }
    }

    //Getters dos arquivos battle.yml, player.yml

    public FileConfiguration getBattleConfig() {
        return this.battleConfig;
    }

    public FileConfiguration getPlayerConfig() {
        return this.playerConfig;
    }

    // Estes listeners devem ser transferidos posteriormente!

    //Método para isolar a visualização de jogadores em partida de jogadores no spawn
    public void updatePlayersVisualization(Player player) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() == otherPlayer.getWorld()) {
                player.showPlayer(this, otherPlayer);
                otherPlayer.showPlayer(this, player);
            } else {
                player.hidePlayer(this, otherPlayer);
                otherPlayer.hidePlayer(this, player);
            }
        }
    }

    public void setBlocksToAir(Location corner1, Location corner2) {
        // Obter o mundo das localizações
        World world = corner1.getWorld();

        // Determinar os limites da região
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        // Iterar através de todos os blocos na região e definir para ar (ID 0)
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    private void protectWorld(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            getLogger().severe("RegionManager é nulo no mundo: " + world.getName());
            return;
        }

        double worldBorderSize = world.getWorldBorder().getSize() / 2;
        double centerX = world.getWorldBorder().getCenter().getX();
        double centerZ = world.getWorldBorder().getCenter().getZ();
        BlockVector3 min = BlockVector3.at(centerX - worldBorderSize, 0, centerZ - worldBorderSize);
        BlockVector3 max = BlockVector3.at(centerX + worldBorderSize, world.getMaxHeight(), centerZ + worldBorderSize);

        ProtectedCuboidRegion globalRegion = new ProtectedCuboidRegion("bw-default", min, max);

        globalRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.OTHER_EXPLOSION, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.LAVA_FIRE, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.LIGHTNING, StateFlag.State.DENY);

        String worldName = world.getName();
        if (worldName.startsWith(getDataFolder() + "/sections/4v4/battle_4v4_")) {
            globalRegion.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
            globalRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
            globalRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
            globalRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
        } else if (worldName.startsWith(getDataFolder() + "/sections/2v2/battle_2v2_")) {
            globalRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
            globalRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
            globalRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.ALLOW);
        }

        if (regionManager.getRegion("bw-default") == null) {
            regionManager.addRegion(globalRegion);
        } else {
            regionManager.getRegion("bw-default").setFlags(globalRegion.getFlags());
        }

        try {
            regionManager.save();
            getLogger().info("Região 'bw-default' criada e atualizada com sucesso!.");
        } catch (Exception e) {
            getLogger().severe("Houve uma falha criação ou atualização da região: " + e.getMessage());
        }
    }
    public String getSpawnWorld() { return spawnWorld; }
    public String getSectionsBaseFolder() { return sectionsBaseFolder; }
    public String getSubfolder2v2() { return subfolder2v2; }
    public String getSubfolder4v4() { return subfolder4v4; }
    public String getBattleWorldName2v2(){ return battleWorldName2v2; }
    public String getBattleWorldName4v4(){ return battleWorldName4v4; }
    public String getGroup2v2Prefix() { return group2v2Prefix; }
    public String getGroup4v4Prefix() { return group4v4Prefix; }
    public int getClockTime2v2() { return clockTime2v2; }
    public boolean isAllow2v2() { return allow2v2; }
    public boolean isAllow4v4() { return allow4v4; }
    public String getSchemBaseFolder() { return schemBaseFolder; }
    public String getSchemSub2v2() { return schemSub2v2; }
    public String getSchemSub4v4() { return schemSub4v4; }
    public String getTemName2v2() { return TemName2v2; }
    public String getTemName4v4() { return TemName4v4; }
    public int getBattleStartingTime() { return battleStartingTime; }
    /*
    public boolean isAllowClock2v2() { return allowClock2v2; }
    public boolean isJoinMessages() { return joinMessages; }
    public boolean isQuitMessages() { return quitMessages; }

    public boolean isAllowSpectateCommand() { return allowSpectateCommand; }

     */
    public String getServerPrefix() { return serverPrefix; }
    public List<String> getRanksPrefix() { return ranksPrefix; }
    public List<Double> getFirst2v2() { return playerF2v2; }
    public List<Double> getSecond2v2() { return playerS2v2; }
    public List<Double> getThird2v2() { return playerT2v2; }
    public List<Double> getFirst4v4() { return playerF4v4; }
    public List<Double> getSecond4v4() { return playerS4v4; }
    public List<Double> getThird4v4() { return playerT4v4; }

    public void loadConfigurations() {
        FileConfiguration config = getConfig();

        spawnWorld = config.getString("geral.spawn-world", "world");
        sectionsBaseFolder = config.getString("battle-worlds.sections-base-folder", "/sections");
        subfolder2v2 = config.getString("battle-worlds.sections-2v2-subfolder", "/2v2");
        subfolder4v4 = config.getString("battle-worlds.sections-4v4-subfolder", "/4v4");
        battleWorldName2v2 = config.getString("battle-worlds.battle2v2-template-name", "/battle_2v2_");
        battleWorldName4v4 = config.getString("battle-worlds.battle4v4-template-name", "/battle_4v4_");
        group2v2Prefix = config.getString("battle-configs.2v2.group-prefix", "GROUP");
        group4v4Prefix = config.getString("battle-configs.4v4.group-prefix", "GROUP");
        clockTime2v2 = config.getInt("battle-configs.2v2.clock-time", 200);
        allow2v2 = config.getBoolean("battle-modes.allow-2v2", true);
        allow4v4 = config.getBoolean("battle-modes.allow-4v4", true);
        battleStartingTime = config.getInt("battle-configs.battle-configs", 5);

        schemBaseFolder = config.getString("schematics.schematics-base-folder", "/schematics");
        schemSub2v2 = config.getString("schematics.schematics-2v2-subfodler", "/2v2");
        schemSub4v4 = config.getString("schematics.schematics-4v4-subfolder", "/4v4");
        TemName2v2 = config.getString("schematics.schematic2v2-template-name", "/default_2v2.schem");
        TemName4v4 = config.getString("schematics.schematic4v4-template-name", "/");
        /*
        allowClock2v2 = config.getBoolean("battle-worlds.activate-clock-2v2", false);
        joinMessages = config.getBoolean("chat.join-messages", false);
        quitMessages = config.getBoolean("chat.quit-messages", false);
        allowSpectateCommand = config.getBoolean("battle-modes.allow-spectate-command", true);

         */
        ranksPrefix = config.getStringList("geral.ranks-prefix");
        serverPrefix = config.getString("chat.server-prefix", "§6[BW-INFO]");
        playerF2v2 = config.getDoubleList("podium.player-first-2v2");
        playerS2v2 = config.getDoubleList("podium.player-second-2v2");
        playerT2v2 = config.getDoubleList("podium.player-third-2v2");
        playerF4v4 = config.getDoubleList("podium.player-first-4v4");
        playerS4v4 = config.getDoubleList("podium.player-second-4v4");
        playerT4v4 = config.getDoubleList("podium.player-third-4v4");
    }

}