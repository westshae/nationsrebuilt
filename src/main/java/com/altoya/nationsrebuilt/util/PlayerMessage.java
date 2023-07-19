package com.altoya.nationsrebuilt.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerMessage {
  public static void error(Player player, String message){
    player.sendMessage("" + ChatColor.BOLD + ChatColor.RED + message);
  }
  public static void success(Player player, String message){
    player.sendMessage("" + ChatColor.GREEN + message);
  }
  public static void broadcast(String message){
    Bukkit.broadcastMessage("" + ChatColor.BOLD + ChatColor.GREEN + "NationsRebuilt: " + ChatColor.RESET + ChatColor.GREEN + message);
  }
}
