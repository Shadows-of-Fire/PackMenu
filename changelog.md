## 4.1.2
* Added a better fix for the panorama thing.  Finally avoids all race conditions.

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