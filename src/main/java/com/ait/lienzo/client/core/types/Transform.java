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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.util.GeometryException;
import com.ait.lienzo.client.core.util.Matrix;

import elemental2.core.Global;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Transform is an affine transformation matrix.
 * <p>
 * In general, an affine transformation is a composition of rotations, translations (i.e. offsets), dilations (i.e. scaling), and shears.
 * The underlying matrix is a 3x3 matrix of which the last 3 coordinates are hardcoded, so internally we only store 6 coordinates.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Affine_transformation">http://en.wikipedia.org/wiki/Affine_transformation</a>
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/awt/geom/AffineTransform.html">http://docs.oracle.com/javase/6/docs/api/java/awt/geom/AffineTransform.html</a>
 * @see <a href="http://mathworld.wolfram.com/AffineTransformation.html">http://mathworld.wolfram.com/AffineTransformation.html</a>
 */
@JsType
public final class Transform
{
    @JsProperty
    public double[] v;

//    public Transform()
//    {
//        this.v = new double[] {1, 0, 0, 1, 0, 0 };
//    }

    /**
     * Constructs a new <code>Transform</code> representing the
     * Identity transformation.
     */
    public Transform()
    {
        this.v = new double[] {1, 0, 0, 1, 0, 0 };
    }

    /**
     * Constructs a new <code>Transform</code> from 6 floating point
     * v representing the 6 specifiable entries of the 3x3
     * transformation matrix.
     *
     * @param m00 the X coordinate scaling element of the 3x3 matrix
     * @param m10 the Y coordinate shearing element of the 3x3 matrix
     * @param m01 the X coordinate shearing element of the 3x3 matrix
     * @param m11 the Y coordinate scaling element of the 3x3 matrix
     * @param m02 the X coordinate translation element of the 3x3 matrix
     * @param m12 the Y coordinate translation element of the 3x3 matrix
     */
    public static Transform makeFromValues(final double m00, final double m10, final double m01, final double m11, final double m02, final double m12)
    {
        Transform t = new Transform();
        t.v[0] = m00;
        t.v[1] = m10;
        t.v[2] = m01;
        t.v[3] = m11;
        t.v[4] = m02;
        t.v[5] = m12;

        return t;
}

    /**
     * Constructs a new <code>Transform</code> from 6 floating point
     * v representing the 6 specifiable entries of the 3x3
     * transformation matrix. 
     *
     * @param m an array with [m00, m10, m01, m11, m02, m12] where:
     * @param m00 the X coordinate scaling element of the 3x3 matrix
     * @param m10 the Y coordinate shearing element of the 3x3 matrix
     * @param m01 the X coordinate shearing element of the 3x3 matrix
     * @param m11 the Y coordinate scaling element of the 3x3 matrix
     * @param m02 the X coordinate translation element of the 3x3 matrix
     * @param m12 the Y coordinate translation element of the 3x3 matrix
     */
    public static Transform makeFromArray(final double[] m)
    {
        return makeFromValues(m[0], m[1], m[2], m[3], m[4], m[5]);
    }

    public final Transform reset()
    {
        v = new double[] {1, 0, 0, 1, 0, 0 };

        return this;
    }

    public boolean isIdentity()
    {
        return (this.v[0] == 1) && (this.v[1] == 0) && (this.v[2] == 0)
               && (this.v[3] == 1) && (this.v[4] == 0) && (this.v[5] == 0);
    }

    /**
     * Returns a copy of this Transform. 
     * The original Transform is not affected.
     * 
     * @return Transform
     */
    public Transform copy()
    {
        Transform jso = new Transform();
        jso.v = new double[] {this.v[0], this.v[1], this.v[2], this.v[3], this.v[4], this.v[5] };
        return jso;
    }

