![banner](https://file.strassburger.dev/cookieclickerz.png)

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

<details>
<summary>Click me!</summary>

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

# The sound that will be played when a player clicks a block
# You can find a list of sounds here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
clickSound: "BLOCK_WOODEN_BUTTON_CLICK_ON"

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