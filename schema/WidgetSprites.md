# Description
Widget Sprites are the list of textures used by a button, with up to four elements for the following button states:
* Enabled
* Disabled
* Enabled and Hovered/Focused
* Disabled and Hovered/Focused

### Sprite Metadata
Most information about how sprites tile is controlled by the supplied `.mcmeta` file. See the GUI section of the [Minecraft Wiki entry on Textures](https://minecraft.wiki/w/Resource_pack#Textures) for more information.  

### Texture Path
Textures for sprites are normalized against `/textures/gui/sprites`, which means a texture of `minecraft:widget/button` refers to `assets/minecraft/textures/gui/sprites/widget/button.png`.  

# Schema
```js
{
    "enabled": "string",         // [Mandatory] || The texture to use when the button is enabled.
    "disabled": "string",        // [Optional]  || The texture to use when the button is disabled. Defaults to the "enabled" texture.
    "enabled_focused": "string", // [Optional]  || The texture to use when the button is enabled and hovered/focused. Defaults to the "enabled" texture.
    "disabled_focused": "string" // [Optional]  || The texture to use when the button is disabled and hovered/focused. Defaults to the "disabled" texture.
}
```

# Examples
The default widget sprites used by the vanilla buttons.
```json
{
	"enabled": "minecraft:widget/button",
	"disabled": "minecraft:widget/button_disabled",
	"enabled_focused": "minecraft:widget/button_highlighted"
}
```
