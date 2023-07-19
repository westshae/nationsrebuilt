package com.altoya.nationsrebuilt.commands.town;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TownVoteConfirmations {
  public static void voteInviteConfirmed(String townName, UUID uuid, FileConfiguration townsData){
    ArrayList<String> currentTownInvites = (ArrayList<String>) townsData.getStringList("towns." + townName + ".invites");

    currentTownInvites.add(uuid.toString());
    townsData.set("towns." + townName + ".invites", currentTownInvites);
    Player invitee = Bukkit.getPlayer(uuid);
    invitee.sendMessage("You have been invited to the town \"" + townName + "\".");
  }

  public static void voteKickConfirmed(String townName, UUID uuid, FileConfiguration townsData, FileConfiguration playersData){

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");
    int currentMemberCount = townsData.getInt("towns." + townName + ".membercount");
    currentTownMembers.remove(uuid.toString());
    townsData.set("towns." + townName + ".members", currentTownMembers);
    townsData.set("towns." + townName + ".membercount", currentMemberCount-1);

    playersData.set("players." + uuid.toString() + ".town", null);

    Player kickee = Bukkit.getPlayer(uuid);
    kickee.sendMessage("You have been kicked from the town \"" + townName + "\".");
  }

}
