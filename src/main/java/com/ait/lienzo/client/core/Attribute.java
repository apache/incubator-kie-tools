/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.i18n.MessageConstants;

/**
 * This class is used internally by the toolkit to provide type-safe property access. 
 */
public class Attribute implements IAttribute
{
    protected static final MessageConstants MESSAGES                         = MessageConstants.MESSAGES;

    public static final Attribute           WIDTH                            = new Attribute("width", MESSAGES.widthLabel(), MESSAGES.widthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           HEIGHT                           = new Attribute("height", MESSAGES.heightLabel(), MESSAGES.heightDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CORNER_RADIUS                    = new Attribute("cornerRadius", MESSAGES.cornerRadiusLabel(), MESSAGES.cornerRadiusDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           FILL                             = new Attribute("fill", MESSAGES.fillLabel(), MESSAGES.fillDescription(), AttributeType.FILL_TYPE);

    public static final Attribute           STROKE                           = new Attribute("stroke", MESSAGES.strokeLabel(), MESSAGES.strokeDescription(), AttributeType.STROKE_TYPE);

    public static final Attribute           STROKE_WIDTH                     = new Attribute("strokeWidth", MESSAGES.strokeWidthLabel(), MESSAGES.strokeWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           LINE_JOIN                        = new Attribute("lineJoin", MESSAGES.lineJoinLabel(), MESSAGES.lineJoinDescription(), AttributeType.LINE_JOIN_TYPE);

    public static final Attribute           X                                = new Attribute("x", MESSAGES.xLabel(), MESSAGES.xDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           Y                                = new Attribute("y", MESSAGES.yLabel(), MESSAGES.yDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           VISIBLE                          = new Attribute("visible", MESSAGES.visibleLabel(), MESSAGES.visibleDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           LISTENING                        = new Attribute("listening", MESSAGES.listeningLabel(), MESSAGES.listeningDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           ID                               = new Attribute("id", MESSAGES.idLabel(), MESSAGES.idDescription(), AttributeType.STRING_TYPE);

    public static final Attribute           NAME                             = new Attribute("name", MESSAGES.nameLabel(), MESSAGES.nameDescription(), AttributeType.STRING_TYPE);

    public static final Attribute           ALPHA                            = new Attribute("alpha", MESSAGES.alphaLabel(), MESSAGES.alphaDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           FILL_ALPHA                       = new Attribute("fillAlpha", MESSAGES.fillAlphaLabel(), MESSAGES.fillAlphaDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           STROKE_ALPHA                     = new Attribute("strokeAlpha", MESSAGES.strokeAlphaLabel(), MESSAGES.strokeAlphaDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SCALE                            = new Attribute("scale", MESSAGES.scaleLabel(), MESSAGES.scaleDescription(), AttributeType.POINT2D_TYPE);

    public static final Attribute           ROTATION                         = new Attribute("rotation", MESSAGES.rotationLabel(), MESSAGES.rotationDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           OFFSET                           = new Attribute("offset", MESSAGES.offsetLabel(), MESSAGES.offsetDescription(), AttributeType.POINT2D_TYPE);

    public static final Attribute           SHEAR                            = new Attribute("shear", MESSAGES.shearLabel(), MESSAGES.shearDescription(), AttributeType.POINT2D_TYPE);

    public static final Attribute           TRANSFORM                        = new Attribute("transform", MESSAGES.transformLabel(), MESSAGES.transformDescription(), AttributeType.TRANSFORM_TYPE);

    public static final Attribute           DRAGGABLE                        = new Attribute("draggable", MESSAGES.draggableLabel(), MESSAGES.draggableDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           FILL_SHAPE_FOR_SELECTION         = new Attribute("fillShapeForSelection", MESSAGES.fillShapeForSelectionLabel(), MESSAGES.fillShapeForSelectionDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           DRAG_CONSTRAINT                  = new Attribute("dragConstraint", MESSAGES.dragConstraintLabel(), MESSAGES.dragConstraintDescription(), AttributeType.DRAG_CONSTRAINT_TYPE);

    public static final Attribute           DRAG_BOUNDS                      = new Attribute("dragBounds", MESSAGES.dragBoundsLabel(), MESSAGES.dragBoundsDescription(), AttributeType.DRAG_BOUNDS_TYPE);

    public static final Attribute           RADIUS                           = new Attribute("radius", MESSAGES.radiusLabel(), MESSAGES.radiusDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLEAR_LAYER_BEFORE_DRAW          = new Attribute("clearLayerBeforeDraw", MESSAGES.clearLayerBeforeDrawLabel(), MESSAGES.clearLayerBeforeDrawDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           TRANSFORMABLE                    = new Attribute("transformable", MESSAGES.transformableLabel(), MESSAGES.transformableDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           TEXT                             = new Attribute("text", MESSAGES.textLabel(), MESSAGES.textDescription(), AttributeType.STRING_TYPE);

    public static final Attribute           FONT_SIZE                        = new Attribute("fontSize", MESSAGES.fontSizeLabel(), MESSAGES.fontSizeDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           FONT_FAMILY                      = new Attribute("fontFamily", MESSAGES.fontFamilyLabel(), MESSAGES.fontFamilyDescription(), AttributeType.STRING_TYPE);

    public static final Attribute           FONT_STYLE                       = new Attribute("fontStyle", MESSAGES.fontStyleLabel(), MESSAGES.fontStyleDescription(), AttributeType.STRING_TYPE);

    public static final Attribute           TEXT_PADDING                     = new Attribute("textPadding", MESSAGES.textPaddingLabel(), MESSAGES.textPaddingDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           POINTS                           = new Attribute("points", MESSAGES.pointsLabel(), MESSAGES.pointsDescription(), AttributeType.POINT2D_ARRAY_TYPE);

    public static final Attribute           STAR_POINTS                      = new Attribute("starPoints", MESSAGES.starPointsLabel(), MESSAGES.starPointsDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           LINE_CAP                         = new Attribute("lineCap", MESSAGES.lineCapLabel(), MESSAGES.lineCapDescription(), AttributeType.LINE_CAP_TYPE);

    public static final Attribute           DASH_ARRAY                       = new Attribute("dashArray", MESSAGES.dashArrayLabel(), MESSAGES.dashArrayDescription(), AttributeType.DASH_ARRAY_TYPE);

    public static final Attribute           DASH_OFFSET                      = new Attribute("dashOffset", MESSAGES.dashOffsetLabel(), MESSAGES.dashOffsetDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SIDES                            = new Attribute("sides", MESSAGES.sidesLabel(), MESSAGES.sidesDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           OUTER_RADIUS                     = new Attribute("outerRadius", MESSAGES.outerRadiusLabel(), MESSAGES.outerRadiusDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           INNER_RADIUS                     = new Attribute("innerRadius", MESSAGES.innerRadiusLabel(), MESSAGES.innerRadiusDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SKEW                             = new Attribute("skew", MESSAGES.skewLabel(), MESSAGES.skewDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SHADOW                           = new Attribute("shadow", MESSAGES.shadowLabel(), MESSAGES.shadowDescription(), AttributeType.SHADOW_TYPE);

    public static final Attribute           START_ANGLE                      = new Attribute("startAngle", MESSAGES.startAngleLabel(), MESSAGES.startAngleDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           END_ANGLE                        = new Attribute("endAngle", MESSAGES.endAngleLabel(), MESSAGES.endAngleDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           COUNTER_CLOCKWISE                = new Attribute("counterClockwise", MESSAGES.counterClockwiseLabel(), MESSAGES.counterClockwiseDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           CONTROL_POINTS                   = new Attribute("controlPoints", MESSAGES.controlPointsLabel(), MESSAGES.controlPointsDescription(), AttributeType.POINT2D_ARRAY_TYPE);

    public static final Attribute           TEXT_BASELINE                    = new Attribute("textBaseline", MESSAGES.textBaseLineLabel(), MESSAGES.textBaseLineDescription(), AttributeType.TEXT_BASELINE_TYPE);

    public static final Attribute           TEXT_ALIGN                       = new Attribute("textAlign", MESSAGES.textAlignLabel(), MESSAGES.textAlignDescription(), AttributeType.TEXT_ALIGN_TYPE);

    public static final Attribute           CLIPPED_IMAGE_WIDTH              = new Attribute("clippedImageWidth", MESSAGES.clippedImageWidthLabel(), MESSAGES.clippedImageWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLIPPED_IMAGE_HEIGHT             = new Attribute("clippedImageHeight", MESSAGES.clippedImageHeightLabel(), MESSAGES.clippedImageHeightDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLIPPED_IMAGE_START_X            = new Attribute("clippedImageStartX", MESSAGES.clippedImageStartXLabel(), MESSAGES.clippedImageStartXDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLIPPED_IMAGE_START_Y            = new Attribute("clippedImageStartY", MESSAGES.clippedImageStartYLabel(), MESSAGES.clippedImageStartYDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLIPPED_IMAGE_DESTINATION_WIDTH  = new Attribute("clippedImageDestinationWidth", MESSAGES.clippedImageDestinationWidthLabel(), MESSAGES.clippedImageDestinationWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CLIPPED_IMAGE_DESTINATION_HEIGHT = new Attribute("clippedImageDestinationHeight", MESSAGES.clippedImageDestinationHeightLabel(), MESSAGES.clippedImageDestinationHeightDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SERIALIZATION_MODE               = new Attribute("serializationMode", MESSAGES.serializationModeLabel(), MESSAGES.serializationModeDescription(), AttributeType.SERIALIZATION_MODE_TYPE);

    public static final Attribute           URL                              = new Attribute("url", MESSAGES.urlLabel(), MESSAGES.urlDescription(), AttributeType.URL_TYPE);

    public static final Attribute           LOOP                             = new Attribute("loop", MESSAGES.loopLabel(), MESSAGES.loopDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           AUTO_PLAY                        = new Attribute("autoPlay", MESSAGES.autoPlayLabel(), MESSAGES.autoPlayDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           PLAYBACK_RATE                    = new Attribute("playbackRate", MESSAGES.playbackRateLabel(), MESSAGES.playbackRateDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           SHOW_POSTER                      = new Attribute("showPoster", MESSAGES.showPosterLabel(), MESSAGES.showPosterDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           VOLUME                           = new Attribute("volume", MESSAGES.volumeLabel(), MESSAGES.volumeDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           BASE_WIDTH                       = new Attribute("baseWidth", MESSAGES.baseWidthLabel(), MESSAGES.baseWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           HEAD_WIDTH                       = new Attribute("headWidth", MESSAGES.headWidthLabel(), MESSAGES.headWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           ARROW_ANGLE                      = new Attribute("arrowAngle", MESSAGES.arrowAngleLabel(), MESSAGES.arrowAngleDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           BASE_ANGLE                       = new Attribute("baseAngle", MESSAGES.baseAngleLabel(), MESSAGES.baseAngleDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           ARROW_TYPE                       = new Attribute("arrowType", MESSAGES.arrowTypeLabel(), MESSAGES.arrowTypeDescription(), AttributeType.ARROW_TYPE);

    public static final Attribute           MITER_LIMIT                      = new Attribute("miterLimit", MESSAGES.miterLimitLabel(), MESSAGES.miterLimitDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           CURVE_FACTOR                     = new Attribute("curveFactor", MESSAGES.curveFactorLabel(), MESSAGES.curveFactorDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           ANGLE_FACTOR                     = new Attribute("angleFactor", MESSAGES.angleFactorLabel(), MESSAGES.angleFactorDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           LINE_FLATTEN                     = new Attribute("lineFlatten", MESSAGES.lineFlattenLabel(), MESSAGES.lineFlattenDescription(), AttributeType.BOOLEAN_TYPE);

    public static final Attribute           TOP_WIDTH                        = new Attribute("topWidth", MESSAGES.topWidthLabel(), MESSAGES.topWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           BOTTOM_WIDTH                     = new Attribute("bottomWidth", MESSAGES.bottomWidthLabel(), MESSAGES.bottomWidthDescription(), AttributeType.NUMBER_TYPE);

    public static final Attribute           IMAGE_SELECTION_MODE             = new Attribute("imageSelectionMode", MESSAGES.imageSelectionModeLabel(), MESSAGES.imageSelectionModeDescription(), AttributeType.IMAGE_SELECTION_MODE_TYPE);

    public static final Attribute           DRAG_MODE                        = new Attribute("dragMode", MESSAGES.dragModeLabel(), MESSAGES.dragModeDescription(), AttributeType.DRAG_MODE_TYPE);

    private final String                    m_property;

    private final String                    m_label;

    private final String                    m_description;

    private final AttributeType             m_type;

    protected Attribute(String property, String label, String description, AttributeType type)
    {
        m_type = type;

        m_label = label;

        m_property = property;

        m_description = description;
    }

    @Override
    public final AttributeType getType()
    {
        return m_type;
    }

    @Override
    public final String getProperty()
    {
        return m_property;
    }

    @Override
    public final String getLabel()
    {
        return m_label;
    }

    @Override
    public final String getDescription()
    {
        return m_description;
    }

    @Override
    public final String toString()
    {
        return m_property;
    }
}
