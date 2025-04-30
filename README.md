![banner](https://file.strassburger.dev/CookieClickerZ_banner.png)

---
![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/paper_vector.svg)
![purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/purpur_vector.svg)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/github_vector.svg)](https://github.com/KartoffelChipss/cookieclickerz)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/project/cookieclickerz)
[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://strassburger.org/discord)
[![gitbook](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/gitbook_vector.svg)](https://cc.strassburger.dev/)

CookieClickerZ is a Minecraft Cookie Clicker plugin, that brings the popular Cookie Clicker game to your Minecraft Server. You can either customize the plugin to the last detail or use the standard installation for the traditional cookieclicker experience.

## Features

* ✅ As many clicker blocks as you want
* ✅ Customizable upgrades
* ✅ Events
* ✅ Prestige system
* ✅ Achievements
* ✅ Top list
* ✅ Offline cookie gathering
* ✅ Anti-cheat system
* ✅ Fully customizable messages, sounds, and items
* ✅ HEX colors and gradients support
* ✅ PlaceholderAPI placeholders
* ✅ Admin commands

## Permissions

* **cookieclickerz.useclicker** - Allows the player to use the clicker block (default: true)
* **cookieclickerz.upgrades** - Allows the player to open the upgrades shop (default: true)
* **cookieclickerz.prestige** - Allows the player to open the prestige menu (default: true)
* **cookieclickerz.top** - Allows the player to open the top menu (default: true)
* **cookieclickerz.viewachievements** - Allows the player to open the achievements menu (default: true)
* **cookieclickerz.numcheatsheet** - Allows the player to open the numcheatsheet (default: true)
* **cookieclickerz.admin.manageclickers** - Allows the player to manage clickers (default: op)
* **cookieclickerz.admin.managecookies** - Allows the player to manage other players cookies (default: op)
* **cookieclickerz.admin.manageprestige** - Allows the player to manage other players prestige (default: op)
* **cookieclickerz.admin.manageevents** - Allows the player to manage events (default: op)
* **cookieclickerz.admin.manageachievements** - Allows the player to manage other players achievements (default: op)

## Configuration

You can customize the plugin to your liking by editing the `config.yml`, `upgrades.yml`, and `prestige.yml` files located in the `plugins/CookieClickerZ` folder.

<details>
<summary>config.yml</summary> 

```yaml
#       _____           _    _         _____ _ _      _               ______
#     / ____|          | |  (_)       / ____| (_)    | |             |___  /
#    | |     ___   ___ | | ___  ___  | |    | |_  ___| | _____ _ __     / /
#    | |    / _ \ / _ \| |/ / |/ _ \ | |    | | |/ __| |/ / _ \ '__|   / /
#    | |___| (_) | (_) |   <| |  __/ | |____| | | (__|   <  __/ |     / /__
#     \_____\___/ \___/|_|\_\_|\___|  \_____|_|_|\___|_|\_\___|_|    /_____|

# !!! COLOR CODES !!!
# This plugin supports old color codes like: &c, &l, &o, etc
# It also supports minimessage, which is a more advanced way to format messages:
# https://docs.advntr.dev/minimessage/format.html
# With these, you can also add HEX colors, gradients, hover and click events, etc

# If set to true, LifeStealZ will check for updates and let you know if there's a newer version
checkForUpdates: true

# Set the language to any code found in the "lang" folder (don't add the .yml extension)
# You can add your own language files. Use https://github.com/KartoffelChipss/LifeStealZ/tree/main/src/main/resources/lang/en-US.yml as a template
# If you want to help translating the plugin, please refer to this article: https://lsz.strassburger.dev/contributing/localization
lang: "en-US"

# The the cookie name
cookieName: "&7Cookies"

offlineCookies:
  # If set to true, players will earn cookies while they are offline
  enabled: true
  # Wether or not to send a message to the player when they join informing them about the cookies they earned while offline
  joinMessage: true

# Wether or not to show a hologram above the clicker block
# You need to have HolographicDisplays installed for this to work:
# https://modrinth.com/plugin/decentholograms
# You can change the content of the hologram in the language file
hologram: true


# === EVENTS ===

events:
  # Toggle Events, like Click Frenzy, Cookie Frenzy, etc
  enabled: true
  # The probability of an event happening for each click
  rates:
    COOKIE_FRENZY: 0.005
    LUCKY: 0.007
    CLICK_FRENZY: 0.002
    RUIN: 0.002
    CURSED_FINGER: 0.002


# === SOUNDS ===

# The sound that will be played when a player clicks a block
# You can find a list of sounds here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
clickSound: "BLOCK_WOODEN_BUTTON_CLICK_ON"
# The sound that will be played when a player buys an upgrade
upgradeSound: "ENTITY_PLAYER_LEVELUP"
# The sound that will be played when a player prestiges
prestigeSound: "ENTITY_PLAYER_LEVELUP"
# The sound when there was an error
errorSound: "ENTITY_VILLAGER_NO"


# === ANTICHEAT ===

anticheat:
  cps:
    # If set to true, the plugin will check for the amount of clicks per second
    enabled: true
    # The maximum amount of clicks per second a player is allowed to do
    max: 15
    # The message that will be sent to a player if they click too fast
    message: "&cYou are clicking too fast!"
    # The commands that will be executed if a player clicks too fast
    commands:
    #- "kick %player% &cYou are clicking too fast!"

  nomovement:
    # If set to true, the plugin will check if a player is moving
    enabled: true
    # The maximum amount of time a player is allowed to not move (in seconds)
    max: 15
    # The message that will be sent to a player if they are not moving
    message: "&cYou are not moving!"
    # The commands that will be executed if a player is not moving
    commands:
    # - "kick %player% &cYou are not moving!"

# === EXPERT ===
# This section is only relevant if you are an expert and know what you are doing

playerCache:
  # If set to true, the plugin will use a cache to store player data
  enabled: true
  # The amount of time (in seconds) the plugin will wait before saving the whole cache to the database
  saveInterval: 60
  # The maximum amount of players that will be stored in the cache
  maxSize: 1000


# === STORAGE ===

storage:
  # The type of storage to use. You have the following options:
  # "SQLite"
  type: "SQLite"

  # This section is only relevant if you use a remote database
  host: "localhost"
  port: 3306
  database: "cookieclicker"
  username: "root"
  password: "password"
```

</details>

<details>
<summary>upgrades.yml</summary>

```yaml
# === UPGRADES ===

# You can add as many upgrades as you want following this structure
wooden_pickaxe:
  # The name of the upgrade
  name: "&6Wooden Pickaxe"
  # The price of the upgrade
  baseprice: "50"
  # The price multiplier for each upgrade
  priceMultiplier: 1.1
  # The material that will be displayed in the shop
  item: "WOODEN_PICKAXE"
  # The amount of cookies per click the upgrade will add
  cpc: "1"
  # The amount of cookies the player will get while offline
  offlineCookies: "0"

...

# Add more items as needed following this structure
```

</details>

<details>
<summary>prestige.yml</summary>

```yaml
# === PRESTIGE ===

# If set to true, the plugin will enable the prestige system
enabled: true

levels:
  1:
    # The name of the prestige level
    name: "&8&l> <!b>&6Prestige I"
    # The price of the prestige level
    cost: "1M"
    # The multiplier that will be applied to the player's cookies
    multiplier: 2

  2:
    name: "&8&l> <!b>&6Prestige II"
    cost: "10M"
    multiplier: 3

  3:
    name: "&8&l> <!b>&6Prestige III"
    price: "100M"
    multiplier: 4

  4:
    name: "&8&l> <!b>&6Prestige IV"
    cost: "1B"
    multiplier: 5

  5:
    name: "&8&l> <!b>&6Prestige V"
    cost: "10B"
    multiplier: 6
```

</details>

[![Usage](https://bstats.org/signatures/bukkit/CookieClickerZ.svg)](https://bstats.org/plugin/bukkit/CookieClickerZ/25442)
