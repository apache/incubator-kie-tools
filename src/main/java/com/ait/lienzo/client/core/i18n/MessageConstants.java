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

import com.ait.lienzo.gwtlienzo.i18n.Constants;

/**
 * An I18 based interface used for Lienzo Constants.
 */
public interface MessageConstants extends Constants
{
    // @FIXME no-op class for now, just to get things compiling (mdp)
    MessageConstants MESSAGES = new MessageConstantsImpl(); //JsUtils.GWT.create(MessageConstants.class);

    @DefaultStringValue("Canvas is not supported in this browser!")
    String getCanvasUnsupportedMessage();

    @DefaultStringValue("Movie playback was aborted.")
    String moviePlaybackWasAborted();

    @DefaultStringValue("Movie network error.")
    String movieNetworkError();

    @DefaultStringValue("Movie decoding error.")
    String movieErrorInDecoding();

    @DefaultStringValue("Movie format not supported.")
    String movieFormatNotSupported();

    @DefaultStringValue("Movie not support in this browser.")
    String movieNotSupportedInThisBrowser();

    // ---------- Validation

    @DefaultStringValue("attribute is required")
    String attributeIsRequired();

    @DefaultStringValue("invalid value for type {0} [{1}]")
    // type, value
    String invalidValueForType();

    @DefaultStringValue("value should be a {0}")
    // type
    String invalidType();

    @DefaultStringValue("attribute is invalid for type {0}")
    // type
    String attributeIsInvalidForType();

    @DefaultStringValue("value must be [{0}]")
    // value
    String attributeValueMustBeFixed();

    @DefaultStringValue("no NodeFactory is registered for type '{0}'")
    // type
    String missingNodeFactory();

    @DefaultStringValue("Invalid array size. Expected value is {0}. Actual value is {1}")
    // expectedValue, actualValue
    String invalidArraySize();

    // ---------- Attributes

    @DefaultStringValue("Width")
    String widthLabel();

    @DefaultStringValue("Width value in pixels.")
    String widthDescription();

    @DefaultStringValue("Height")
    String heightLabel();

    @DefaultStringValue("Height value in pixels.")
    String heightDescription();

    @DefaultStringValue("Min Width")
    String minWidthLabel();

    @DefaultStringValue("Minimum Width value in pixels.")
    String minWidthDescription();

    @DefaultStringValue("Max Width")
    String maxWidthLabel();

    @DefaultStringValue("Maximum Width value in pixels.")
    String maxWidthDescription();

    @DefaultStringValue("Min Height")
    String minHeightLabel();

    @DefaultStringValue("Minimum Height value in pixels.")
    String minHeightDescription();

    @DefaultStringValue("Max Height")
    String maxHeightLabel();

    @DefaultStringValue("Maximum Height value in pixels.")
    String maxHeightDescription();

    @DefaultStringValue("Corner Radius")
    String cornerRadiusLabel();

    @DefaultStringValue("The radius of a 90 degree arc, which is used as a rounded corner.")
    String cornerRadiusDescription();

    @DefaultStringValue("Fill")
    String fillLabel();

    @DefaultStringValue("The color or gradient used to fill a shape.")
    String fillDescription();

    @DefaultStringValue("Stroke")
    String strokeLabel();

    @DefaultStringValue("The color of the outline of a shape.")
    String strokeDescription();

    @DefaultStringValue("Stroke Width")
    String strokeWidthLabel();

    @DefaultStringValue("Width in pixels of the outline of a shape.")
    String strokeWidthDescription();

    @DefaultStringValue("Line Join")
    String lineJoinLabel();

    @DefaultStringValue("Specifies how the connection of individual stroke segments will be drawn.")
    String lineJoinDescription();

    @DefaultStringValue("X")
    String xLabel();

    @DefaultStringValue("X coordinate.")
    String xDescription();

    @DefaultStringValue("Y")
    String yLabel();

    @DefaultStringValue("Y coordinate.")
    String yDescription();

    @DefaultStringValue("Visible")
    String visibleLabel();

    @DefaultStringValue("Indicates if the shape is visible or not.")
    String visibleDescription();

    @DefaultStringValue("Listening")
    String listeningLabel();

    @DefaultStringValue("Indicates if the shape is listening for events.")
    String listeningDescription();

    @DefaultStringValue("ID")
    String idLabel();

    @DefaultStringValue("Unique identifier for the shape.")
    String idDescription();

    @DefaultStringValue("Name")
    String nameLabel();

    @DefaultStringValue("Unique name given to the shape.")
    String nameDescription();

