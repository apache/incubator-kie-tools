package org.gwtbootstrap3.extras.animate.client.ui.constants;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2014 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.gwtbootstrap3.client.ui.base.helper.EnumHelper;
import org.gwtproject.dom.client.Style;

/**
 * Enumeration of CSS animations from Animate.css project.
 * See: http://daneden.github.io/animate.css/
 *
 * @author Pavel Zlámal
 */
public enum Animation implements Style.HasCssName {

    BOUNCE("animated bounce"),
    BOUNCE_IN("animated bounceIn"),
    BOUNCE_IN_DOWN("animated bounceInDown"),
    BOUNCE_IN_LEFT("animated bounceInLeft"),
    BOUNCE_IN_RIGHT("animated bounceInRight"),
    BOUNCE_IN_UP("animated bounceInUp"),
    BOUNCE_OUT("animated bounceOut"),
    BOUNCE_OUT_DOWN("animated bounceOutDown"),
    BOUNCE_OUT_LEFT("animated bounceOutLeft"),
    BOUNCE_OUT_RIGHT("animated bounceOutRight"),
    BOUNCE_OUT_UP("animated bounceOutUp"),
    FADE_IN("animated fadeIn"),
    FADE_IN_DOWN("animated fadeInDown"),
    FADE_IN_DOWN_BIG("animated fadeInDownBig"),
    FADE_IN_LEFT("animated fadeInLeft"),
    FADE_IN_LEFT_BIG("animated fadeInLeftBig"),
    FADE_IN_RIGHT("animated fadeInRight"),
    FADE_IN_RIGHT_BIG("animated fadeInRightBig"),
    FADE_IN_UP("animated fadeInUp"),
    FADE_IN_UP_BIG("animated fadeInUpBig"),
    FADE_OUT("animated fadeOut"),
    FADE_OUT_DOWN("animated fadeOutDown"),
    FADE_OUT_DOWN_BIG("animated fadeOutDownBig"),
    FADE_OUT_LEFT("animated fadeOutLeft"),
    FADE_OUT_LEFT_BIG("animated fadeOutLeftBig"),
    FADE_OUT_RIGHT("animated fadeOutRight"),
    FADE_OUT_RIGHT_BIG("animated fadeOutRightBig"),
    FADE_OUT_UP("animated fadeOutUp"),
    FADE_OUT_UP_BIG("animated fadeOutUpBig"),
    FLASH("animated flash"),
    FLIP("animated flip"),
    FLIP_IN_X("animated flipInX"),
    FLIP_IN_Y("animated flipInY"),
    FLIP_OUT_X("animated flipOutX"),
    FLIP_OUT_Y("animated flipOutY"),
    HINGE("animated hinge"),
    JELLO("animated jello"),
    LIGHTSPEED_IN("animated lightSpeedIn"),
    LIGHTSPEED_OUT("animated lightSpeedOut"),
    NO_ANIMATION(""),
    PULSE("animated pulse"),
    ROLL_IN("animated rollIn"),
    ROLL_OUT("animated rollOut"),
    ROTATE_IN("animated rotateIn"),
    ROTATE_IN_DOWN_LEFT("animated rotateInDownLeft"),
    ROTATE_IN_DOWN_RIGHT("animated rotateInDownRight"),
    ROTATE_IN_UP_LEFT("animated rotateInUpLeft"),
    ROTATE_IN_UP_RIGHT("animated rotateInUpRight"),
    ROTATE_OUT("animated rotateOut"),
    ROTATE_OUT_DOWN_LEFT("animated rotateOutDownLeft"),
    ROTATE_OUT_DOWN_RIGHT("animated rotateOutDownRight"),
    ROTATE_OUT_UP_LEFT("animated rotateOutUpLeft"),
    ROTATE_OUT_UP_RIGHT("animated rotateOutUpRight"),
    RUBBER_BAND("animated rubberBand"),
    SHAKE("animated shake"),
    SLIDE_IN_UP("animated slideInUp"),
    SLIDE_IN_DOWN("animated slideInDown"),
    SLIDE_IN_LEFT("animated slideInLeft"),
    SLIDE_IN_RIGHT("animated slideInRight"),
    SLIDE_OUT_UP("animated slideOutUp"),
    SLIDE_OUT_DOWN("animated slideOutDown"),
    SLIDE_OUT_LEFT("animated slideOutLeft"),
    SLIDE_OUT_RIGHT("animated slideOutRight"),
    SWING("animated swing"),
    TADA("animated tada"),
    WOBBLE("animated wobble"),
    ZOOM_IN("animated zoomIn"),
    ZOOM_IN_DOWN("animated zoomInDown"),
    ZOOM_IN_LEFT("animated zoomInLeft"),
    ZOOM_IN_RIGHT("animated zoomInRight"),
    ZOOM_IN_UP("animated zoomInUp"),
    ZOOM_OUT("animated zoomOut"),
    ZOOM_OUT_DOWN("animated zoomOutDown"),
    ZOOM_OUT_LEFT("animated zoomOutLeft"),
    ZOOM_OUT_RIGHT("animated zoomOutRight"),
    ZOOM_OUT_UP("animated zoomOutUp");

    private final String cssClass;

    private Animation(final String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public String getCssName() {
        return cssClass;
    }

    public static Animation fromStyleName(final String styleName) {
        return EnumHelper.fromStyleName(styleName, Animation.class, NO_ANIMATION);
    }

}
