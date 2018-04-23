/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * An I18 based interface used for Lienzo Constants.
 */
public interface MessageConstants extends Constants
{
    public static final MessageConstants MESSAGES = GWT.create(MessageConstants.class);

    @DefaultStringValue("Canvas is not supported in this browser!")
    public String getCanvasUnsupportedMessage();

    @DefaultStringValue("Movie playback was aborted.")
    public String moviePlaybackWasAborted();

    @DefaultStringValue("Movie network error.")
    public String movieNetworkError();

    @DefaultStringValue("Movie decoding error.")
    public String movieErrorInDecoding();

    @DefaultStringValue("Movie format not supported.")
    public String movieFormatNotSupported();

    @DefaultStringValue("Movie not support in this browser.")
    public String movieNotSupportedInThisBrowser();

    // ---------- Validation

    @DefaultStringValue("attribute is required")
    public String attributeIsRequired();

    @DefaultStringValue("invalid value for type {0} [{1}]")
    // type, value
    public String invalidValueForType();

    @DefaultStringValue("value should be a {0}")
    // type
    public String invalidType();

    @DefaultStringValue("attribute is invalid for type {0}")
    // type
    public String attributeIsInvalidForType();

    @DefaultStringValue("value must be [{0}]")
    // value
    public String attributeValueMustBeFixed();

    @DefaultStringValue("no NodeFactory is registered for type '{0}'")
    // type
    public String missingNodeFactory();

    @DefaultStringValue("Invalid array size. Expected value is {0}. Actual value is {1}")
    // expectedValue, actualValue
    public String invalidArraySize();

    // ---------- Attributes

    @DefaultStringValue("Width")
    public String widthLabel();

    @DefaultStringValue("Width value in pixels.")
    public String widthDescription();

    @DefaultStringValue("Height")
    public String heightLabel();

    @DefaultStringValue("Height value in pixels.")
    public String heightDescription();

    @DefaultStringValue("Min Width")
    public String minWidthLabel();

    @DefaultStringValue("Minimum Width value in pixels.")
    public String minWidthDescription();

    @DefaultStringValue("Max Width")
    public String maxWidthLabel();

    @DefaultStringValue("Maximum Width value in pixels.")
    public String maxWidthDescription();

    @DefaultStringValue("Min Height")
    public String minHeightLabel();

    @DefaultStringValue("Minimum Height value in pixels.")
    public String minHeightDescription();

    @DefaultStringValue("Max Height")
    public String maxHeightLabel();

    @DefaultStringValue("Maximum Height value in pixels.")
    public String maxHeightDescription();

    @DefaultStringValue("Corner Radius")
    public String cornerRadiusLabel();

    @DefaultStringValue("The radius of a 90 degree arc, which is used as a rounded corner.")
    public String cornerRadiusDescription();

    @DefaultStringValue("Fill")
    public String fillLabel();

    @DefaultStringValue("The color or gradient used to fill a shape.")
    public String fillDescription();

    @DefaultStringValue("Stroke")
    public String strokeLabel();

    @DefaultStringValue("The color of the outline of a shape.")
    public String strokeDescription();

    @DefaultStringValue("Stroke Width")
    public String strokeWidthLabel();

    @DefaultStringValue("Width in pixels of the outline of a shape.")
    public String strokeWidthDescription();

    @DefaultStringValue("Line Join")
    public String lineJoinLabel();

    @DefaultStringValue("Specifies how the connection of individual stroke segments will be drawn.")
    public String lineJoinDescription();

    @DefaultStringValue("X")
    public String xLabel();

    @DefaultStringValue("X coordinate.")
    public String xDescription();

    @DefaultStringValue("Y")
    public String yLabel();

    @DefaultStringValue("Y coordinate.")
    public String yDescription();

    @DefaultStringValue("Visible")
    public String visibleLabel();

    @DefaultStringValue("Indicates if the shape is visible or not.")
    public String visibleDescription();

    @DefaultStringValue("Listening")
    public String listeningLabel();

    @DefaultStringValue("Indicates if the shape is listening for events.")
    public String listeningDescription();

    @DefaultStringValue("ID")
    public String idLabel();

