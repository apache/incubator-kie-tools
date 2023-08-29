/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import jsinterop.annotations.JsType;

@JsType
public class JsCanvasAnimations {

    public void alpha(IPrimitive<?> shape, double value, long duration) {
        animate(shape, AnimationProperty.Properties.ALPHA(value), duration);
    }

    public void x(IPrimitive<?> shape, double value, long duration) {
        animate(shape, AnimationProperty.Properties.X(value), duration);
    }

    public void y(IPrimitive<?> shape, double value, long duration) {
        animate(shape, AnimationProperty.Properties.Y(value), duration);
    }

    public void fillColor(Shape<?> shape, String color, long duration) {
        animate(shape, AnimationProperty.Properties.FILL_COLOR(color), duration);
    }

    public void fillAlpha(Shape<?> shape, double alpha, long duration) {
        animate(shape, AnimationProperty.Properties.FILL_ALPHA(alpha), duration);
    }

    public void strokeColor(Shape<?> shape, String color, long duration) {
        animate(shape, AnimationProperty.Properties.STROKE_COLOR(color), duration);
    }

    public void strokeAlpha(Shape<?> shape, double alpha, long duration) {
        animate(shape, AnimationProperty.Properties.STROKE_ALPHA(alpha), duration);
    }

    public void width(Shape<?> shape, double size, long duration) {
        animate(shape, AnimationProperty.Properties.WIDTH(size), duration);
    }

    public void height(Shape<?> shape, double size, long duration) {
        animate(shape, AnimationProperty.Properties.HEIGHT(size), duration);
    }

    public void radius(Shape<?> shape, double size, long duration) {
        animate(shape, AnimationProperty.Properties.RADIUS(size), duration);
    }

    public void rotationDegrees(Shape<?> shape, double degrees, long duration) {
        animate(shape, AnimationProperty.Properties.ROTATION_DEGREES(degrees), duration);
    }

    private static void animate(IPrimitive<?> shape, AnimationProperty property, long duration) {
        shape.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(property), duration);
    }

}
