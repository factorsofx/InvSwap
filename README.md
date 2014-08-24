InvSwap
=======

Bukkit Plugin to allow inventory switching
------------------------------------------

This plugin allows players to use the /invswap command to save and retrieve multiple inventories.

It was primarily designed for creative mode to allow multiple sets of items to quickly swap in between to
build with. For example, a player could have one inventory titled "redstone" that had items for building
redstone, another called "building" that had varieties of stone, and a third called "pixelart" that had the
different colors of wool. To retrieve these, they would only need to type /invswap load \<inventory name\>.

Saving an inventory is just as simple. Just type /invswap save \<name of save\> and then you can load it
using the aforementioned command.

This plugin does save NBT data for your items, so if you have written books, custom item names, enchantments, or anything else they will be kept through the saving-loading process. When saving, the positions in your inventory are kept as well.

Permissions
-----------

This plugin has several permissions.
- invswap.save allows you to save inventories
- invswap.load allows you to load inventories
- invswap.default includes both of those and no others


- invswap.unlimited allows you unlimited inventories, as opposed to the default limit (5)
- invswap.publish allows you to save inventories to the public list
- invswap.admin includes both of those AND invswap.default

To Do List
----------
