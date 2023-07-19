package com.altoya.nationsrebuilt.commands.town;

import org.bukkit.entity.Player;

public class TownHelp {
  public static void helpSubCommand(Player player, String[] args) {
    if (!player.hasPermission("nationsrebuilt.town.help")) return;

    player.sendMessage("Help");
  }

}