    /**
     * Concatenates this transform with a translation transformation.
     * It basically moves a node with the specified offset (tx,ty).
     * 
     * This is equivalent to calling concatenate(T), where T is an
     * <code>Transform</code> represented by the following matrix:
     * <pre>
     *      [   1    0    tx  ]
     *      [   0    1    ty  ]
     *      [   0    0    1   ]
     * </pre>
     * @param tx the distance by which coordinates are translated in the
     * X axis direction
     * @param ty the distance by which coordinates are translated in the
     * Y axis direction
     * 
     * @return this Transform
     */
    public final Transform translate(final double tx, final double ty)
    {
        this.v[4] += this.v[0] * tx + this.v[2] * ty;

        this.v[5] += this.v[1] * tx + this.v[3] * ty;

        return this;
    }

    /**
     * Concatenates this transform with a scaling transformation.
     * This is equivalent to calling concatenate(S), where S is an
     * <code>Transform</code> represented by the following matrix:
     * <pre>
     *      [   sx   0    0   ]
     *      [   0    sy   0   ]
     *      [   0    0    1   ]
     * </pre>
     * @param sx the factor by which coordinates are scaled along the   
     * X axis direction
     * @param sy the factor by which coordinates are scaled along the
     * Y axis direction 
     * @return this Transform
     */
    public final Transform scaleWithXY(final double sx, final double sy)
    {
        this.v[0] *= sx;

        this.v[1] *= sx;

        this.v[2] *= sy;

        this.v[3] *= sy;

        return this;
    }

    /**
     * Concatenates this transform with a scaling transformation.
     * Same as <pre>scaleWithXY(scaleFactor, scaleFactor)</pre>
     * 
     * @see #scaleWithXY(double, double)
     * @param scaleFactor used as the scaleWithXY factor for both x and y directions
     * @return this Transform
     */
    public final Transform scale(final double scaleFactor)
    {
        this.v[0] *= scaleFactor;

        this.v[1] *= scaleFactor;

        this.v[2] *= scaleFactor;

        this.v[3] *= scaleFactor;

        return this;
    }

    /**
     * Concatenates this transform with a shearing transformation.
     * This is equivalent to calling concatenate(SH), where SH is an
     * <code>Transform</code> represented by the following matrix:
     * <pre>
     *      [   1   shx   0   ]
     *      [  shy   1    0   ]
     *      [   0    0    1   ]
     * </pre>
     * @param shx the multiplier by which coordinates are shifted in the
     * direction of the positive X axis as a factor of their Y coordinate
     * @param shy the multiplier by which coordinates are shifted in the
     * direction of the positive Y axis as a factor of their X coordinate
     * @return this Transform
     */
    public final Transform shear(final double shx, final double shy)
    {
        double m00 = this.v[0];

        double m10 = this.v[1];

        this.v[0] += shy * this.v[2];

        this.v[1] += shy * this.v[3];

        this.v[2] += shx * m00;

        this.v[3] += shx * m10;

        return this;
    }

    /**
     * Concatenates this transform with a rotation transformation.
     * This is equivalent to calling concatenate(R), where R is an
     * <code>Transform</code> represented by the following matrix:
     * <pre>
     *      [   cos(theta)    -sin(theta)    0   ]
     *      [   sin(theta)     cos(theta)    0   ]
     *      [       0              0         1   ]
     * </pre>
     * Rotating by a positive angle theta rotates points on the positive
     * X axis toward the positive Y axis.
     * 
     * @param theta the angle of rotation measured in radians
     * 
     * @return this Transform
     */
    public final Transform rotate(final double theta)
    {
        double c = Math.cos(theta);

        double s = Math.sin(theta);

        double m11 = this.v[0] * c + this.v[2] * s;

        double m12 = this.v[1] * c + this.v[3] * s;

        double m21 = this.v[0] * -s + this.v[2] * c;

        double m22 = this.v[1] * -s + this.v[3] * c;

        this.v[0] = m11;

        this.v[1] = m12;

        this.v[2] = m21;

        this.v[3] = m22;

        return this;
    }

