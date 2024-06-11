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

package org.kie.lienzo.client;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.IndefiniteAnimation;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

@JsType(isNative = false, namespace = GLOBAL)
public class AnimatedCirclesExample extends BaseExample implements Example {

    private IAnimationHandle animationHandle;

    private List<MotionCircle> circles = new ArrayList<MotionCircle>();
    private boolean animate = true;
    private final int yBottomOffSet = 0;
    int total = 100; //GWT.isProdMode() ? 100 : 3;

    public AnimatedCirclesExample(final String title) {
        super(title);
    }

    public void destroy() {
        super.destroy();
        animationHandle.stop();
    }

    public void run() {
        for (int i = 0; i < total; i++) {
            MotionCircle circle = new MotionCircle(Math.max(40, Math.random() * 60));

            circle.setAlpha(0.75)
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor());
            setRandomLocation(circle);
            circles.add(circle);
            layer.add(circle);
        }

        IAnimationCallback callback = new IAnimationCallback() {
            @Override
            public void onStart(final IAnimation animation, final IAnimationHandle handle) {

            }

            @Override
            public void onFrame(final IAnimation animation, final IAnimationHandle handle) {
                if (animate) {
                    //animateCircle(((MotionCircle)animation.getNode()));
                    if (animate) {
                        for (MotionCircle circle : circles) {
                            animateCircle(circle);
                        }
                        layer.batch();
                        //AnimationScheduler.get().requestAnimationFrame(this);
                    }
                }
            }

            @Override
            public void onClose(final IAnimation animation, final IAnimationHandle handle) {

            }
        };

        IndefiniteAnimation animation = new IndefiniteAnimation(callback);
        animationHandle = animation.run();
    }

    private void animateCircle(MotionCircle circle) {

        double x = circle.getX();
        double y = circle.getY();
        double r = circle.getRadius();

        if ((circle.getxVelocity() > 0 && x + circle.getxVelocity() + r > width) ||
                (circle.getxVelocity() < 0 && x + circle.getxVelocity() - r < 0)) {
            circle.setxVelocity(-circle.getxVelocity());
        }

        if ((circle.getyVelocity() > 0 && y + circle.getyVelocity() + r > height - yBottomOffSet) ||
                (circle.getyVelocity() < 0 && y + circle.getyVelocity() - r < 0)) {
            circle.setyVelocity(-circle.getyVelocity());
        }

        circle.setX(x + circle.getxVelocity());
        circle.setY(y + circle.getyVelocity());
    }

    private static final class MotionCircle extends Circle {

        private double xVelocity = Math.random() * 3;
        private double yVelocity = Math.random() * 3;

        public MotionCircle(double radius) {
            super(radius);
        }

        public double getxVelocity() {
            return xVelocity;
        }

        public void setxVelocity(double xVelocity) {
            this.xVelocity = xVelocity;
        }

        public double getyVelocity() {
            return yVelocity;
        }

        public void setyVelocity(double yVelocity) {
            this.yVelocity = yVelocity;
        }
    }
}
