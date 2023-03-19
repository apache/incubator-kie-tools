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

package com.ait.lienzo.client.core;

import java.util.Objects;

import com.ait.lienzo.client.core.i18n.MessageConstants;

/**
 * This class is used internally by the toolkit to provide type-safe property access.
 */
public class Attribute {

    protected static final MessageConstants MESSAGES = MessageConstants.MESSAGES;

    public static final Attribute WIDTH = new Attribute("width", MESSAGES.widthLabel(), MESSAGES.widthDescription(), true);

    public static final Attribute HEIGHT = new Attribute("height", MESSAGES.heightLabel(), MESSAGES.heightDescription(), true);

    public static final Attribute MIN_WIDTH = new Attribute("minWidth", MESSAGES.minWidthLabel(), MESSAGES.minWidthDescription(), true);

    public static final Attribute MAX_WIDTH = new Attribute("maxWidth", MESSAGES.maxWidthLabel(), MESSAGES.maxWidthDescription(), true);

    public static final Attribute MIN_HEIGHT = new Attribute("minHeight", MESSAGES.minHeightLabel(), MESSAGES.minHeightDescription(), true);

    public static final Attribute MAX_HEIGHT = new Attribute("maxHeight", MESSAGES.maxHeightLabel(), MESSAGES.maxHeightDescription(), true);

    public static final Attribute CORNER_RADIUS = new Attribute("cornerRadius", MESSAGES.cornerRadiusLabel(), MESSAGES.cornerRadiusDescription(), true);

    public static final Attribute FILL = new Attribute("fill", MESSAGES.fillLabel(), MESSAGES.fillDescription(), true);

    public static final Attribute STROKE = new Attribute("stroke", MESSAGES.strokeLabel(), MESSAGES.strokeDescription(), true);

    public static final Attribute STROKE_WIDTH = new Attribute("strokeWidth", MESSAGES.strokeWidthLabel(), MESSAGES.strokeWidthDescription(), true);

    public static final Attribute LINE_JOIN = new Attribute("lineJoin", MESSAGES.lineJoinLabel(), MESSAGES.lineJoinDescription());

    public static final Attribute X = new Attribute("x", MESSAGES.xLabel(), MESSAGES.xDescription(), true);

    public static final Attribute Y = new Attribute("y", MESSAGES.yLabel(), MESSAGES.yDescription(), true);

    public static final Attribute VISIBLE = new Attribute("visible", MESSAGES.visibleLabel(), MESSAGES.visibleDescription());

    public static final Attribute LISTENING = new Attribute("listening", MESSAGES.listeningLabel(), MESSAGES.listeningDescription());

    public static final Attribute ID = new Attribute("id", MESSAGES.idLabel(), MESSAGES.idDescription());

    public static final Attribute ALPHA = new Attribute("alpha", MESSAGES.alphaLabel(), MESSAGES.alphaDescription(), true);

    public static final Attribute FILL_ALPHA = new Attribute("fillAlpha", MESSAGES.fillAlphaLabel(), MESSAGES.fillAlphaDescription(), true);

    public static final Attribute STROKE_ALPHA = new Attribute("strokeAlpha", MESSAGES.strokeAlphaLabel(), MESSAGES.strokeAlphaDescription(), true);

    public static final Attribute SCALE = new Attribute("scale", MESSAGES.scaleLabel(), MESSAGES.scaleDescription(), true);

    public static final Attribute ROTATION = new Attribute("rotation", MESSAGES.rotationLabel(), MESSAGES.rotationDescription(), true);

    public static final Attribute OFFSET = new Attribute("offset", MESSAGES.offsetLabel(), MESSAGES.offsetDescription(), true);

    public static final Attribute SHEAR = new Attribute("shear", MESSAGES.shearLabel(), MESSAGES.shearDescription(), true);

    public static final Attribute TRANSFORM = new Attribute("transform", MESSAGES.transformLabel(), MESSAGES.transformDescription());

    public static final Attribute DRAGGABLE = new Attribute("draggable", MESSAGES.draggableLabel(), MESSAGES.draggableDescription());

    public static final Attribute EDITABLE = new Attribute("editable", MESSAGES.editableLabel(), MESSAGES.editableDescription());

    public static final Attribute FILL_SHAPE_FOR_SELECTION = new Attribute("fillShapeForSelection", MESSAGES.fillShapeForSelectionLabel(), MESSAGES.fillShapeForSelectionDescription());

