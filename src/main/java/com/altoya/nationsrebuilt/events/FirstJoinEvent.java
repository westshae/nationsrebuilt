package com.altoya.nationsrebuilt.events;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinEvent implements Listener{

  
  @EventHandler // Detects player inventory interaction
  public void playerFirstJoinEvent(PlayerJoinEvent event){
    Player player = event.getPlayer();
    UUID playerUUID = player.getUniqueId();

    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);
    
    boolean playerExists = playersData.contains("players." + playerUUID.toString());
    if(playerExists) return;
    playersData.set("players." + playerUUID.toString() + ".name", player.getName());
    try {
      playersData.save(playersFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

