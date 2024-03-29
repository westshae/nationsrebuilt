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

public class TownVote {
  public static void townVoteSubCommand(Player player, String[] args) {
    if(args.length != 3){
      PlayerMessage.error(player, "Must have 3 arguments. /town vote {vote-number} {yes-no-none}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.vote")){
      PlayerMessage.error(player, "No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID playerUUID = player.getUniqueId();

    boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(!hasTown){
      PlayerMessage.error(player, "You have no town.");
      return;
    }

    String townName = playersData.getString("players." + playerUUID.toString() + ".town.name");

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    ArrayList<String> currentTownVotes = (ArrayList<String>) townsData.getStringList("towns." + townName + ".votes");
    String[] vote = null;
    String voteForRemoval = null;
    for(String currentvote : currentTownVotes){
      String[] splitVote = currentvote.split(":");
      int number = Integer.parseInt(splitVote[0]);
      //if vote numbers match argument vote number
      if(number != Integer.parseInt(args[1])) continue;
      vote = splitVote;
      voteForRemoval = currentvote;
    }
    if(vote == null || voteForRemoval == null) return;
    currentTownVotes.remove(voteForRemoval);
    if(vote.length == 7){
      for(String stringUUID : vote[6].split("=")){
        if(UUID.fromString(stringUUID) == playerUUID){
          PlayerMessage.error(player, "You have already voted on this vote.");
          return;
        }
      }  
    }

    int voteNumber = Integer.parseInt(vote[0]);
    String voteType = vote[1];
    UUID voteUUID = UUID.fromString(vote[2]);

    int voteForYes = Integer.parseInt(vote[3]);
    int voteForNone = Integer.parseInt(vote[4]);
    int voteForNo = Integer.parseInt(vote[5]);
    String votePlayers = "";
    if(vote.length != 7){
      votePlayers += playerUUID.toString();
    } else {
      vote[6] += "=" + playerUUID.toString();
    }


    String playerVote = args[2];
    if(playerVote.toLowerCase().equals("yes")){
      voteForYes += 1;
    } else if (playerVote.toLowerCase().equals("none")){
      voteForNone += 1;
    } else{
      voteForNo += 1;
    }

    String voteString = voteNumber + ":" + voteType + ":" + voteUUID.toString() + ":" + voteForYes  + ":" + voteForNone + ":" + voteForNo + ":" + votePlayers;

    int townPlayerCount = townsData.getInt("towns." + townName + "membercount");
    int totalVotes = voteForYes + voteForNone + voteForNo;
    boolean majorityVote = voteForYes >=(townPlayerCount/2 + 1);

    if(majorityVote){
      //Vote passes
      //add new vote, announce vote, complete action
      ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

      for(String uuidString : currentTownMembers){
        UUID uuid = UUID.fromString(uuidString);
        Player currentPlayer = Bukkit.getPlayer(uuid);
        PlayerMessage.success(currentPlayer, "A vote to " + voteType + " a player named \"" + Bukkit.getPlayer(voteUUID).getName() + "\" has succeeded. The vote has now been completed with a final vote of " + voteForYes + ":" + voteForNone + ":" + voteForNo + ".");
      }

      switch(voteType.toLowerCase()){
        case "invite":
          TownVoteConfirmations.voteInviteConfirmed(townName, voteUUID, townsData);
          break;
        case "kick":
          TownVoteConfirmations.voteKickConfirmed(townName, voteUUID, townsData, playersData);
          break;
        default:
          break;
      }
    } 
    else if (totalVotes != townPlayerCount && !majorityVote){
      //Vote incomplete
      currentTownVotes.add(voteString);
      ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

      if(voteType.toLowerCase().equals("yes")){
        for(String uuidString : currentTownMembers){
          UUID uuid = UUID.fromString(uuidString);
          Player currentPlayer = Bukkit.getPlayer(uuid);
          PlayerMessage.success(currentPlayer, currentPlayer.getName() + " has voted " + voteType + " for vote number " + voteNumber);
        }
  
      } else if (voteType.toLowerCase().equals("none")){
        for(String uuidString : currentTownMembers){
          UUID uuid = UUID.fromString(uuidString);
          Player currentPlayer = Bukkit.getPlayer(uuid);
          PlayerMessage.success(currentPlayer, currentPlayer.getName() + " has voted " + voteType + " for vote number " + voteNumber);
        }
  
      } else if (voteType.toLowerCase().equals("no")){
        for(String uuidString : currentTownMembers){
          UUID uuid = UUID.fromString(uuidString);
          Player currentPlayer = Bukkit.getPlayer(uuid);
          PlayerMessage.error(currentPlayer, currentPlayer.getName() + " has voted " + voteType + " for vote number " + voteNumber);
        }
  
      }
    } 
    else if (totalVotes == townPlayerCount && !majorityVote){
      //Vote fails permanently
      ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

      for(String uuidString : currentTownMembers){
        UUID uuid = UUID.fromString(uuidString);
        Player currentPlayer = Bukkit.getPlayer(uuid);
        PlayerMessage.error(currentPlayer, "A vote to " + voteType + " a player named \"" + Bukkit.getPlayer(voteUUID).getName() + "\" has failed. The vote has now been removed with a final vote of " + voteForYes + ":" + voteForNone + ":" + voteForNo + ".");
      }
  
    }

    townsData.set("towns." + townName + ".votes", currentTownVotes);
    try {
      townsData.save(townsFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