    public static final Attribute FILL_BOUNDS_FOR_SELECTION = new Attribute("fillBoundsForSelection", MESSAGES.fillBoundsForSelectionLabel(), MESSAGES.fillBoundsForSelectionDescription());

    public static final Attribute SELECTION_BOUNDS_OFFSET = new Attribute("selectionBoundsOffset", MESSAGES.selectionBoundsOffsetLabel(), MESSAGES.selectionBoundsOffsetDescription());

    public static final Attribute SELECTION_STROKE_OFFSET = new Attribute("selectionStrokeOffset", MESSAGES.selectionStrokeOffsetLabel(), MESSAGES.selectionStrokeOffsetDescription());

    public static final Attribute DRAG_CONSTRAINT = new Attribute("dragConstraint", MESSAGES.dragConstraintLabel(), MESSAGES.dragConstraintDescription());

    public static final Attribute DRAG_BOUNDS = new Attribute("dragBounds", MESSAGES.dragBoundsLabel(), MESSAGES.dragBoundsDescription());

    public static final Attribute RADIUS = new Attribute("radius", MESSAGES.radiusLabel(), MESSAGES.radiusDescription(), true);

    public static final Attribute RADIUS_X = new Attribute("radiusX", MESSAGES.radiusXLabel(), MESSAGES.radiusXDescription(), true);

    public static final Attribute RADIUS_Y = new Attribute("radiusY", MESSAGES.radiusYLabel(), MESSAGES.radiusYDescription(), true);

    public static final Attribute CLEAR_LAYER_BEFORE_DRAW = new Attribute("clearLayerBeforeDraw", MESSAGES.clearLayerBeforeDrawLabel(), MESSAGES.clearLayerBeforeDrawDescription());

    public static final Attribute TRANSFORMABLE = new Attribute("transformable", MESSAGES.transformableLabel(), MESSAGES.transformableDescription());

    public static final Attribute TEXT = new Attribute("text", MESSAGES.textLabel(), MESSAGES.textDescription());

    public static final Attribute FONT_SIZE = new Attribute("fontSize", MESSAGES.fontSizeLabel(), MESSAGES.fontSizeDescription(), true);

    public static final Attribute FONT_FAMILY = new Attribute("fontFamily", MESSAGES.fontFamilyLabel(), MESSAGES.fontFamilyDescription());

    public static final Attribute FONT_STYLE = new Attribute("fontStyle", MESSAGES.fontStyleLabel(), MESSAGES.fontStyleDescription());

    public static final Attribute POINTS = new Attribute("points", MESSAGES.pointsLabel(), MESSAGES.pointsDescription(), true);

    public static final Attribute STAR_POINTS = new Attribute("starPoints", MESSAGES.starPointsLabel(), MESSAGES.starPointsDescription(), true);

    public static final Attribute LINE_CAP = new Attribute("lineCap", MESSAGES.lineCapLabel(), MESSAGES.lineCapDescription());

    public static final Attribute DASH_ARRAY = new Attribute("dashArray", MESSAGES.dashArrayLabel(), MESSAGES.dashArrayDescription(), true);

    public static final Attribute DASH_OFFSET = new Attribute("dashOffset", MESSAGES.dashOffsetLabel(), MESSAGES.dashOffsetDescription(), true);

    public static final Attribute SIDES = new Attribute("sides", MESSAGES.sidesLabel(), MESSAGES.sidesDescription(), true);

    public static final Attribute OUTER_RADIUS = new Attribute("outerRadius", MESSAGES.outerRadiusLabel(), MESSAGES.outerRadiusDescription(), true);

    public static final Attribute INNER_RADIUS = new Attribute("innerRadius", MESSAGES.innerRadiusLabel(), MESSAGES.innerRadiusDescription(), true);

    public static final Attribute SKEW = new Attribute("skew", MESSAGES.skewLabel(), MESSAGES.skewDescription(), true);

    public static final Attribute SHADOW = new Attribute("shadow", MESSAGES.shadowLabel(), MESSAGES.shadowDescription());

    public static final Attribute START_ANGLE = new Attribute("startAngle", MESSAGES.startAngleLabel(), MESSAGES.startAngleDescription(), true);

    public static final Attribute END_ANGLE = new Attribute("endAngle", MESSAGES.endAngleLabel(), MESSAGES.endAngleDescription(), true);