    @DefaultStringValue("Unique identifier for the shape.")
    public String idDescription();

    @DefaultStringValue("Name")
    public String nameLabel();

    @DefaultStringValue("Unique name given to the shape.")
    public String nameDescription();

    @DefaultStringValue("Alpha")
    public String alphaLabel();

    @DefaultStringValue("The alpha transparency for the shape.")
    public String alphaDescription();

    @DefaultStringValue("Stroke Alpha")
    public String strokeAlphaLabel();

    @DefaultStringValue("The alpha transparency for the shape's stroke.")
    public String strokeAlphaDescription();

    @DefaultStringValue("Fill Alpha")
    public String fillAlphaLabel();

    @DefaultStringValue("The alpha transparency for the shape's fill.")
    public String fillAlphaDescription();

    @DefaultStringValue("Scale")
    public String scaleLabel();

    @DefaultStringValue("Scale at which the shape is drawn.")
    public String scaleDescription();

    @DefaultStringValue("Rotation")
    public String rotationLabel();

    @DefaultStringValue("Radians used for the rotation of the shape around its origin or offset position.")
    public String rotationDescription();

    @DefaultStringValue("Offset")
    public String offsetLabel();

    @DefaultStringValue("The offset from origin at which a shape will be rotated around.")
    public String offsetDescription();

    @DefaultStringValue("Draggable")
    public String draggableLabel();

    @DefaultStringValue("Indicates if the shape can be dragged.")
    public String draggableDescription();

    @DefaultStringValue("Drag Constraint")
    public String dragConstraintLabel();

    @DefaultStringValue("Drag constraints for the shape limit how the shape can be dragged.")
    public String dragConstraintDescription();

    @DefaultStringValue("Drag Bounds")
    public String dragBoundsLabel();

    @DefaultStringValue("Drag bounds determine where the shape can be dragged.")
    public String dragBoundsDescription();

    @DefaultStringValue("Radius")
    public String radiusLabel();

    @DefaultStringValue("The radius of a circle or circular arc type.")
    public String radiusDescription();

    @DefaultStringValue("Radius X")
    public String radiusXLabel();

    @DefaultStringValue("The x radius of a elliptical arc type.")
    public String radiusXDescription();

    @DefaultStringValue("Radius Y")
    public String radiusYLabel();

    @DefaultStringValue("The y radius of a elliptical arc type.")
    public String radiusYDescription();

    @DefaultStringValue("Clear Layer")
    public String clearLayerBeforeDrawLabel();

    @DefaultStringValue("Indicates if the layer should be cleared before drawing.")
    public String clearLayerBeforeDrawDescription();

    @DefaultStringValue("Text")
    public String textLabel();

    @DefaultStringValue("String value of a Text shape.")
    public String textDescription();

    @DefaultStringValue("Font Size")
    public String fontSizeLabel();

    @DefaultStringValue("Text font size in points. i.e., 24.")
    public String fontSizeDescription();

    @DefaultStringValue("Font Family")
    public String fontFamilyLabel();

    @DefaultStringValue("Text font family. i.e., Tahoma.")
    public String fontFamilyDescription();

    @DefaultStringValue("Font Style")
    public String fontStyleLabel();

    @DefaultStringValue("Text font style. e.g., bold, italic, normal, etc.")
    public String fontStyleDescription();

    @DefaultStringValue("Points")
    public String pointsLabel();

    @DefaultStringValue("Number of points the shape has.")
    public String pointsDescription();

    @DefaultStringValue("Star points.")
    public String starPointsLabel();

    @DefaultStringValue("Number of points the star has.")
    public String starPointsDescription();

    @DefaultStringValue("Line Cap")
    public String lineCapLabel();

    @DefaultStringValue("Specifies how the end of a shapes stroke will be drawn.")
    public String lineCapDescription();

    @DefaultStringValue("Dash Array")
    public String dashArrayLabel();

    @DefaultStringValue("The outline of the shape will be drawn as a dashed line. The dash array specifies how the dashes are drawn.")
    public String dashArrayDescription();

    @DefaultStringValue("Sides")
    public String sidesLabel();