    @DefaultStringValue("Alpha")
    String alphaLabel();

    @DefaultStringValue("The alpha transparency for the shape.")
    String alphaDescription();

    @DefaultStringValue("Stroke Alpha")
    String strokeAlphaLabel();

    @DefaultStringValue("The alpha transparency for the shape's stroke.")
    String strokeAlphaDescription();

    @DefaultStringValue("Fill Alpha")
    String fillAlphaLabel();

    @DefaultStringValue("The alpha transparency for the shape's fill.")
    String fillAlphaDescription();

    @DefaultStringValue("Scale")
    String scaleLabel();

    @DefaultStringValue("Scale at which the shape is drawn.")
    String scaleDescription();

    @DefaultStringValue("Rotation")
    String rotationLabel();

    @DefaultStringValue("Radians used for the rotation of the shape around its origin or offset position.")
    String rotationDescription();

    @DefaultStringValue("Offset")
    String offsetLabel();

    @DefaultStringValue("The offset from origin at which a shape will be rotated around.")
    String offsetDescription();

    @DefaultStringValue("Draggable")
    String draggableLabel();

    @DefaultStringValue("Indicates if the shape can be dragged.")
    String draggableDescription();

    @DefaultStringValue("Drag Constraint")
    String dragConstraintLabel();

    @DefaultStringValue("Drag constraints for the shape limit how the shape can be dragged.")
    String dragConstraintDescription();

    @DefaultStringValue("Drag Bounds")
    String dragBoundsLabel();

    @DefaultStringValue("Drag bounds determine where the shape can be dragged.")
    String dragBoundsDescription();

    @DefaultStringValue("Radius")
    String radiusLabel();

    @DefaultStringValue("The radius of a circle or circular arc type.")
    String radiusDescription();

    @DefaultStringValue("Radius X")
    String radiusXLabel();

    @DefaultStringValue("The x radius of a elliptical arc type.")
    String radiusXDescription();

    @DefaultStringValue("Radius Y")
    String radiusYLabel();

    @DefaultStringValue("The y radius of a elliptical arc type.")
    String radiusYDescription();

    @DefaultStringValue("Clear Layer")
    String clearLayerBeforeDrawLabel();

    @DefaultStringValue("Indicates if the layer should be cleared before drawing.")
    String clearLayerBeforeDrawDescription();

    @DefaultStringValue("Text")
    String textLabel();

    @DefaultStringValue("String value of a Text shape.")
    String textDescription();

    @DefaultStringValue("Font Size")
    String fontSizeLabel();

    @DefaultStringValue("Text font size in points. i.e., 24.")
    String fontSizeDescription();

    @DefaultStringValue("Font Family")
    String fontFamilyLabel();

    @DefaultStringValue("Text font family. i.e., Tahoma.")
    String fontFamilyDescription();

    @DefaultStringValue("Font Style")
    String fontStyleLabel();

    @DefaultStringValue("Text font style. e.g., bold, italic, normal, etc.")
    String fontStyleDescription();

    @DefaultStringValue("Points")
    String pointsLabel();

    @DefaultStringValue("Number of points the shape has.")
    String pointsDescription();

    @DefaultStringValue("Star points.")
    String starPointsLabel();

    @DefaultStringValue("Number of points the star has.")
    String starPointsDescription();

    @DefaultStringValue("Line Cap")
    String lineCapLabel();

    @DefaultStringValue("Specifies how the end of a shapes stroke will be drawn.")
    String lineCapDescription();

    @DefaultStringValue("Dash Array")
    String dashArrayLabel();

    @DefaultStringValue("The outline of the shape will be drawn as a dashed line. The dash array specifies how the dashes are drawn.")
    String dashArrayDescription();

    @DefaultStringValue("Sides")
    String sidesLabel();

    @DefaultStringValue("Number of sides the shape has.")
    String sidesDescription();

    @DefaultStringValue("Outer Radius")
    String outerRadiusLabel();

    @DefaultStringValue("The radius of the shape's outer enclosing circle.")
    String outerRadiusDescription();

    @DefaultStringValue("Inner Radius")
    String innerRadiusLabel();

    @DefaultStringValue("The radius of the shape's inner enclosing circle.")
    String innerRadiusDescription();

    @DefaultStringValue("Skew")
    String skewLabel();

    @DefaultStringValue("The skew in pixels of a Parallelogram.")
    String skewDescription();

    @DefaultStringValue("Shadow")
    String shadowLabel();

    @DefaultStringValue("The value for the shape's shadow.")
    String shadowDescription();