    public static final Attribute COUNTER_CLOCKWISE = new Attribute("counterClockwise", MESSAGES.counterClockwiseLabel(), MESSAGES.counterClockwiseDescription());

    public static final Attribute CONTROL_POINTS = new Attribute("controlPoints", MESSAGES.controlPointsLabel(), MESSAGES.controlPointsDescription());

    public static final Attribute TEXT_BASELINE = new Attribute("textBaseline", MESSAGES.textBaseLineLabel(), MESSAGES.textBaseLineDescription());

    public static final Attribute TEXT_ALIGN = new Attribute("textAlign", MESSAGES.textAlignLabel(), MESSAGES.textAlignDescription());

    public static final Attribute TEXT_UNIT = new Attribute("textUnit", MESSAGES.textUnitLabel(), MESSAGES.textUnitDescription());

    public static final Attribute CLIPPED_IMAGE_WIDTH = new Attribute("clippedImageWidth", MESSAGES.clippedImageWidthLabel(), MESSAGES.clippedImageWidthDescription(), true);

    public static final Attribute CLIPPED_IMAGE_HEIGHT = new Attribute("clippedImageHeight", MESSAGES.clippedImageHeightLabel(), MESSAGES.clippedImageHeightDescription(), true);

    public static final Attribute CLIPPED_IMAGE_START_X = new Attribute("clippedImageStartX", MESSAGES.clippedImageStartXLabel(), MESSAGES.clippedImageStartXDescription(), true);

    public static final Attribute CLIPPED_IMAGE_START_Y = new Attribute("clippedImageStartY", MESSAGES.clippedImageStartYLabel(), MESSAGES.clippedImageStartYDescription(), true);

    public static final Attribute CLIPPED_IMAGE_DESTINATION_WIDTH = new Attribute("clippedImageDestinationWidth", MESSAGES.clippedImageDestinationWidthLabel(), MESSAGES.clippedImageDestinationWidthDescription(), true);

    public static final Attribute CLIPPED_IMAGE_DESTINATION_HEIGHT = new Attribute("clippedImageDestinationHeight", MESSAGES.clippedImageDestinationHeightLabel(), MESSAGES.clippedImageDestinationHeightDescription(), true);

    public static final Attribute SERIALIZATION_MODE = new Attribute("serializationMode", MESSAGES.serializationModeLabel(), MESSAGES.serializationModeDescription());

    public static final Attribute URL = new Attribute("url", MESSAGES.urlLabel(), MESSAGES.urlDescription());

    public static final Attribute LOOP = new Attribute("loop", MESSAGES.loopLabel(), MESSAGES.loopDescription());

    public static final Attribute AUTO_PLAY = new Attribute("autoPlay", MESSAGES.autoPlayLabel(), MESSAGES.autoPlayDescription());

    public static final Attribute PLAYBACK_RATE = new Attribute("playbackRate", MESSAGES.playbackRateLabel(), MESSAGES.playbackRateDescription());

    public static final Attribute SHOW_POSTER = new Attribute("showPoster", MESSAGES.showPosterLabel(), MESSAGES.showPosterDescription());

    public static final Attribute VOLUME = new Attribute("volume", MESSAGES.volumeLabel(), MESSAGES.volumeDescription());

    public static final Attribute BASE_WIDTH = new Attribute("baseWidth", MESSAGES.baseWidthLabel(), MESSAGES.baseWidthDescription(), true);

    public static final Attribute HEAD_WIDTH = new Attribute("headWidth", MESSAGES.headWidthLabel(), MESSAGES.headWidthDescription(), true);

    public static final Attribute ARROW_ANGLE = new Attribute("arrowAngle", MESSAGES.arrowAngleLabel(), MESSAGES.arrowAngleDescription(), true);

    public static final Attribute BASE_ANGLE = new Attribute("baseAngle", MESSAGES.baseAngleLabel(), MESSAGES.baseAngleDescription(), true);

    public static final Attribute ARROW_TYPE = new Attribute("arrowType", MESSAGES.arrowTypeLabel(), MESSAGES.arrowTypeDescription());

    public static final Attribute MITER_LIMIT = new Attribute("miterLimit", MESSAGES.miterLimitLabel(), MESSAGES.miterLimitDescription(), true);

    public static final Attribute CURVE_FACTOR = new Attribute("curveFactor", MESSAGES.curveFactorLabel(), MESSAGES.curveFactorDescription(), true);

    public static final Attribute ANGLE_FACTOR = new Attribute("angleFactor", MESSAGES.angleFactorLabel(), MESSAGES.angleFactorDescription(), true);

