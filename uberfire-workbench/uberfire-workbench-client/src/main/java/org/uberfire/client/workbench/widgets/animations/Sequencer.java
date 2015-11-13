/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.widgets.animations;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;

/**
 * Run a list of animations in order.
 */
public class Sequencer {

    private int currentAnimationIndex = 0;

    //Simple holder for sequenced animation details
    private class AnimationConfiguration {

        final WrappedAnimation animation;

        final int              duration;

        AnimationConfiguration(final WrappedAnimation animation,
                               final int duration) {
            this.animation = animation;
            this.duration = duration;
        }

    }

    //A wrapper for sequenced animations allowing us to hook into the onComplete method to launch the next animation
    private class WrappedAnimation extends Animation {

        private final SequencedAnimation animation;

        WrappedAnimation(final SequencedAnimation animation) {
            this.animation = animation;
        }

        @Override
        public void onComplete() {
            //Pass through to the wrapped animation
            animation.onComplete();
            runNextAnimation();
        }

        @Override
        protected void onUpdate(double progress) {
            //Pass through to the wrapped animation
            animation.onUpdate( progress );
        }

        @Override
        public void cancel() {
            //Pass through to the wrapped animation
            animation.cancel();
        }

        @Override
        protected double interpolate(double progress) {
            //Pass through to the wrapped animation
            return animation.interpolate( progress );
        }

        @Override
        protected void onCancel() {
            //Pass through to the wrapped animation
            animation.onCancel();
        }

        @Override
        protected void onStart() {
            //Pass through to the wrapped animation
            animation.onStart();
        }

    }

    //The list of animations
    private List<AnimationConfiguration> animations = new ArrayList<AnimationConfiguration>();

    /**
     * Add an animation to the list of animations to be sequenced.
     * 
     * @param animation
     * @param duration
     */
    public void add(final SequencedAnimation animation,
                    final int duration) {
        animations.add( new AnimationConfiguration( new WrappedAnimation( animation ),
                                                    duration ) );
    }

    /**
     * Run all animations.
     */
    public void run() {
        runNextAnimation();
    }

    /**
     * Reset the sequence to the begining.
     */
    public void reset() {
        currentAnimationIndex = 0;
    }

    private void runNextAnimation() {
        if ( currentAnimationIndex < animations.size() ) {
            final AnimationConfiguration config = animations.get( currentAnimationIndex++ );
            final WrappedAnimation animation = config.animation;
            final int duration = config.duration;
            animation.run( duration );
        } else {
            reset();
        }
    }

}
