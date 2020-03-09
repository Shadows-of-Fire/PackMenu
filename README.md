# PackMenu [![](http://cf.way2muchnoise.eu/packmenu.svg)](https://www.curseforge.com/minecraft/mc-mods/packmenu) [![](http://cf.way2muchnoise.eu/versions/packmenu.svg)](https://www.curseforge.com/minecraft/mc-mods/packmenu)
Tool for editing the main menu in modpacks.  This mod was developed as a replacement for Custom Main Menu in Minecraft 1.15+.  

__Pack Menu has the following features:__

* Changing the background image of the Main Menu
* Rearranging or disabling any menu elements, such as the title image, splash text, and the panorama fade.
* Creation of custom buttons, and through that, the ability to completely overhaul the buttons on the menu.  Buttons are created using JSON files, with the defaults shipped with the provided resource pack.
* Display of a custom logo separate from the background.  This logo can have splash text attached to it, and can draw from a custom splash text list instead of the defaults.
* Creation of a custom background slideshow, using any number of images.
* Creation of a custom panorama by overriding the vanilla panorama assets.
 

PackMenu uses a resource pack to load it's textures.  This resource pack is located at <gamedir>/packmenu/resources.zip, or at <gamedir>/packmenu/resources (as a folder) if enabled in the config.
This means you can also use PackMenu to load any textures that your modpack may require for any reason.

__Important Resource Paths (for overriding things):__

* The location of the Custom Background Image is assets\packmenu\textures\gui\background.png.  This image should be 1920x1080.
* Buttons, by default, use the texture sheet assets\minecraft\textures\gui\widgets.png. However, this is used by non-main menu buttons, so overriding it is not advisable.  Instead, you can point buttons at packmenu's copy of that file assets\packmenu\textures\gui\widgets.png and override that one.
* Buttons are loaded from assets\<any domain>\buttons. Any json files in such a directory will be treated as if it were a button (similar to advancements or recipes, but in this directory).  More information about buttons, including the JSON specification, is available on the wiki.

See [Curseforge](https://www.curseforge.com/minecraft/mc-mods/packmenu) or [the wiki](https://github.com/Shadows-of-Fire/PackMenu/wiki) for more information.
