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
      case "votelist":
        this.townVoteListSubCommand(player);
        break;
      case "vote":
        this.townVoteSubCommand(player, args);
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

    int townVoteCount = townsData.getInt("towns." + townName + ".votecount");

    currentTownVotes.add((townVoteCount + 1) + ":invite:" + inviteeUUID.toString() + ":1:0:0:" + playerUUID.toString());
    townsData.set("towns." + townName + ".votecount", (townVoteCount+1));
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

    int townVoteCount = townsData.getInt("towns." + townName + ".votecount");

    currentTownVotes.add((townVoteCount + 1) + ":kick:" + kickeeUUID.toString() + ":1:0:0:" + playerUUID.toString());
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
      currentPlayer.sendMessage("A vote to kick a player named \"" + Bukkit.getPlayer(kickeeUUID).getName() + "\" has been added. Check /town vote.");
    }

  }

  private void townVoteListSubCommand(Player player) {
    if (!player.hasPermission("nationsrebuilt.town.votelist")){
      player.sendMessage("No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

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

    if(currentTownVotes.size() == 0){
      player.sendMessage("There are no votes available in your town.");
      return;
    }

    for(String vote: currentTownVotes){
      player.sendMessage(vote);
    }
  }

  private void townVoteSubCommand(Player player, String[] args) {
    if(args.length != 3){
      player.sendMessage("Must have 3 arguments. /town vote {vote-number} {yes-no-none}");
      return;
    }
    if (!player.hasPermission("nationsrebuilt.town.vote")){
      player.sendMessage("No permission to run this command.");
      return;
    }

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "players.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

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
    for(String stringUUID : vote[6].split("=")){
      if(UUID.fromString(stringUUID) == playerUUID){
        player.sendMessage("You have already voted on this vote.");
        return;
      }
    }

    int voteNumber = Integer.parseInt(vote[0]);
    String voteType = vote[1];
    UUID voteUUID = UUID.fromString(vote[2]);

    int voteForYes = Integer.parseInt(vote[3]);
    int voteForNone = Integer.parseInt(vote[4]);
    int voteForNo = Integer.parseInt(vote[5]);
    vote[6] += "=" + playerUUID.toString();


    String playerVote = args[2];
    if(playerVote.toLowerCase().equals("yes")){
      voteForYes += 1;
    } else if (playerVote.toLowerCase().equals("none")){
      voteForNone += 1;
    } else{
      voteForNo += 1;
    }

    String voteString = voteNumber + ":" + voteType + ":" + voteUUID.toString() + ":" + voteForYes  + ":" + voteForNone + ":" + voteForNo + ":" + vote[6];

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
        currentPlayer.sendMessage("A vote to " + voteType + " a player named \"" + Bukkit.getPlayer(voteUUID).getName() + "\" has succeeded. The vote has now been completed with a final vote of " + voteForYes + ":" + voteForNone + ":" + voteForNo + ".");
      }

      switch(voteType.toLowerCase()){
        case "invite":
          this.voteInviteConfirmed(townName, voteUUID, townsData);
          break;
        case "kick":
          this.voteKickConfirmed(townName, voteUUID, townsData, playersData);
          break;
        default:
          break;
      }
    } 
    else if (totalVotes != townPlayerCount && !majorityVote){
      //Vote incomplete
      currentTownVotes.add(voteString);
    } 
    else if (totalVotes == townPlayerCount && !majorityVote){
      //Vote fails permanently
      ArrayList<String> currentTownMembers = (ArrayList<String>) townsData.getStringList("towns." + townName + ".members");

      for(String uuidString : currentTownMembers){
        UUID uuid = UUID.fromString(uuidString);
        Player currentPlayer = Bukkit.getPlayer(uuid);
        currentPlayer.sendMessage("A vote to " + voteType + " a player named \"" + Bukkit.getPlayer(voteUUID).getName() + "\" has failed. The vote has now been removed with a final vote of " + voteForYes + ":" + voteForNone + ":" + voteForNo + ".");
      }
  
    }

    townsData.set("towns." + townName + ".votes", currentTownVotes);
    try {
      townsData.save(townsFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void voteInviteConfirmed(String townName, UUID uuid, FileConfiguration townsData){
    ArrayList<String> currentTownInvites = (ArrayList<String>) townsData.getStringList("towns." + townName + ".invites");

    currentTownInvites.add(uuid.toString());
    townsData.set("towns." + townName + ".invites", currentTownInvites);
    Player invitee = Bukkit.getPlayer(uuid);
    invitee.sendMessage("You have been invited to the town \"" + townName + "\".");
  }

  private void voteKickConfirmed(String townName, UUID uuid, FileConfiguration townsData, FileConfiguration playersData){

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
