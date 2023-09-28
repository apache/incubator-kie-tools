package org.gwtbootstrap3.extras.notify.client.ui;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
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

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyIconType;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPlacement;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPosition;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyUrlTarget;
import org.gwtbootstrap3.extras.notify.client.event.NotifyCloseHandler;
import org.gwtbootstrap3.extras.notify.client.event.NotifyClosedHandler;
import org.gwtbootstrap3.extras.notify.client.event.NotifyShowHandler;
import org.gwtbootstrap3.extras.notify.client.event.NotifyShownHandler;

/**
 * This class represent basic Notify's settings, that you can use to customize display of each Notify.
 * <p/>
 * You can also set current state as default for all new Notifies.
 * @author jeffisenhart
 * @author Sven Jacobs
 * @author Joshua Godi
 * @author Pavel Zlámal
 * @author Xiaodong SUN
 * @see #makeDefault()
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NotifySettings {

    /**
     * Default constructor
     */
    @JsConstructor
    protected NotifySettings() {
    }

    /**
     * Creates a new instance of {@link NotifySettings}.
     * @return a new instance of {@link NotifySettings}.
     */
    @JsOverlay
    public static NotifySettings newSettings() {
        return new NotifySettings();
    }

    /**
     * Set element name or class or ID to append Notify to. Default is 'body'.
     * @param element Name, class or ID
     */
    @JsOverlay
    public final void setElement(String element) {
        Js.asPropertyMap(this).set("element", element);
    }

    /**
     * Set custom position to the Notify container element. Default is null.
     * @param position one of STATIC, FIXED, RELATIVE, ABSOLUTE, or null
     */
    @JsOverlay
    public final void setPosition(final NotifyPosition position) {
        setPosition((position != null) ? position.getPosition() : null);
    }

    /**
     * Set native property of Notify's position.
     * @param position Notify's position to the container element
     */
    @JsOverlay
    private final void setPosition(String position) {
        Js.asPropertyMap(this).set("position", position);
    }

    /**
     * Set type of Notify (CSS style class name). Default is INFO.
     * @param type one of INFO, WARNING, DANGER, SUCCESS
     * @see NotifyType
     */
    @JsOverlay
    public final void setType(final NotifyType type) {
        setType((type != null) ? type.getCssName() : NotifyType.INFO.getCssName());
    }

    /**
     * Set custom style name to Notify. Resulting class name is "alert-[customType]".
     * @param customType Style name to set
     */
    @JsOverlay
    public final void setType(String customType) {
        Js.asPropertyMap(this).set("type", customType);
    }

    /**
     * If <code>false</code>, the <code>data-notify="dismiss"</code> element in
     * the template will be hidden. Default is <code>true</code>.
     * @param allowDismiss if <code>false</code>, the close icon will be hidden
     */
    @JsOverlay
    public final void setAllowDismiss(boolean allowDismiss) {
        Js.asPropertyMap(this).set("allow_dismiss", allowDismiss);
    }

    /**
     * If <code>true</code>, the notification should display a progress bar.
     * Default is <code>false</code>.
     * @param showProgressbar if <code>true</code>, the progress bar will be displayed
     * @since 3.0.1
     */
    @JsOverlay
    public final void setShowProgressbar(boolean showProgressbar) {
        Js.asPropertyMap(this).set("showProgressbar", showProgressbar);
    }

    /**
     * Set placement of Notify on screen. Default placement is {@link NotifyPlacement#TOP_RIGHT}.
     * @param placement Notify's placement on screen
     * @see NotifyPlacement
     */
    @JsOverlay
    public final void setPlacement(final NotifyPlacement placement) {
        setNotifyPlacement((placement != null) ? placement : NotifyPlacement.TOP_RIGHT);
    }

    /**
     * Set native property of Notify's placement.
     * @param placement Notify's placement on screen
     */
    @JsOverlay
    private final void setNotifyPlacement(final NotifyPlacement placement) {
        String from = placement.getFrom();
        String align = placement.getAlign();

        JsPropertyMap map = JsPropertyMap.of();
        map.set("from", from);
        map.set("align", align);

        Js.asPropertyMap(this).set("placement", map);
    }

    /**
     * If <code>true</code>, newer notifications push down older ones. Default
     * is <code>false</code>.<br>
     * <br>
     * <strong>WARNING: </strong> Be careful when setting
     * <code>newestOnTop</code> to <code>true</code> when a placement that
     * already contains a notification has <code>newest_on_top</code> to
     * <code>false</code>. It may cause issues with the plug-ins ability to
     * place the notification in the correct location.
     * @param newestOnTop if <code>true</code>, newer notifications push down older ones
     * @since 3.0.0
     */
    @JsOverlay
    public final void setNewestOnTop(boolean newestOnTop) {
        Js.asPropertyMap(this).set("newest_on_top", newestOnTop);
    }

    /**
     * Set offset (space between Notify and screen/browser edges) for each axis. Default is 20 PX for both.
     * @param offX Offset for X axis in PX
     * @param offY Offset for Y axis in PX
     */
    @JsOverlay
    public final void setOffset(int offX, int offY) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("x", offX);
        map.set("y", offY);

        Js.asPropertyMap(this).set("offset", map);
    }

    /**
     * Set custom spacing between two Notifies. Default is 10 PX.
     * @param space Spacing in PX
     */
    @JsOverlay
    public final void setSpacing(int space) {
        Js.asPropertyMap(this).set("spacing", space);
    }

    /**
     * Set custom Z-index. Default is 1031.
     * @param zIndex Z-index
     */
    @JsOverlay
    public final void setZIndex(int zIndex) {
        Js.asPropertyMap(this).set("z_index", zIndex);
    }

    /**
     * Set delay, how long Notify stays on screen. Default is 5000 ms.
     * Set to zero for unlimited time.
     * @param mDelay Delay in milliseconds or zero for unlimited
     */
    @JsOverlay
    public final void setDelay(int mDelay) {
        Js.asPropertyMap(this).set("delay", mDelay);
    }

    /**
     * Set timer. It's value is removed from remaining 'delay' on each 'timer' period.
     * This way you can speed up hiding of Notify. If timer > remaining delay, Notify is
     * hidden after delay runs out (ignoring timer).
     * @param timer Time in milliseconds
     * @see #setDelay(int)
     */
    @JsOverlay
    public final void setTimer(int timer) {
        Js.asPropertyMap(this).set("timer", timer);
    }

    /**
     * Set custom URL target.<br>
     * <br>
     * Defaults to {@link NotifyUrlTarget#BLANK}.
     * @param urlTarget URL target
     */
    @JsOverlay
    public final void setUrlTarget(NotifyUrlTarget urlTarget) {
        setUrlTarget((urlTarget != null) ? urlTarget.getTarget() : NotifyUrlTarget.BLANK.getTarget());
    }

    /**
     * Set custom URL target. Default is "_blank".
     * <p/>
     * See http://www.w3schools.com/tags/att_a_target.asp for possible values.
     * @param customUrlTarget URL target
     */
    @JsOverlay
    public final void setUrlTarget(String customUrlTarget) {
        Js.asPropertyMap(this).set("url_target", customUrlTarget);
    }

    /**
     * Pause countdown of display timeout when mouse is hovering above the Notify.
     * Countdown continues (not restarted) if mouse leaves the Notify.
     * @param pauseOnMouseOver TRUE = pause / FALSE = not pause
     */
    @JsOverlay
    public final void setPauseOnMouseOver(boolean pauseOnMouseOver) {
        Js.asPropertyMap(this).set("mouse_over", pauseOnMouseOver ? "pause" : null);
    }

    /**
     * Set Animation to Notify when it enters and exit the screen.
     * <p>
     * Default is enter = Animation.FADE_IN_DOWN, exit = Animation.FADE_OUT_UP
     * @param enter animation style when Notify enters the screen
     * @param exit animation style when Notify exists the screen
     * @see org.gwtbootstrap3.extras.animate.client.ui.constants.Animation
     */
    @JsOverlay
    public final void setAnimation(Animation enter, Animation exit) {
        setAnimation((enter != null) ? enter.getCssName() : Animation.NO_ANIMATION.getCssName(),
                (exit != null) ? exit.getCssName() : Animation.NO_ANIMATION.getCssName());
    }

    /**
     * Set custom CSS style for animations of Notify when it enters and exits the screen.
     * You must write your own CSS animation definition.
     * @param enter animation style when Notify enters the screen
     * @param exit animation style when Notify exists the screen
     */
    @JsOverlay
    public final void setAnimation(String enter, String exit) {
        JsPropertyMap conf = JsPropertyMap.of();
        conf.set("enter", enter);
        conf.set("exit", exit);
        Js.asPropertyMap(this).set("animate", conf);
    }

    /**
     * Set the Notify's show event handler. The show event fires immediately when
     * the show instance method is called.
     * @param handler
     */
    @JsOverlay
    public final void setShowHandler(final NotifyShowHandler handler) {
        onShow((handler != null) ? handler : NotifyShowHandler.DEFAULT_SHOW_HANDLER);
    }

    @JsOverlay
    private final void onShow(NotifyShowHandler handler) {
        Js.asPropertyMap(this).set("onShow", (Fn) () -> handler.onShow());
    }

    /**
     * Set the Notify's shown event handler. This event is fired when the modal has
     * been made visible to the user (will wait for CSS transitions to complete).
     * @param handler
     */
    @JsOverlay
    public final void setShownHandler(final NotifyShownHandler handler) {
        onShown((handler != null) ? handler : NotifyShownHandler.DEFAULT_SHOWN_HANDLER);
    }

    @JsOverlay
    private final void onShown(NotifyShownHandler handler) {
        Js.asPropertyMap(this).set("onShow", (Fn) () -> handler.onShown());
    }

    /**
     * Set the Notify's close event handler. This event is fired immediately when
     * the notification is closing.
     * @param handler
     */
    @JsOverlay
    public final void setCloseHandler(final NotifyCloseHandler handler) {
        onClose((handler != null) ? handler : NotifyCloseHandler.DEFAULT_CLOSE_HANDLER);
    }

    @JsOverlay
    private final void onClose(NotifyCloseHandler handler) {
        Js.asPropertyMap(this).set("onClose", (Fn) () -> handler.onClose());
    }

    /**
     * Set the Notify's closed event handler. This event is fired when the modal
     * has finished closing and is removed from the document (will wait for CSS
     * transitions to complete).
     * @param handler
     */
    @JsOverlay
    public final void setClosedHandler(final NotifyClosedHandler handler) {
        onClosed((handler != null) ? handler : NotifyClosedHandler.DEFAULT_CLOSED_HANDLER);
    }

    @JsOverlay
    private final void onClosed(NotifyClosedHandler handler) {
        Js.asPropertyMap(this).set("onClosed", (Fn) () -> handler.onClosed());
    }

    /**
     * Set icon type you will use for Notify. Default is 'class', which
     * allows to use iconic fonts like FontAwesome.
     * If you want to use images instead of class, set value to "image".<br>
     * <br>
     * Defaults to {@link NotifyIconType#CLASS}.
     * @param iconType the icon type
     * @see NotifyIconType
     */
    @JsOverlay
    public final void setIconType(NotifyIconType iconType) {
        setIconType((iconType != null) ? iconType.getType() : NotifyIconType.CLASS.getType());
    }

    /**
     * Set native property of Notify's icon type.
     * @param iconType Notify's icon type.
     */
    @JsOverlay
    private final void setIconType(String iconType) {
        Js.asPropertyMap(this).set("icon_type", iconType);
    }

    /**
     * Set custom HTML Template of Notify. Default value is:
     * <p/>
     * <p>
     * &lt;div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert"&gt;<br/>
     * &nbsp;&nbsp;&lt;button type="button" aria-hidden="true" class="close" data-notify="dismiss"&gt;x&lt;/button&gt;<br/>
     * &nbsp;&nbsp;&lt;span data-notify="icon"&gt;&lt;/span&gt;<br/>
     * &nbsp;&nbsp;&lt;span data-notify="title"&gt;{1}&lt;/span&gt;<br/>
     * &nbsp;&nbsp;&lt;span data-notify="message"&gt;{2}&lt;/span&gt;<br/>
     * &nbsp;&nbsp;&lt;div class="progress" data-notify="progressbar"&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"&gt;&lt;/div&gt;<br/>
     * &nbsp;&nbsp;&lt;/div&gt;<br/>
     * &nbsp;&nbsp;&lt;a href="{3}" target="{4}" data-notify="url"&gt;&lt;/a&gt;<br/>
     * &lt;/div&gt;
     * <p>
     * <p/>
     * Where:
     * <ul>
     * <li>{0} = type</li>
     * <li>{1} = title</li>
     * <li>{2} = message</li>
     * <li>{3} = url</li>
     * <li>{4} = target</li>
     * </ul>
     * @param html Custom HTML template
     * @see documentation at: http://bootstrap-notify.remabledesigns.com/
     */
    @JsOverlay
    public final void setTemplate(String html) {
        Js.asPropertyMap(this).set("template", html);
    }

    /**
     * Make this NotifySettings as default for all new Notifies.
     * <p/>
     * Values set to this NotifySettings overrides original default values.
     * If value for some property is not set, original default value is kept.
     */
    @JsOverlay
    public final void makeDefault() {
        Notify._Notify.notifyDefaults();
    }

    @FunctionalInterface
    @JsFunction
    interface Fn {

        void onInvoke();
    }
}
