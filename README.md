# Three Lives

A Fabric mod for Minecraft 26.3. In hardcore worlds every player gets several lives instead of one.

## What it does

- Each player has a number of lives (3 by default). Dying costs one life.
- While lives remain you respawn normally. After your last life the usual hardcore rules apply and you become a spectator.
- The mod only does something in hardcore worlds.
- Remaining lives are shown as turquoise hearts above the health bar. With more than 10 lives you see a single heart and a number, like `x12`.
- The death screen shows how many lives you have left.
- On a dedicated server the lives count for every player, even without the mod installed. Only players with the mod see the hearts.

## Gamerule

The number of lives is set with the gamerule `threelives:lives` (default 3, minimum 1):

```
/gamerule threelives:lives 5
```

Changing it later only affects the future: deaths that were already counted stay, and spectators stay spectators.

## Commands

For operators. Each subcommand also has a permission node (`threelives.command.get`, `threelives.command.set`, `threelives.command.reset`) for permission mods such as LuckPerms.

- `/threelives get <player>` shows the remaining lives.
- `/threelives set <player> <lives>` sets the remaining lives (1 up to the gamerule value).
- `/threelives reset <player>` gives back all lives.

## Requirements

- Minecraft 26.3, Fabric Loader and [Fabric API](https://modrinth.com/mod/fabric-api).
- Optional: [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config) for a settings screen.

## License

MIT, see [LICENSE](LICENSE).
