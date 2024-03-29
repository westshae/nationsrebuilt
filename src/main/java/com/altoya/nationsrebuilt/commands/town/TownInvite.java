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

public class TownInvite {
  public static void townInviteSubCommand(Player player, String[] args) {
    if(args.length != 2){
      PlayerMessage.error(player, "Must have 2 arguments. /town invite {player-name}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.invite")){
      PlayerMessage.error(player, "No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID inviteeUUID = Bukkit.getPlayer(args[1].toString()).getUniqueId();
    
    boolean inviteeExists = playersData.contains("players." + inviteeUUID.toString());

    if(!inviteeExists) {
      PlayerMessage.error(player, "The user you are trying to invite doesn't exist.");
      return;
    }

    UUID playerUUID = player.getUniqueId();

    boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(!hasTown){
      PlayerMessage.error(player,"You have no town.");
      return;
    }
    String townName = playersData.getString("players." + playerUUID.toString() + ".town.name");

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");
    for(String currentUUID : currentTownMembers){
      if(currentUUID.equals(inviteeUUID.toString())){
        PlayerMessage.error(player,"This user is already in this town.");
        return;
      }
    }

    ArrayList<String> currentInvites = (ArrayList<String>) townsData.getStringList("towns." + townName + ".invites");
    for(String currentUUID : currentInvites){
      if(currentUUID.equals(inviteeUUID.toString())){
        PlayerMessage.error(player,"This player already has an invite.");
        return;
      }
    }


    ArrayList<String> currentTownVotes = (ArrayList<String>) townsData.getStringList("towns." + townName + ".votes");

    int townVoteCount = townsData.getInt("towns." + townName + ".votecount");

    currentTownVotes.add((townVoteCount+1)+ ":invite:" + inviteeUUID.toString() + ":0:0:0:");
    townsData.set("towns." + townName + ".votecount", townVoteCount);
    townsData.set("towns." + townName + ".votes", currentTownVotes);
    
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

    for(String uuidString : currentTownMembers){
      UUID uuid = UUID.fromString(uuidString);
      Player currentPlayer = Bukkit.getPlayer(uuid);
      PlayerMessage.success(currentPlayer, "A vote to invite a new player named \"" + Bukkit.getPlayer(inviteeUUID).getName() + "\" has been added. Check /town votelist.");
    }
  }

}
