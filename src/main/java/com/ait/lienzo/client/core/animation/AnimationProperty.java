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

package com.ait.lienzo.client.core.animation;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ColorExtractor;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.Color.HSL;
import com.ait.lienzo.shared.core.types.IColor;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * AnimationProperty defines what node attribute is modified during a "tweening" animation 
 * and what its ultimate target value is.
 * Several can be animated in parallel, by adding them to an {@link AnimationProperties}.
 * <p>
 * See {@link Properties} for convenience methods to create animations for common node attributes.
 * 
 * @see Properties
 * @see AnimationProperties
 * @see AnimationTweener
 */

public interface AnimationProperty
{
    boolean init(Node<?> node);

    boolean apply(Node<?> node, double percent);

    boolean isStateful();

    boolean isRefreshing();

    AnimationProperty copy();

    /**
     * Properties provides convenience methods for defining which attributes of an IPrimitive node 
     * will be animated during a "tweening" animation.
     * <p>
     * The resulting {@link AnimationProperty} objects should be grouped together in
     * an {@link AnimationProperties} object.
     * 
     * @see AnimationProperty
     * @see AnimationProperties
     * @see AnimationTweener
     */

    class Properties
    {
        private Properties() {

        }

        public static final AnimationProperty X(final double x)
        {
            return new DoubleAnimationProperty(x, Attribute.X);
        }

        public static final AnimationProperty X(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.X);
        }

        public static final AnimationProperty Y(final double y)
        {
            return new DoubleAnimationProperty(y, Attribute.Y);
        }

