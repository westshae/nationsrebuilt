package com.altoya.nationsrebuilt;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.altoya.nationsrebuilt.commands.town.TownMain;
import com.altoya.nationsrebuilt.events.FirstJoinEvent;
public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        loadConfig();//Loads .yml

        //How to register commands
        this.getCommand("town").setExecutor(new TownMain());

        //How to register eventListeners
        this.getServer().getPluginManager().registerEvents(new FirstJoinEvent(), this);
    }

    public void loadConfig() {
        //Get potential config file
        File configFile = new File(getDataFolder(), "config.yml"); //TODO UPDATE FILENAME

        File townsFile = new File(getDataFolder(), "towns.yml");
        FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);
        try {
            townsData.save(townsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File playersFile = new File(getDataFolder(), "players.yml");
        FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);
        try {
            playersData.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File nationsFile = new File(getDataFolder(), "nations.yml");        
        FileConfiguration nationsData = YamlConfiguration.loadConfiguration(nationsFile);
        try {
            nationsData.save(nationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(!configFile.exists()){
            //Add new defaults, path might be items.0.modelID
            // getConfig().addDefault("pathInYml", "valueToSet");
        }



        //Load config
        getConfig().options().copyDefaults(true);
        saveConfig();
        
    }

    
}