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

import com.google.gwt.user.client.ui.Widget;

/**
 * A linear animation to fade a Widget from 0.0 to 1.0 opacity
 */
public class LinearFadeInAnimation extends SequencedAnimation {

    private Widget widget;

    public LinearFadeInAnimation(final Widget widget) {
        this.widget = widget;
    }

    @Override
    public void onUpdate(double progress) {
        this.widget.getElement().getStyle().setOpacity( progress );
    }

    @Override
    public double interpolate(double progress) {
        return progress;
    }

}