    @DefaultStringValue("Number of sides the shape has.")
    public String sidesDescription();

    @DefaultStringValue("Outer Radius")
    public String outerRadiusLabel();

    @DefaultStringValue("The radius of the shape's outer enclosing circle.")
    public String outerRadiusDescription();

    @DefaultStringValue("Inner Radius")
    public String innerRadiusLabel();

    @DefaultStringValue("The radius of the shape's inner enclosing circle.")
    public String innerRadiusDescription();

    @DefaultStringValue("Skew")
    public String skewLabel();

    @DefaultStringValue("The skew in pixels of a Parallelogram.")
    public String skewDescription();

    @DefaultStringValue("Shadow")
    public String shadowLabel();

    @DefaultStringValue("The value for the shape's shadow.")
    public String shadowDescription();

    @DefaultStringValue("Start Angle")
    public String startAngleLabel();

    @DefaultStringValue("The start angle of a shape's circular arc.")
    public String startAngleDescription();

    @DefaultStringValue("End Angle")
    public String endAngleLabel();

    @DefaultStringValue("The end angle of a shape's circular arc.")
    public String endAngleDescription();

    @DefaultStringValue("Counter Clockwise")
    public String counterClockwiseLabel();

    @DefaultStringValue("Indicates if the shape's circular arc should be drawn counter clockwise.")
    public String counterClockwiseDescription();

    @DefaultStringValue("Control Points")
    public String controlPointsLabel();

    @DefaultStringValue("The control points of a Bezier or Quadratic curve.")
    public String controlPointsDescription();

    @DefaultStringValue("Text Baseline")
    public String textBaseLineLabel();

    @DefaultStringValue("Vertical positioning for the text in the canvas.")
    public String textBaseLineDescription();

    @DefaultStringValue("Text Align")
    public String textAlignLabel();

    @DefaultStringValue("Horizontal positioning for the text in the canvas.")
    public String textAlignDescription();

    @DefaultStringValue("Clipped Image Width")
    public String clippedImageWidthLabel();

    @DefaultStringValue("The width of the clipped image (i.e., x coordinate where clipping ends).")
    public String clippedImageWidthDescription();

    @DefaultStringValue("Clipped Image Height")
    public String clippedImageHeightLabel();

    @DefaultStringValue("The height of the clipped image (i.e., y coordinate where clipping ends).")
    public String clippedImageHeightDescription();

    @DefaultStringValue("Clipped Image Destination Width")
    public String clippedImageDestinationWidthLabel();

    @DefaultStringValue("The destination width of the clipped image.")
    public String clippedImageDestinationWidthDescription();

    @DefaultStringValue("Clipped Image Destination Height")
    public String clippedImageDestinationHeightLabel();

    @DefaultStringValue("The destination height of the clipped image.")
    public String clippedImageDestinationHeightDescription();

    @DefaultStringValue("Clipped Image X")
    public String clippedImageStartXLabel();

    @DefaultStringValue("The x coordinate where clipping for the image begins.")
    public String clippedImageStartXDescription();

    @DefaultStringValue("Clipped Image Y")
    public String clippedImageStartYLabel();

    @DefaultStringValue("The y coordinate where clipping for the image begins.")
    public String clippedImageStartYDescription();

    @DefaultStringValue("Serialization Mode")
    public String serializationModeLabel();

    @DefaultStringValue("Used when deserializing a Picture.")
    public String serializationModeDescription();

    @DefaultStringValue("URL")
    public String urlLabel();

    @DefaultStringValue("Source URL for a Picture or Movie.")
    public String urlDescription();

    @DefaultStringValue("Loop")
    public String loopLabel();

    @DefaultStringValue("Indicates if the Movie should loop.")
    public String loopDescription();

    @DefaultStringValue("Volume")
    public String volumeLabel();

    @DefaultStringValue("The Movie's (audio-only or video) volume.")
    public String volumeDescription();

    @DefaultStringValue("Base Width")
    public String baseWidthLabel();

    @DefaultStringValue("The width of the non-pointy end of an arrow.")
    public String baseWidthDescription();

    @DefaultStringValue("Head Width")
    public String headWidthLabel();

