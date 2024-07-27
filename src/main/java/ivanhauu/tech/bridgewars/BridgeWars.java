package ivanhauu.tech.bridgewars;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

public final class BridgeWars extends JavaPlugin {

    private GetPlayerRank getPlayerRank;
    private WorldManager worldManager;
    private GenerateChest generateChest;
    private PlayerWinner playerWinner;
    private JoinSession joinSession;

    private PlayerAdvancement playerAdvancement;
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

    @Override
    public void onEnable() {
        getLogger().info("O plugin BridgeWars foi iniciado!");

        worldManager = new WorldManager(this);
        playerWinner = new PlayerWinner(this);
        generateChest = new GenerateChest(this);
        getPlayerRank = new GetPlayerRank(this);
        joinSession = new JoinSession(this);

        playerAdvancement = new PlayerAdvancement();
        playerChat = new PlayerChat(getPlayerRank);
        playerDamage = new PlayerDamage(this, playerWinner);
        playerDeath = new PlayerDeath(this, playerWinner);
        playerMove = new PlayerMove(this);
        playerQuitJoin = new PlayerQuitJoin(this);
        startBattle = new StartBattle(this, generateChest);

        //Registradores de eventos
        getServer().getPluginManager().registerEvents(playerAdvancement, this);
        getServer().getPluginManager().registerEvents(playerChat, this);
        getServer().getPluginManager().registerEvents(playerDamage, this);
        getServer().getPluginManager().registerEvents(playerDeath, this);
        getServer().getPluginManager().registerEvents(playerMove, this);
        getServer().getPluginManager().registerEvents(playerQuitJoin, this);
        getServer().getPluginManager().registerEvents(startBattle, this);

        createBattleConfig();
        createPlayerConfig();

        // Executors dos comandos:
        getCommand("fight").setExecutor(new Fight(joinSession));
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("ptop").setExecutor(new Ptop(this));

        //Carregando as sections
        Bukkit.getConsoleSender().sendMessage("§6[INICIANDO O CARREGAMENTO DAS SECTIONS]");

        protectWorld(Bukkit.getWorld("world"));

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
        getLogger().info("O plugin BridgeWars foi encerrado!");
    }

    public PlayerWinner getPlayerWinner() {
        return playerWinner;
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

    //Getters dos arquivos battle.yml e player.yml
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

        if (regionManager != null) {
            BlockVector3 min = BlockVector3.at(world.getWorldBorder().getCenter().getX() - world.getWorldBorder().getSize() / 2, 0, world.getWorldBorder().getCenter().getZ() - world.getWorldBorder().getSize() / 2);
            BlockVector3 max = BlockVector3.at(world.getWorldBorder().getCenter().getX() + world.getWorldBorder().getSize() / 2, world.getMaxHeight(), world.getWorldBorder().getCenter().getZ() + world.getWorldBorder().getSize() / 2);
            ProtectedRegion region = new ProtectedCuboidRegion("no-build", min, max);

            region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
            region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
            region.setFlag(Flags.TNT, StateFlag.State.DENY);
            region.setFlag(Flags.OTHER_EXPLOSION, StateFlag.State.DENY);
            region.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
            region.setFlag(Flags.LAVA_FIRE, StateFlag.State.DENY);
            region.setFlag(Flags.LIGHTNING, StateFlag.State.DENY);
            region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
            region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
            if (world.getName().startsWith(getDataFolder() + "/sections/4v4/battle_4v4_")) {
                region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                region.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
            }

            regionManager.addRegion(region);
        }
    }

}