    /**
     * Same as {@link #concatenate(Transform)}
     * 
     * @param transform
     * 
     * @return this Transform
     */
    public final Transform multiply(final Transform transform)
    {
        double m11 = this.v[0] * transform.v[0] + this.v[2] * transform.v[1];

        double m12 = this.v[1] * transform.v[0] + this.v[3] * transform.v[1];

        double m21 = this.v[0] * transform.v[2] + this.v[2] * transform.v[3];

        double m22 = this.v[1] * transform.v[2] + this.v[3] * transform.v[3];

        double dx = this.v[0] * transform.v[4] + this.v[2] * transform.v[5] + this.v[4];

        double dy = this.v[1] * transform.v[4] + this.v[3] * transform.v[5] + this.v[5];

        this.v[0] = m11;

        this.v[1] = m12;

        this.v[2] = m21;

        this.v[3] = m22;

        this.v[4] = dx;

        this.v[5] = dy;

        return this;
    }

    /**
     * Concatenates a <code>Transform</code> <code>Tx</code> to
     * this <code>Transform</code> Cx in the most commonly useful
     * way to provide a new user space
     * that is mapped to the former user space by <code>Tx</code>.
     * Cx is updated to perform the combined transformation.
     * Transforming a point p by the updated transform Cx' is
     * equivalent to first transforming p by <code>Tx</code> and then
     * transforming the result by the original transform Cx like this:
     * Cx'(p) = Cx(Tx(p))  
     * In matrix notation, if this transform Cx is
     * represented by the matrix [this] and <code>Tx</code> is represented
     * by the matrix [Tx] then this method does the following:
     * <pre>
     *      [this] = [this] x [Tx]
     * </pre>
     * @param Tx the <code>Transform</code> object to be
     * concatenated with this <code>Transform</code> object.
     */
    public final Transform concatenate(final Transform transform)
    {
        multiply(transform);

        return this;
    }

    /**
     * Returns the inverse transform Tx' of this transform Tx, which
     * maps coordinates transformed by Tx back
     * to their original coordinates.
     * In other words, Tx'(Tx(p)) = p = Tx(Tx'(p)).
     * <p>
     * If this transform maps all coordinates onto a point or a line
     * then it will not have an inverse, since coordinates that do
     * not lie on the destination point or line will not have an inverse
     * mapping.
     * The <code>getDeterminant</code> method can be used to determine if this
     * transform has no inverse, in which case an exception will be
     * thrown if the <code>invert</code> method is called.
     * @see #getDeterminant
     * @exception GeometryException if the matrix cannot be inverted.
     * @return a new Transform
     */
    public final Transform getInverse() throws GeometryException
    {
        if (Math.abs(getDeterminant()) <= Double.MIN_VALUE)
        {
            throw new GeometryException("Can't invert this matrix - determinant is near 0");
        }

        //[0] m00, [1] m10, [2] m01, [3] m11, [4] m02, [5] m12
        double m00 = this.v[0];
        double m10 = this.v[1];
        double m01 = this.v[2];
        double m11 = this.v[3];
        double m02 = this.v[4];
        double m12 = this.v[5];

        double det = (m00 * m11) - (m01 * m10);

        Transform inverse = new Transform();
        inverse.v = new double[] {
                m11 / det,
                -m10 / det,
                -m01 / det,
                m00 / det,
                ((m01 * m12) - (m11 * m02)) / det,
                ((m10 * m02) - (m00 * m12)) / det
        };

        return inverse;
    }

    public final double getDeterminant()
    {
        return this.v[0] * this.v[3] - this.v[2] * this.v[1]; // m00 * m11 - m01 * m10
    }

    /**
     * Transforms the specified <code>ptSrc</code> and stores the result
     * in <code>ptDst</code>.
     * If <code>ptDst</code> is <code>null</code>, a new {@link Point2D}
     * object is allocated and then the result of the transformation is
     * stored in this object.
     * In either case, <code>ptDst</code>, which containsBoundingBox the
     * transformed point, is returned for convenience.
     * If <code>ptSrc</code> and <code>ptDst</code> are the same
     * object, the input point is correctly overwritten with
     * the transformed point.
     * @param ptSrc the specified <code>Point2D</code> to be transformed
     * @param ptDst the specified <code>Point2D</code> that stores the
     * result of transforming <code>ptSrc</code>
     * @return the <code>ptDst</code> after transforming
     * <code>ptSrc</code> and storing the result in <code>ptDst</code>.
     */
    public final void transform(final Point2D ptSrc, final Point2D ptDst)
    {
        double x = ptSrc.getX();
        double y = ptSrc.getY();
        ptDst.setX(x * this.v[0] + y * this.v[2] + this.v[4]);
        ptDst.setY(x * this.v[1] + y * this.v[3] + this.v[5]);
    }

