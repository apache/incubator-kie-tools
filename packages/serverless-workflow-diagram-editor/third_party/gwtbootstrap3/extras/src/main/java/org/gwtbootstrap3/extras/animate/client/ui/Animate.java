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

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.gwtbootstrap3.client.shared.js.JQuery;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.gwtproject.core.client.Scheduler;
import org.kie.j2cl.tools.processors.common.injectors.StyleInjector;

import static org.gwtbootstrap3.client.shared.js.JQuery.$;

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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final Animation animation) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final Animation animation, final int count) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final Animation animation, final int count, final int duration) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final Animation animation, final int count, final int duration, final int delay) {

        if (widget != null && animation != null) {
            // on valid input
            if (widget.classList.contains(animation.getCssName())) {
                // animation is present, remove it and run again.
                stopAnimation(widget, animation.getCssName() + " " + getStyleNameFromAnimation(animation.getCssName(),count,duration,delay));
                Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        styleElement(widget, animation.getCssName(), count, duration, delay);
                        return false;
                    }
                }, 200);
                return animation.getCssName() + " " + getStyleNameFromAnimation(animation.getCssName(),count,duration,delay);
            } else {
                // animation was not present, run immediately
                return styleElement(widget, animation.getCssName(), count, duration, delay);
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final String animation) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final String animation, final int count) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final String animation, final int count, final int duration) {
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    public static String animate(final HTMLElement widget, final String animation, final int count, final int duration, final int delay) {

        if (widget != null && animation != null) {
            // on valid input
            if (widget.classList.contains(animation)) {
                // animation is present, remove it and run again.
                stopAnimation(widget, animation);
                Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        styleElement(widget, animation, count, duration, delay);
                        return false;
                    }
                }, 200);
                return animation + " " + getStyleNameFromAnimation(animation,count,duration,delay);
            } else {
                // animation was not present, run immediately
                return styleElement(widget, animation, count, duration, delay);
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
     * @return Animation's CSS class name, which can be removed to stop animation.
     */
    private static String styleElement(HTMLElement element, String animation, int count, int duration, int delay) {

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
            StyleInjector.fromString(styleSheet).inject();

            usedStyles.add(animation + " " + getStyleNameFromAnimation(animation, count, duration, delay));

        }

        addClassName(element, animation + " " + getStyleNameFromAnimation(animation,count,duration,delay));
        return animation + " " + getStyleNameFromAnimation(animation,count,duration,delay);
    }

    public static final boolean addClassName(HTMLElement element, String className) {
        className = trimClassName(className);
        String oldClassName = element.className;
        int idx = indexOfName(oldClassName, className);
        if (idx == -1) {
            if (oldClassName.length() > 0) {
                element.className = (oldClassName + " " + className);
            } else {
                element.className = (className);
            }
            return true;
        } else {
            return false;
        }
    }

    private static String trimClassName(String className) {
        assert className != null : "Unexpectedly null class name";

        className = className.trim();

        assert !className.isEmpty() : "Unexpectedly empty class name";

        return className;
    }

    private static int indexOfName(String nameList, String name) {
        int idx;
        for(idx = nameList.indexOf(name); idx != -1; idx = nameList.indexOf(name, idx + 1)) {
            if (idx == 0 || nameList.charAt(idx - 1) == ' ') {
                int last = idx + name.length();
                int lastPos = nameList.length();
                if (last == lastPos || last < lastPos && nameList.charAt(last) == ' ') {
                    break;
                }
            }
        }

        return idx;
    }

    /**
     * Removes custom animation class on animation end.
     *
     * @param widget Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    public static final void removeAnimationOnEnd(final HTMLElement widget, final String animation) {
        if (widget != null && animation != null) {
            _removeAnimationOnEnd(widget, animation);
        }
    }

    /**
     * Removes custom animation class on animation end.
     *
     * @param element Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    private static void _removeAnimationOnEnd(HTMLElement element, String animation) {

        JQueryExt elem = (JQueryExt) $(element);
        JsArray arrayString = new JsArray<String>();
        arrayString.push("webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend");
        JsPropertyMap params = JsPropertyMap.of();
        params.set("elem", elem);

        elem.one(arrayString, Js.uncheckedCast(params), event -> ((HasRemoveClass)Js.asPropertyMap(Js.asPropertyMap(event).get("data")).get("elem")).removeClass(animation));
    }

    /**
     * Removes custom animation class and stops animation.
     *
     * @param widget Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    public static final void stopAnimation(final HTMLElement widget, final String animation){
        if (widget != null && animation != null) {
            _stopAnimation(widget, animation);
        }
    }

    /**
     * Removes custom animation class and stops animation.
     *
     * @param element Element to remove style from.
     * @param animation Animation CSS class to remove.
     */
    private static final void _stopAnimation(final HTMLElement element, String animation) {
        JQueryExt elem = (JQueryExt) $(element);
        ((HasRemoveClass) Js.uncheckedCast(elem)).removeClass(animation);
    }

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

    @JsType(
            isNative = true,
            namespace = "<global>",
            name = "jQuery"
    )
    private static class JQueryExt extends JQuery {

        @JsMethod
        native void one(JsArray arrayString, JsObject params, Fn callback);

    }

    @FunctionalInterface
    @JsFunction
    interface Fn {
        void onInvoke(Event event);
    }

    @JsType(
            isNative = true,
            name = "Object",
            namespace = "<global>"
    )
    static class HasRemoveClass implements JsPropertyMap {
        native void removeClass(String animation);
    }

}
