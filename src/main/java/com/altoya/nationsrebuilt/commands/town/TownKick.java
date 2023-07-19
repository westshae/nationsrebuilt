package com.altoya.nationsrebuilt.commands.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TownKick {
  public static void townKickSubCommand(Player player, String[] args) {
    if(args.length != 2){
      player.sendMessage("Must have 2 arguments. /town kick {player-name}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.kick")){
      player.sendMessage("No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID kickeeUUID = Bukkit.getPlayer(args[1].toString()).getUniqueId();
    
    boolean kickeeExists = playersData.contains("players." + kickeeUUID.toString());

    if(!kickeeExists) {
      player.sendMessage("The user you are trying to kick doesn't exist.");
      return;
    }


    UUID playerUUID = player.getUniqueId();

    boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(!hasTown){
      player.sendMessage("You have no town.");
      return;
    }
    String townName = playersData.getString("players." + playerUUID.toString() + ".town.name");

    String kickeeTownName = playersData.getString("players." + kickeeUUID.toString() + ".town.name");
    if(!kickeeTownName.equals(townName)){
      player.sendMessage("This user isn't a member of your town.");
      return;
    }

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    ArrayList<String> currentTownVotes = (ArrayList<String>) townsData.getStringList("towns." + townName + ".votes");

    int townVoteCount = townsData.getInt("towns." + townName + ".votecount");

    currentTownVotes.add((townVoteCount + 1) + ":kick:" + kickeeUUID.toString() + ":0:0:0:");
    townsData.set("towns." + townName + ".votes", currentTownVotes);
    townsData.set("towns." + townName + ".votecount", (townVoteCount + 1));
    
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

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

    for(String uuidString : currentTownMembers){
      UUID uuid = UUID.fromString(uuidString);
      Player currentPlayer = Bukkit.getPlayer(uuid);
      currentPlayer.sendMessage("A vote to kick a player named \"" + Bukkit.getPlayer(kickeeUUID).getName() + "\" has been added. Check /town votelist.");
    }

  }

}
