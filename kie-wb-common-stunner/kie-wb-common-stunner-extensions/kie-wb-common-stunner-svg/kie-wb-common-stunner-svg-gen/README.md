Stunner - SVG View Generation
==============================

This module allows to use SVG image files in order to declare Stunner's shape views.

It generates Lienzo view code for a given SVG declaration.

In order to generate the views, it relies on some optional conventions, also the SVG specification support is limited. Please refer to next sections.

SVG - Supported elements
--------------------------

| Element |
| ------------- |
| Rectangle |
| Circle |
| Path |
| Image |
| Group |


SVG - Supported attributes
--------------------------

| Element  | Attribute |
| ------------- | ------------- |
| generic  | fill  |
| generic  | fill-opacity  |
| generic  | stroke  |
| generic  | stroke-opacity  |
| generic  | stroke-width  |
| generic  | dash-array  |
| generic  | translate (transform)  |
| generic  | scale (transform)  |
| generic  | style  |
| generic  | class  |
| rectangle  | x  |
| rectangle  | y  |
| rectangle  | rx  |
| rectangle  | ry  |
| rectangle  | width  |
| rectangle  | height  |
| circle  | cx  |
| circle  | cy  |
| circle  | r  |
| image   | href  |
| path  | d  |

SVG - Custom attributes
--------------------------

Namespace URI for `stunner`: `http://kiegroup.org/2017/stunner`

| Element  | Attribute | Values | Description |
| ------------- | ------------- | ------------- | ------------- |
| generic  | stunner:layout  | CENTER,TOP,LEFT,BOTTOM,RIGHT | The layout position inside the parent shape |
| generic  | stunner:shape-state  | FILL, STROKE (default) | Indicates which is the shape used for displaying the different states and the attributes to use (fill/stroke attributes) |
| generic  | stunner:shape  | exclude | Excludes the code generation for the shape |
| generic  | stunner:transform  | non-scalable, scalable (default) | Specifies whether the children shape will be transformed (translated/scaled) when the parent is being transformed (eg: when a user resize a shape from the UI) |

CSS - Supported properties
--------------------------

| Property |
| ------------- |
| opacity |
| fill |
| fill-opacity |
| stroke |
| stroke-opacity |
| stroke-width |
| stroke-dasharray |
| font-family |
| font-size |

CSS - Supported selectors
--------------------------

| Selector  | Example | Description |
| ------------- | ------------- | ------------- |
| class selector | .myclass | Selector by class |
| id selector | #myid | Selector by id |
| containment selector | .myclass1 .myclass2 | Select elements inside other elements |
| stunner shape selector (custom) | #shapeId .myclass1 .myclass2 | It overrides default styles for concrete Stunner shapes |


SVG - Restrictions & considerations
--------------------------------------
* The SVG declaration MUST contain values for the `width` and `height` attributes - this values are the one used to scale the resulting shape to the given size
* No viewBox support
* First element in the SVG declaration CANNOT be a container (group)
* First element in the SVG declaration is considered the main shape and defaults to `STROKE` shape-state
* Non visible elements are not present in the resulting generated code for the view, for performance reasons
* Empty groups are not supported
* Try to clean file content - external entities, DTDs, etc
