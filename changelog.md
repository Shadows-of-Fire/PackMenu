## 7.0.3
* Removed uses of `printStackTrace`, which might ease rare issues where `System.err` is unstable.

## 7.0.2
* Made hover objects more customizable.
  * Removed hover-related fields from `ButtonText` and added separate `hover_text` and `hover_icon` fields to `JsonButton`.
* Fixed default text rendering offset.
  * The previous version set the default offset in the json to `-4` instead of properly centering the text and making the default json offset `0`.
  * This _might_ break some existing files if anyone is already correcting for the `-4` offset, but I don't think anyone is yet.
* Added the ability to render slideshow images in a random order.
* FITFC: Added Brazillian translation.

## 7.0.1
* Fixed json buttons always using the vanilla sprites, instead of the specified value.

## 7.0.0
* Updated to Minecraft 1.21!
* Rewrote the internal structure of `JsonButton` to handle the changes to vanilla GUI code.
* Wrote schema documents detailing the new format. See [here](./schema/JsonButton.md).
* Removed the ability to use zip files as the packmenu-provided resource pack. Only folder packs are supported.

## 6.1.2
* Made it possible for the slideshow to only run one time instead of repeating infinitely.

## 6.1.1
* Removed forge dependency line from the mods.toml and marked as Forge and NeoForge for CF.
  * The dependency will be added back and the Forge marker will be removed once CF supports Neo correctly.

## 6.1.0
* Updated to Placebo 8.2.0

## 6.0.0
* Updated to 1.20.1.

## 5.1.0
* Proper port to 1.19.2
* Removed shadowed Placebo code and reintroduced dependency now that Placebo has been updated.
* Fixed slideshow fade not working.

## 5.0.2
* Mixin fix for real this time, maybe.

## 5.0.1
* Mixin fix

## 5.0.0
* Alpha port to 1.19.2

## 4.1.1
* Added a potential fix for the panorama loading the vanilla panorama.

## 4.1.0
* Added an explicit disable option for the logo.
* Fixed logo not drawing with translucency.
* Fixed slideshow not drawing at all.

## 4.0.0
* Initial update to 1.18.1

## 3.0.0
* Initial update to 1.17.1

## 2.5.0
* Added the Supporters Screen, where you can display the names of those who support you and a link to a donation page.

## 2.4.2
* Allow buttons to specify hover text

## 2.4.1
* Fix arbitrary scaling not working.  Previously only 1/n where n is an integer scaling worked properly.  Scaling may still have issues in some random cases.

## 2.4.0
* Added a REALMS action so that clients with PackMenu can connect to realms once more.
* Made it so that multiple panoramas can be specified.  One will be chosen at random during resource reload.
* All elements that supported an offset (title, splash, java edition, forge warnings) now support having their anchor points changed.
* When a button has text that is too long for it's button, it will be trimmed and have "..." placed at the end of it.
* Hovering over a button with "..." appended will make the text scroll so that longer text can be read.
* Added a screen type for the resource pack menu (RESOURCE_PACKS)