/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.toolbox;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.mvp.Command;

public class ToolboxVisibilityExecutors {

    private static final double ANIMATION_SCALE_DELAY_MILLIS = 50;
    private static final double ANIMATION_ALPHA_DELAY_MILLIS = 50;

    public static AnimatedAlphaGroupExecutor alpha(final double targetAlphaValue) {
        return new AnimatedAlphaGroupExecutor(targetAlphaValue);
    }

    public static AnimatedScaleXGroupExecutor upScaleX() {
        return scaleX(1,
                      0.1,
                      1);
    }

    public static AnimatedScaleXGroupExecutor downScaleX() {
        return scaleX(0,
                      1,
                      0.1);
    }

    public static AnimatedScaleYGroupExecutor upScaleY() {
        return scaleY(1,
                      0.1,
                      1);
    }

    public static AnimatedScaleYGroupExecutor downScaleY() {
        return scaleY(0,
                      1,
                      0.1);
    }

    public static AnimatedScaleXGroupExecutor scaleX(final double targetAlphaValue,
                                                     final double startScale,
                                                     final double endScale) {
        return new AnimatedScaleXGroupExecutor(targetAlphaValue,
                                               startScale,
                                               endScale);
    }

    private static AnimatedScaleYGroupExecutor scaleY(final double targetAlphaValue,
                                                      final double startScale,
                                                      final double endScale) {
        return new AnimatedScaleYGroupExecutor(targetAlphaValue,
                                               startScale,
                                               endScale);
    }

    public abstract static class AnimatedGroupExecutor<T extends AnimatedGroupExecutor>
            implements BiConsumer<Group, Command> {

        private double animationDuration;
        private AnimationTweener animationTweener;

        public AnimatedGroupExecutor(final double duration) {
            this.animationTweener = AnimationTweener.LINEAR;
            this.animationDuration = duration;
        }

        protected abstract AnimationProperties getProperties();

        @Override
        public void accept(final Group group,
                           final Command callback) {
            animate(group,
                    callback);
        }

        private void animate(final Group group,
                             final Command callback) {
            group.animate(animationTweener,
                          getProperties(),
                          animationDuration,
                          new AnimationCallback() {
                              @Override
                              public void onClose(IAnimation animation,
                                                  IAnimationHandle handle) {
                                  super.onClose(animation,
                                                handle);
                                  callback.execute();
                              }
                          });
        }

        public T setAnimationTweener(final AnimationTweener animationTweener) {
            this.animationTweener = animationTweener;
            return cast();
        }

        public T setAnimationDuration(final double millis) {
            this.animationDuration = millis;
            return cast();
        }

        @SuppressWarnings("unchecked")
        private T cast() {
            return (T) this;
        }
    }

    public static class AnimatedAlphaGroupExecutor extends AnimatedGroupExecutor<AnimatedAlphaGroupExecutor> {

        private double alpha;

        protected AnimatedAlphaGroupExecutor(final double alpha) {
            super(ANIMATION_ALPHA_DELAY_MILLIS);
            this.alpha = alpha;
        }

        @Override
        protected AnimationProperties getProperties() {
            return AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(alpha));
        }

        public AnimatedAlphaGroupExecutor setAlpha(final double alpha) {
            this.alpha = alpha;
            return this;
        }
    }

    public abstract static class AnimatedScaleGroupExecutor<T extends AnimatedScaleGroupExecutor>
            extends AnimatedGroupExecutor<T> {

        private final double alpha;

        protected AnimatedScaleGroupExecutor(final double alpha) {
            super(ANIMATION_SCALE_DELAY_MILLIS);
            this.alpha = alpha;
        }

        protected abstract Point2D getInitialScale();

        protected abstract Point2D getEndScale();

        @Override
        protected AnimationProperties getProperties() {
            return AnimationProperties.toPropertyList(AnimationProperty.Properties.SCALE(getEndScale()));
        }

        @Override
        public void accept(final Group group,
                           final Command callback) {
            group
                    .setScale(getInitialScale())
                    .setAlpha(1);
            super.accept(group,
                         () -> {
                             group.setAlpha(alpha);
                             callback.execute();
                         });
        }
    }

    public static class AnimatedScaleXGroupExecutor extends AnimatedScaleGroupExecutor<AnimatedScaleXGroupExecutor> {

        private final double start;
        private final double end;

        protected AnimatedScaleXGroupExecutor(final double alpha,
                                              final double start,
                                              final double end) {
            super(alpha);
            this.start = start;
            this.end = end;
        }

        @Override
        protected Point2D getInitialScale() {
            return new Point2D(start,
                               1);
        }

        @Override
        protected Point2D getEndScale() {
            return new Point2D(end,
                               1);
        }
    }

    public static class AnimatedScaleYGroupExecutor extends AnimatedScaleGroupExecutor<AnimatedScaleYGroupExecutor> {

        private final double start;
        private final double end;

        protected AnimatedScaleYGroupExecutor(final double alpha,
                                              final double start,
                                              final double end) {
            super(alpha);
            this.start = start;
            this.end = end;
        }

        @Override
        protected Point2D getInitialScale() {
            return new Point2D(1,
                               start);
        }

        @Override
        protected Point2D getEndScale() {
            return new Point2D(1,
                               end);
        }
    }
}
