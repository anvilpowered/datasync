# MSDataSync

<img src="icon.png" width="290" height="290" align="right">

#### Quick Links
[Join our development discord](https://discord.gg/8RUzuwu)

[Ore page](https://ore.spongepowered.org/Cableguy20/DataSync)

[Installation Guide](https://github.com/AnvilPowered/DataSync/wiki/Installation)

Have you ever had problems with players losing items?

Do you run a multi server network where every server needs to share inventories and other player data?

### Look no further

_MSDataSync_ is an advanced player data backup plugin that lets you store, manage, edit and rollback player data!

(Compatible with forge and vanilla)

# Features

## Backup player data

- _MSDataSync_ creates automated snapshots with an interval specified in your config

- As of this writing, a snapshot will include (by default)
  - Inventory
  - Health
  - Hunger
  - Experience
  - Game mode

- You can manually create a snapshot for a player with `/sync snapshot create <name>`

- Or create a snapshot for everyone on the server with `/sync up`

- By default, _MSDataSync_ will load the latest snapshot when a player leaves and create a snapshot when a player joins.
This can be disabled in the config.

## Restore player data

- The old way

```
Player: HeLp I LOst mY sTuFf!!!!! OWnER!!!!!!

Owner: screenshots or it didnt happen, sorry bud

Player: this server is dumb, im never coming back!

Player has left the game
```

- With _MSDataSync_

```
Player: HeLp I LOst mY sTuFf!!!!! OWnER!!!!!!

Owner: when did this happen?

Player: just now, can you help plz

[Owner runs /sync snapshot restore Player]

[Player gets restored to the latest snapshot]

Player: thanks!
```

- Browse through snapshots by date and restore, edit or delete it (separate permissions for viewing and editing)

## Optimize backups

_MSDataSync_ automatically deletes old snapshots for optimal storage efficiency. The default optimization strategy will:
- Keep all snapshots within the last hour
- Keep up to one snapshot per hour for the last 24 hours (not including the first hour)
- Keep up to one snapshot per day for the last 7 days (not including the first day)
- Delete all snapshots older than 7 days

## Comes with child lock

Don't you hate running dangerous commands by accident! Me too.
We included a child lock so you would be slightly less likely to accidentally break stuff!
- `/sync lock [<on:off>]`

## Reload command actually works

Changed any settings? Just run `/sync reload` and the plugin will reload
- Database connection gets reopened (with updated db settings from config)
- Sync task is restarted (with updated settings from config)

## Compiling 

1. Run `./gradlew runSpigotBuildTools`
    - This may take some time, go grab a drink while you wait (This only has to be ran once)
2. run `./gradlew assemble`