    @DefaultStringValue("Start Angle")
    String startAngleLabel();

    @DefaultStringValue("The start angle of a shape's circular arc.")
    String startAngleDescription();

    @DefaultStringValue("End Angle")
    String endAngleLabel();

    @DefaultStringValue("The end angle of a shape's circular arc.")
    String endAngleDescription();

    @DefaultStringValue("Counter Clockwise")
    String counterClockwiseLabel();

    @DefaultStringValue("Indicates if the shape's circular arc should be drawn counter clockwise.")
    String counterClockwiseDescription();

    @DefaultStringValue("Control Points")
    String controlPointsLabel();

    @DefaultStringValue("The control points of a Bezier or Quadratic curve.")
    String controlPointsDescription();

    @DefaultStringValue("Text Baseline")
    String textBaseLineLabel();

    @DefaultStringValue("Vertical positioning for the text in the canvas.")
    String textBaseLineDescription();

    @DefaultStringValue("Text Align")
    String textAlignLabel();

    @DefaultStringValue("Horizontal positioning for the text in the canvas.")
    String textAlignDescription();

    @DefaultStringValue("Clipped Image Width")
    String clippedImageWidthLabel();

    @DefaultStringValue("The width of the clipped image (i.e., x coordinate where clipping ends).")
    String clippedImageWidthDescription();

    @DefaultStringValue("Clipped Image Height")
    String clippedImageHeightLabel();

    @DefaultStringValue("The height of the clipped image (i.e., y coordinate where clipping ends).")
    String clippedImageHeightDescription();

    @DefaultStringValue("Clipped Image Destination Width")
    String clippedImageDestinationWidthLabel();

    @DefaultStringValue("The destination width of the clipped image.")
    String clippedImageDestinationWidthDescription();

    @DefaultStringValue("Clipped Image Destination Height")
    String clippedImageDestinationHeightLabel();

    @DefaultStringValue("The destination height of the clipped image.")
    String clippedImageDestinationHeightDescription();

    @DefaultStringValue("Clipped Image X")
    String clippedImageStartXLabel();

    @DefaultStringValue("The x coordinate where clipping for the image begins.")
    String clippedImageStartXDescription();

    @DefaultStringValue("Clipped Image Y")
    String clippedImageStartYLabel();

    @DefaultStringValue("The y coordinate where clipping for the image begins.")
    String clippedImageStartYDescription();

    @DefaultStringValue("Serialization Mode")
    String serializationModeLabel();

    @DefaultStringValue("Used when deserializing a Picture.")
    String serializationModeDescription();

    @DefaultStringValue("URL")
    String urlLabel();

    @DefaultStringValue("Source URL for a Picture or Movie.")
    String urlDescription();

    @DefaultStringValue("Loop")
    String loopLabel();

    @DefaultStringValue("Indicates if the Movie should loop.")
    String loopDescription();

    @DefaultStringValue("Volume")
    String volumeLabel();

    @DefaultStringValue("The Movie's (audio-only or video) volume.")
    String volumeDescription();

    @DefaultStringValue("Base Width")
    String baseWidthLabel();

    @DefaultStringValue("The width of the non-pointy end of an arrow.")
    String baseWidthDescription();

    @DefaultStringValue("Head Width")
    String headWidthLabel();

    @DefaultStringValue("The width of the side of the triangle formed by the tip of the arrow, which is parallel to the base.")
    String headWidthDescription();

    @DefaultStringValue("Arrow Angle")
    String arrowAngleLabel();

    @DefaultStringValue("The angle between the midline and the outer diagonal of the arrow's tip.")
    String arrowAngleDescription();

    @DefaultStringValue("Base Angle")
    String baseAngleLabel();

    @DefaultStringValue("The angle between the outer diagonal and the inner diagonal of the arrow's tip.")
    String baseAngleDescription();

    @DefaultStringValue("Arrow Type")
    String arrowTypeLabel();

    @DefaultStringValue("Indicates at which end the tip of the arrow should be.")
    String arrowTypeDescription();

    @DefaultStringValue("Transform")
    String transformLabel();

    @DefaultStringValue("The transformation matrix.")
    String transformDescription();

    @DefaultStringValue("Miter Limit")
    String miterLimitLabel();

    @DefaultStringValue("The pixel limit Miter LineJoins extend.")
    String miterLimitDescription();

    @DefaultStringValue("Curve Factor")
    String curveFactorLabel();

    @DefaultStringValue("The curvyness factor applied to curves on a spline.")
    String curveFactorDescription();

    @DefaultStringValue("Angle Factor")
    String angleFactorLabel();

