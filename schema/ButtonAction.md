# Description
A button action is the on-click effect of a button. 

# Dependencies
This object references the following objects:
1. [ScreenType](./ScreenType.md)

# Subtypes
Button actions are subtyped, meaning each subtype declares a `"type"` key and its own parameters.

## Connect To Server
Connects to a target server via the specified ip address.  

### Schema
```js
{
    "type": "packmenu:connect_to_server",
    "ip_address": "string"   // [Mandatory] || The IP Address of the target server.
}
```

## Reload
Reloads the PackMenu config and all resource packs.  

### Schema
```js
{
    "type": "packmenu:reload"
}
```

## Open Screen
Opens the specified screen.  

### Schema
```js
{
    "type": "packmenu:open_screen",
    "screen": ScreenType     // [Mandatory] || The type of screen to open.
}
```

## Open URL
Opens the specified web URL.  

### Schema
```js
{
    "type": "packmenu:open_url",
    "url": "string"          // [Mandatory] || The URL to open.
}
```

## Quit
Quits the game.

### Schema
```js
{
    "type": "packmenu:quit"
}
```

## None
Takes no action.  Useful for creating co-opting buttons as static images.  

### Schema
```js
{
    "type": "packmenu:none"
}
```
