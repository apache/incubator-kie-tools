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

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPlacement;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;

/**
 * This class represent instance of displayed Notify.
 * <p/>
 * You can display new Notify using static methods, e.g.:
 * {@link #notify(String)},
 * {@link #notify(String, NotifyType)},
 * {@link #notify(String, NotifySettings)} and others
 * <p/>
 * To further configure Notify before displaying see:
 * {@see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings}
 * <p/>
 * You can update displayed Notify by:
 * {@link #updateTitle(String)},
 * {@link #updateMessage(String)},
 * {@link #updateIcon(String)},
 * {@link #updateType(NotifyType)},
 * <p/>
 * You can hide displayed Notify:
 * {@link #hide()},
 * {@link #hideAll()},
 * {@link #hideAll(NotifyPlacement)}
 * @author jeffisenhart
 * @author Sven Jacobs
 * @author Joshua Godi
 * @author Pavel Zlámal
 * @author treblereel
 */
public class Notify {

    protected Notify() {
    }

    /**
     * Display Notify with custom message, and default settings.
     * @param message Message to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String message) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        _Notify.notify(map, null);
    }

    /**
     * Display Notify with custom title, message, and default settings.
     * @param title Title to set
     * @param message Message to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String title, final String message) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        _Notify.notify(map, null);
    }

    /**
     * Display Notify with custom title, message, icon, and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon Icon to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String title, final String message, final String icon) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        map.set("icon", icon);
        _Notify.notify(map, null);
    }

    /**
     * Display Notify with custom title, message, icon, and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String title, final String message, final IconType iconType) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName());
    }

    /**
     * Display Notify with custom title, message, icon, URL, and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon IconType to set
     * @param url Url to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String title, final String message, final String icon, final String url) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        map.set("icon", icon);
        map.set("url", url);
        _Notify.notify(map, null);
    }

    /**
     * Display Notify with custom title, message, icon, url and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set
     * @param url Url to set
     * @return Displayed Notify for update or hiding.
     */
    public static final void notify(final String title, final String message, final IconType iconType, final String url) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName(), url);
    }

    /**
     * Display Notify with custom message, type and default settings.
     * @param message Message to set
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String message, final NotifyType type) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        JsPropertyMap tmap = JsPropertyMap.of();
        tmap.set("type", type.getCssName());

        _Notify.notify(map, tmap);
    }

    /**
     * Display Notify with custom title, message, type and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String title, final String message, final NotifyType type) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("title", title);
        map.set("message", message);
        JsPropertyMap tmap = JsPropertyMap.of();
        tmap.set("type", type.getCssName());

        _Notify.notify(map, tmap);
    }

    /**
     * Display Notify with custom title, message, icon, type and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon Icon to set
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String title, final String message, final String icon, final NotifyType type) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("title", title);
        map.set("message", message);
        map.set("icon", icon);
        JsPropertyMap tmap = JsPropertyMap.of();
        tmap.set("type", type.getCssName());

        _Notify.notify(map, tmap);
    }

    /**
     * Display Notify with custom title, message, icon, type and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set (css name of icon form FONT AWESOME)
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String title, final String message, final IconType iconType, final NotifyType type) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName(), type);
    }

    /**
     * Display Notify with custom title, message, icon, url, type and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon Icon to set
     * @param url Url to set
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String title, final String message, final String icon, final String url, final NotifyType type) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("title", title);
        map.set("message", message);
        map.set("icon", icon);
        map.set("url", url);
        JsPropertyMap tmap = JsPropertyMap.of();
        tmap.set("type", type.getCssName());

        _Notify.notify(map, tmap);
    }

    /**
     * Display Notify with custom title, message, icon, url, type and default settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set (css name of icon form FONT AWESOME)
     * @param url Url to set
     * @param type NotifyType
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public static final void notify(final String title, final String message, final IconType iconType, final String url, final NotifyType type) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName(), url, type);
    }

    /**
     * Display Notify with custom message and custom settings.
     * @param message Message to set
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String message, final NotifySettings settings) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        _Notify.notify(map, settings);
    }

    /**
     * Display Notify with custom title, message and custom settings.
     * @param title Title to set
     * @param message Message to set
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String title, final String message, final NotifySettings settings) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        _Notify.notify(map, settings);
    }

    /**
     * Display Notify with custom title, message, icon and custom settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon Icon to set
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String title, final String message, final String icon, final NotifySettings settings) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        map.set("icon", icon);
        _Notify.notify(map, settings);
    }

    /**
     * Display Notify with custom title, message, icon and custom settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set (css name of icon form FONT AWESOME)
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String title, final String message, final IconType iconType, final NotifySettings settings) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName(), settings);
    }

    /**
     * Display Notify with custom title, message, icon, URL and custom settings.
     * @param title Title to set
     * @param message Message to set
     * @param icon Icon to set
     * @param url Url to set
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String title, final String message, final String icon, final String url, final NotifySettings settings) {
        JsPropertyMap map = JsPropertyMap.of();
        map.set("message", message);
        map.set("title", title);
        map.set("icon", icon);
        map.set("url", url);
        _Notify.notify(map, settings);
    }

    /**
     * Display Notify with custom title, message, icon, URL and custom settings.
     * @param title Title to set
     * @param message Message to set
     * @param iconType IconType to set
     * @param url Url to set
     * @param settings custom settings
     * @return Displayed Notify for update or hiding.
     * @see org.gwtbootstrap3.extras.notify.client.ui.NotifySettings
     */
    public static final void notify(final String title, final String message, final IconType iconType, final String url, final NotifySettings settings) {
        Notify.notify(title, message, Styles.FONT_AWESOME_BASE + " " + iconType.getCssName(), url, settings);
    }

    /**
     * Hide all displayed Notifies.
     */
    public static final void hideAll() {
        _Notify.notifyClose();
    }

    /**
     * Hide all displayed Notifies on specific screen location.
     * @param placement Notify's placement on screen.
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyPlacement
     */
    public static final void hideAll(NotifyPlacement placement) {
        if (placement != null) {
            _Notify.notifyClose(placement.getPlacement());
        }
    }

    /**
     * Updates title parameter of once displayed Notify.
     * @param title Title to set
     */
    public final void updateTitle(String title) {
        ((Wrapper) Js.uncheckedCast(this)).update("title", title);
    }

    /**
     * Updates message parameter of once displayed Notify.
     * @param message Message to set
     */
    public final void updateMessage(String message) {
        ((Wrapper) Js.uncheckedCast(this)).update("message", message);
    }

    /**
     * Updates Icon parameter of once displayed Notify.
     * @param icon Icon to set
     */
    public final void updateIcon(String icon) {
        ((Wrapper) Js.uncheckedCast(this)).update("icon", icon);
    }

    /**
     * Updates Icon parameter of once displayed Notify.
     * This method is shortcut when using FONT AWESOME iconic font.
     * @param type IconType to get CSS class name to set
     */
    public final void updateIcon(final IconType type) {
        if (type != null) {
            updateIcon(Styles.FONT_AWESOME_BASE + " " + type.getCssName());
        }
    }

    /**
     * Update type of once displayed Notify (CSS style class name).
     * @param type one of INFO, WARNING, DANGER, SUCCESS
     * @see org.gwtbootstrap3.extras.notify.client.constants.NotifyType
     */
    public final void updateType(final NotifyType type) {
        if (type != null) {
            updateType(type.getCssName());
        }
    }

    /**
     * Update type of once displayed Notify (CSS style class name).
     * Resulting class name to use is "alert-[type]".
     * @param type CSS class name to set
     */
    private final void updateType(String type) {
        ((Wrapper) Js.uncheckedCast(this)).update("type", type);
    }

    /**
     * Update URL target of once displayed Notify.
     * @param target URL target to set
     */
    private final void updateTarget(String target) {
        ((Wrapper) Js.uncheckedCast(this)).updateTarget("target", target);
    }

    /**
     * Hide this Notify.
     */
    public final void hide() {
        ((Wrapper) Js.uncheckedCast(this)).close();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static class Wrapper {

        @JsMethod
        native void close();

        @JsMethod
        native void updateTarget(String target, String value);

        @JsMethod
        native void update(String type, String value);
    }

    @JsType(
            isNative = true,
            namespace = "<global>",
            name = "jQuery"
    )
    static class _Notify {

        @JsMethod
        native static void notify(String msg);

        @JsMethod
        native static void notify(JsPropertyMap value, Object var);

        @JsMethod
        native static void notifyClose();

        @JsMethod
        native static void notifyClose(String placement);

        @JsMethod
        native static void notifyDefaults();
    }
}