        public static final AnimationProperty Y(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.Y);
        }

        public static final AnimationProperty WIDTH(final double wide)
        {
            return new DoubleAnimationProperty(wide, Attribute.WIDTH);
        }

        public static final AnimationProperty WIDTH(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.WIDTH);
        }

        public static final AnimationProperty HEIGHT(final double high)
        {
            return new DoubleAnimationProperty(high, Attribute.HEIGHT);
        }

        public static final AnimationProperty HEIGHT(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.HEIGHT);
        }

        public static final AnimationProperty ALPHA(final double alpha)
        {
            return new DoubleAnimationPropertyConstrained(alpha, Attribute.ALPHA, 0.0, 1.0, 1.0);
        }

        public static final AnimationProperty ALPHA(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.ALPHA, 0.0, 1.0);
        }

        public static final AnimationProperty FILL_ALPHA(final double alpha)
        {
            return new DoubleAnimationPropertyConstrained(alpha, Attribute.FILL_ALPHA, 0.0, 1.0, 1.0);
        }

        public static final AnimationProperty FILL_ALPHA(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.FILL_ALPHA, 0.0, 1.0);
        }

        public static final AnimationProperty STROKE_ALPHA(final double alpha)
        {
            return new DoubleAnimationPropertyConstrained(alpha, Attribute.STROKE_ALPHA, 0.0, 1.0, 1.0);
        }

        public static final AnimationProperty STROKE_ALPHA(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.STROKE_ALPHA, 0.0, 1.0);
        }

        public static final AnimationProperty RADIUS(final double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty RADIUS(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty FONT_SIZE(final double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.FONT_SIZE, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty FONT_SIZE(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.FONT_SIZE, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty CORNER_RADIUS(final double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.CORNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty CORNER_RADIUS(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.CORNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty OUTER_RADIUS(final double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.OUTER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty OUTER_RADIUS(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.OUTER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty INNER_RADIUS(final double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.INNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty INNER_RADIUS(final double origin, final double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.INNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty ROTATION(final double rotation)
        {
            return new DoubleAnimationProperty(rotation, Attribute.ROTATION);
        }

        public static final AnimationProperty ROTATION(final double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.ROTATION);
        }

        public static final AnimationProperty ROTATION_DEGREES(final double degrees)
        {
            return new DoubleAnimationProperty(degrees * Math.PI / 180, Attribute.ROTATION);
        }

        public static final AnimationProperty ROTATION_DEGREES(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin * Math.PI / 180, target * Math.PI / 180, Attribute.ROTATION);
        }

        public static final AnimationProperty START_ANGLE(final double rotation)
        {
            return new DoubleAnimationProperty(rotation, Attribute.START_ANGLE);
        }

        public static final AnimationProperty START_ANGLE(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.START_ANGLE);
        }

        public static final AnimationProperty START_ANGLE_DEGREES(final double degrees)
        {
            return new DoubleAnimationProperty(degrees * Math.PI / 180, Attribute.START_ANGLE);
        }

        public static final AnimationProperty START_ANGLE_DEGREES(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin * Math.PI / 180, target * Math.PI / 180, Attribute.START_ANGLE);
        }

        public static final AnimationProperty END_ANGLE(final double rotation)
        {
            return new DoubleAnimationProperty(rotation, Attribute.END_ANGLE);
        }

        public static final AnimationProperty END_ANGLE(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.END_ANGLE);
        }

        public static final AnimationProperty END_ANGLE_DEGREES(final double degrees)
        {
            return new DoubleAnimationProperty(degrees * Math.PI / 180, Attribute.END_ANGLE);
        }

        public static final AnimationProperty END_ANGLE_DEGREES(final double origin, final double target)
        {
            return new DoubleRangeAnimationProperty(origin * Math.PI / 180, target * Math.PI / 180, Attribute.END_ANGLE);
        }

        public static final AnimationProperty STROKE_WIDTH(final double stroke)
        {
            return new DoubleAnimationPropertyConstrained(stroke, Attribute.STROKE_WIDTH, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty SCALE(final Point2D scale)
        {
            return new Point2DAnimationProperty_1(scale, Attribute.SCALE);
        }

        public static final AnimationProperty SCALE(final double scale)
        {
            return new Point2DAnimationProperty_1(new Point2D(scale, scale), Attribute.SCALE);
        }

        public static final AnimationProperty SCALE(final double x, final double y)
        {
            return new Point2DAnimationProperty_1(new Point2D(x, y), Attribute.SCALE);
        }

        public static final AnimationProperty OFFSET(final Point2D offset)
        {
            return new Point2DAnimationProperty_0(offset, Attribute.OFFSET);
        }

        public static final AnimationProperty OFFSET(final double value)
        {
            return new Point2DAnimationProperty_0(new Point2D(value, value), Attribute.OFFSET);
        }

        public static final AnimationProperty OFFSET(final double x, final double y)
        {
            return new Point2DAnimationProperty_0(new Point2D(x, y), Attribute.OFFSET);
        }

        public static final AnimationProperty SHEAR(final Point2D shear)
        {
            return new Point2DAnimationProperty_0(shear, Attribute.SHEAR);
        }

        public static final AnimationProperty SHEAR(final double value)
        {
            return new Point2DAnimationProperty_0(new Point2D(value, value), Attribute.SHEAR);
        }

        public static final AnimationProperty SHEAR(final double x, final double y)
        {
            return new Point2DAnimationProperty_0(new Point2D(x, y), Attribute.SHEAR);
        }

        public static final AnimationProperty POSITIONING(final IPositioningCalculator calc)
        {
            return new PositioningAnimationProperty(calc);
        }

        public static final AnimationProperty DASH_OFFSET(final double offset)
        {
            return new DoubleAnimationProperty(offset, Attribute.DASH_OFFSET);
        }

        public static final AnimationProperty FILL_COLOR(final String color)
        {
            return new StringFillColorAnimationProperty(color, Attribute.FILL);
        }

        public static final AnimationProperty FILL_COLOR(final IColor color)
        {
            return new StringFillColorAnimationProperty(color.getColorString(), Attribute.FILL);
        }

        public static final AnimationProperty STROKE_COLOR(final String color)
        {
            return new StringStrokeColorAnimationProperty(color, Attribute.FILL);
        }

        public static final AnimationProperty STROKE_COLOR(final IColor color)
        {
            return new StringStrokeColorAnimationProperty(color.getColorString(), Attribute.FILL);
        }

        private static final class StringFillColorAnimationProperty extends AbstractStringColorAnimationProperty
        {
            public StringFillColorAnimationProperty(final String target, final Attribute attribute)
            {
                super(target, attribute);
            }

            @Override
            protected String getColorString(final Node<?> node)
            {
                return node.asShape().getFillColor();
            }

            @Override
            protected void setColorString(final Node<?> node, final String color)
            {
                node.asShape().setFillColor(color);
            }

            @Override
            public StringFillColorAnimationProperty copy()
            {
                return new StringFillColorAnimationProperty(getTarget(), getAttribute());
            }
        }

        private static final class StringStrokeColorAnimationProperty extends AbstractStringColorAnimationProperty
        {
            public StringStrokeColorAnimationProperty(final String target, final Attribute attribute)
            {
                super(target, attribute);
            }

            @Override
            protected String getColorString(final Node<?> node)
            {
                return node.asShape().getStrokeColor();
            }

            @Override
            protected void setColorString(final Node<?> node, final String color)
            {
                node.asShape().setStrokeColor(color);
            }

            @Override
            public StringStrokeColorAnimationProperty copy()
            {
                return new StringStrokeColorAnimationProperty(getTarget(), getAttribute());
            }
        }

        private abstract static class AbstractStringColorAnimationProperty implements AnimationProperty
        {
            private final String    m_target;

            private final Attribute m_attribute;

            private double          m_origin_h;

            private double          m_origin_s;

            private double          m_origin_l;

            private double          m_origin_a;

            private double          m_target_h;

            private double          m_target_s;

            private double          m_target_l;

            private double          m_target_a;

            public AbstractStringColorAnimationProperty(final String target, final Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            protected final String getTarget()
            {
                return m_target;
            }

            @Override
            public boolean isRefreshing()
            {
                return false;
            }

            protected Attribute getAttribute()
            {
                return m_attribute;
            }

            protected abstract String getColorString(Node<?> node);

            protected abstract void setColorString(Node<?> node, String color);

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    Color cend = ColorExtractor.extract(m_target);

                    String color = getColorString(node);

                    if ((null == color) || ((color = color.trim()).isEmpty()))
                    {
                        color = "transparent";
                    }
                    Color cbeg;

                    if ("transparent".equals(color))
                    {
                        cbeg = new Color(cend.getR(), cend.getG(), cend.getB(), 0.0);
                    }
                    else
                    {
                        cbeg = ColorExtractor.extract(color);
                    }
                    HSL hbeg = cbeg.getHSL();

                    HSL hend = cend.getHSL();

                    m_origin_h = hbeg.getH();

                    m_origin_s = hbeg.getS();

                    m_origin_l = hbeg.getL();

                    m_origin_a = cbeg.getA();

                    m_target_h = hend.getH();

                    m_target_s = hend.getS();

                    m_target_l = hend.getL();

                    m_target_a = cend.getA();

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                final double h = (m_origin_h + ((m_target_h - m_origin_h) * percent));

                final double s = (m_origin_s + ((m_target_s - m_origin_s) * percent));

                final double l = (m_origin_l + ((m_target_l - m_origin_l) * percent));

                final double a = (m_origin_a + ((m_target_a - m_origin_a) * percent));

                setColorString(node, Color.fromNormalizedHSL(h, s, l).setA(a).getColorString());

                return true;
            }

            @Override
            public boolean isStateful()
            {
                return true;
            }
        }

        private static final class PositioningAnimationProperty implements AnimationProperty
        {
            private final IPositioningCalculator m_calc;

            public PositioningAnimationProperty(final IPositioningCalculator calc)
            {
                m_calc = calc;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                return (node != null) && (m_calc != null) ? true : false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                final Point2D posn = m_calc.calculate(percent);

                if (posn != null)
                {
                    node.setX(posn.getX());
                    node.setY(posn.getY());
                    return true;
                }
                return false;
            }

            @Override
            public PositioningAnimationProperty copy()
            {
                if (m_calc.isStateful())
                {
                    return new PositioningAnimationProperty(m_calc);
                }
                else
                {
                    return new PositioningAnimationProperty(m_calc.copy());
                }
            }

            @Override
            public boolean isStateful()
            {
                return m_calc.isStateful();
            }

            @Override
            public boolean isRefreshing()
            {
                return false;
            }
        }

        private static final class DoubleRangeAnimationProperty implements AnimationProperty
        {
            private final double    m_target;

            private final double    m_origin;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public DoubleRangeAnimationProperty(final double origin, final double target, final Attribute attribute)
            {
                m_origin = origin;

                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  (m_origin + ((m_target - m_origin) * percent)));

                return true;
            }

            @Override
            public DoubleRangeAnimationProperty copy()
            {
                return new DoubleRangeAnimationProperty(m_origin, m_target, m_attribute);
            }

            @Override
            public boolean isStateful()
            {
                return false;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }

        private static final class DoubleAnimationProperty implements AnimationProperty
        {
            private double          m_origin;

            private final double    m_target;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public DoubleAnimationProperty(final double target, final Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);

                    m_origin = Js.coerceToDouble(nodeMap.get(m_attribute.getProperty()));

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  (m_origin + ((m_target - m_origin) * percent)));

                return true;
            }

            @Override
            public DoubleAnimationProperty copy()
            {
                return new DoubleAnimationProperty(m_target, m_attribute);
            }

            @Override
            public boolean isStateful()
            {
                return true;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }

        private static final class DoubleRangeAnimationPropertyConstrained implements AnimationProperty
        {
            private double          m_origin;

            private double          m_target;

            private final double    m_minval;

            private final double    m_maxval;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public DoubleRangeAnimationPropertyConstrained(final double origin, final double target, final Attribute attribute, final double minval, final double maxval)
            {
                m_origin = origin;

                m_target = target;

                m_minval = minval;

                m_maxval = maxval;

                m_attribute = attribute;

                if (m_origin < m_minval)
                {
                    m_origin = m_minval;
                }
                if (m_origin > m_maxval)
                {
                    m_origin = m_maxval;
                }
                if (m_target < m_minval)
                {
                    m_target = m_minval;
                }
                if (m_target > m_maxval)
                {
                    m_target = m_maxval;
                }
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                double value = (m_origin + ((m_target - m_origin) * percent));

                if (value < m_minval)
                {
                    value = m_minval;
                }
                if (value > m_maxval)
                {
                    value = m_maxval;
                }

                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  value);

                return true;
            }

            @Override
            public DoubleRangeAnimationPropertyConstrained copy()
            {
                return new DoubleRangeAnimationPropertyConstrained(m_origin, m_target, m_attribute, m_minval, m_maxval);
            }

            @Override
            public boolean isStateful()
            {
                return false;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }

        private static final class DoubleAnimationPropertyConstrained implements AnimationProperty
        {
            private double          m_origin;

            private double          m_target;

            private final double    m_minval;

            private final double    m_maxval;

            private final double    m_defval;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public DoubleAnimationPropertyConstrained(final double target, final Attribute attribute, final double minval, final double maxval)
            {
                this(target, attribute, minval, maxval, 0);
            }

            public DoubleAnimationPropertyConstrained(final double target, final Attribute attribute, final double minval, final double maxval, final double defval)
            {
                m_target = target;

                m_minval = minval;

                m_maxval = maxval;

                m_defval = defval;

                m_attribute = attribute;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);

                    if (nodeMap.has(m_attribute.getProperty()))
                    {
                        m_origin = Js.coerceToDouble(nodeMap.get(m_attribute.getProperty()));
                    }
                    else
                    {
                        m_origin = m_defval;
                    }
                    if (m_origin < m_minval)
                    {
                        m_origin = m_minval;
                    }
                    if (m_origin > m_maxval)
                    {
                        m_origin = m_maxval;
                    }
                    if (m_target < m_minval)
                    {
                        m_target = m_minval;
                    }
                    if (m_target > m_maxval)
                    {
                        m_target = m_maxval;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                double value = (m_origin + ((m_target - m_origin) * percent));

                if (value < m_minval)
                {
                    value = m_minval;
                }
                if (value > m_maxval)
                {
                    value = m_maxval;
                }
                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  value);

                return true;
            }

            @Override
            public DoubleAnimationPropertyConstrained copy()
            {
                return new DoubleAnimationPropertyConstrained(m_target, m_attribute, m_minval, m_maxval);
            }

            @Override
            public boolean isStateful()
            {
                return true;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }

        private static final class Point2DAnimationProperty_0 implements AnimationProperty
        {
            private double          m_orig_x;

            private double          m_orig_y;

            private final double    m_targ_x;

            private final double    m_targ_y;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public Point2DAnimationProperty_0(final Point2D target, final Attribute attribute)
            {
                m_targ_x = target.getX();

                m_targ_y = target.getY();

                m_attribute = attribute;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);

                    final Point2D orig = Js.uncheckedCast(nodeMap.get(m_attribute.getProperty()));

                    if (null == orig)
                    {
                        m_orig_x = 0;

                        m_orig_y = 0;
                    }
                    else
                    {
                        m_orig_x = orig.getX();

                        m_orig_y = orig.getY();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  new Point2D(m_orig_x + ((m_targ_x - m_orig_x) * percent), m_orig_y + ((m_targ_y - m_orig_y) * percent)));

                return true;
            }

            @Override
            public Point2DAnimationProperty_0 copy()
            {
                return new Point2DAnimationProperty_0(new Point2D(m_targ_x, m_targ_y), m_attribute);
            }

            @Override
            public boolean isStateful()
            {
                return true;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }

        private static final class Point2DAnimationProperty_1 implements AnimationProperty
        {
            private double          m_orig_x;

            private double          m_orig_y;

            private final double    m_targ_x;

            private final double    m_targ_y;

            private final Attribute m_attribute;

            private boolean         m_refreshing = false;

            public Point2DAnimationProperty_1(final Point2D target, final Attribute attribute)
            {
                m_targ_x = target.getX();

                m_targ_y = target.getY();

                m_attribute = attribute;
            }

            @Override
            public boolean init(final Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_refreshing = node.getBoundingBoxAttributes().contains(m_attribute);

                    JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);

                    final Point2D orig = Js.uncheckedCast(nodeMap.get(m_attribute.getProperty()));

                    if (null == orig)
                    {
                        m_orig_x = 1;

                        m_orig_y = 1;
                    }
                    else
                    {
                        m_orig_x = orig.getX();

                        m_orig_y = orig.getY();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(final Node<?> node, final double percent)
            {
                JsPropertyMap<Object> nodeMap  = Js.uncheckedCast(node);
                nodeMap.set(m_attribute.getProperty(),  new Point2D(m_orig_x + ((m_targ_x - m_orig_x) * percent), m_orig_y + ((m_targ_y - m_orig_y) * percent)));

                return true;
            }

            @Override
            public Point2DAnimationProperty_1 copy()
            {
                return new Point2DAnimationProperty_1(new Point2D(m_targ_x, m_targ_y), m_attribute);
            }

            @Override
            public boolean isStateful()
            {
                return true;
            }

            @Override
            public boolean isRefreshing()
            {
                return m_refreshing;
            }
        }
    }
}
