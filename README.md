# KingOfTheHill / koth-plate

Small Paper 1.20.1 plugin for a simple King of the Hill pressure plate with Vault economy rewards.

This plugin was made as a practical learning project for a real Minecraft server workflow: keep the setup small, make the behavior predictable, and document enough that another server owner can build and test it without digging through the source.

## What It Does

An admin stands on a block and runs `/koth set`. The plugin places a heavy weighted pressure plate at the player's feet and saves it to `config.yml`.

When a player holds the plate for the configured time without leaving, dying, quitting, or switching to Creative/Spectator, they receive an economy reward through Vault. If they keep standing on the plate, the timer can start again.

## Project Status

- **Useful now:** yes, for small servers that need one simple capture point.
- **Finished product:** feature-complete MVP, not a large multi-arena KOTH suite.
- **Main risks:** economy-provider setup, server-version assumptions, and manual balance tuning.
- **If needed:** it can be extended with multiple points, scoreboards, seasons, or richer rewards.

## Features

- One capture point configured in-game.
- Vault reward support.
- Configurable capture time and reward amount.
- Optional active schedule window.
- RU/EN message files.
- Diagnostics command for quick server checks.
- Safe checks for unloaded worlds/chunks and invalid gamemodes.

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

The plugin jar will be created in:

```text
target/king-of-the-hill-1.0.7-SNAPSHOT.jar
```

## Configuration Example

```yaml
settings:
  language: ru
  capture-time: 20
  visual-hold-buffer-seconds: 1
  reward-amount: 25.0
  respect-cancelled-physical-events: false
  after-reward:
    mode: 2
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

## Author Note

Made with care while learning by shipping useful tools for my own Minecraft community. It is not a large commercial plugin, but it should be readable, buildable, and easy to improve if someone needs it.