    @DefaultStringValue("The angle factor applied to curves on a spline.")
    String angleFactorDescription();

    @DefaultStringValue("Line Flatten")
    String lineFlattenLabel();

    @DefaultStringValue("If we flatten 3 co-linear points on a spline.")
    String lineFlattenDescription();

    @DefaultStringValue("Shear")
    String shearLabel();

    @DefaultStringValue("Shear transform.")
    String shearDescription();

    @DefaultStringValue("Fill Shape For Selection")
    String fillShapeForSelectionLabel();

    @DefaultStringValue("If a shape should be filled for events on the selection layer.")
    String fillShapeForSelectionDescription();

    @DefaultStringValue("Fill Shape Bounding Box For Selection")
    String fillBoundsForSelectionLabel();

    @DefaultStringValue("If a shape's bounding box should be filled for events on the selection layer.")
    String fillBoundsForSelectionDescription();

    @DefaultStringValue("The pixels that will be used to increase the bounding box size on the selection layer.")
    String selectionBoundsOffsetLabel();

    @DefaultStringValue("The pixels that will be used to increase the bounding box size on the selection layer.")
    String selectionBoundsOffsetDescription();

    @DefaultStringValue("The pixels that will be used to increase the stroke wdith on the selection layer.")
    String selectionStrokeOffsetLabel();

    @DefaultStringValue("The pixels that will be used to increase the bounding box on the selection layer.")
    String selectionStrokeOffsetDescription();

    @DefaultStringValue("Transformable")
    String transformableLabel();

    @DefaultStringValue("If a Layer applies global transforms from the Viewport.")
    String transformableDescription();

    @DefaultStringValue("Dash Offset")
    String dashOffsetLabel();

    @DefaultStringValue("Pixel units to offset before dash array is applied.")
    String dashOffsetDescription();

    @DefaultStringValue("Auto Play")
    String autoPlayLabel();

    @DefaultStringValue("If a Movie automatically plays on first draw.")
    String autoPlayDescription();

    @DefaultStringValue("Playback Rate")
    String playbackRateLabel();

    @DefaultStringValue("Movie playback rate ( 1.0 is normal, 2.0 is double speed, -0.5 is half speed in reverse,etc ).")
    String playbackRateDescription();

    @DefaultStringValue("Show Poster")
    String showPosterLabel();

    @DefaultStringValue("If a Movie has a poster image, show this when Movie is not playing.")
    String showPosterDescription();

    @DefaultStringValue("Top Width")
    String topWidthLabel();

    @DefaultStringValue("Top width of an IsoscelesTrapezoid.")
    String topWidthDescription();

    @DefaultStringValue("Bottom Width")
    String bottomWidthLabel();

    @DefaultStringValue("Bottom width of an IsoscelesTrapezoid.")
    String bottomWidthDescription();

    @DefaultStringValue("Image Selection Mode")
    String imageSelectionModeLabel();

    @DefaultStringValue("If events on a Image use the bounding box, or ignore transparent pixels.")
    String imageSelectionModeDescription();

    @DefaultStringValue("Drag Mode")
    String dragModeLabel();

    @DefaultStringValue("If a shape is dragged on the Drag Layer, or in it's own Layer.")
    String dragModeDescription();

    @DefaultStringValue("Path")
    String pathLabel();

    @DefaultStringValue("A valid SVG Path specification.")
    String pathDescription();

    @DefaultStringValue("Tick Rate")
    String tickRateLabel();

    @DefaultStringValue("Ticks per second of a Sprite.")
    String tickRateDescription();

    @DefaultStringValue("Sprite Behavior Map")
    String spriteBehaviorMapLabel();

    @DefaultStringValue("Map of Sprite Behaviors.")
    String spriteBehaviorMapDescription();

    @DefaultStringValue("Sprite Behavior")
    String spriteBehaviorLabel();

    @DefaultStringValue("Current Sprite Behavior.")
    String spriteBehaviorDescription();

    @DefaultStringValue("Editable")
    String editableLabel();

    @DefaultStringValue("If an item is editable.")
    String editableDescription();

    @DefaultStringValue("Active")
    String activeLabel();

    @DefaultStringValue("If an item is active.")
    String activeDescription();

    @DefaultStringValue("Value")
    String valueLabel();

    @DefaultStringValue("Numeric value of an item.")
    String valueDescription();

    @DefaultStringValue("Color")
    String colorLabel();

    @DefaultStringValue("Color of an item.")
    String colorDescription();

    @DefaultStringValue("Matrix")
    String matrixLabel();

