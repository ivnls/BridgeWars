package ivanhauu.tech.battlesessions;

import ivanhauu.tech.battlesessions.commands.*;
import ivanhauu.tech.battlesessions.listeners.*;
import ivanhauu.tech.battlesessions.utils.GetPlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class BattleSessions extends JavaPlugin {

    private GetPlayerRank getPlayerRank;
    private WorldManager worldManager;
    private GenerateChest generateChest;

    private denyAllowBuild denyAllowBuild;
    private onAdvancement onAdvancement;
    private onPlayerDamage onPlayerDamage;
    private onPlayerKill onPlayerKill;
    private onPlayerQuitJoin onPlayerQuitJoin;
    private playerChat playerChat;
    private playerMove playerMove;
    private startBattle startBattle;

    private File battleFile;
    private FileConfiguration battleConfig;
    private File playerFile;
    private FileConfiguration playerConfig;

    @Override
    public void onEnable() {
        getLogger().info("O plugin BattleSessions foi iniciado!");
        worldManager = new WorldManager(this);
        generateChest = new GenerateChest(this);
        startBattle = new startBattle(this, generateChest);
        onPlayerKill = new onPlayerKill(worldManager, this);
        denyAllowBuild = new denyAllowBuild(this);
        onPlayerQuitJoin = new onPlayerQuitJoin(this, worldManager, getPlayerRank);
        onPlayerDamage = new onPlayerDamage(this);
        getPlayerRank = new GetPlayerRank(this);
        playerChat = new playerChat(this, getPlayerRank);
        onAdvancement = new onAdvancement();
        playerMove = new playerMove(this);

        createBattleConfig();
        createPlayerConfig();

        getServer().getPluginManager().registerEvents(onPlayerKill, this);
        getServer().getPluginManager().registerEvents(startBattle, this);
        getServer().getPluginManager().registerEvents(denyAllowBuild, this);
        getServer().getPluginManager().registerEvents(onPlayerQuitJoin, this);
        getServer().getPluginManager().registerEvents(onPlayerDamage, this);
        getServer().getPluginManager().registerEvents(playerChat, this);
        getServer().getPluginManager().registerEvents(onAdvancement, this);
        getServer().getPluginManager().registerEvents(playerMove, this);

        // Executors dos comandos:
        getCommand("fight").setExecutor(new Fight(worldManager, this));
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("ptop").setExecutor(new Ptop(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("O plugin BattleSessions foi encerrado!");
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

    // Salvar o arquivo player.yml
    public void savePlayerConfig() {
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao salvar o arquivo PlayerConfig: " + e.getMessage());
        }
    }

    // Método para setar uma partida 8v8 como iniciada(true) terminada(false)
    public void is8v8BattleStart(String battleName, boolean is8v8BattleStarted) {
        battleConfig.set("worlds." + battleName + ".is8v8BattleStarted", is8v8BattleStarted);
        try {
            battleConfig.save(battleFile);
        } catch (IOException e) {
            getLogger().warning("Erro ao modificar --> salvar o arquivo battleConfig para partida 8v8: " + e.getMessage());
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

}