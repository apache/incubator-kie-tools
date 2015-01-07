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

package com.ait.lienzo.client.core.animation;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.positioning.IPositioningCalculator;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ColorExtractor;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.IColor;

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
    public boolean init(Node<?> node);

    public boolean apply(Node<?> node, double percent);

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
    public static class Properties
    {
        public static final AnimationProperty X(double x)
        {
            return new DoubleAnimationProperty(x, Attribute.X);
        }

        public static final AnimationProperty X(double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.X);
        }

        public static final AnimationProperty Y(double y)
        {
            return new DoubleAnimationProperty(y, Attribute.Y);
        }

        public static final AnimationProperty Y(double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.Y);
        }

        public static final AnimationProperty WIDTH(double wide)
        {
            return new DoubleAnimationProperty(wide, Attribute.WIDTH);
        }

        public static final AnimationProperty WIDTH(double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.WIDTH);
        }

        public static final AnimationProperty HEIGHT(double high)
        {
            return new DoubleAnimationProperty(high, Attribute.HEIGHT);
        }

        public static final AnimationProperty HEIGHT(double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.HEIGHT);
        }

        public static final AnimationProperty ALPHA(double alpha)
        {
            return new DoubleAnimationPropertyConstrained(alpha, Attribute.ALPHA, 0.0, 1.0);
        }

        public static final AnimationProperty ALPHA(double origin, double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.ALPHA, 0.0, 1.0);
        }

        public static final AnimationProperty ROTATION(double rotation)
        {
            return new DoubleAnimationProperty(rotation, Attribute.ROTATION);
        }

        public static final AnimationProperty ROTATION(double origin, double target)
        {
            return new DoubleRangeAnimationProperty(origin, target, Attribute.ROTATION);
        }

        public static final AnimationProperty RADIUS(double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty RADIUS(double origin, double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty OUTER_RADIUS(double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.OUTER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty OUTER_RADIUS(double origin, double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.OUTER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty INNER_RADIUS(double radius)
        {
            return new DoubleAnimationPropertyConstrained(radius, Attribute.INNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty INNER_RADIUS(double origin, double target)
        {
            return new DoubleRangeAnimationPropertyConstrained(origin, target, Attribute.INNER_RADIUS, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty ROTATION_DEGREES(double degrees)
        {
            return new DoubleAnimationProperty(degrees * Math.PI / 180, Attribute.ROTATION);
        }

        public static final AnimationProperty STROKE_WIDTH(double stroke)
        {
            return new DoubleAnimationPropertyConstrained(stroke, Attribute.STROKE_WIDTH, 0.0, Float.MAX_VALUE);
        }

        public static final AnimationProperty SCALE(Point2D scale)
        {
            return new Point2DAnimationProperty_1(scale, Attribute.SCALE);
        }

        public static final AnimationProperty SCALE(double scale)
        {
            return new Point2DAnimationProperty_1(new Point2D(scale, scale), Attribute.SCALE);
        }

        public static final AnimationProperty SCALE(double x, double y)
        {
            return new Point2DAnimationProperty_1(new Point2D(x, y), Attribute.SCALE);
        }

        public static final AnimationProperty OFFSET(Point2D scale)
        {
            return new Point2DAnimationProperty_0(scale, Attribute.OFFSET);
        }

        public static final AnimationProperty OFFSET(double value)
        {
            return new Point2DAnimationProperty_0(new Point2D(value, value), Attribute.OFFSET);
        }

        public static final AnimationProperty OFFSET(double x, double y)
        {
            return new Point2DAnimationProperty_0(new Point2D(x, y), Attribute.OFFSET);
        }

        public static final AnimationProperty SHEAR(Point2D shear)
        {
            return new Point2DAnimationProperty_0(shear, Attribute.SHEAR);
        }

        public static final AnimationProperty SHEAR(double value)
        {
            return new Point2DAnimationProperty_0(new Point2D(value, value), Attribute.SHEAR);
        }

        public static final AnimationProperty SHEAR(double x, double y)
        {
            return new Point2DAnimationProperty_0(new Point2D(x, y), Attribute.SHEAR);
        }

        public static final AnimationProperty POSITIONING(IPositioningCalculator calc)
        {
            return new PositioningAnimationProperty(calc);
        }

        public static final AnimationProperty DASH_OFFSET(double offset)
        {
            return new DoubleAnimationProperty(offset, Attribute.DASH_OFFSET);
        }

        public static final AnimationProperty FILL_COLOR(String color)
        {
            return new StringFillColorAnimationProperty(color, Attribute.FILL);
        }

        public static final AnimationProperty FILL_COLOR(IColor color)
        {
            return new StringFillColorAnimationProperty(color.getColorString(), Attribute.FILL);
        }

        public static final AnimationProperty STROKE_COLOR(String color)
        {
            return new StringStrokeColorAnimationProperty(color, Attribute.FILL);
        }

        public static final AnimationProperty STROKE_COLOR(IColor color)
        {
            return new StringStrokeColorAnimationProperty(color.getColorString(), Attribute.FILL);
        }

        private static final class StringFillColorAnimationProperty extends AbstractStringColorAnimationProperty
        {
            public StringFillColorAnimationProperty(String target, Attribute attribute)
            {
                super(target, attribute);
            }

            @Override
            protected String getColorString(Node<?> node)
            {
                return node.getAttributes().getFillColor();
            }

            @Override
            protected void setColorString(Node<?> node, String color)
            {
                node.getAttributes().setFillColor(color);
            }
        }

        private static final class StringStrokeColorAnimationProperty extends AbstractStringColorAnimationProperty
        {
            public StringStrokeColorAnimationProperty(String target, Attribute attribute)
            {
                super(target, attribute);
            }

            @Override
            protected String getColorString(Node<?> node)
            {
                return node.getAttributes().getStrokeColor();
            }

            @Override
            protected void setColorString(Node<?> node, String color)
            {
                node.getAttributes().setStrokeColor(color);
            }
        }

        private static abstract class AbstractStringColorAnimationProperty implements AnimationProperty
        {
            private String    m_target;

            private Attribute m_attribute;

            private double    m_origin_r;

            private double    m_origin_g;

            private double    m_origin_b;

            private double    m_origin_a;

            private double    m_target_r;

            private double    m_target_g;

            private double    m_target_b;

            private double    m_target_a;

            public AbstractStringColorAnimationProperty(String target, Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            protected abstract String getColorString(Node<?> node);

            protected abstract void setColorString(Node<?> node, String color);

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
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
                    m_origin_r = cbeg.getR();

                    m_origin_g = cbeg.getG();

                    m_origin_b = cbeg.getB();

                    m_origin_a = cbeg.getA();

                    m_target_r = cend.getR();

                    m_target_g = cend.getG();

                    m_target_b = cend.getB();

                    m_target_a = cend.getA();

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                double r = (m_origin_r + ((m_target_r - m_origin_r) * percent));

                double g = (m_origin_g + ((m_target_g - m_origin_g) * percent));

                double b = (m_origin_b + ((m_target_b - m_origin_b) * percent));

                double a = (m_origin_a + ((m_target_a - m_origin_a) * percent));

                setColorString(node, new Color(((int) (r + 0.5)), ((int) (g + 0.5)), ((int) (b + 0.5)), a).getColorString());

                return true;
            }
        }

        private static final class PositioningAnimationProperty implements AnimationProperty
        {
            private final IPositioningCalculator m_calc;

            public PositioningAnimationProperty(IPositioningCalculator calc)
            {
                m_calc = calc;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_calc != null))
                {
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                Point2D posn = m_calc.calculate(percent);

                if (posn != null)
                {
                    node.getAttributes().put(Attribute.X.getProperty(), posn.getX());

                    node.getAttributes().put(Attribute.Y.getProperty(), posn.getY());

                    return true;
                }
                return false;
            }
        }

        private static final class DoubleRangeAnimationProperty implements AnimationProperty
        {
            private double    m_target;

            private double    m_origin;

            private Attribute m_attribute;

            public DoubleRangeAnimationProperty(double origin, double target, Attribute attribute)
            {
                m_origin = origin;

                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                node.getAttributes().put(m_attribute.getProperty(), (m_origin + ((m_target - m_origin) * percent)));

                return true;
            }
        }

        private static final class DoubleAnimationProperty implements AnimationProperty
        {
            private double    m_target;

            private double    m_origin;

            private Attribute m_attribute;

            public DoubleAnimationProperty(double target, Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_origin = node.getAttributes().getDouble(m_attribute.getProperty());

                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                node.getAttributes().put(m_attribute.getProperty(), (m_origin + ((m_target - m_origin) * percent)));

                return true;
            }
        }

        private static final class DoubleRangeAnimationPropertyConstrained implements AnimationProperty
        {
            private double          m_origin;

            private double          m_target;

            private final double    m_minval;

            private final double    m_maxval;

            private final Attribute m_attribute;

            public DoubleRangeAnimationPropertyConstrained(double origin, double target, Attribute attribute, double minval, double maxval)
            {
                m_origin = origin;

                m_target = target;

                m_minval = minval;

                m_maxval = maxval;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
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
            public boolean apply(Node<?> node, double percent)
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
                node.getAttributes().put(m_attribute.getProperty(), value);

                return true;
            }
        }

        private static final class DoubleAnimationPropertyConstrained implements AnimationProperty
        {
            private double          m_origin;

            private double          m_target;

            private final double    m_minval;

            private final double    m_maxval;

            private final Attribute m_attribute;

            public DoubleAnimationPropertyConstrained(double target, Attribute attribute, double minval, double maxval)
            {
                m_target = target;

                m_minval = minval;

                m_maxval = maxval;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_origin = node.getAttributes().getDouble(m_attribute.getProperty());

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
            public boolean apply(Node<?> node, double percent)
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
                node.getAttributes().put(m_attribute.getProperty(), value);

                return true;
            }
        }

        private static final class Point2DAnimationProperty_0 implements AnimationProperty
        {
            private Point2D   m_target;

            private Point2D   m_origin;

            private Attribute m_attribute;

            public Point2DAnimationProperty_0(Point2D target, Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_origin = node.getAttributes().getPoint2D(m_attribute.getProperty());

                    if (null == m_origin)
                    {
                        m_origin = new Point2D(0, 0);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                double x = m_origin.getX() + ((m_target.getX() - m_origin.getX()) * percent);

                double y = m_origin.getY() + ((m_target.getY() - m_origin.getY()) * percent);

                node.getAttributes().putPoint2D(m_attribute.getProperty(), new Point2D(x, y));

                return true;
            }
        }

        private static final class Point2DAnimationProperty_1 implements AnimationProperty
        {
            private Point2D   m_target;

            private Point2D   m_origin;

            private Attribute m_attribute;

            public Point2DAnimationProperty_1(Point2D target, Attribute attribute)
            {
                m_target = target;

                m_attribute = attribute;
            }

            @Override
            public boolean init(Node<?> node)
            {
                if ((node != null) && (m_attribute != null) && (node.getAttributeSheet().contains(m_attribute)))
                {
                    m_origin = node.getAttributes().getPoint2D(m_attribute.getProperty());

                    if (null == m_origin)
                    {
                        m_origin = new Point2D(1, 1);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean apply(Node<?> node, double percent)
            {
                double x = m_origin.getX() + ((m_target.getX() - m_origin.getX()) * percent);

                double y = m_origin.getY() + ((m_target.getY() - m_origin.getY()) * percent);

                node.getAttributes().putPoint2D(m_attribute.getProperty(), new Point2D(x, y));

                return true;
            }
        }
    }
}
