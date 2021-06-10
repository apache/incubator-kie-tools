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

import com.ait.lienzo.client.core.shape.json.validators.ArrayValidator;
import com.ait.lienzo.client.core.shape.json.validators.SpriteBehaviorMapValidator;
import com.ait.lienzo.client.core.shape.json.validators.BooleanValidator;
import com.ait.lienzo.client.core.shape.json.validators.ColorValidator;
import com.ait.lienzo.client.core.shape.json.validators.DragBoundsValidator;
import com.ait.lienzo.client.core.shape.json.validators.EnumValidator;
import com.ait.lienzo.client.core.shape.json.validators.IAttributeTypeValidator;
import com.ait.lienzo.client.core.shape.json.validators.LinearGradientValidator;
import com.ait.lienzo.client.core.shape.json.validators.MultiTypeValidator;
import com.ait.lienzo.client.core.shape.json.validators.NumberValidator;
import com.ait.lienzo.client.core.shape.json.validators.PatternGradientValidator;
import com.ait.lienzo.client.core.shape.json.validators.Point2DValidator;
import com.ait.lienzo.client.core.shape.json.validators.RadialGradientValidator;
import com.ait.lienzo.client.core.shape.json.validators.ShadowValidator;
import com.ait.lienzo.client.core.shape.json.validators.StringValidator;
import com.ait.lienzo.client.core.shape.json.validators.TransformValidator;
import com.ait.lienzo.client.core.shape.json.validators.URLValidator;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.ImageSerializationMode;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;

/**
 * Each {@link Attribute} has an AttributeType.
 * It's basically an Enum that can be extended by toolkit users.
 * <p>
 * Each AttributeType has its own {@link IAttributeTypeValidator}
 * that is used when deserializing from JSON.
 */
public class AttributeType
{
    public static final AttributeType           TRANSFORM_TYPE              = new AttributeType(TransformValidator.INSTANCE);

    public static final AttributeType           COLOR_TYPE                  = new AttributeType(ColorValidator.INSTANCE);

    public static final AttributeType           STRING_TYPE                 = new AttributeType(StringValidator.INSTANCE);

    public static final AttributeType           URL_TYPE                    = new AttributeType(URLValidator.INSTANCE);

    public static final AttributeType           NUMBER_TYPE                 = new AttributeType(NumberValidator.INSTANCE);

    public static final AttributeType           NUMBER_ARRAY_TYPE           = new AttributeType(new ArrayValidator(NumberValidator.INSTANCE));

    public static final AttributeType           BOOLEAN_TYPE                = new AttributeType(BooleanValidator.INSTANCE);

    public static final AttributeType           POINT2D_TYPE                = new AttributeType(Point2DValidator.INSTANCE);

    public static final AttributeType           POINT2D_ARRAY_TYPE          = new AttributeType(new ArrayValidator(Point2DValidator.INSTANCE));

    public static final AttributeType           LINEAR_GRADIENT_TYPE        = new AttributeType(LinearGradientValidator.INSTANCE);

    public static final AttributeType           PATTERN_GRADIENT_TYPE       = new AttributeType(PatternGradientValidator.INSTANCE);

    public static final AttributeType           RADIAL_GRADIENT_TYPE        = new AttributeType(RadialGradientValidator.INSTANCE);

    public static final AttributeType           SHADOW_TYPE                 = new AttributeType(ShadowValidator.INSTANCE);

    public static final AttributeType           SERIALIZATION_MODE_TYPE     = new AttributeType(new EnumValidator<>("SerializationMode", ImageSerializationMode.values()));

    public static final AttributeType           IMAGE_SELECTION_MODE_TYPE   = new AttributeType(new EnumValidator<>("ImageSelectionMode", ImageSelectionMode.values()));

    public static final AttributeType           DASH_ARRAY_TYPE             = new AttributeType(new ArrayValidator(NumberValidator.INSTANCE));

    public static final AttributeType           LINE_CAP_TYPE               = new AttributeType(new EnumValidator<>("LineCap", LineCap.values()));

    public static final AttributeType           LINE_JOIN_TYPE              = new AttributeType(new EnumValidator<>("LineJoin", LineJoin.values()));

    public static final AttributeType           DRAG_BOUNDS_TYPE            = new AttributeType(DragBoundsValidator.INSTANCE);

    public static final AttributeType           DRAG_CONSTRAINT_TYPE        = new AttributeType(new EnumValidator<>("DragConstraint", DragConstraint.values()));

    public static final AttributeType           DRAG_MODE_TYPE              = new AttributeType(new EnumValidator<>("DragMode", DragMode.values()));

    public static final AttributeType           TEXT_ALIGN_TYPE             = new AttributeType(new EnumValidator<>("TextAlign", TextAlign.values()));

    public static final AttributeType           TEXT_BASELINE_TYPE          = new AttributeType(new EnumValidator<>("TextBaseLine", TextBaseLine.values()));

    public static final AttributeType           TEXT_UNIT_TYPE              = new AttributeType(new EnumValidator<>("TextUnit", TextUnit.values()));

    public static final AttributeType           COMPOSITE_OPERATION_TYPE    = new AttributeType(new EnumValidator<>("CompositeOperation", CompositeOperation.values()));

    public static final AttributeType           ARROW_TYPE                  = new AttributeType(new EnumValidator<>("ArrowType", ArrowType.values()));

    public static final AttributeType           FILL_TYPE                   = new MultiAttributeType("Color or Gradient", COLOR_TYPE, LINEAR_GRADIENT_TYPE, PATTERN_GRADIENT_TYPE, RADIAL_GRADIENT_TYPE);

    public static final AttributeType           BEHAVIOR_MAP_TYPE           = new AttributeType(SpriteBehaviorMapValidator.INSTANCE);

    public static final AttributeType           STROKE_TYPE                 = COLOR_TYPE;

    public static final AttributeType           DIRECTION_TYPE              = new AttributeType(new EnumValidator<>("Direction", Direction.values()));

    public static final AttributeType           EVENT_PROPAGATION_MODE_TYPE = new AttributeType(new EnumValidator<>("EventPropagationMode", EventPropagationMode.values()));

    private final IAttributeTypeValidator m_validator;

    protected AttributeType(final IAttributeTypeValidator validator)
    {
        m_validator = Objects.requireNonNull(validator);
    }

    public void validate(final Object val, final ValidationContext ctx) throws ValidationException
    {
        m_validator.validate(val, ctx);
    }

    public static class MultiAttributeType extends AttributeType
    {
        public MultiAttributeType(final String desc, final AttributeType... types)
        {
            super(new MultiTypeValidator(desc, types));
        }
    }
}
