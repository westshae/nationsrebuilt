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

public class TownJoin {
  public static void townJoinSubCommand(Player player, String[] args) {
    if (!player.hasPermission("nationsrebuilt.town.join")){
      PlayerMessage.error(player, "No permission to run this command.");
      return;
    }
    if(args.length != 2){
      PlayerMessage.error(player, "Must have 2 arguments. /town join {town-name}");
      return;
    }
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID uuid = player.getUniqueId();

    boolean hasTown = playersData.getBoolean("players." + uuid.toString() + ".town.has");
    if(hasTown){
      PlayerMessage.error(player, "You already are in a town.");
      return;
    }

    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    String townName = args[1].toLowerCase();

    if(!townsData.contains("towns." + townName)){
      PlayerMessage.error(player, "The town \"" + townName + "\" doesn't exist.");
      return;
    }

    ArrayList<String> currentInvites = (ArrayList<String>) townsData.getStringList("towns." + townName + ".invites");

    if(!currentInvites.contains(uuid.toString())){
      PlayerMessage.error(player, "You don't have an invite for the town \"" + townName + "\"");
      return;
    }

    playersData.set("players." + uuid.toString() + ".town.has", true);
    playersData.set("players." + uuid.toString() + ".town.name", townName);

    ArrayList<String> inviteList = (ArrayList<String>) townsData.getStringList("towns." + townName + ".invites");
    ArrayList<String> memberList = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");
    int memberCount = townsData.getInt("towns." + townName + ".membercount");

    inviteList.remove(uuid.toString());
    memberList.add(uuid.toString());
    memberCount += 1;

    townsData.set("towns." + townName + ".invites", inviteList);
    townsData.set("towns." + townName + ".members", memberList);
    townsData.set("towns." + townName + ".membercount", memberCount);

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

    for(String uuidString : currentTownMembers){
      UUID currentUUID = UUID.fromString(uuidString);
      Player currentPlayer = Bukkit.getPlayer(currentUUID);
      PlayerMessage.success(currentPlayer, "The player " + player.getName() + " has joined the town.");
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