    @DefaultStringValue("Convolve filter matrix.")
    String matrixDescription();

    @DefaultStringValue("Inverted")
    String invertedLabel();

    @DefaultStringValue("Filter is inverted.")
    String invertedDescription();

    @DefaultStringValue("Gain")
    String gainLabel();

    @DefaultStringValue("Gain of a filter.")
    String gainDescription();

    @DefaultStringValue("Bias")
    String biasLabel();

    @DefaultStringValue("Bias of a filter.")
    String biasDescription();

    @DefaultStringValue("Unit")
    String textUnitLabel();

    @DefaultStringValue("Unit size of Text (px,pt).")
    String textUnitDescription();

    @DefaultStringValue("Head Direction")
    String headDirectionLabel();

    @DefaultStringValue("Head Direction of a PolyLine connector.")
    String headDirectionDescription();

    @DefaultStringValue("Tail Direction")
    String tailDirectionLabel();

    @DefaultStringValue("Tail Direction of a PolyLine connector.")
    String tailDirectionDescription();

    @DefaultStringValue("Head Offset")
    String headOffsetLabel();

    @DefaultStringValue("Head Offset of a PolyLine connector.")
    String headOffsetDescription();

    @DefaultStringValue("Tail Offset")
    String tailOffsetLabel();

    @DefaultStringValue("Tail Offset of a PolyLine connector.")
    String tailOffsetDescription();

    @DefaultStringValue("Correction Offset")
    String correctionOffsetLabel();

    @DefaultStringValue("Correction Offset of a PolyLine connector.")
    String correctionOffsetDescription();

    @DefaultStringValue("Event Propgation")
    String eventPropagationModeLabel();

    @DefaultStringValue("Event propgation from Shapes to parent Group's")
    public String eventPropagationModedDescription();

    @DefaultStringValue("Decorator Length")
    String decoratorLengthLabel();

    @DefaultStringValue("Decorator Length")
    String decoratorLengthDescription();

    @DefaultStringValue("Arrow Ratio")
    String arrowRatioLabel();

    @DefaultStringValue("Arrow Ratio")
    String arrowRatioDescription();

    class MessageConstantsImpl implements MessageConstants
    {
        private String emptyString = "";

        @Override public String getCanvasUnsupportedMessage()
        {
            return emptyString;
        }

        @Override public String moviePlaybackWasAborted()
        {
            return emptyString;
        }

        @Override public String movieNetworkError()
        {
            return emptyString;
        }

        @Override public String movieErrorInDecoding()
        {
            return emptyString;
        }

        @Override public String movieFormatNotSupported()
        {
            return emptyString;
        }

        @Override public String movieNotSupportedInThisBrowser()
        {
            return emptyString;
        }

        @Override public String attributeIsRequired()
        {
            return emptyString;
        }

        @Override public String invalidValueForType()
        {
            return emptyString;
        }

        @Override public String invalidType()
        {
            return emptyString;
        }

        @Override public String attributeIsInvalidForType()
        {
            return emptyString;
        }

        @Override public String attributeValueMustBeFixed()
        {
            return emptyString;
        }

        @Override public String missingNodeFactory()
        {
            return emptyString;
        }

        @Override public String invalidArraySize()
        {
            return emptyString;
        }

        @Override public String widthLabel()
        {
            return emptyString;
        }

        @Override public String widthDescription()
        {
            return emptyString;
        }

        @Override public String heightLabel()
        {
            return emptyString;
        }

        @Override public String heightDescription()
        {
            return emptyString;
        }

        @Override public String minWidthLabel()
        {
            return emptyString;
        }

        @Override public String minWidthDescription()
        {
            return emptyString;
        }

        @Override public String maxWidthLabel()
        {
            return emptyString;
        }

        @Override public String maxWidthDescription()
        {
            return emptyString;
        }

        @Override public String minHeightLabel()
        {
            return emptyString;
        }

        @Override public String minHeightDescription()
        {
            return emptyString;
        }

        @Override public String maxHeightLabel()
        {
            return emptyString;
        }

        @Override public String maxHeightDescription()
        {
            return emptyString;
        }

        @Override public String cornerRadiusLabel()
        {
            return emptyString;
        }

        @Override public String cornerRadiusDescription()
        {
            return emptyString;
        }

        @Override public String fillLabel()
        {
            return emptyString;
        }

        @Override public String fillDescription()
        {
            return emptyString;
        }

        @Override public String strokeLabel()
        {
            return emptyString;
        }

        @Override public String strokeDescription()
        {
            return emptyString;
        }

