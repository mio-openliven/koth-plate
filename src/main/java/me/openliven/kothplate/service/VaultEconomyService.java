package me.openliven.kothplate.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public final class VaultEconomyService implements EconomyService {
    private final Economy economy;

    public VaultEconomyService(Economy economy) {
        this.economy = economy;
    }

    @Override
    public EconomyDepositResult deposit(Player player, double amount) {
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            return EconomyDepositResult.success();
        }
        return EconomyDepositResult.failure(response.errorMessage);
    }
}
