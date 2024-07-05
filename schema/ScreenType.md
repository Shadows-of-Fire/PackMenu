# Description
A screen type is one of the Minecraft GUIs which can be opened by a button.  

The following types exist:
* `singleplayer` - The singleplayer world select screen.
* `multiplayer` - The multiplayer server select screen.
* `mods` - The Neo mods list menu.
* `language` - The language select screen.
* `options` - The Minecraft options screen.
* `accessibility` - The accessibility options screen.
* `resource_packs` - The resource pack selection screen.
* `supporters` - The packmenu-provided patreon supporters display screen.
* `realms` - The Minecraft Realms screen. 

# Schema
```js
"string" // [Mandatory] || The name of the screen type, from the options above.
```