        @Override public String strokeWidthLabel()
        {
            return emptyString;
        }

        @Override public String strokeWidthDescription()
        {
            return emptyString;
        }

        @Override public String lineJoinLabel()
        {
            return emptyString;
        }

        @Override public String lineJoinDescription()
        {
            return emptyString;
        }

        @Override public String xLabel()
        {
            return emptyString;
        }

        @Override public String xDescription()
        {
            return emptyString;
        }

        @Override public String yLabel()
        {
            return emptyString;
        }

        @Override public String yDescription()
        {
            return emptyString;
        }

        @Override public String visibleLabel()
        {
            return emptyString;
        }

        @Override public String visibleDescription()
        {
            return emptyString;
        }

        @Override public String listeningLabel()
        {
            return emptyString;
        }

        @Override public String listeningDescription()
        {
            return emptyString;
        }

        @Override public String idLabel()
        {
            return emptyString;
        }

        @Override public String idDescription()
        {
            return emptyString;
        }

        @Override public String nameLabel()
        {
            return emptyString;
        }

        @Override public String nameDescription()
        {
            return emptyString;
        }

        @Override public String alphaLabel()
        {
            return emptyString;
        }

        @Override public String alphaDescription()
        {
            return emptyString;
        }

        @Override public String strokeAlphaLabel()
        {
            return emptyString;
        }

        @Override public String strokeAlphaDescription()
        {
            return emptyString;
        }

        @Override public String fillAlphaLabel()
        {
            return emptyString;
        }

        @Override public String fillAlphaDescription()
        {
            return emptyString;
        }

        @Override public String scaleLabel()
        {
            return emptyString;
        }

        @Override public String scaleDescription()
        {
            return emptyString;
        }

        @Override public String rotationLabel()
        {
            return emptyString;
        }

        @Override public String rotationDescription()
        {
            return emptyString;
        }

        @Override public String offsetLabel()
        {
            return emptyString;
        }

        @Override public String offsetDescription()
        {
            return emptyString;
        }

        @Override public String draggableLabel()
        {
            return emptyString;
        }

        @Override public String draggableDescription()
        {
            return emptyString;
        }

        @Override public String dragConstraintLabel()
        {
            return emptyString;
        }

        @Override public String dragConstraintDescription()
        {
            return emptyString;
        }

        @Override public String dragBoundsLabel()
        {
            return emptyString;
        }

        @Override public String dragBoundsDescription()
        {
            return emptyString;
        }

        @Override public String radiusLabel()
        {
            return emptyString;
        }

        @Override public String radiusDescription()
        {
            return emptyString;
        }

        @Override public String radiusXLabel()
        {
            return emptyString;
        }

        @Override public String radiusXDescription()
        {
            return emptyString;
        }

        @Override public String radiusYLabel()
        {
            return emptyString;
        }

        @Override public String radiusYDescription()
        {
            return emptyString;
        }

        @Override public String clearLayerBeforeDrawLabel()
        {
            return emptyString;
        }

        @Override public String clearLayerBeforeDrawDescription()
        {
            return emptyString;
        }

        @Override public String textLabel()
        {
            return emptyString;
        }

        @Override public String textDescription()
        {
            return emptyString;
        }

        @Override public String fontSizeLabel()
        {
            return emptyString;
        }

        @Override public String fontSizeDescription()
        {
            return emptyString;
        }

        @Override public String fontFamilyLabel()
        {
            return emptyString;
        }

        @Override public String fontFamilyDescription()
        {
            return emptyString;
        }

        @Override public String fontStyleLabel()
        {
            return emptyString;
        }

        @Override public String fontStyleDescription()
        {
            return emptyString;
        }

        @Override public String pointsLabel()
        {
            return emptyString;
        }

        @Override public String pointsDescription()
        {
            return emptyString;
        }

        @Override public String starPointsLabel()
        {
            return emptyString;
        }

        @Override public String starPointsDescription()
        {
            return emptyString;
        }

        @Override public String lineCapLabel()
        {
            return emptyString;
        }

        @Override public String lineCapDescription()
        {
            return emptyString;
        }

        @Override public String dashArrayLabel()
        {
            return emptyString;
        }

        @Override public String dashArrayDescription()
        {
            return emptyString;
        }

        @Override public String sidesLabel()
        {
            return emptyString;
        }

        @Override public String sidesDescription()
        {
            return emptyString;
        }

        @Override public String outerRadiusLabel()
        {
            return emptyString;
        }

        @Override public String outerRadiusDescription()
        {
            return emptyString;
        }