    /**
     * Concatenates this transform with a translation, a rotation and another translation transformation, 
     * resulting in an scaling with respect to the specified point (x,y).
     * <p>
     * Equivalent to:
     * <pre>
     *  translate(x, y);
     *  scaleWithXY(scaleWithXY, scaleWithXY);
     *  translate(-x, -y);
     *  </pre>
     * @param scale
     * @param x
     * @param y
     */
    public Transform scaleAboutPoint(final double scale, final double x, final double y)
    {
        translate(x, y);

        scaleWithXY(scale, scale);

        translate(-x, -y);

        return this;
    }

    /**
     * Returns the X coordinate scaling element (m00) of the 3x3
     * affine transformation matrix.
     * @return a double value that is the X coordinate of the scaling
     *  element of the affine transformation matrix.
     */
    public double getScaleX()
    {
        return get(0);
    }

    /**
     * Returns the Y coordinate scaling element (m11) of the 3x3
     * affine transformation matrix.
     * @return a double value that is the Y coordinate of the scaling
     *  element of the affine transformation matrix.
     */
    public double getScaleY()
    {
        return get(3);
    }

    /**
    * Returns the X coordinate shearing element (m01) of the 3x3
    * affine transformation matrix.
    * @return a double value that is the X coordinate of the shearing
    *  element of the affine transformation matrix.
    */
    public double getShearX()
    {
        return get(2);
    }

    /**
     * Returns the Y coordinate shearing element (m10) of the 3x3
     * affine transformation matrix.
     * @return a double value that is the Y coordinate of the shearing
     *  element of the affine transformation matrix.
     */
    public double getShearY()
    {
        return get(1);
    }

    /**
     * Returns the X coordinate of the translation element (m02) of the
     * 3x3 affine transformation matrix.
     * @return a double value that is the X coordinate of the translation
     *  element of the affine transformation matrix.
     */
    public double getTranslateX()
    {
        return get(4);
    }

    /**
     * Returns the Y coordinate of the translation element (m12) of the
     * 3x3 affine transformation matrix.
     * @return a double value that is the Y coordinate of the translation
     *  element of the affine transformation matrix. 
     */
    public double getTranslateY()
    {
        return get(5);
    }

    /**
     * Returns the underlying matrix v.
     * 
     * @param i index into the array [m00, m10, m01, m11, m02, m12]
     * @return matrix value
     */
    public final double get(final int i)
    {
        return v[i];
    }


