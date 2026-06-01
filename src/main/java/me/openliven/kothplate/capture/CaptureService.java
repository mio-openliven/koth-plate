package me.openliven.kothplate.capture;

import me.openliven.kothplate.config.PluginSettings;
import me.openliven.kothplate.plate.PlateService;
import me.openliven.kothplate.schedule.ScheduleService;
import me.openliven.kothplate.service.EconomyDepositResult;
import me.openliven.kothplate.service.EconomyService;
import me.openliven.kothplate.service.MessageService;
import me.openliven.kothplate.service.VisualEffectService;
import me.openliven.kothplate.service.AfterRewardActionService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CaptureService {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final MessageService messages;
    private final VisualEffectService visuals;
    private final AfterRewardActionService afterRewardActions;
    private final PlateService plates;
    private final Clock clock;
    private final CaptureProgress progress = new CaptureProgress();

    private PluginSettings settings;
    private ScheduleService schedule;
    private UUID activePlayerId;
    private BukkitTask activeTask;
    private final Map<UUID, Long> lastCancelNoticeAt = new HashMap<>();

    public CaptureService(
            JavaPlugin plugin,
            EconomyService economy,
            MessageService messages,
            VisualEffectService visuals,
            AfterRewardActionService afterRewardActions,
            PlateService plates,
            Clock clock
    ) {
        this.plugin = plugin;
        this.economy = economy;
        this.messages = messages;
        this.visuals = visuals;
        this.afterRewardActions = afterRewardActions;
        this.plates = plates;
        this.clock = clock;
    }

    public void updateSettings(PluginSettings settings) {
        this.settings = settings;
        this.schedule = new ScheduleService(settings.schedule(), clock);
        cancelActiveCapture(false);
    }

    public void tryStart(Player player) {
        if (settings == null || settings.platePosition() == null || activePlayerId != null) {
            return;
        }

        if (!CaptureEligibility.canCapture(player.getGameMode(), player.isDead())) {
            return;
        }

        if (!schedule.isActiveNow()) {
            messages.action(player, "actionbar-inactive");
            return;
        }

        if (!plates.isStandingOnPlate(player, settings.platePosition())) {
            return;
        }

        activePlayerId = player.getUniqueId();
        progress.reset(settings.captureSeconds());
        activeTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> tickActiveCapture(activePlayerId), 0L, 20L);
    }

    public void cancelIfActive(UUID playerId, boolean notify) {
        if (activePlayerId != null && activePlayerId.equals(playerId)) {
            cancelActiveCapture(notify);
        }
    }

    public void cancelIfActive(Player player, boolean notify) {
        if (activePlayerId != null && activePlayerId.equals(player.getUniqueId())) {
            cancelActiveCapture(notify, player);
        }
    }

    public void cancelActiveCapture(boolean notify) {
        cancelActiveCapture(notify, null);
    }

    private void cancelActiveCapture(boolean notify, Player knownPlayer) {
        UUID cancelledPlayerId = activePlayerId;
        if (activeTask != null) {
            activeTask.cancel();
            activeTask = null;
        }
        activePlayerId = null;
        progress.reset(settings == null ? 20 : settings.captureSeconds());

        if (cancelledPlayerId != null) {
            Player player = knownPlayer == null ? Bukkit.getPlayer(cancelledPlayerId) : knownPlayer;
            if (player != null && player.isOnline()) {
                player.sendActionBar(Component.empty());
            }
            if (notify && player != null && player.isOnline() && canSendCancelNotice(cancelledPlayerId)) {
                visuals.play(player, settings.visuals().fail());
                messages.send(player, "capture-cancelled");
            }
        }
    }

    private boolean canSendCancelNotice(UUID playerId) {
        long now = System.currentTimeMillis();
        long lastNoticeAt = lastCancelNoticeAt.getOrDefault(playerId, 0L);
        if (now - lastNoticeAt < 3000L) {
            return false;
        }
        lastCancelNoticeAt.put(playerId, now);
        return true;
    }

    private void tickActiveCapture(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            cancelActiveCapture(false);
            return;
        }

        if (!canContinueCapture(player)) {
            cancelActiveCapture(true);
            return;
        }

        CaptureProgress.CaptureTick tick = progress.tick(playerId, settings.captureSeconds(), settings.visualHoldBufferSeconds());
        messages.action(player, "actionbar-timer", "%time%", Integer.toString(tick.displayedSeconds()));

        if (tick.completed()) {
            EconomyDepositResult deposit = economy.deposit(player, settings.rewardAmount());
            if (!deposit.successful()) {
                plugin.getLogger().warning("Failed to pay KoTH reward to " + player.getName() + ": " + deposit.errorMessage());
                messages.send(player, "reward-failed");
                cancelActiveCapture(false, player);
                return;
            }
            visuals.play(player, settings.visuals().success());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            messages.send(player, "reward-given",
                    "%time%", Integer.toString(settings.captureSeconds()),
                    "%reward%", formatAmount(settings.rewardAmount()));
            if (settings.afterReward().enabled()) {
                afterRewardActions.apply(player, settings.afterReward(), settings.platePosition());
                cancelActiveCapture(false, player);
            } else {
                progress.reset(settings.captureSeconds());
            }
        }
    }

    private boolean canContinueCapture(Player player) {
        if (!CaptureEligibility.canCapture(player.getGameMode(), player.isDead())) {
            return false;
        }
        if (!schedule.isActiveNow()) {
            return false;
        }
        return plates.isStandingOnPlate(player, settings.platePosition());
    }

    private String formatAmount(double amount) {
        if (amount == Math.rint(amount)) {
            return Long.toString(Math.round(amount));
        }
        return Double.toString(amount);
    }
}
