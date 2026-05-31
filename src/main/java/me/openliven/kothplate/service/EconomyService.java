package me.openliven.kothplate.service;

import org.bukkit.entity.Player;

public interface EconomyService {
    EconomyDepositResult deposit(Player player, double amount);
}