    public static final Attribute LINE_FLATTEN = new Attribute("lineFlatten", MESSAGES.lineFlattenLabel(), MESSAGES.lineFlattenDescription());

    public static final Attribute TOP_WIDTH = new Attribute("topWidth", MESSAGES.topWidthLabel(), MESSAGES.topWidthDescription(), true);

    public static final Attribute BOTTOM_WIDTH = new Attribute("bottomWidth", MESSAGES.bottomWidthLabel(), MESSAGES.bottomWidthDescription(), true);

    public static final Attribute IMAGE_SELECTION_MODE = new Attribute("imageSelectionMode", MESSAGES.imageSelectionModeLabel(), MESSAGES.imageSelectionModeDescription());

    public static final Attribute DRAG_MODE = new Attribute("dragMode", MESSAGES.dragModeLabel(), MESSAGES.dragModeDescription());

    public static final Attribute PATH = new Attribute("path", MESSAGES.pathLabel(), MESSAGES.pathDescription());

    public static final Attribute TICK_RATE = new Attribute("tickRate", MESSAGES.tickRateLabel(), MESSAGES.tickRateDescription(), true);

    public static final Attribute SPRITE_BEHAVIOR_MAP = new Attribute("spriteBehaviorMap", MESSAGES.spriteBehaviorMapLabel(), MESSAGES.spriteBehaviorMapDescription());

    public static final Attribute SPRITE_BEHAVIOR = new Attribute("spriteBehavior", MESSAGES.spriteBehaviorLabel(), MESSAGES.spriteBehaviorDescription());

    public static final Attribute ACTIVE = new Attribute("active", MESSAGES.activeLabel(), MESSAGES.activeDescription());

    public static final Attribute VALUE = new Attribute("value", MESSAGES.valueLabel(), MESSAGES.valueDescription(), true);

    public static final Attribute COLOR = new Attribute("color", MESSAGES.colorLabel(), MESSAGES.colorDescription(), true);

    public static final Attribute MATRIX = new Attribute("matrix", MESSAGES.matrixLabel(), MESSAGES.matrixDescription());

    public static final Attribute INVERTED = new Attribute("inverted", MESSAGES.invertedLabel(), MESSAGES.invertedDescription());

    public static final Attribute GAIN = new Attribute("gain", MESSAGES.gainLabel(), MESSAGES.gainDescription(), true);

    public static final Attribute BIAS = new Attribute("bias", MESSAGES.biasLabel(), MESSAGES.biasDescription(), true);

    public static final Attribute HEAD_OFFSET = new Attribute("headOffset", MESSAGES.headOffsetLabel(), MESSAGES.headOffsetDescription(), true);

    public static final Attribute TAIL_OFFSET = new Attribute("tailOffset", MESSAGES.tailOffsetLabel(), MESSAGES.tailOffsetDescription(), true);

    public static final Attribute CORRECTION_OFFSET = new Attribute("correctionOffset", MESSAGES.correctionOffsetLabel(), MESSAGES.correctionOffsetDescription(), true);

    public static final Attribute HEAD_DIRECTION = new Attribute("headDirection", MESSAGES.headDirectionLabel(), MESSAGES.headDirectionDescription());

    public static final Attribute TAIL_DIRECTION = new Attribute("tailDirection", MESSAGES.tailDirectionLabel(), MESSAGES.tailDirectionDescription());

    public static final Attribute EVENT_PROPAGATION_MODE = new Attribute("eventPropagationMode", MESSAGES.eventPropagationModeLabel(), MESSAGES.eventPropagationModedDescription());

    private final String m_prop;

    private final String m_labl;

    private final String m_desc;

    private final boolean m_anim;

    protected Attribute(final String prop, final String labl, final String desc) {
        this(prop, labl, desc, false);
    }

    protected Attribute(final String prop, final String labl, final String desc, final boolean anim) {
        m_anim = anim;

        m_labl = Objects.requireNonNull(labl);

        m_prop = Objects.requireNonNull(prop);

        m_desc = Objects.requireNonNull(desc);
    }

    public final String getProperty() {
        return m_prop;
    }

    public final String getLabel() {
        return m_labl;
    }

    public final String getDescription() {
        return m_desc;
    }

    public final boolean isAnimatable() {
        return m_anim;
    }

    @Override
    public final String toString() {
        return m_prop;
    }
}