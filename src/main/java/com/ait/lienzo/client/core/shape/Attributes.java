/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.ImmediateAttributesChangedBatcher;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterConvolveMatrix;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.DragBounds.DragBoundsJSO;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.LinearGradient.LinearGradientJSO;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.NFastStringCountingSet;
import com.ait.lienzo.client.core.types.NFastStringMapMixedJSO;
import com.ait.lienzo.client.core.types.NativeInternalType;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.PatternGradient.PatternGradientJSO;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2D.Point2DJSO;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.RadialGradient.RadialGradientJSO;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Shadow.ShadowJSO;
import com.ait.lienzo.client.core.types.SpriteBehaviorMap;
import com.ait.lienzo.client.core.types.SpriteBehaviorMap.SpriteBehaviorMapJSO;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.types.Transform.TransformJSO;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.AutoScaleType;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.ImageSerializationMode;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class Attributes
{
    private final IJSONSerializable<?>   m_ser;

    private final NFastStringMapMixedJSO m_jso;

    private NFastStringCountingSet       m_set;

    private HandlerManager               m_man;

    private IAttributesChangedBatcher    m_bat;

    public Attributes(final IJSONSerializable<?> ser)
    {
        m_ser = ser;

        m_jso = NFastStringMapMixedJSO.make();
    }

    public Attributes(final JavaScriptObject jso, final IJSONSerializable<?> ser)
    {
        m_ser = ser;

        if ((null != jso) && (NFastStringMapMixedJSO.typeOf(jso) == NativeInternalType.OBJECT))
        {
            m_jso = jso.cast();
        }
        else
        {
            m_jso = NFastStringMapMixedJSO.make();
        }
    }

    public final NFastStringMapMixedJSO getJSO()
    {
        return m_jso;
    }

    public final HandlerRegistration addAttributesChangedHandler(final Attribute attribute, final AttributesChangedHandler handler)
    {
        if ((null == attribute) || (null == handler))
        {
            return null;
        }
        if (null != m_ser)
        {
            final String name = attribute.getProperty();

            if (null == m_set)
            {
                m_set = new NFastStringCountingSet();
            }
            m_set.inc(name);

            if (null == m_man)
            {
                m_man = new HandlerManager(m_ser);
            }
            final HandlerRegistration proxy = m_man.addHandler(AttributesChangedEvent.getType(), handler);

            return new HandlerRegistration()
            {
                @Override
                public void removeHandler()
                {
                    if (null != m_set)
                    {
                        m_set.dec(name);

                        if (m_set.isEmpty())
                        {
                            m_set = null;
                        }
                    }
                    proxy.removeHandler();
                }
            };
        }
        return null;
    }

    private final void checkDispatchAttributeChanged(final String name)
    {
        if ((null != m_set) && (null != m_ser))
        {
            if (m_set.contains(name))
            {
                if (null != m_bat)
                {
                    m_bat.bufferAttributeWithManager(name, m_man);
                }
                else
                {
                    final IAttributesChangedBatcher bat = LienzoCore.get().getAttributesChangedBatcher();

                    if (null != bat)
                    {
                        bat.bufferAttributeWithManager(name, m_man);
                    }
                    else
                    {
                        ImmediateAttributesChangedBatcher.INSTANCE.bufferAttributeWithManager(name, m_man);
                    }
                }
            }
        }
    }

    public final void setAttributesChangedBatcher(final IAttributesChangedBatcher bat)
    {
        if (null != bat)
        {
            m_bat = bat.copy();
        }
        else
        {
            m_bat = null;
        }
    }

    public final AutoScaleType getAutoScale()
    {
        return AutoScaleType.lookup(getString(Attribute.AUTO_SCALE.getProperty()));
    }

    public final void setAutoScale(final AutoScaleType type)
    {
        if (null != type)
        {
            put(Attribute.AUTO_SCALE.getProperty(), type.getValue());
        }
        else
        {
            delete(Attribute.AUTO_SCALE.getProperty());
        }
    }

    public final Point2D getViewLocation()
    {
        final JavaScriptObject view = getObject(Attribute.VIEW_LOCATION.getProperty());

        if (null != view)
        {
            final Point2DJSO pjso = view.cast();

            return new Point2D(pjso);
        }
        return null;
    }

    public final void setViewLocation(final Point2D view)
    {
        if (null != view)
        {
            put(Attribute.VIEW_LOCATION.getProperty(), view.getJSO());
        }
        else
        {
            delete(Attribute.VIEW_LOCATION.getProperty());
        }
    }

    public final double getViewDomain()
    {
        final double domain = m_jso.getDouble(Attribute.VIEW_DOMAIN.getProperty());

        if (domain < 1)
        {
            return 0;
        }
        return domain;
    }

    public final void setViewDomain(final double domain)
    {
        if (domain < 1)
        {
            delete(Attribute.VIEW_DOMAIN.getProperty());
        }
        else
        {
            put(Attribute.VIEW_DOMAIN.getProperty(), domain);
        }
    }

    public final boolean isClearLayerBeforeDraw()
    {
        if (isDefined(Attribute.CLEAR_LAYER_BEFORE_DRAW))
        {
            return getBoolean(Attribute.CLEAR_LAYER_BEFORE_DRAW.getProperty());
        }
        return true;
    }

    public final void setClearLayerBeforeDraw(boolean clear)
    {
        put(Attribute.CLEAR_LAYER_BEFORE_DRAW.getProperty(), clear);
    }

    public final boolean isTransformable()
    {
        if (isDefined(Attribute.TRANSFORMABLE))
        {
            return getBoolean(Attribute.TRANSFORMABLE.getProperty());
        }
        return true;
    }

    public final void setTransformable(boolean transformable)
    {
        put(Attribute.TRANSFORMABLE.getProperty(), transformable);
    }

    public final void setFillAlpha(double alpha)
    {
        if (alpha < 0)
        {
            alpha = 0;
        }
        else if (alpha > 1)
        {
            alpha = 1;
        }
        put(Attribute.FILL_ALPHA.getProperty(), alpha);
    }

    public final double getFillAlpha()
    {
        if (typeOf(Attribute.FILL_ALPHA) == NativeInternalType.NUMBER)
        {
            double alpha = m_jso.getDouble(Attribute.FILL_ALPHA.getProperty());

            if (alpha < 0)
            {
                alpha = 0;
            }
            else if (alpha > 1)
            {
                alpha = 1;
            }
            return alpha;
        }
        return 1;
    }

    public final void setStrokeAlpha(double alpha)
    {
        if (alpha < 0)
        {
            alpha = 0;
        }
        else if (alpha > 1)
        {
            alpha = 1;
        }
        put(Attribute.STROKE_ALPHA.getProperty(), alpha);
    }

    public final double getStrokeAlpha()
    {
        if (typeOf(Attribute.STROKE_ALPHA) == NativeInternalType.NUMBER)
        {
            double alpha = m_jso.getDouble(Attribute.STROKE_ALPHA.getProperty());

            if (alpha < 0)
            {
                alpha = 0;
            }
            else if (alpha > 1)
            {
                alpha = 1;
            }
            return alpha;
        }
        return 1;
    }

    public final void setFillColor(String fill)
    {
        if ((null != fill) && (false == (fill = fill.trim()).isEmpty()))
        {
            put(Attribute.FILL.getProperty(), fill);
        }
        else
        {
            delete(Attribute.FILL.getProperty());
        }
    }

    public final String getFillColor()
    {
        return getString(Attribute.FILL.getProperty());
    }

    public final void setFillGradient(LinearGradient gradient)
    {
        if (null != gradient)
        {
            put(Attribute.FILL.getProperty(), gradient.getJSO());
        }
        else
        {
            delete(Attribute.FILL.getProperty());
        }
    }

    public final void setFillGradient(RadialGradient gradient)
    {
        if (null != gradient)
        {
            put(Attribute.FILL.getProperty(), gradient.getJSO());
        }
        else
        {
            delete(Attribute.FILL.getProperty());
        }
    }

    public final void setFillGradient(PatternGradient gradient)
    {
        if (null != gradient)
        {
            put(Attribute.FILL.getProperty(), gradient.getJSO());
        }
        else
        {
            delete(Attribute.FILL.getProperty());
        }
    }

    public final FillGradient getFillGradient()
    {
        JavaScriptObject fill = getObject(Attribute.FILL.getProperty());

        if (null == fill)
        {
            return null;
        }
        String type = m_jso.getString("type", fill);

        if (LinearGradient.TYPE.equals(type))
        {
            return new LinearGradient((LinearGradientJSO) fill);
        }
        else if (RadialGradient.TYPE.equals(type))
        {
            return new RadialGradient((RadialGradientJSO) fill);
        }
        else if (PatternGradient.TYPE.equals(type))
        {
            return new PatternGradient((PatternGradientJSO) fill);
        }
        return null;
    }

    public final void setStrokeColor(String stroke)
    {
        if ((null != stroke) && (false == (stroke = stroke.trim()).isEmpty()))
        {
            put(Attribute.STROKE.getProperty(), stroke);
        }
        else
        {
            delete(Attribute.STROKE.getProperty());
        }
    }

    public final String getStrokeColor()
    {
        return getString(Attribute.STROKE.getProperty());
    }

    public final void setLineCap(LineCap lineCap)
    {
        if (null != lineCap)
        {
            put(Attribute.LINE_CAP.getProperty(), lineCap.getValue());
        }
        else
        {
            delete(Attribute.LINE_CAP.getProperty());
        }
    }

    public final LineCap getLineCap()
    {
        return LineCap.lookup(getString(Attribute.LINE_CAP.getProperty()));
    }

    public final void setLineJoin(LineJoin lineJoin)
    {
        if (null != lineJoin)
        {
            put(Attribute.LINE_JOIN.getProperty(), lineJoin.getValue());
        }
        else
        {
            delete(Attribute.LINE_JOIN.getProperty());
        }
    }

    public final LineJoin getLineJoin()
    {
        return LineJoin.lookup(getString(Attribute.LINE_JOIN.getProperty()));
    }

    public final void setMiterLimit(double limit)
    {
        put(Attribute.MITER_LIMIT.getProperty(), limit);
    }

    public final double getMiterLimit()
    {
        return getDouble(Attribute.MITER_LIMIT.getProperty());
    }

    public final void setStrokeWidth(double width)
    {
        put(Attribute.STROKE_WIDTH.getProperty(), width);
    }

    public final double getStrokeWidth()
    {
        return getDouble(Attribute.STROKE_WIDTH.getProperty());
    }

    public final void setX(double x)
    {
        put(Attribute.X.getProperty(), x);
    }

    public final void setY(double y)
    {
        put(Attribute.Y.getProperty(), y);
    }

    public final void setVisible(boolean visible)
    {
        put(Attribute.VISIBLE.getProperty(), visible);
    }

    public final boolean isVisible()
    {
        if (isDefined(Attribute.VISIBLE))
        {
            return getBoolean(Attribute.VISIBLE.getProperty());
        }
        return true;
    }

    public final void setDraggable(boolean draggable)
    {
        put(Attribute.DRAGGABLE.getProperty(), draggable);
    }

    public final boolean isDraggable()
    {
        return getBoolean(Attribute.DRAGGABLE.getProperty());
    }

    public final void setEditable(boolean editable)
    {
        put(Attribute.EDITABLE.getProperty(), editable);
    }

    public final boolean isEditable()
    {
        return getBoolean(Attribute.EDITABLE.getProperty());
    }

    public final void setFillShapeForSelection(boolean selection)
    {
        put(Attribute.FILL_SHAPE_FOR_SELECTION.getProperty(), selection);
    }

    public final boolean isFillShapeForSelection()
    {
        if (isDefined(Attribute.FILL_SHAPE_FOR_SELECTION))
        {
            return getBoolean(Attribute.FILL_SHAPE_FOR_SELECTION.getProperty());
        }
        return LienzoCore.get().getDefaultFillShapeForSelection();
    }

    public final void setListening(boolean listening)
    {
        put(Attribute.LISTENING.getProperty(), listening);
    }

    public final boolean isListening()
    {
        if (isDefined(Attribute.LISTENING))
        {
            return getBoolean(Attribute.LISTENING.getProperty());
        }
        return true;
    }

    public final void setName(String name)
    {
        if (null != name)
        {
            put(Attribute.NAME.getProperty(), name);
        }
        else
        {
            delete(Attribute.NAME.getProperty());
        }
    }

    public final void setDashArray(DashArray array)
    {
        if (null != array)
        {
            put(Attribute.DASH_ARRAY.getProperty(), array.getJSO());
        }
        else
        {
            delete(Attribute.DASH_ARRAY.getProperty());
        }
    }

    public final DashArray getDashArray()
    {
        JsArrayMixed dash = getArray(Attribute.DASH_ARRAY.getProperty());

        if (null != dash)
        {
            NFastDoubleArrayJSO djso = dash.cast();

            return new DashArray(djso);
        }
        return new DashArray();
    }

    public final void setDragConstraint(DragConstraint constraint)
    {
        if (null != constraint)
        {
            put(Attribute.DRAG_CONSTRAINT.getProperty(), constraint.getValue());
        }
        else
        {
            delete(Attribute.DRAG_CONSTRAINT.getProperty());
        }
    }

    public final DragConstraint getDragConstraint()
    {
        return DragConstraint.lookup(getString(Attribute.DRAG_CONSTRAINT.getProperty()));
    }

    public final String getName()
    {
        return getString(Attribute.NAME.getProperty());
    }

    public final void setID(String id)
    {
        if (null != id)
        {
            put(Attribute.ID.getProperty(), id);
        }
        else
        {
            delete(Attribute.ID.getProperty());
        }
    }

    public final String getID()
    {
        return getString(Attribute.ID.getProperty());
    }

    public final void setRotation(double radians)
    {
        put(Attribute.ROTATION.getProperty(), radians);
    }

    public final double getRotation()
    {
        return getDouble(Attribute.ROTATION.getProperty());
    }

    public final void setRotationDegrees(double degrees)
    {
        put(Attribute.ROTATION.getProperty(), degrees * Math.PI / 180);
    }

    public final double getRotationDegrees()
    {
        return getDouble(Attribute.ROTATION.getProperty()) * 180 / Math.PI;
    }

    public final void setRadius(double radius)
    {
        put(Attribute.RADIUS.getProperty(), radius);
    }

    public final void setCornerRadius(double cornerRadius)
    {
        put(Attribute.CORNER_RADIUS.getProperty(), cornerRadius);
    }

    public final void setAlpha(double alpha)
    {
        if (alpha < 0)
        {
            alpha = 0;
        }
        if (alpha > 1)
        {
            alpha = 1;
        }
        put(Attribute.ALPHA.getProperty(), alpha);
    }

    public final void setScale(Point2D scale)
    {
        if (null != scale)
        {
            put(Attribute.SCALE.getProperty(), scale.getJSO());
        }
        else
        {
            delete(Attribute.SCALE.getProperty());
        }
    }

    public final void setScale(double scalex, double scaley)
    {
        setScale(new Point2D(scalex, scaley));
    }

    public final void setScale(double value)
    {
        setScale(new Point2D(value, value));
    }

    public final Point2D getScale()
    {
        JavaScriptObject scale = getObject(Attribute.SCALE.getProperty());

        if (null != scale)
        {
            Point2DJSO pjso = scale.cast();

            return new Point2D(pjso);
        }
        return null;
    }

    public final void setShear(double shearX, double shearY)
    {
        setShear(new Point2D(shearX, shearY));
    }

    public final void setShear(Point2D shear)
    {
        if (null != shear)
        {
            put(Attribute.SHEAR.getProperty(), shear.getJSO());
        }
        else
        {
            delete(Attribute.SHEAR.getProperty());
        }
    }

    public final Point2D getShear()
    {
        JavaScriptObject shear = getObject(Attribute.SHEAR.getProperty());

        if (null != shear)
        {
            Point2DJSO pjso = shear.cast();

            return new Point2D(pjso);
        }
        return null;
    }

    public final void setOffset(Point2D offset)
    {
        if (null != offset)
        {
            put(Attribute.OFFSET.getProperty(), offset.getJSO());
        }
        else
        {
            delete(Attribute.OFFSET.getProperty());
        }
    }

    public final void setOffset(double x, double y)
    {
        setOffset(new Point2D(x, y));
    }

    public final Point2D getOffset()
    {
        JavaScriptObject offset = getObject(Attribute.OFFSET.getProperty());

        if (null != offset)
        {
            Point2DJSO pjso = offset.cast();

            return new Point2D(pjso);
        }
        return null;
    }

    public final void setTransform(Transform transform)
    {
        if (null != transform)
        {
            put(Attribute.TRANSFORM.getProperty(), transform.getJSO());
        }
        else
        {
            delete(Attribute.TRANSFORM.getProperty());
        }
    }

    public final Transform getTransform()
    {
        JavaScriptObject xrfm = getArray(Attribute.TRANSFORM.getProperty());

        if (null != xrfm)
        {
            TransformJSO pjso = xrfm.cast();

            return new Transform(pjso);
        }
        return null;
    }

    public final void setWidth(double width)
    {
        put(Attribute.WIDTH.getProperty(), width);
    }

    public final void setHeight(double height)
    {
        put(Attribute.HEIGHT.getProperty(), height);
    }

    public final void setPoints(Point2DArray points)
    {
        if (null != points)
        {
            put(Attribute.POINTS.getProperty(), points.getJSO());
        }
        else
        {
            delete(Attribute.POINTS.getProperty());
        }
    }

    public final Point2DArray getPoints()
    {
        JsArray<JavaScriptObject> points = getArrayOfJSO(Attribute.POINTS.getProperty());

        if (null != points)
        {
            return new Point2DArray(points);
        }
        return new Point2DArray();
    }

    public final void setStarPoints(int points)
    {
        if (points < 5)
        {
            points = 5;
        }
        put(Attribute.STAR_POINTS.getProperty(), points);
    }

    public final void setText(String text)
    {
        if (null == text)
        {
            text = "";
        }
        put(Attribute.TEXT.getProperty(), text);
    }

    public final String getText()
    {
        String text = getString(Attribute.TEXT.getProperty());

        if (null == text)
        {
            text = "";
        }
        return text;
    }

    public final void setFontSize(double points)
    {
        if (points <= 0.0)
        {
            points = LienzoCore.get().getDefaultFontSize();
        }
        put(Attribute.FONT_SIZE.getProperty(), points);
    }

    public final double getFontSize()
    {
        double points = getDouble(Attribute.FONT_SIZE.getProperty());

        if (points <= 0.0)
        {
            points = LienzoCore.get().getDefaultFontSize();
        }
        return points;
    }

    public final void setSkew(double skew)
    {
        put(Attribute.SKEW.getProperty(), skew);
    }

    public final void setFontFamily(String family)
    {
        if ((null == family) || (family = family.trim()).isEmpty())
        {
            put(Attribute.FONT_FAMILY.getProperty(), LienzoCore.get().getDefaultFontFamily());
        }
        else
        {
            put(Attribute.FONT_FAMILY.getProperty(), family);
        }
    }

    public final String getFontFamily()
    {
        String family = getString(Attribute.FONT_FAMILY.getProperty());

        if ((null == family) || (family = family.trim()).isEmpty())
        {
            family = LienzoCore.get().getDefaultFontFamily();
        }
        return family;
    }

    public final void setFontStyle(String style)
    {
        if ((null == style) || (style = style.trim()).isEmpty())
        {
            put(Attribute.FONT_STYLE.getProperty(), LienzoCore.get().getDefaultFontStyle());
        }
        else
        {
            put(Attribute.FONT_STYLE.getProperty(), style);
        }
    }

    public final String getFontStyle()
    {
        String style = getString(Attribute.FONT_STYLE.getProperty());

        if ((null == style) || (style = style.trim()).isEmpty())
        {
            style = LienzoCore.get().getDefaultFontStyle();
        }
        return style;
    }

    public final void setTextBaseLine(TextBaseLine baseline)
    {
        if (null != baseline)
        {
            put(Attribute.TEXT_BASELINE.getProperty(), baseline.getValue());
        }
        else
        {
            delete(Attribute.TEXT_BASELINE.getProperty());
        }
    }

    public final void setTextUnit(TextUnit unit)
    {
        if (null != unit)
        {
            put(Attribute.TEXT_UNIT.getProperty(), unit.getValue());
        }
        else
        {
            delete(Attribute.TEXT_UNIT.getProperty());
        }
    }

    public final TextUnit getTextUnit()
    {
        return TextUnit.lookup(getString(Attribute.TEXT_UNIT.getProperty()));
    }

    public final void setTextAlign(TextAlign textAlign)
    {
        if (null != textAlign)
        {
            put(Attribute.TEXT_ALIGN.getProperty(), textAlign.getValue());
        }
        else
        {
            delete(Attribute.TEXT_ALIGN.getProperty());
        }
    }

    public final TextBaseLine getTextBaseLine()
    {
        return TextBaseLine.lookup(getString(Attribute.TEXT_BASELINE.getProperty()));
    }

    public final TextAlign getTextAlign()
    {
        return TextAlign.lookup(getString(Attribute.TEXT_ALIGN.getProperty()));
    }

    public final void setShadow(Shadow shadow)
    {
        if (null != shadow)
        {
            put(Attribute.SHADOW.getProperty(), shadow.getJSO());
        }
        else
        {
            delete(Attribute.SHADOW.getProperty());
        }
    }

    public final Shadow getShadow()
    {
        JavaScriptObject shadow = getObject(Attribute.SHADOW.getProperty());

        if (null != shadow)
        {
            ShadowJSO sjso = shadow.cast();

            return new Shadow(sjso);
        }
        return null;
    }

    public final void setStartAngle(double startAngle)
    {
        put(Attribute.START_ANGLE.getProperty(), startAngle);
    }

    public final void setEndAngle(double endAngle)
    {
        put(Attribute.END_ANGLE.getProperty(), endAngle);
    }

    public final void setCounterClockwise(boolean counterClockwise)
    {
        put(Attribute.COUNTER_CLOCKWISE.getProperty(), counterClockwise);
    }

    public final void setControlPoints(Point2DArray controlPoints)
    {
        if (null != controlPoints)
        {
            put(Attribute.CONTROL_POINTS.getProperty(), controlPoints.getJSO());
        }
        else
        {
            delete(Attribute.CONTROL_POINTS.getProperty());
        }
    }

    public final Point2DArray getControlPoints()
    {
        JsArray<JavaScriptObject> points = getArrayOfJSO(Attribute.CONTROL_POINTS.getProperty());

        if (null != points)
        {
            return new Point2DArray(points);
        }
        return new Point2DArray();
    }

    public final double getX()
    {
        return getDouble(Attribute.X.getProperty());
    }

    public final double getY()
    {
        return getDouble(Attribute.Y.getProperty());
    }

    public final double getRadius()
    {
        return getDouble(Attribute.RADIUS.getProperty());
    }

    public final double getCornerRadius()
    {
        return getDouble(Attribute.CORNER_RADIUS.getProperty());
    }

    public final double getWidth()
    {
        return getDouble(Attribute.WIDTH.getProperty());
    }

    public final double getHeight()
    {
        return getDouble(Attribute.HEIGHT.getProperty());
    }

    public final int getStarPoints()
    {
        int points = getInteger(Attribute.STAR_POINTS.getProperty());

        if (points < 5)
        {
            points = 5;
        }
        return points;
    }

    public final int getSides()
    {
        int sides = getInteger(Attribute.SIDES.getProperty());

        if (sides < 3)
        {
            sides = 3;
        }
        return sides;
    }

    public final void setSides(int sides)
    {
        if (sides < 3)
        {
            sides = 3;
        }
        put(Attribute.SIDES.getProperty(), sides);
    }

    public final double getStartAngle()
    {
        return getDouble(Attribute.START_ANGLE.getProperty());
    }

    public final double getEndAngle()
    {
        return getDouble(Attribute.END_ANGLE.getProperty());
    }

    public final boolean isCounterClockwise()
    {
        return getBoolean(Attribute.COUNTER_CLOCKWISE.getProperty());
    }

    public final double getSkew()
    {
        return getDouble(Attribute.SKEW.getProperty());
    }

    public final double getInnerRadius()
    {
        return getDouble(Attribute.INNER_RADIUS.getProperty());
    }

    public final void setInnerRadius(double radius)
    {
        put(Attribute.INNER_RADIUS.getProperty(), radius);
    }

    public final void setOuterRadius(double radius)
    {
        put(Attribute.OUTER_RADIUS.getProperty(), radius);
    }

    public final double getOuterRadius()
    {
        return getDouble(Attribute.OUTER_RADIUS.getProperty());
    }

    public final double getAlpha()
    {
        if (typeOf(Attribute.ALPHA) == NativeInternalType.NUMBER)
        {
            double alpha = m_jso.getDouble(Attribute.ALPHA.getProperty());

            if (alpha < 0)
            {
                alpha = 0;
            }
            else if (alpha > 1)
            {
                alpha = 1;
            }
            return alpha;
        }
        return 1;
    }

    public final void setOffset(double xy)
    {
        setOffset(new Point2D(xy, xy));
    }

    public final DragBounds getDragBounds()
    {
        JavaScriptObject bounds = getObject(Attribute.DRAG_BOUNDS.getProperty());

        if (null != bounds)
        {
            DragBoundsJSO djso = bounds.cast();

            return new DragBounds(djso);
        }
        return null;
    }

    public final void setDragBounds(DragBounds bounds)
    {
        if (null != bounds)
        {
            put(Attribute.DRAG_BOUNDS.getProperty(), bounds.getJSO());
        }
        else
        {
            delete(Attribute.DRAG_BOUNDS.getProperty());
        }
    }

    public final DragMode getDragMode()
    {
        return DragMode.lookup(getString(Attribute.DRAG_MODE.getProperty()));
    }

    public final void setDragMode(DragMode mode)
    {
        if (null != mode)
        {
            put(Attribute.DRAG_MODE.getProperty(), mode.getValue());
        }
        else
        {
            delete(Attribute.DRAG_MODE.getProperty());
        }
    }

    public final void setClippedImageStartX(int clippedImageStartX)
    {
        put(Attribute.CLIPPED_IMAGE_START_X.getProperty(), clippedImageStartX);
    }

    public final int getClippedImageStartX()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_START_X.getProperty());
    }

    public final void setClippedImageStartY(int clippedImageStartY)
    {
        put(Attribute.CLIPPED_IMAGE_START_Y.getProperty(), clippedImageStartY);
    }

    public final int getClippedImageStartY()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_START_Y.getProperty());
    }

    public final void setClippedImageWidth(int clippedImageWidth)
    {
        put(Attribute.CLIPPED_IMAGE_WIDTH.getProperty(), clippedImageWidth);
    }

    public final int getClippedImageWidth()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_WIDTH.getProperty());
    }

    public final void setClippedImageHeight(int clippedImageHeight)
    {
        put(Attribute.CLIPPED_IMAGE_HEIGHT.getProperty(), clippedImageHeight);
    }

    public final int getClippedImageHeight()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_HEIGHT.getProperty());
    }

    public final void setClippedImageDestinationWidth(int clippedImageDestinationWidth)
    {
        put(Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH.getProperty(), clippedImageDestinationWidth);
    }

    public final int getClippedImageDestinationWidth()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH.getProperty());
    }

    public final void setClippedImageDestinationHeight(int clippedImageDestinationHeight)
    {
        put(Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT.getProperty(), clippedImageDestinationHeight);
    }

    public final int getClippedImageDestinationHeight()
    {
        return getInteger(Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT.getProperty());
    }

    public final void setSerializationMode(ImageSerializationMode mode)
    {
        if (null != mode)
        {
            put(Attribute.SERIALIZATION_MODE.getProperty(), mode.getValue());
        }
        else
        {
            delete(Attribute.SERIALIZATION_MODE.getProperty());
        }
    }

    public final ImageSerializationMode getSerializationMode()
    {
        return ImageSerializationMode.lookup(getString(Attribute.SERIALIZATION_MODE.getProperty()));
    }

    public final void setImageSelectionMode(ImageSelectionMode mode)
    {
        if (null != mode)
        {
            put(Attribute.IMAGE_SELECTION_MODE.getProperty(), mode.getValue());
        }
        else
        {
            delete(Attribute.IMAGE_SELECTION_MODE.getProperty());
        }
    }

    public final ImageSelectionMode getImageSelectionMode()
    {
        return ImageSelectionMode.lookup(getString(Attribute.IMAGE_SELECTION_MODE.getProperty()));
    }

    public final void setBaseWidth(double baseWidth)
    {
        put(Attribute.BASE_WIDTH.getProperty(), baseWidth);
    }

    public final double getBaseWidth()
    {
        return getDouble(Attribute.BASE_WIDTH.getProperty());
    }

    public final void setHeadWidth(double headWidth)
    {
        put(Attribute.HEAD_WIDTH.getProperty(), headWidth);
    }

    public final double getHeadWidth()
    {
        return getDouble(Attribute.HEAD_WIDTH.getProperty());
    }

    public final void setArrowAngle(double arrowAngle)
    {
        put(Attribute.ARROW_ANGLE.getProperty(), arrowAngle);
    }

    public final double getArrowAngle()
    {
        return getDouble(Attribute.ARROW_ANGLE.getProperty());
    }

    public final void setBaseAngle(double baseAngle)
    {
        put(Attribute.BASE_ANGLE.getProperty(), baseAngle);
    }

    public final double getBaseAngle()
    {
        return getDouble(Attribute.BASE_ANGLE.getProperty());
    }

    public final void setArrowType(ArrowType arrowType)
    {
        if (null != arrowType)
        {
            put(Attribute.ARROW_TYPE.getProperty(), arrowType.getValue());
        }
        else
        {
            delete(Attribute.ARROW_TYPE.getProperty());
        }
    }

    public final ArrowType getArrowType()
    {
        return ArrowType.lookup(getString(Attribute.ARROW_TYPE.getProperty()));
    }

    public final void setURL(String url)
    {
        if (null != url)
        {
            put(Attribute.URL.getProperty(), url);
        }
        else
        {
            delete(Attribute.URL.getProperty());
        }
    }

    public final String getURL()
    {
        return getString(Attribute.URL.getProperty());
    }

    public final void setLoop(boolean loop)
    {
        put(Attribute.LOOP.getProperty(), loop);
    }

    public final boolean isLoop()
    {
        return getBoolean(Attribute.LOOP.getProperty());
    }

    public final void setPlaybackRate(double rate)
    {
        put(Attribute.PLAYBACK_RATE.getProperty(), rate);
    }

    public final double getPlaybackRate()
    {
        if (isDefined(Attribute.PLAYBACK_RATE))
        {
            return getDouble(Attribute.PLAYBACK_RATE.getProperty());
        }
        return 1.0;
    }

    public final void setVolume(double volume)
    {
        if (volume > 1.0)
        {
            volume = 1.0;
        }
        else if (volume < 0.0)
        {
            volume = 0.0;
        }
        put(Attribute.VOLUME.getProperty(), volume);
    }

    public final double getVolume()
    {
        if (typeOf(Attribute.VOLUME) == NativeInternalType.NUMBER)
        {
            double volume = m_jso.getDouble(Attribute.VOLUME.getProperty());

            if (volume < 0)
            {
                volume = 0;
            }
            else if (volume > 1)
            {
                volume = 1;
            }
            return volume;
        }
        return 0.5;
    }

    public final void setAutoPlay(boolean play)
    {
        put(Attribute.AUTO_PLAY.getProperty(), play);
    }

    public final boolean isAutoPlay()
    {
        return getBoolean(Attribute.AUTO_PLAY.getProperty());
    }

    public final void setShowPoster(boolean show)
    {
        put(Attribute.SHOW_POSTER.getProperty(), show);
    }

    public final boolean isShowPoster()
    {
        return getBoolean(Attribute.SHOW_POSTER.getProperty());
    }

    public final double getCurveFactor()
    {
        if (typeOf(Attribute.CURVE_FACTOR) == NativeInternalType.NUMBER)
        {
            double factor = m_jso.getDouble(Attribute.CURVE_FACTOR.getProperty());

            if (factor <= 0)
            {
                factor = 0.5;
            }
            else if (factor > 1)
            {
                factor = 1;
            }
            return factor;
        }
        return 0.5;
    }

    public final void setCurveFactor(double factor)
    {
        if (factor <= 0)
        {
            factor = 0.5;
        }
        else if (factor > 1)
        {
            factor = 1;
        }
        put(Attribute.CURVE_FACTOR.getProperty(), factor);
    }

    public final double getAngleFactor()
    {
        if (typeOf(Attribute.ANGLE_FACTOR) == NativeInternalType.NUMBER)
        {
            double factor = m_jso.getDouble(Attribute.ANGLE_FACTOR.getProperty());

            if (factor < 0)
            {
                factor = 0;
            }
            else if (factor > 1)
            {
                factor = 1;
            }
            return factor;
        }
        return 0;
    }

    public final boolean getLineFlatten()
    {
        return getBoolean(Attribute.LINE_FLATTEN.getProperty());
    }

    public final void setLineFlatten(boolean flat)
    {
        put(Attribute.LINE_FLATTEN.getProperty(), flat);
    }

    public final void setAngleFactor(double factor)
    {
        if (factor < 0)
        {
            factor = 0;
        }
        else if (factor > 1)
        {
            factor = 1;
        }
        put(Attribute.ANGLE_FACTOR.getProperty(), factor);
    }

    public final void setTopWidth(double topwidth)
    {
        put(Attribute.TOP_WIDTH.getProperty(), topwidth);
    }

    public final double getTopWidth()
    {
        return getDouble(Attribute.TOP_WIDTH.getProperty());
    }

    public final void setBottomWidth(double bottomwidth)
    {
        put(Attribute.BOTTOM_WIDTH.getProperty(), bottomwidth);
    }

    public final double getBottomWidth()
    {
        return getDouble(Attribute.BOTTOM_WIDTH.getProperty());
    }

    public final void setDashOffset(double offset)
    {
        put(Attribute.DASH_OFFSET.getProperty(), offset);
    }

    public final double getDashOffset()
    {
        return getDouble(Attribute.DASH_OFFSET.getProperty());
    }

    public final void setHeadOffset(double offset)
    {
        if (offset < 0)
        {
            offset = 0;
        }
        put(Attribute.HEAD_OFFSET.getProperty(), offset);
    }

    public final double getHeadOffset()
    {
        if (typeOf(Attribute.HEAD_OFFSET) == NativeInternalType.NUMBER)
        {
            double offset = m_jso.getDouble(Attribute.HEAD_OFFSET.getProperty());

            if (offset >= 0)
            {
                return offset;
            }
        }
        return 0;
    }

    public final void setHeadDirection(Direction direction)
    {
        if (null != direction)
        {
            put(Attribute.HEAD_DIRECTION.getProperty(), direction.getValue());
        }
        else
        {
            delete(Attribute.HEAD_DIRECTION.getProperty());
        }
    }

    public final Direction getHeadDirection()
    {
        return Direction.lookup(getString(Attribute.HEAD_DIRECTION.getProperty()));
    }

    public final void setTailOffset(double offset)
    {
        if (offset < 0)
        {
            offset = 0;
        }
        put(Attribute.TAIL_OFFSET.getProperty(), offset);
    }

    public final double getTailOffset()
    {
        if (typeOf(Attribute.TAIL_OFFSET) == NativeInternalType.NUMBER)
        {
            double offset = m_jso.getDouble(Attribute.TAIL_OFFSET.getProperty());

            if (offset >= 0)
            {
                return offset;
            }
        }
        return 0;
    }

    public final void setTailDirection(Direction direction)
    {
        if (null != direction)
        {
            put(Attribute.TAIL_DIRECTION.getProperty(), direction.getValue());
        }
        else
        {
            delete(Attribute.TAIL_DIRECTION.getProperty());
        }
    }

    public final Direction getTailDirection()
    {
        return Direction.lookup(getString(Attribute.TAIL_DIRECTION.getProperty()));
    }

    public final void setCorrectionOffset(double offset)
    {
        if (offset < 0)
        {
            offset = LienzoCore.get().getDefaultConnectorOffset();
        }
        put(Attribute.CORRECTION_OFFSET.getProperty(), offset);
    }

    public final double getCorrectionOffset()
    {
        if (typeOf(Attribute.CORRECTION_OFFSET) == NativeInternalType.NUMBER)
        {
            double offset = m_jso.getDouble(Attribute.CORRECTION_OFFSET.getProperty());

            if (offset >= 0)
            {
                return offset;
            }
        }
        return LienzoCore.get().getDefaultConnectorOffset();
    }

    public final boolean hasAnyTransformAttributes()
    {
        return hasAnyTransformAttributes(m_jso);
    }

    public final boolean hasComplexTransformAttributes()
    {
        return hasComplexTransformAttributes(m_jso);
    }

    public final boolean hasExtraStrokeAttributes()
    {
        return hasExtraStrokeAttributes(m_jso);
    }

    private static final native boolean hasAnyTransformAttributes(NFastStringMapMixedJSO jso)
    /*-{
        return ((jso.x !== undefined) || (jso.y !== undefined) || (jso.rotation !== undefined) || (jso.scale !== undefined) || (jso.shear !== undefined) || (jso.transform !== undefined));
    }-*/;

    private static final native boolean hasComplexTransformAttributes(NFastStringMapMixedJSO jso)
    /*-{
        return ((jso.rotation !== undefined) || (jso.scale !== undefined) || (jso.shear !== undefined));
    }-*/;

    private static final native boolean hasExtraStrokeAttributes(NFastStringMapMixedJSO jso)
    /*-{
        return ((jso.dashArray !== undefined) || (jso.lineJoin !== undefined) || (jso.lineCap !== undefined) || (jso.miterLimit !== undefined));
    }-*/;

    public final void put(final String name, final String value)
    {
        m_jso.put(name, value);

        checkDispatchAttributeChanged(name);
    }

    public final void put(final String name, final int value)
    {
        m_jso.put(name, value);

        checkDispatchAttributeChanged(name);
    }

    public final void put(final String name, final double value)
    {
        m_jso.put(name, value);

        checkDispatchAttributeChanged(name);
    }

    public final void put(final String name, final boolean value)
    {
        m_jso.put(name, value);

        checkDispatchAttributeChanged(name);
    }

    public final void put(final String name, final JavaScriptObject value)
    {
        m_jso.put(name, value);

        checkDispatchAttributeChanged(name);
    }

    public final boolean isEmpty()
    {
        return m_jso.isEmpty();
    }

    public final int getInteger(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.NUMBER)
        {
            return m_jso.getInteger(name);
        }
        return 0;
    }

    public final double getDouble(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.NUMBER)
        {
            return m_jso.getDouble(name);
        }
        return 0;
    }

    public final Point2D getPoint2D(String name)
    {
        JavaScriptObject offset = getObject(name);

        if (null != offset)
        {
            Point2DJSO pjso = offset.cast();

            return new Point2D(pjso);
        }
        return null;
    }

    public final void putPoint2D(String name, Point2D point)
    {
        if (null != point)
        {
            put(name, point.getJSO());
        }
        else
        {
            delete(Attribute.SCALE.getProperty());
        }
    }

    public final void setPath(String path)
    {
        if ((null == path) || (path = path.trim()).isEmpty())
        {
            delete(Attribute.PATH.getProperty());
        }
        else
        {
            put(Attribute.PATH.getProperty(), path);
        }
    }

    public final String getPath()
    {
        String path = getString(Attribute.PATH.getProperty());

        if ((null == path) || (path = path.trim()).isEmpty())
        {
            return null;
        }
        return path;
    }

    public final void setSpriteBehaviorMap(SpriteBehaviorMap smap)
    {
        if (null == smap)
        {
            delete(Attribute.SPRITE_BEHAVIOR_MAP.getProperty());
        }
        else
        {
            put(Attribute.SPRITE_BEHAVIOR_MAP.getProperty(), smap.getJSO());
        }
    }

    public final SpriteBehaviorMap getSpriteBehaviorMap()
    {
        JavaScriptObject object = getObject(Attribute.SPRITE_BEHAVIOR_MAP.getProperty());

        if (null != object)
        {
            SpriteBehaviorMapJSO sjso = object.cast();

            return new SpriteBehaviorMap(sjso);
        }
        return null;
    }

    public final void setSpriteBehavior(String behavior)
    {
        if ((null == behavior) || (behavior = behavior.trim()).isEmpty())
        {
            delete(Attribute.SPRITE_BEHAVIOR.getProperty());
        }
        else
        {
            put(Attribute.SPRITE_BEHAVIOR.getProperty(), behavior);
        }
    }

    public final String getSpriteBehavior()
    {
        String behavior = getString(Attribute.SPRITE_BEHAVIOR.getProperty());

        if ((null == behavior) || (behavior = behavior.trim()).isEmpty())
        {
            return null;
        }
        return behavior;
    }

    public final double getTickRate()
    {
        return getDouble(Attribute.TICK_RATE.getProperty());
    }

    public final void setTickRate(double rate)
    {
        put(Attribute.TICK_RATE.getProperty(), rate);
    }

    public final String getString(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.STRING)
        {
            return m_jso.getString(name);
        }
        return null;
    }

    public final void setActive(boolean active)
    {
        put(Attribute.ACTIVE.getProperty(), active);
    }

    public final boolean isActive()
    {
        if (isDefined(Attribute.ACTIVE))
        {
            return this.getBoolean(Attribute.ACTIVE.getProperty());
        }
        return true;
    }

    public final void setMatrix(double... matrix)
    {
        FilterConvolveMatrix mjso = FilterConvolveMatrix.make();

        for (int i = 0; i < matrix.length; i++)
        {
            mjso.push(matrix[i]);
        }
        setMatrix(mjso);
    }

    public final void setMatrix(FilterConvolveMatrix matrix)
    {
        put(Attribute.MATRIX.getProperty(), matrix);
    }

    public final FilterConvolveMatrix getMatrix()
    {
        JavaScriptObject mjso = getArray(Attribute.MATRIX.getProperty());

        if (null != mjso)
        {
            return mjso.cast();
        }
        return FilterConvolveMatrix.make();
    }

    public final double getValue()
    {
        return getDouble(Attribute.VALUE.getProperty());
    }

    public final void setValue(double value)
    {
        put(Attribute.VALUE.getProperty(), value);
    }

    public final void setColor(String color)
    {
        put(Attribute.COLOR.getProperty(), color);
    }

    public final String getColor()
    {
        return getString(Attribute.COLOR.getProperty());
    }

    public final void setInverted(boolean inverted)
    {
        put(Attribute.INVERTED.getProperty(), inverted);
    }

    public final boolean isInverted()
    {
        return getBoolean(Attribute.INVERTED.getProperty());
    }

    public final double getGain()
    {
        return getDouble(Attribute.GAIN.getProperty());
    }

    public final void setGain(double gain)
    {
        put(Attribute.GAIN.getProperty(), gain);
    }

    public final double getBias()
    {
        return getDouble(Attribute.BIAS.getProperty());
    }

    public final void setBias(double bias)
    {
        put(Attribute.BIAS.getProperty(), bias);
    }

    public final boolean getBoolean(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.BOOLEAN)
        {
            return m_jso.getBoolean(name);
        }
        return false;
    }

    public final JavaScriptObject getObject(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.OBJECT)
        {
            return m_jso.getObject(name);
        }
        return null;
    }

    public final JsArray<JavaScriptObject> getArrayOfJSO(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.ARRAY)
        {
            return m_jso.getArrayOfJSO(name);
        }
        return null;
    }

    public final JsArrayMixed getArray(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.ARRAY)
        {
            return m_jso.getArray(name);
        }
        return null;
    }

    public final boolean isDefined(Attribute attr)
    {
        return m_jso.isDefined(attr.getProperty());
    }

    public final void delete(String name)
    {
        m_jso.delete(name);

        checkDispatchAttributeChanged(name);
    }

    public final NativeInternalType typeOf(Attribute attr)
    {
        return m_jso.typeOf(attr.getProperty());
    }
}