    public final String toJSONString()
    {
        return Global.JSON.stringify(this);
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (!(other instanceof Transform)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((Transform) other).same(this);
    }

    public final boolean same(Transform that)
    {
        return (this.v[0] == that.v[0]) && (this.v[1] == that.v[1])
               && (this.v[2] == that.v[2]) && (this.v[3] == that.v[3])
               && (this.v[4] == that.v[4]) && (this.v[5] == that.v[5]);
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public static final Transform fromXY(Transform xfrm, final double x, final double y)
    {

        if ( xfrm == null)
        {
            xfrm = new Transform();
        }
        xfrm.v[0] = 1;
        xfrm.v[1] = 0;
        xfrm.v[2] = 0;
        xfrm.v[3] = 1;
        xfrm.v[4] = x;
        xfrm.v[5] = y;

        return xfrm;
    }

    /**
     * Returns a Transform that converts
     * the 3 source points into the 3 destination points.
     *
     * @param src       Array with 3 (different) source points
     * @param target    Array with 3 target points
     * @return Transform
     */
    public static final Transform create3PointTransform(Point2DArray src, Point2DArray target)
    {
        // Determine T so that:
        //
        // T * P = P' for each Point
        //
        // where T = (a b c)
        // (d e f)
        //
        // T * P => x' = (ax + by + c)
        // y' = (dx + ey + f)
        //
        // Given are 3 points and their projections: P1, P1', P2, P2', P3, P3'.
        // P1 is (x1, y1) P1' is (x1', y1')
        //
        // (ax1 + by1 + c ) = x1'
        // ( dx1 + ey1 + f) = y1'
        // (ax2 + by2 + c ) = x2'
        // ( dx2 + ey2 + f) = y2'
        // (ax3 + by3 + c ) = x3'
        // ( dx3 + ey3 + f) = y3'

        Point2D p1 = src.get(0);
        Point2D p2 = src.get(1);
        Point2D p3 = src.get(2);

        Point2D p1_ = target.get(0);
        Point2D p2_ = target.get(1);
        Point2D p3_ = target.get(2);

        double[][] eq = { { p1.getX(), p1.getY(), 1, 0, 0, 0 }, { 0, 0, 0, p1.getX(), p1.getY(), 1 }, { p2.getX(), p2.getY(), 1, 0, 0, 0 }, { 0, 0, 0, p2.getX(), p2.getY(), 1 }, { p3.getX(), p3.getY(), 1, 0, 0, 0 }, { 0, 0, 0, p3.getX(), p3.getY(), 1 }, };

        double[][] s = { { p1_.getX(), p1_.getY(), p2_.getX(), p2_.getY(), p3_.getX(), p3_.getY() } };
        Matrix m = new Matrix(eq);
        Matrix rhs = new Matrix(s).transpose();
        Matrix T = m.solve(rhs);

        double[][] d = T.getData();
        return Transform.makeFromValues(d[0][0], d[3][0], d[1][0], d[4][0], d[2][0], d[5][0]);
    }

    /**
     * Creates a Transform for a viewport. The visible area is defined by the rectangle
     * [x, y, width, height] and the viewport's width and height.
     * 
     * @param x X coordinate of the top-left corner of the new view area.
     * @param y Y coordinate of the top-left corner of the new view area.
     * @param width Width of the new view area.
     * @param height Height of the new View area.
     * @param viewportWidth Width of the Viewport.
     * @param viewportHeight Height of the Viewport.
     * @return Transform
     */
    public static Transform createViewportTransform(double x, double y, double width, double height, double viewportWidth, double viewportHeight)
    {
        if (width <= 0 || height <= 0)
        {
            return null;
        }
        double scaleX = viewportWidth / width;

        double scaleY = viewportHeight / height;

        double scale;

        if (scaleX > scaleY)
        {
            // use scaleY

            scale = scaleY;

            double dw = viewportWidth / scale - width;

            x -= dw / 2;
        }
        else
        {
            scale = scaleX;

            double dh = viewportHeight / scale - height;

            y -= dh / 2;
        }
        // x' = m[0] + x*m[1] y' = m[2] + y*m[3]

        double m02 = -x * scale;

        double m12 = -y * scale;

        return Transform.makeFromValues(scale, 0, 0, scale, m02, m12);
    }

//    /**
//     * Javascript class to store the Transform matrix v.
//     * It's an array with 6 v:
//     *
//     * [0] m00, [1] m10, [2] m01, [3] m11, [4] m02, [5] m12
//     *
//     */
//    @JsType
//    public static final class TransformJSO
//    {
//        @JsProperty
//        public double[] v;
//
//        protected TransformJSO()
//        {
//            this.v = new double[] {1, 0, 0, 1, 0, 0 };
//        }
//
//        public static final TransformJSO makeXY(double x, double y)
//        {
//            TransformJSO jso = new TransformJSO();
//            jso.v = new double[] {1, 0, 0, 1, x, y };
//            return jso;
//        };
//
//        public static final TransformJSO makeFromValues(double m00, double m10, double m01, double m11, double m02, double m12)
//        {
//            TransformJSO jso = new TransformJSO();
//            jso.v = new double[] {m00, m10, m01, m11, m02, m12};
//            return jso;
//        };
//
//        public final void reset()
//        {
//            v = new double[] {1, 0, 0, 1, 0, 0 };
//        };
//
//        public final void translate(double x, double y)
//        {
//			this.v[4] += this.v[0] * x + this.v[2] * y;
//
//			this.v[5] += this.v[1] * x + this.v[3] * y;
//        };
//
//        public final boolean same(TransformJSO that)
//        {
//			return (this.v[0] == that.v[0]) && (this.v[1] == that.v[1])
//                   && (this.v[2] == that.v[2]) && (this.v[3] == that.v[3])
//                   && (this.v[4] == that.v[4]) && (this.v[5] == that.v[5]);
//        };
//
//        public final boolean isIdentity()
//        {
//			return (this.v[0] == 1) && (this.v[1] == 0) && (this.v[2] == 0)
//                   && (this.v[3] == 1) && (this.v[4] == 0) && (this.v[5] == 0);
//        };
//
//        public final TransformJSO copy()
//        {
//            TransformJSO jso = new TransformJSO();
//            jso.v = new double[] {this.v[0], this.v[1], this.v[2], this.v[3], this.v[4], this.v[5] };
//            return jso;
//        };
//
//        public final void scaleWithXY(double sx, double sy)
//        {
//			this.v[0] *= sx;
//
//			this.v[1] *= sx;
//
//			this.v[2] *= sy;
//
//			this.v[3] *= sy;
//        };
//
//        public final void shear(double shx, double shy)
//        {
//			double m00 = this.v[0];
//
//			double m10 = this.v[1];
//
//			this.v[0] += shy * this.v[2];
//
//			this.v[1] += shy * this.v[3];
//
//			this.v[2] += shx * m00;
//
//			this.v[3] += shx * m10;
//        };
//
//        public final void rotate(double rad)
//        {
//			double c = Math.cos(rad);
//
//            double s = Math.sin(rad);
//
//            double m11 = this.v[0] * c + this.v[2] * s;
//
//            double m12 = this.v[1] * c + this.v[3] * s;
//
//            double m21 = this.v[0] * -s + this.v[2] * c;
//
//            double m22 = this.v[1] * -s + this.v[3] * c;
//
//			this.v[0] = m11;
//
//			this.v[1] = m12;
//
//			this.v[2] = m21;
//
//			this.v[3] = m22;
//        };
//
//        public final void multiply(TransformJSO transform)
//        {
//            double m11 = this.v[0] * transform.v[0] + this.v[2] * transform.v[1];
//
//            double m12 = this.v[1] * transform.v[0] + this.v[3] * transform.v[1];
//
//            double m21 = this.v[0] * transform.v[2] + this.v[2] * transform.v[3];
//
//            double m22 = this.v[1] * transform.v[2] + this.v[3] * transform.v[3];
//
//            double dx = this.v[0] * transform.v[4] + this.v[2] * transform.v[5] + this.v[4];
//
//            double dy = this.v[1] * transform.v[4] + this.v[3] * transform.v[5] + this.v[5];
//
//			this.v[0] = m11;
//
//			this.v[1] = m12;
//
//			this.v[2] = m21;
//
//			this.v[3] = m22;
//
//			this.v[4] = dx;
//
//			this.v[5] = dy;
//        };
//
//        public final double getDeterminant()
//        {
//			return this.v[0] * this.v[3] - this.v[2] * this.v[1]; // m00 * m11 - m01 * m10
//        };
//
//        public final TransformJSO getInverse()
//        {
//			//[0] m00, [1] m10, [2] m01, [3] m11, [4] m02, [5] m12
//            double m00 = this.v[0];
//            double m10 = this.v[1];
//            double m01 = this.v[2];
//            double m11 = this.v[3];
//            double m02 = this.v[4];
//            double m12 = this.v[5];
//
//            double det = m00 * m11 - m01 * m10;
//
//            TransformJSO jso = new TransformJSO();
//            jso.v = new double[] {m11 / det, -m10 / det, -m01 / det, m00 / det,
//					(m01 * m12 - m11 * m02) / det,
//					(m10 * m02 - m00 * m12) / det };
//
//            return jso;
//        };
//
//        public final double get(int i)
//        {
//			return this.v[i];
//        };
//
//        public final void transform(Point2D src, Point2D target)
//        {
//			double x = src.getX();
//			double y = src.getY();
//			target.setX(x * this.v[0] + y * this.v[2] + this.v[4]);
//			target.setY(x * this.v[1] + y * this.v[3] + this.v[5]);
//        };
//    }
}
