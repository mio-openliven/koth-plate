# KingOfTheHill

Small Paper 1.20.1 plugin for a simple King of the Hill pressure plate.

An admin stands on a block and runs `/koth set`. The plugin places a heavy weighted pressure plate at the player's feet and saves it to `config.yml`. When a player holds the plate for 20 seconds without leaving, dying, quitting, or switching to Creative/Spectator, they receive 25 coins through Vault economy, for example EssentialsX Economy. If they keep standing on the plate, the timer starts again.

## Requirements

- Paper 1.20.1
- Java 17 or newer on the server
- Vault
- An economy provider connected to Vault, such as EssentialsX Economy

## Commands

- `/koth set` - place the capture plate where you stand
- `/koth reload` - reload `config.yml`, `ru.yml`, and `en.yml`
- `/koth info` - show current point settings and diagnostics

Alias: `/kothplate`

Permission: `koth.admin` (default: op)

## Build

```powershell
.\mvnw.cmd package
```

The plugin jar will be created in `target/king-of-the-hill-1.0.2.jar`.

## Configuration

```yaml
settings:
  language: ru
  capture-time: 20
  reward-amount: 25.0
  respect-cancelled-physical-events: false
  schedule:
    enabled: false
    timezone: Europe/Berlin
    windows:
      - start: '18:00'
        end: '23:00'
```

The plate location is saved automatically after `/koth set`.

## Diagnostics

`/koth info` checks the saved point without scanning all online players. It reports whether the world is loaded, whether the chunk is loaded, whether the configured block is still the correct heavy weighted pressure plate, and whether the schedule is active right now.
