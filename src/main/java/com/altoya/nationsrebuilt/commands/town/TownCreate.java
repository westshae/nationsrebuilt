package com.altoya.nationsrebuilt.commands.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.altoya.nationsrebuilt.util.PlayerMessage;

public class TownCreate {
  public static void townCreateSubCommand(Player player, String[] args) {
    if(args.length != 2){
      PlayerMessage.error(player, "Must have 2 arguments. /town create {town-name}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.create")){
      PlayerMessage.error(player, "No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID playerUUID = player.getUniqueId();

    Boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(hasTown){
      PlayerMessage.error(player, "You already own a town.");
      return;
    }

    //Town name must be 6-14 characters, no whitespaces.
    String townName = args[1].toString();
    Pattern pattern = Pattern.compile("(^\\w{5,15}\\S$)");
    if(!pattern.matcher(townName).matches()){
      PlayerMessage.error(player, "Town names must be 6-14 characters, only letters and underscores, with no whitespace.");
      return;
    }

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    //Set player/town data files with new values.
    if(townsData.contains("towns." + townName)){
      PlayerMessage.error(player,"This town name already exists. Choose another one.");
      return;
    }

    ArrayList<String> memberList = new ArrayList<String>();
    memberList.add(playerUUID.toString());

    townsData.set("towns." + townName + ".name", townName);
    townsData.set("towns." + townName + ".members", memberList);
    townsData.set("towns." + townName + ".votes", new ArrayList<String>());
    townsData.set("towns." + townName + ".invites", new ArrayList<String>());
    townsData.set("towns." + townName + ".votecount", 0);
    townsData.set("towns." + townName + ".membercount", 1);
    playersData.set("players." + playerUUID.toString() + ".town.has", true);
    playersData.set("players."+playerUUID.toString()+".town.name", townName);

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
    PlayerMessage.success(player, "Your town, \"" + townName + "\" has been created.");
  }

}
