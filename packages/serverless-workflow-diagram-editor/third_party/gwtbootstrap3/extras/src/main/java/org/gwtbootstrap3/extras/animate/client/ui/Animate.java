package org.gwtbootstrap3.extras.animate.client.ui;

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

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.UIObject;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;

/**
 * Utility class to dynamically animate objects using CSS animations.
 *
 * @author Pavel Zlámal
 */
public class Animate {

    // store used styles, so they are not injected to the DOM everytime.
    private static final ArrayList<String> usedStyles = new ArrayList<String>();

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs only once.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Type of animation to apply.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final Animation animation) {
        return animate(widget, animation, 1, -1, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Type of animation to apply.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final Animation animation, final int count) {
        return animate(widget, animation, count, -1, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Type of animation to apply.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param duration Animation duration in ms. 0 disables animation, any negative value keeps default of original animation.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final Animation animation, final int count, final int duration) {
        return animate(widget, animation, count, duration, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Type of animation to apply.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param duration Animation duration in ms. 0 disables animation, any negative value keeps default of original animation.
     * @param delay Delay before starting the animation loop in ms. Value <= 0 means no delay.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final Animation animation, final int count, final int duration, final int delay) {

        if (widget != null && animation != null) {
            // on valid input
            if (widget.getStyleName().contains(animation.getCssName())) {
                // animation is present, remove it and run again.
                stopAnimation(widget, animation.getCssName() + " " + getStyleNameFromAnimation(animation.getCssName(),count,duration,delay));
                Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        styleElement(widget.getElement(), animation.getCssName(), count, duration, delay);
                        return false;
                    }
                }, 200);
                return animation.getCssName() + " " + getStyleNameFromAnimation(animation.getCssName(),count,duration,delay);
            } else {
                // animation was not present, run immediately
                return styleElement(widget.getElement(), animation.getCssName(), count, duration, delay);
            }
        } else {
            return null;
        }

    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs only once.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Custom CSS class name used as animation.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final String animation) {
        return animate(widget, animation, 1, -1, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Custom CSS class name used as animation.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final String animation, final int count) {
        return animate(widget, animation, count, -1, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Custom CSS class name used as animation.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param duration Animation duration in ms. 0 disables animation, any negative value keeps default of original animation.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final String animation, final int count, final int duration) {
        return animate(widget, animation, count, duration, -1);
    }

    /**
     * Animate any element with specific animation. Animation is done by CSS and runs multiple times.
     *
     * Animation is started when element is appended to the DOM or new (not same) animation is added
     * to already displayed element. Animation runs on hidden elements too and is not paused/stopped
     * when element is set as hidden.
     *
     * @param widget Widget to apply animation to.
     * @param animation Custom CSS class name used as animation.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param duration Animation duration in ms. 0 disables animation, any negative value keeps default of original animation.
     * @param delay Delay before starting the animation loop in ms. Value <= 0 means no delay.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static <T extends UIObject> String animate(final T widget, final String animation, final int count, final int duration, final int delay) {

        if (widget != null && animation != null) {
            // on valid input
            if (widget.getStyleName().contains(animation)) {
                // animation is present, remove it and run again.
                stopAnimation(widget, animation);
                Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        styleElement(widget.getElement(), animation, count, duration, delay);
                        return false;
                    }
                }, 200);
                return animation + " " + getStyleNameFromAnimation(animation,count,duration,delay);
            } else {
                // animation was not present, run immediately
                return styleElement(widget.getElement(), animation, count, duration, delay);
            }
        } else {
            return null;
        }

    }

    /**
     * Styles element with animation class. New class name is generated to customize count, duration and delay.
     * Style is removed on animation end (if not set to infinite).
     *
     * @param element Element to apply animation to.
     * @param animation Type of animation to apply.
     * @param count Number of animation repeats. 0 disables animation, any negative value set repeats to infinite.
     * @param duration Animation duration in ms. 0 disables animation, any negative value keeps default of original animation.
     * @param delay Delay before starting the animation loop in ms. Value <= 0 means no delay.
     * @param <T> Any object extending UIObject class (typically Widget).
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    private static <T extends UIObject> String styleElement(Element element, String animation, int count, int duration, int delay) {

        if (!usedStyles.contains(animation + " " + getStyleNameFromAnimation(animation,count,duration,delay))) {

            String styleSheet = "." + getStyleNameFromAnimation(animation, count, duration, delay) + " {";

            // 1 is default, 0 disable animation, any negative -> infinite loop
            if (count >= 0) {

                styleSheet += "-webkit-animation-iteration-count: " + count + ";" +
                        "-moz-animation-iteration-count:" + count + ";" +
                        "-ms-animation-iteration-count:" + count + ";" +
                        "-o-animation-iteration-count:" + count + ";" +
                        "animation-iteration-count:" + count + ";";

            } else {

                styleSheet += "-webkit-animation-iteration-count: infinite;" +
                        "-moz-animation-iteration-count: infinite;" +
                        "-ms-animation-iteration-count: infinite;" +
                        "-o-animation-iteration-count: infinite;" +
                        "animation-iteration-count: infinite;";

            }

            // if not default (any negative -> use default)
            if (duration >= 0) {

                styleSheet += "-webkit-animation-duration: " + duration + "ms;" +
                        "-moz-animation-duration:" + duration + "ms;" +
                        "-ms-animation-duration:" + duration + "ms;" +
                        "-o-animation-duration:" + duration + "ms;" +
                        "animation-duration:" + duration + "ms;";

            }

            // if not default (any negative -> use default)
            if (delay >= 0) {

                styleSheet += "-webkit-animation-delay: " + delay + "ms;" +
                        "-moz-animation-delay:" + delay + "ms;" +
                        "-ms-animation-delay:" + delay + "ms;" +
                        "-o-animation-delay:" + delay + "ms;" +
                        "animation-delay:" + delay + "ms;";

            }

            styleSheet += "}";

            // inject new style
            StyleInjector.injectAtEnd(styleSheet, true);

            usedStyles.add(animation + " " + getStyleNameFromAnimation(animation, count, duration, delay));

        }

        // start animation
        element.addClassName(animation + " " + getStyleNameFromAnimation(animation,count,duration,delay));

        // remove animation on end so we could start it again
        // removeAnimationOnEnd(element, animation + " anim-"+count+"-"+duration+"-"+delay);

        return animation + " " + getStyleNameFromAnimation(animation,count,duration,delay);

    }

    /**
     * Removes custom animation class on animation end.
     *
     * @param widget Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    public static final <T extends UIObject> void removeAnimationOnEnd(final T widget, final String animation) {
        if (widget != null && animation != null) {
            removeAnimationOnEnd(widget.getElement(), animation);
        }
    }

    /**
     * Removes custom animation class on animation end.
     *
     * @param element Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    private static final native void removeAnimationOnEnd(Element element, String animation) /*-{

        var elem = $wnd.jQuery(element);
        elem.one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', { elem: elem }, function(event) {
            event.data.elem.removeClass(animation);
        });

    }-*/;

    /**
     * Removes custom animation class and stops animation.
     *
     * @param widget Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    public static final <T extends UIObject> void stopAnimation(final T widget, final String animation){
        if (widget != null && animation != null) {
            stopAnimation(widget.getElement(), animation);
        }
    }

    /**
     * Removes custom animation class and stops animation.
     *
     * @param element Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    private static final native void stopAnimation(Element element, String animation) /*-{
        $wnd.jQuery(element).removeClass(animation);
    }-*/;

    /**
     * Helper method, which returns unique class name for combination of animation and it's settings.
     *
     * @param animation Animation CSS class name.
     * @param count Number of animation repeats.
     * @param duration Animation duration in ms.
     * @param delay Delay before starting the animation loop in ms.
     * @return String representation of class name like "animation-count-duration-delay".
     */
    private static String getStyleNameFromAnimation(final String animation, int count, int duration, int delay) {

        // fix input
        if (count < 0) count = -1;
        if (duration < 0) duration = -1;
        if (delay < 0) delay = -1;

        String styleName = "";

        // for all valid animations
        if (animation != null && !animation.isEmpty() && animation.split(" ").length > 1) {

            styleName += animation.split(" ")[1]+"-"+count+"-"+duration+"-"+delay;

        // for all custom animations
        } else if (animation != null && !animation.isEmpty() && animation.split(" ").length == 1) {

            styleName += animation+"-"+count+"-"+duration+"-"+delay;

        }

        return styleName;

    }

}
