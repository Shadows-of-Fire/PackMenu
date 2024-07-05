# PackMenu [![](http://cf.way2muchnoise.eu/packmenu.svg)](https://www.curseforge.com/minecraft/mc-mods/packmenu) [![](http://cf.way2muchnoise.eu/versions/packmenu.svg)](https://www.curseforge.com/minecraft/mc-mods/packmenu)
Tool for editing the main menu in modpacks.  This mod was developed as a replacement for Custom Main Menu in Minecraft 1.15+.  

## Features

* Changing the background image of the Main Menu
* Rearranging or disabling any menu elements, such as the title image, splash text, and the panorama fade.
* Creation of custom buttons, and through that, the ability to completely overhaul the buttons on the menu.  Buttons are created using JSON files, with the defaults shipped with the provided resource pack.
* Display of a custom logo separate from the background.  This logo can have splash text attached to it, and can draw from a custom splash text list instead of the defaults.
* Creation of a custom background slideshow, using any number of images.
* Creation of a custom panorama by overriding the vanilla panorama assets.
  
PackMenu uses a resource pack to load its textures.  This resource pack is located at \<gamedir\>/packmenu/resources (as a folder).  
This means you can also use PackMenu to load any textures that your modpack may require for any reason.

## Overriding Resources

Changing certain vanilla assets requires overriding them through normal resource pack methods. You can reference the [Vanilla Wiki](https://minecraft.wiki/w/Tutorials/Creating_a_resource_pack) for instructions, and use [MCAssets](https://mcasset.cloud/) to get a copy of the vanilla assets.

## Creating Buttons
Custom buttons are loaded from the `buttons/` resource type. The examples are visible [here](https://github.com/Shadows-of-Fire/PackMenu/tree/1.21/src/main/resources/builtin_resource_pack/assets/packmenu/buttons).  
For more details, reference the [schema for JsonButton](https://github.com/Shadows-of-Fire/PackMenu/blob/1.21/schema/JsonButton.md).