        @Override public String innerRadiusLabel()
        {
            return emptyString;
        }

        @Override public String innerRadiusDescription()
        {
            return emptyString;
        }

        @Override public String skewLabel()
        {
            return emptyString;
        }

        @Override public String skewDescription()
        {
            return emptyString;
        }

        @Override public String shadowLabel()
        {
            return emptyString;
        }

        @Override public String shadowDescription()
        {
            return emptyString;
        }

        @Override public String startAngleLabel()
        {
            return emptyString;
        }

        @Override public String startAngleDescription()
        {
            return emptyString;
        }

        @Override public String endAngleLabel()
        {
            return emptyString;
        }

        @Override public String endAngleDescription()
        {
            return emptyString;
        }

        @Override public String counterClockwiseLabel()
        {
            return emptyString;
        }

        @Override public String counterClockwiseDescription()
        {
            return emptyString;
        }

        @Override public String controlPointsLabel()
        {
            return emptyString;
        }

        @Override public String controlPointsDescription()
        {
            return emptyString;
        }

        @Override public String textBaseLineLabel()
        {
            return emptyString;
        }

        @Override public String textBaseLineDescription()
        {
            return emptyString;
        }

        @Override public String textAlignLabel()
        {
            return emptyString;
        }

        @Override public String textAlignDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageWidthLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageWidthDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageHeightLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageHeightDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageDestinationWidthLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageDestinationWidthDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageDestinationHeightLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageDestinationHeightDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageStartXLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageStartXDescription()
        {
            return emptyString;
        }

        @Override public String clippedImageStartYLabel()
        {
            return emptyString;
        }

        @Override public String clippedImageStartYDescription()
        {
            return emptyString;
        }

        @Override public String serializationModeLabel()
        {
            return emptyString;
        }

        @Override public String serializationModeDescription()
        {
            return emptyString;
        }

        @Override public String urlLabel()
        {
            return emptyString;
        }

        @Override public String urlDescription()
        {
            return emptyString;
        }

        @Override public String loopLabel()
        {
            return emptyString;
        }

        @Override public String loopDescription()
        {
            return emptyString;
        }

        @Override public String volumeLabel()
        {
            return emptyString;
        }

        @Override public String volumeDescription()
        {
            return emptyString;
        }

        @Override public String baseWidthLabel()
        {
            return emptyString;
        }

        @Override public String baseWidthDescription()
        {
            return emptyString;
        }

        @Override public String headWidthLabel()
        {
            return emptyString;
        }

        @Override public String headWidthDescription()
        {
            return emptyString;
        }

        @Override public String arrowAngleLabel()
        {
            return emptyString;
        }

        @Override public String arrowAngleDescription()
        {
            return emptyString;
        }

        @Override public String baseAngleLabel()
        {
            return emptyString;
        }

        @Override public String baseAngleDescription()
        {
            return emptyString;
        }

        @Override public String arrowTypeLabel()
        {
            return emptyString;
        }

        @Override public String arrowTypeDescription()
        {
            return emptyString;
        }

        @Override public String transformLabel()
        {
            return emptyString;
        }

        @Override public String transformDescription()
        {
            return emptyString;
        }

        @Override public String miterLimitLabel()
        {
            return emptyString;
        }

        @Override public String miterLimitDescription()
        {
            return emptyString;
        }

        @Override public String curveFactorLabel()
        {
            return emptyString;
        }

        @Override public String curveFactorDescription()
        {
            return emptyString;
        }

        @Override public String angleFactorLabel()
        {
            return emptyString;
        }

        @Override public String angleFactorDescription()
        {
            return emptyString;
        }

        @Override public String lineFlattenLabel()
        {
            return emptyString;
        }

        @Override public String lineFlattenDescription()
        {
            return emptyString;
        }

        @Override public String shearLabel()
        {
            return emptyString;
        }

        @Override public String shearDescription()
        {
            return emptyString;
        }

        @Override public String fillShapeForSelectionLabel()
        {
            return emptyString;
        }

        @Override public String fillShapeForSelectionDescription()
        {
            return emptyString;
        }

        @Override public String fillBoundsForSelectionLabel()
        {
            return emptyString;
        }

        @Override public String fillBoundsForSelectionDescription()
        {
            return emptyString;
        }

        @Override public String selectionBoundsOffsetLabel()
        {
            return emptyString;
        }

        @Override public String selectionBoundsOffsetDescription()
        {
            return emptyString;
        }

        @Override public String selectionStrokeOffsetLabel()
        {
            return emptyString;
        }

