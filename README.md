InvSwap
=======

Bukkit Plugin to allow inventory switching
------------------------------------------

This plugin allows players to use the /invswap command to save and retrieve multiple inventories.

It does not store NBT data, however, so items like Written Books will be lost, and custom names as well.
It was primarily designed for creative mode to allow multiple sets of items to quickly swap in between to
build with. For example, a player could have one inventory titled "redstone" that had items for building
redstone, another called "building" that had varieties of stone, and a third called "pixelart" that had the
different colors of wool. To retrieve these, they would only need to type /invswap load \<inventory name\>