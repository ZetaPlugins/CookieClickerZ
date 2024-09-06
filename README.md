![banner](https://cdn.modrinth.com/data/cached_images/88e8a8dce22dd3ae2510467453107342f13049cd.png)

---
![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/paper_vector.svg)
![purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/purpur_vector.svg)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/github_vector.svg)](https://github.com/KartoffelChipss/cookieclickerz)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://strassburger.dev/)
[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://strassburger.org/discord)
[![gitbook](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/gitbook_vector.svg)](https://strassburger.dev/)

CookieClickerZ is a Minecraft Cookie Clicker plugin, that brings the highly popular Cookie Clicker game to your Minecraft Server. You can either customize the plugin to the last detail or use the standard installation for the traditional cookieclicker experience.

## Features

* ✅ As many clicker blocks as you want
* ✅ Events
* ✅ Custom upgrades
* ✅ PlaceholderAPI placeholders
* ✅ Admin commands
* ✅ HEX colors and gradients support
* ✅ SQLite and MySQL support

## Permissions

* **cookieclickerz.useclicker** - Allows the player to use the clicker block (default: true)
* **cookieclickerz.openshop** - Allows the player to open the shop (default: true)
* **cookieclickerz.manageclickers** - Allows the player to manage clickers (default: op)
* **cookieclickerz.managecookies** - Allows the player to manage cookies (default: op)

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
#    \_____\___/ \___/|_|\_\_|\___|  \_____|_|_|\___|_|\_\___|_|    /_____|

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
      - "kick %player% &cYou are clicking too fast!"

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


# === STORAGE ===

storage:
  # The type of storage to use. You have the following options:
  # "SQLite"
  type: "SQLite"

  # This section is only relevant if you use a MySQL database
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

stone_pickaxe:
  name: "&6Stone Pickaxe"
  baseprice: "150"
  priceMultiplier: 1.2
  item: "STONE_PICKAXE"
  cpc: "2"
  offlineCookies: "1"

iron_pickaxe:
  name: "&6Iron Pickaxe"
  baseprice: "500"
  priceMultiplier: 1.3
  item: "IRON_PICKAXE"
  cpc: "4"
  offlineCookies: "2"

# Advanced Resources
diamond_pickaxe:
  name: "&6Diamond Pickaxe"
  baseprice: "2000"
  priceMultiplier: 1.4
  item: "DIAMOND_PICKAXE"
  cpc: "8"
  offlineCookies: "5"

netherite_pickaxe:
  name: "&6Netherite Pickaxe"
  baseprice: "10000"
  priceMultiplier: 1.5
  item: "NETHERITE_PICKAXE"
  cpc: "16"
  offlineCookies: "10"

# Rare Items
enchanted_apple:
  name: "&6Enchanted Golden Apple"
  baseprice: "50000"
  priceMultiplier: 1.6
  item: "ENCHANTED_GOLDEN_APPLE"
  cpc: "32"
  offlineCookies: "20"

elytra:
  name: "&6Elytra"
  baseprice: "250000"
  priceMultiplier: 1.7
  item: "ELYTRA"
  cpc: "64"
  offlineCookies: "40"

dragon_egg:
  name: "&6Dragon Egg"
  baseprice: "1000000"
  priceMultiplier: 1.8
  item: "DRAGON_EGG"
  cpc: "128"
  offlineCookies: "80"

# Special Items
beacon:
  name: "&6Beacon"
  baseprice: "5000000"
  priceMultiplier: 1.9
  item: "BEACON"
  cpc: "256"
  offlineCookies: "160"

nether_star:
  name: "&6Nether Star"
  baseprice: "20000000"
  priceMultiplier: 2.0
  item: "NETHER_STAR"
  cpc: "512"
  offlineCookies: "320"

heart_of_the_sea:
  name: "&6Heart of the Sea"
  baseprice: "100000000"
  priceMultiplier: 2.1
  item: "HEART_OF_THE_SEA"
  cpc: "1024"
  offlineCookies: "640"

# Exotic Items
totem_of_undying:
  name: "&6Totem of Undying"
  baseprice: "500000000"
  priceMultiplier: 2.2
  item: "TOTEM_OF_UNDYING"
  cpc: "2048"
  offlineCookies: "1280"

end_crystal:
  name: "&6End Crystal"
  baseprice: "2500000000"
  priceMultiplier: 2.3
  item: "END_CRYSTAL"
  cpc: "4096"
  offlineCookies: "2560"

shulker_shell:
  name: "&6Shulker Shell"
  baseprice: "10000000000"
  priceMultiplier: 2.4
  item: "SHULKER_SHELL"
  cpc: "8192"
  offlineCookies: "5120"

# Ultimate Items
enchanted_netherite:
  name: "&6Enchanted Netherite Ingot"
  baseprice: "50000000000"
  priceMultiplier: 2.5
  item: "NETHERITE_INGOT"
  cpc: "16384"
  offlineCookies: "10240"

ancient_debris:
  name: "&6Ancient Debris"
  baseprice: "250000000000"
  priceMultiplier: 2.6
  item: "ANCIENT_DEBRIS"
  cpc: "32768"
  offlineCookies: "20480"

block_of_netherite:
  name: "&6Block of Netherite"
  baseprice: "1000000000000"
  priceMultiplier: 2.7
  item: "NETHERITE_BLOCK"
  cpc: "65536"
  offlineCookies: "40960"

# Ultimate Special Items
god_apple:
  name: "&6God Apple"
  baseprice: "5000000000000"
  priceMultiplier: 2.8
  item: "ENCHANTED_GOLDEN_APPLE"
  cpc: "131072"
  offlineCookies: "81920"

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