        @Override public String selectionStrokeOffsetDescription()
        {
            return emptyString;
        }

        @Override public String transformableLabel()
        {
            return emptyString;
        }

        @Override public String transformableDescription()
        {
            return emptyString;
        }

        @Override public String dashOffsetLabel()
        {
            return emptyString;
        }

        @Override public String dashOffsetDescription()
        {
            return emptyString;
        }

        @Override public String autoPlayLabel()
        {
            return emptyString;
        }

        @Override public String autoPlayDescription()
        {
            return emptyString;
        }

        @Override public String playbackRateLabel()
        {
            return emptyString;
        }

        @Override public String playbackRateDescription()
        {
            return emptyString;
        }

        @Override public String showPosterLabel()
        {
            return emptyString;
        }

        @Override public String showPosterDescription()
        {
            return emptyString;
        }

        @Override public String topWidthLabel()
        {
            return emptyString;
        }

        @Override public String topWidthDescription()
        {
            return emptyString;
        }

        @Override public String bottomWidthLabel()
        {
            return emptyString;
        }

        @Override public String bottomWidthDescription()
        {
            return emptyString;
        }

        @Override public String imageSelectionModeLabel()
        {
            return emptyString;
        }

        @Override public String imageSelectionModeDescription()
        {
            return emptyString;
        }

        @Override public String dragModeLabel()
        {
            return emptyString;
        }

        @Override public String dragModeDescription()
        {
            return emptyString;
        }

        @Override public String pathLabel()
        {
            return emptyString;
        }

        @Override public String pathDescription()
        {
            return emptyString;
        }

        @Override public String tickRateLabel()
        {
            return emptyString;
        }

        @Override public String tickRateDescription()
        {
            return emptyString;
        }

        @Override public String spriteBehaviorMapLabel()
        {
            return emptyString;
        }

        @Override public String spriteBehaviorMapDescription()
        {
            return emptyString;
        }

        @Override public String spriteBehaviorLabel()
        {
            return emptyString;
        }

        @Override public String spriteBehaviorDescription()
        {
            return emptyString;
        }

        @Override public String editableLabel()
        {
            return emptyString;
        }

        @Override public String editableDescription()
        {
            return emptyString;
        }

        @Override public String activeLabel()
        {
            return emptyString;
        }

        @Override public String activeDescription()
        {
            return emptyString;
        }

        @Override public String valueLabel()
        {
            return emptyString;
        }

        @Override public String valueDescription()
        {
            return emptyString;
        }

        @Override public String colorLabel()
        {
            return emptyString;
        }

        @Override public String colorDescription()
        {
            return emptyString;
        }

        @Override public String matrixLabel()
        {
            return emptyString;
        }

        @Override public String matrixDescription()
        {
            return emptyString;
        }

        @Override public String invertedLabel()
        {
            return emptyString;
        }

        @Override public String invertedDescription()
        {
            return emptyString;
        }

        @Override public String gainLabel()
        {
            return emptyString;
        }

        @Override public String gainDescription()
        {
            return emptyString;
        }

        @Override public String biasLabel()
        {
            return emptyString;
        }

        @Override public String biasDescription()
        {
            return emptyString;
        }

        @Override public String textUnitLabel()
        {
            return emptyString;
        }

        @Override public String textUnitDescription()
        {
            return emptyString;
        }

        @Override public String headDirectionLabel()
        {
            return emptyString;
        }

        @Override public String headDirectionDescription()
        {
            return emptyString;
        }

        @Override public String tailDirectionLabel()
        {
            return emptyString;
        }

        @Override public String tailDirectionDescription()
        {
            return emptyString;
        }

        @Override public String headOffsetLabel()
        {
            return emptyString;
        }

        @Override public String headOffsetDescription()
        {
            return emptyString;
        }

        @Override public String tailOffsetLabel()
        {
            return emptyString;
        }

        @Override public String tailOffsetDescription()
        {
            return emptyString;
        }

        @Override public String correctionOffsetLabel()
        {
            return emptyString;
        }

        @Override public String correctionOffsetDescription()
        {
            return emptyString;
        }

        @Override public String eventPropagationModeLabel()
        {
            return emptyString;
        }

        @Override public String eventPropagationModedDescription()
        {
            return emptyString;
        }

        @Override public String decoratorLengthLabel()
        {
            return emptyString;
        }

        @Override public String decoratorLengthDescription()
        {
            return emptyString;
        }

        @Override public String arrowRatioLabel()
        {
            return emptyString;
        }

        @Override public String arrowRatioDescription()
        {
            return emptyString;
        }
    }
}