    @DefaultStringValue("The width of the side of the triangle formed by the tip of the arrow, which is parallel to the base.")
    public String headWidthDescription();

    @DefaultStringValue("Arrow Angle")
    public String arrowAngleLabel();

    @DefaultStringValue("The angle between the midline and the outer diagonal of the arrow's tip.")
    public String arrowAngleDescription();

    @DefaultStringValue("Base Angle")
    public String baseAngleLabel();

    @DefaultStringValue("The angle between the outer diagonal and the inner diagonal of the arrow's tip.")
    public String baseAngleDescription();

    @DefaultStringValue("Arrow Type")
    public String arrowTypeLabel();

    @DefaultStringValue("Indicates at which end the tip of the arrow should be.")
    public String arrowTypeDescription();

    @DefaultStringValue("Transform")
    public String transformLabel();

    @DefaultStringValue("The transformation matrix.")
    public String transformDescription();

    @DefaultStringValue("Miter Limit")
    public String miterLimitLabel();

    @DefaultStringValue("The pixel limit Miter LineJoins extend.")
    public String miterLimitDescription();

    @DefaultStringValue("Curve Factor")
    public String curveFactorLabel();

    @DefaultStringValue("The curvyness factor applied to curves on a spline.")
    public String curveFactorDescription();

    @DefaultStringValue("Angle Factor")
    public String angleFactorLabel();

    @DefaultStringValue("The angle factor applied to curves on a spline.")
    public String angleFactorDescription();

    @DefaultStringValue("Line Flatten")
    public String lineFlattenLabel();

    @DefaultStringValue("If we flatten 3 co-linear points on a spline.")
    public String lineFlattenDescription();

    @DefaultStringValue("Shear")
    public String shearLabel();

    @DefaultStringValue("Shear transform.")
    public String shearDescription();

    @DefaultStringValue("Fill Shape For Selection")
    public String fillShapeForSelectionLabel();

    @DefaultStringValue("If a shape should be filled for events on the selection layer.")
    public String fillShapeForSelectionDescription();

    @DefaultStringValue("Fill Shape Bounding Box For Selection")
    public String fillBoundsForSelectionLabel();

    @DefaultStringValue("If a shape's bounding box should be filled for events on the selection layer.")
    public String fillBoundsForSelectionDescription();

    @DefaultStringValue("The pixels that will be used to increase the bounding box size on the selection layer.")
    public String selectionBoundsOffsetLabel();

    @DefaultStringValue("The pixels that will be used to increase the bounding box size on the selection layer.")
    public String selectionBoundsOffsetDescription();

    @DefaultStringValue("The pixels that will be used to increase the stroke wdith on the selection layer.")
    public String selectionStrokeOffsetLabel();

    @DefaultStringValue("The pixels that will be used to increase the bounding box on the selection layer.")
    public String selectionStrokeOffsetDescription();

    @DefaultStringValue("Transformable")
    public String transformableLabel();

    @DefaultStringValue("If a Layer applies global transforms from the Viewport.")
    public String transformableDescription();

    @DefaultStringValue("Dash Offset")
    public String dashOffsetLabel();

    @DefaultStringValue("Pixel units to offset before dash array is applied.")
    public String dashOffsetDescription();

    @DefaultStringValue("Auto Play")
    public String autoPlayLabel();

    @DefaultStringValue("If a Movie automatically plays on first draw.")
    public String autoPlayDescription();

    @DefaultStringValue("Playback Rate")
    public String playbackRateLabel();

    @DefaultStringValue("Movie playback rate ( 1.0 is normal, 2.0 is double speed, -0.5 is half speed in reverse,etc ).")
    public String playbackRateDescription();

    @DefaultStringValue("Show Poster")
    public String showPosterLabel();

    @DefaultStringValue("If a Movie has a poster image, show this when Movie is not playing.")
    public String showPosterDescription();

    @DefaultStringValue("Top Width")
    public String topWidthLabel();

    @DefaultStringValue("Top width of an IsoscelesTrapezoid.")
    public String topWidthDescription();

    @DefaultStringValue("Bottom Width")
    public String bottomWidthLabel();

