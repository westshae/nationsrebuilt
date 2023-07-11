package com.altoya.nationsrebuilt.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TownCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("town")) return true;
    if (!sender.hasPermission("nationsrebuilt.town")) return true;
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;


    switch (args[0].toLowerCase()) {
      case "create":
        this.townCreateSubCommand(player, args);
        break;
      case "invite":
        this.townInviteSubCommand(player, args);
        break;
      case "help":
        this.helpSubCommand(player, args);
        break;
      case "kick":
        this.townKickSubCommand(player,args);
        break;
      default:
        return false;
    }

    return true;
  }

  private void helpSubCommand(Player player, String[] args) {
    if (!player.hasPermission("nationsrebuilt.town.help")) return;

    player.sendMessage("Help");
  }

  private void townCreateSubCommand(Player player, String[] args) {
    if(args.length != 2){
      player.sendMessage("Must have 2 arguments. /town create {town-name}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.create")){
      player.sendMessage("No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID playerUUID = player.getUniqueId();

    Boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(hasTown){
      player.sendMessage("You already own a town.");
      return;
    }

    //Town name must be 6-14 characters, no whitespaces.
    String townName = args[1].toString();
    Pattern pattern = Pattern.compile("(^\\w{5,15}\\S$)");
    if(!pattern.matcher(townName).matches()){
      player.sendMessage("Town names must be 6-14 characters, only letters and underscores, with no whitespace.");
      return;
    }

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    //Set player/town data files with new values.
    if(townsData.contains("towns." + townName)){
      player.sendMessage("This town name already exists. Choose another one.");
      return;
    }

    ArrayList<String> memberList = new ArrayList<String>();
    memberList.add(playerUUID.toString());

    townsData.set("towns." + townName + ".name", townName);
    townsData.set("towns." + townName + ".members", memberList);
    townsData.set("towns." + townName + ".votes", new ArrayList<String>());
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
    player.sendMessage("Your town, \"" + townName + "\" has been created.");
  }

  private void townInviteSubCommand(Player player, String[] args) {
    if(args.length != 2){
      player.sendMessage("Must have 2 arguments. /town invite {player-name}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.invite")){
      player.sendMessage("No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID inviteeUUID = Bukkit.getPlayer(args[1].toString()).getUniqueId();
    
    boolean inviteeExists = playersData.contains("players." + inviteeUUID.toString());

    if(!inviteeExists) {
      player.sendMessage("The user you are trying to invite doesn't exist.");
      return;
    }

    UUID playerUUID = player.getUniqueId();

    boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(!hasTown){
      player.sendMessage("You have no town.");
      return;
    }
    String townName = playersData.getString("players." + playerUUID.toString() + ".town.name");

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    ArrayList<String> currentTownVotes = (ArrayList<String>) townsData.getStringList("towns." + townName + ".votes");

    currentTownVotes.add("invite:" + inviteeUUID.toString());
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

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

    for(String uuidString : currentTownMembers){
      UUID uuid = UUID.fromString(uuidString);
      Player currentPlayer = Bukkit.getPlayer(uuid);
      currentPlayer.sendMessage("A vote to invite a new player named \"" + Bukkit.getPlayer(inviteeUUID).getName() + "\" has been added. Check /town vote.");
    }

  }
  
  
  private void townKickSubCommand(Player player, String[] args) {
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

    currentTownVotes.add("kick:" + kickeeUUID.toString());
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

    ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

    for(String uuidString : currentTownMembers){
      UUID uuid = UUID.fromString(uuidString);
      Player currentPlayer = Bukkit.getPlayer(uuid);
      currentPlayer.sendMessage("A vote to kick a player named \"" + Bukkit.getPlayer(kickeeUUID).getName() + "\" has been added. Check /town vote.");
    }

  }



}
