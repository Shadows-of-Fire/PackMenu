# Description
A JSON Button is the primary interface for how you add menu content via packmenu. Each button specifies a position, texture, and action, and will show up on the screen as expected.  
You can also make buttons with no actions that effectively act as still images.

# Dependencies
This object references the following objects:
1. [AnchorPoint](./AnchorPoint.md)
2. [WidgetSprites](./WidgetSprites.md)
3. [ButtonIcon](./ButtonIcon.md)
4. [ButtonAction](./ButtonAction.md)
5. [ButtonText](./ButtonText.md)

# Schema
```js
{
    "x": int,                   // [Mandatory] || The x position of the button, in pixels. Will be adjusted against the anchor point.
    "y": int,                   // [Mandatory] || The y position of the button, in pixels. Will be adjusted against the anchor point.
    "width": int,               // [Mandatory] || The width of the button, in pixels.
    "height": int,              // [Mandatory] || The height of the button, in pixels.
    "anchor": AnchorPoint,      // [Optional]  || The anchor point for the button. Defaults to the "default" anchor point.
    "sprites": WidgetSprites,   // [Optional]  || The sprites for the button. Defaults to the vanilla sprites.
    "icon": ButtonIcon,         // [Optional]  || An optional button icon to draw. Defaults to no icon.
    "hover_icon": ButtonIcon,   // [Optional]  || The icon drawn when the button is hovered. Defaults to the value of "icon".
    "action": ButtonAction,     // [Mandatory] || The action performed by this button on click.
    "text": ButtonText,         // [Optional]  || The text to draw on this button. Defaults to no text.
    "hover_text": ButtonText,   // [Optional]  || The text to draw when the button is hovered. Defaults to the value of "text".
    "active": boolean,          // [Optional]  || If this button is active and can be clicked. Defaults to true.
    "scale_x": float,           // [Optional]  || A scale factor for x values. May have uncertain results if the reciprocal is not an integer.
    "scale_y": float            // [Optional]  || A scale factor for y values. May have uncertain results if the reciprocal is not an integer.
}
```

# Examples
The Akliz Sponsor button shipped by default.
```json
{
    "x": -150,
    "y": 0,
    "width": 150,
    "height": 20,
    "anchor": "top_right",
    "icon": {
        "texture": "packmenu:icon/akliz",
        "width": 150,
        "height": 20
    },
    "action": {
        "type": "packmenu:open_url",
        "url": "https://www.akliz.net/partners"
    },
    "text": {
        "key": "packmenu.akliz",
        "x_offset": 5
    }
}
```