    @DefaultStringValue("Bottom width of an IsoscelesTrapezoid.")
    public String bottomWidthDescription();

    @DefaultStringValue("Image Selection Mode")
    public String imageSelectionModeLabel();

    @DefaultStringValue("If events on a Image use the bounding box, or ignore transparent pixels.")
    public String imageSelectionModeDescription();

    @DefaultStringValue("Drag Mode")
    public String dragModeLabel();

    @DefaultStringValue("If a shape is dragged on the Drag Layer, or in it's own Layer.")
    public String dragModeDescription();

    @DefaultStringValue("Path")
    public String pathLabel();

    @DefaultStringValue("A valid SVG Path specification.")
    public String pathDescription();

    @DefaultStringValue("Tick Rate")
    public String tickRateLabel();

    @DefaultStringValue("Ticks per second of a Sprite.")
    public String tickRateDescription();

    @DefaultStringValue("Sprite Behavior Map")
    public String spriteBehaviorMapLabel();

    @DefaultStringValue("Map of Sprite Behaviors.")
    public String spriteBehaviorMapDescription();

    @DefaultStringValue("Sprite Behavior")
    public String spriteBehaviorLabel();

    @DefaultStringValue("Current Sprite Behavior.")
    public String spriteBehaviorDescription();

    @DefaultStringValue("Editable")
    public String editableLabel();

    @DefaultStringValue("If an item is editable.")
    public String editableDescription();

    @DefaultStringValue("Active")
    public String activeLabel();

    @DefaultStringValue("If an item is active.")
    public String activeDescription();

    @DefaultStringValue("Value")
    public String valueLabel();

    @DefaultStringValue("Numeric value of an item.")
    public String valueDescription();

    @DefaultStringValue("Color")
    public String colorLabel();

    @DefaultStringValue("Color of an item.")
    public String colorDescription();

    @DefaultStringValue("Matrix")
    public String matrixLabel();

    @DefaultStringValue("Convolve filter matrix.")
    public String matrixDescription();

    @DefaultStringValue("Inverted")
    public String invertedLabel();

    @DefaultStringValue("Filter is inverted.")
    public String invertedDescription();

    @DefaultStringValue("Gain")
    public String gainLabel();

    @DefaultStringValue("Gain of a filter.")
    public String gainDescription();

    @DefaultStringValue("Bias")
    public String biasLabel();

    @DefaultStringValue("Bias of a filter.")
    public String biasDescription();

    @DefaultStringValue("Unit")
    public String textUnitLabel();

    @DefaultStringValue("Unit size of Text (px,pt).")
    public String textUnitDescription();

    @DefaultStringValue("Head Direction")
    public String headDirectionLabel();

    @DefaultStringValue("Head Direction of a PolyLine connector.")
    public String headDirectionDescription();

    @DefaultStringValue("Tail Direction")
    public String tailDirectionLabel();

    @DefaultStringValue("Tail Direction of a PolyLine connector.")
    public String tailDirectionDescription();

    @DefaultStringValue("Head Offset")
    public String headOffsetLabel();

    @DefaultStringValue("Head Offset of a PolyLine connector.")
    public String headOffsetDescription();

    @DefaultStringValue("Tail Offset")
    public String tailOffsetLabel();

    @DefaultStringValue("Tail Offset of a PolyLine connector.")
    public String tailOffsetDescription();

    @DefaultStringValue("Correction Offset")
    public String correctionOffsetLabel();

    @DefaultStringValue("Correction Offset of a PolyLine connector.")
    public String correctionOffsetDescription();

    @DefaultStringValue("Event Propgation")
    public String eventPropagationModeLabel();

    @DefaultStringValue("Event propgation from Shapes to parent Group's")
    public String eventPropagationModedDescription();

    @DefaultStringValue("Decorator Length")
    public String decoratorLengthLabel();

    @DefaultStringValue("Decorator Length")
    public String decoratorLengthDescription();

    @DefaultStringValue("Arrow Ratio")
    public String arrowRatioLabel();

    @DefaultStringValue("Arrow Ratio")
    public String arrowRatioDescription();
}