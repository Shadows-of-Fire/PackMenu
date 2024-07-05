# Description
A button icon holds data to draw an image on a button on top of the usual [widget sprites](./WidgetSprites.md).  

### Draw Location
Icons are drawn centered on the button, which means that a 15x15 icon on a 20x20 button will appear exactly in the middle.  
If you want to have better control over the drawn location, you can make an icon with the same size as the button, and pad the texture file with blank space.

### Texture Path
Textures for icons are normalized against `/textures/gui/sprites`, which means a texture of `minecraft:icon/accessibility` refers to `assets/minecraft/textures/gui/sprites/icon/accessibility.png`.

# Schema
```js
{
    "texture": "string", // [Mandatory] || The texture path for the icon.
    "width":   int,      // [Mandatory] || The width of the icon, in pixels.
    "height":  int       // [Mandatory] || The height of the icon, in pixels.
}
```

# Examples
The 150x20 icon used by the Akliz button. This icon has the same size as the full button, which allows it to draw left-aligned content by overlaying the entire space.
```json
{
	"texture": "packmenu:icon/akliz",
	"width": 150,
	"height": 20
}
```
