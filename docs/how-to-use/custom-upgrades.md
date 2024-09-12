---
description: With CookieClickeZ, you can add as many upgrades as you want!
---

# 🆙 Custom Upgrades

In the `upgrades.yml` config file, you can add your own upgrades with the following schema:

<pre class="language-yaml" data-title="upgrades.yml"><code class="lang-yaml"><strong># ... other upgrades...
</strong><strong>
</strong><strong>my_upgrade_id:
</strong>  # The name of the upgrade
  name: "&#x26;6Such a great custom upgrade"
  # The base price of the upgrade
  baseprice: "50"
  # The price multiplier for each upgrade (1 to keep the price the same)
  priceMultiplier: 1.1
  # The material that will be displayed in the shop
  item: "WOODEN_PICKAXE"
  # The amount of cookies per click the upgrade will add
  cpc: "1"
  # The amount of cookies the player will get while offline (when enabled in config.yml)
  offlineCookies: "0"
  
  # ... more upgrades ...
</code></pre>

You can find a list of all valid materials here:

{% embed url="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html" %}
