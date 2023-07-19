package com.altoya.nationsrebuilt.commands.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.altoya.nationsrebuilt.util.PlayerMessage;

public class TownLeave {
  public static void townLeaveSubCommand(Player player, String[] args) {
    if (!player.hasPermission("nationsrebuilt.town.leave")){
      PlayerMessage.error(player, "No permission to run this command.");
      return;
    }
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID uuid = player.getUniqueId();

    boolean hasTown = playersData.getBoolean("players." + uuid.toString() + ".town.has");
    if(!hasTown){
      PlayerMessage.error(player, "You aren't in a town.");
      return;
    }

    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    String townName = playersData.getString("players." + uuid.toString() + ".town.name");

    playersData.set("players." + uuid.toString() + ".town", null);

    ArrayList<String> memberList = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");
    int memberCount = townsData.getInt("towns." + townName + ".membercount");

    if(memberCount == 1){
      townsData.set("towns." + townName , null);
      PlayerMessage.broadcast(townName + " has been disbanded.");
    } else {
      memberList.remove(uuid.toString());
      memberCount -= 1;
  
      townsData.set("towns." + townName + ".members", memberList);
      townsData.set("towns." + townName + ".membercount", memberCount);
  
      if(memberCount != 0){
        for(String uuidString : memberList){
          UUID currentUUID = UUID.fromString(uuidString);
          Player currentPlayer = Bukkit.getPlayer(currentUUID);
          PlayerMessage.error(currentPlayer, "The player " + player.getName() + " has left the town.");
        }  
      }
    }

    try {
      townsData.save(townsFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      playersData.save(playersFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
