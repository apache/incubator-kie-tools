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

package org.kie.workbench.common.stunner.client.widgets.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

import org.gwtbootstrap3.extras.animate.client.ui.Animate;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingWidgetView;

/**
 * Extension for the Stunner's common floating views which adds support for Bootstrap3 animations.
 */
@Dependent
@Specializes
public class AnimatedFloatingWidgetView extends FloatingWidgetView {

    private static Logger LOGGER = Logger.getLogger(AnimatedFloatingWidgetView.class.getName());
    private static final int DURATION = 600;

    private String aid;

    @Override
    protected void doShow() {
        getPanel().getElement().getStyle().setOpacity(0);
        super.doShow();
        LOGGER.log(Level.FINE,
                   "Showing animated floating view.");
        aid = Animate.animate(getPanel(),
                              Animation.FADE_IN,
                              1,
                              DURATION);
    }

    @Override
    protected void doHide() {
        LOGGER.log(Level.FINE,
                   "Hiding animated floating view.");
        if (null != aid) {
            LOGGER.log(Level.FINE,
                       "Stopping last animation [" + aid + "]");
            Animate.stopAnimation(getPanel(),
                                  aid);
            this.aid = null;
        }
        super.doHide();
    }
}
