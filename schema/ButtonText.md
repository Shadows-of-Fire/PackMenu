# Description
A button text object holds all information needed to draw text on a button. Text that exceeds the button's width will be automatically suffixed with `...` and scrolled on hover.

# Schema
```js
{
    "key": "string",        // [Mandatory] || A language key for the displayed text.
    "x_offset": int,        // [Optional]  || The x-offset, in pixels, for drawing the text. Defaults to 0.
    "y_offset": int,        // [Optional]  || The y-offset, in pixels, for drawing the text. Defaults to -4.
    "color": int,           // [Optional]  || The color of the text. Defaults to white (0xFFFFFF).
    "drop_shadow": boolean  // [Optional]  || If the text will have a drop shadow. Defaults to true.
}
```

# Examples
The text information used by the Akliz button.
```json
{
	"key": "packmenu.akliz",
	"x_offset": 5
}
```
