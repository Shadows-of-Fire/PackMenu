# Description
An anchor point is a descriptor binding an object to a relative area on the screen. Each point binds to a different (x, y) coordinate.  
  
In the following descriptors, `width` and `height` refer to the width and height of the screen, in pixels.  
  
The following anchor points exist:  
* `top_left` - Binds to (0, 0).
* `top_center` - Binds to (width / 2, 0).
* `top_right` - Binds to (width, 0).
* `middle_left` - Binds to (0, height / 2).
* `middle_center` - Binds to (width, height / 2).
* `middle_right` - Binds to (width, height / 2).
* `bottom_left` - Binds to (width, height).
* `bottom_center` - Binds to (width, height).
* `bottom_right` - Binds to (width, height).
* `default` - Special value used by the vanilla buttons. Binds to (width / 2, height / 4 + 48).

# Schema
```js
"string" // [Mandatory] || The name of the anchor point, from the options above.
```
