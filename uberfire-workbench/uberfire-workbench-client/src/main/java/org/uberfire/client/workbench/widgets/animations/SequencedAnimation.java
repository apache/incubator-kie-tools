/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

import com.google.gwt.animation.client.Animation;

/**
 * An animation that can be sequenced to run in a list of animations. In reality
 * we only GWT's Animation's protected methods to be public so we can chain the
 * completion of one Animation to another.
 */
public abstract class SequencedAnimation extends Animation {

    @Override
    public double interpolate(double progress) {
        return super.interpolate( progress );
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    public abstract void onUpdate(double progress);

}