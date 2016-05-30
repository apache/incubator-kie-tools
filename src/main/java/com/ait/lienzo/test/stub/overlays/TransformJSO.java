/*
 *
 *    Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.util.LienzoMockitoLogger;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Stub for class <code>com.ait.lienzo.client.core.types.Transform$TransformJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass( "com.ait.lienzo.client.core.types.Transform$TransformJSO" )
public class TransformJSO extends JavaScriptObject
{
    private double[] matrix = new double[6];
    
    protected TransformJSO()
    {
        LienzoMockitoLogger.log("TransformJSO", "Creating custom Lienzo overlay type.");
    }

    public static TransformJSO make() {
        return make(1d, 0d , 0d, 1d, 0d, 0d);
    }
    
    public static TransformJSO make(double x, double y) {
        return make(1d, 0d , 0d, 1d, x, y);
    }

    public static TransformJSO make(double m00, double m10, double m01, double m11, double m02, double m12)
    {
        TransformJSO jso =  new TransformJSO();
        jso.matrix[0] = m00;
        jso.matrix[1] = m10;
        jso.matrix[2] = m01;
        jso.matrix[3] = m11;
        jso.matrix[4] = m02;
        jso.matrix[5] = m12;
        return jso;
    };

    public void reset() {
        this.matrix[0] = 1;
        this.matrix[1] = 0;
        this.matrix[2] = 0;
        this.matrix[3] = 1;
        this.matrix[4] = 0;
        this.matrix[5] = 0;
    }

    public void translate(double x, double y) {
        this.matrix[4] += this.matrix[0] * x + this.matrix[2] * y;
        this.matrix[5] += this.matrix[1] * x + this.matrix[3] * y;
    }

    public boolean same(TransformJSO that) {
        return (this.matrix[0] == that.matrix[0]) && (this.matrix[1] == that.matrix[1])
                && (this.matrix[2] == that.matrix[2]) && (this.matrix[3] == that.matrix[3])
                && (this.matrix[4] == that.matrix[4]) && (this.matrix[5] == that.matrix[5]);
    }

    public boolean isIdentity() {
        return (this.matrix[0] == 1) && (this.matrix[1] == 0) && (this.matrix[2] == 0)
                && (this.matrix[3] == 1) && (this.matrix[4] == 0) && (this.matrix[5] == 0);
    }

    public TransformJSO copy() {
        return make(this.matrix[0], this.matrix[1], this.matrix[2], this.matrix[3], this.matrix[4], this.matrix[5]);
    }

    public void scale(double sx, double sy) {
        this.matrix[0] *= sx;

        this.matrix[1] *= sx;

        this.matrix[2] *= sy;

        this.matrix[3] *= sy;
    }
        
    public void shear(double shx, double shy) {
        double m00 = this.matrix[0];

        double m10 = this.matrix[1];

        this.matrix[0] += shy * this.matrix[2];

        this.matrix[1] += shy * this.matrix[3];

        this.matrix[2] += shx * m00;

        this.matrix[3] += shx * m10;
    }

    public void rotate(double rad) {
        double c = Math.cos(rad);

        double s = Math.sin(rad);

        double m11 = this.matrix[0] * c + this.matrix[2] * s;

        double m12 = this.matrix[1] * c + this.matrix[3] * s;

        double m21 = this.matrix[0] * -s + this.matrix[2] * c;

        double m22 = this.matrix[1] * -s + this.matrix[3] * c;

        this.matrix[0] = m11;

        this.matrix[1] = m12;

        this.matrix[2] = m21;

        this.matrix[3] = m22;
    }

    public void multiply(TransformJSO transform) {
        double m11 = this.matrix[0] * transform.matrix[0] + this.matrix[2] * transform.matrix[1];

        double m12 = this.matrix[1] * transform.matrix[0] + this.matrix[3] * transform.matrix[1];

        double m21 = this.matrix[0] * transform.matrix[2] + this.matrix[2] * transform.matrix[3];

        double m22 = this.matrix[1] * transform.matrix[2] + this.matrix[3] * transform.matrix[3];

        double dx = this.matrix[0] * transform.matrix[4] + this.matrix[2] * transform.matrix[5] + this.matrix[4];

        double dy = this.matrix[1] * transform.matrix[4] + this.matrix[3] * transform.matrix[5] + this.matrix[5];

        this.matrix[0] = m11;

        this.matrix[1] = m12;

        this.matrix[2] = m21;

        this.matrix[3] = m22;

        this.matrix[4] = dx;

        this.matrix[5] = dy;
    }

    public double getDeterminant() {
        return this.matrix[0] * this.matrix[3] - this.matrix[2] * this.matrix[1]; // m00 * m11 - m01 * m10
    }

    public TransformJSO getInverse() {
        double m00 = this.matrix[0];
        double m10 = this.matrix[1];
        double m01 = this.matrix[2];
        double m11 = this.matrix[3];
        double m02 = this.matrix[4];
        double m12 = this.matrix[5];

        double det = m00 * m11 - m01 * m10;

        return make(m11 / det, -m10 / det, -m01 / det, m00 / det,
                (m01 * m12 - m11 * m02) / det,
                (m10 * m02 - m00 * m12) / det);
    }
    
    public double get(int i) {
        return this.matrix[i];
    }

    public void transform(Point2D.Point2DJSO src, Point2D.Point2DJSO target) {
        double x = src.getX();
        double y = src.getY();
        target.setX(x * this.matrix[0] + y * this.matrix[2] + this.matrix[4]);
        target.setY(x * this.matrix[1] + y * this.matrix[3] + this.matrix[5]);
    }

}