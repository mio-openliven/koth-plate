package me.openliven.kothplate.service;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public final class VaultEconomyService implements EconomyService {
    private final Economy economy;

    public VaultEconomyService(Economy economy) {
        this.economy = economy;
    }

    @Override
    public void deposit(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }
}
