package org.gwtbootstrap3.client.ui.base.helper;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 GwtBootstrap3
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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.UIObject;
import org.gwtbootstrap3.client.ui.constants.DeviceSize;
import org.gwtbootstrap3.client.ui.constants.Responsiveness;

/**
 * Helper methods regarding CSS styling of UIObjects.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public final class StyleHelper {

    /**
     * Convenience method for first removing all enum style constants and then adding the single one.
     *
     * @see #removeEnumStyleNames(com.google.gwt.user.client.ui.UIObject, Class)
     * @see #addEnumStyleName(com.google.gwt.user.client.ui.UIObject, com.google.gwt.dom.client.Style.HasCssName)
     */
    public static <E extends Style.HasCssName, F extends Enum<? extends Style.HasCssName>> void addUniqueEnumStyleName(final UIObject uiObject,
                                                                                                                       final Class<F> enumClass,
                                                                                                                       final E style) {
        removeEnumStyleNames(uiObject, enumClass);
        addEnumStyleName(uiObject, style);
    }

    /**
     * Removes all CSS style names specified by an enum that implements {@link Style.HasCssName} from an UIObject.
     *
     * @param uiObject  Object to remove CSS class names from
     * @param enumClass Enum representing CSS class names
     * @param <E>       Enum type implementing {@link Style.HasCssName}
     */
    public static <E extends Enum<? extends Style.HasCssName>> void removeEnumStyleNames(final UIObject uiObject,
                                                                                         final Class<E> enumClass) {

        for (final Enum<? extends Style.HasCssName> constant : enumClass.getEnumConstants()) {
            final String cssClass = ((Style.HasCssName) constant).getCssName();

            if (cssClass != null && !cssClass.isEmpty()) {
                uiObject.removeStyleName(cssClass);
            }
        }
    }

    /**
     * Adds enum value style name to UIObject unless style is {@code null}.
     *
     * @param uiObject Object to add style to
     * @param style    Style name
     */
    public static <E extends Style.HasCssName> void addEnumStyleName(final UIObject uiObject,
                                                                     final E style) {

        if (style != null && style.getCssName() != null && !style.getCssName().isEmpty()) {
            uiObject.addStyleName(style.getCssName());
        }
    }

    /**
     * Removes enum value style name from UIObject unless style is {@code null}.
     *
     * @param uiObject Object to remove style from
     * @param style    Style name
     */
    public static <E extends Style.HasCssName> void removeEnumStyleName(final UIObject uiObject,
                                                                        final E style) {

        if (style != null && style.getCssName() != null && !style.getCssName().isEmpty()) {
            uiObject.removeStyleName(style.getCssName());
        }
    }

    /**
     * Returns {@code true} if specified style is contained in space-separated list of styles
     *
     * @param styleNames Space-separated list of styles
     * @param style      Style to look for
     * @return True if contains style
     */
    public static boolean containsStyle(final String styleNames,
                                        final String style) {

        if (styleNames == null || style == null) {
            return false;
        }

        final String[] styles = styleNames.split("\\s");

        for (final String s : styles) {
            if (style.equals(s)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Toggles a style name on a ui object
     *
     * @param uiObject    Object to toggle style on
     * @param toggleStyle whether or not to toggle the style name on the object
     * @param styleName   Style name
     */
    public static void toggleStyleName(final UIObject uiObject,
                                       final boolean toggleStyle,
                                       final String styleName) {
        if (toggleStyle) {
            uiObject.addStyleName(styleName);
        } else {
            uiObject.removeStyleName(styleName);
        }
    }

    /**
     * Sets the ui object to be hidden on the device size
     *
     * @param uiObject   object to be hidden on the device size
     * @param deviceSize device size
     */
    public static void setHiddenOn(final UIObject uiObject,
                                   final DeviceSize deviceSize) {
        // Split the enum up by _ to get the different devices
        // Separates the SM_MD into [SM, MD] so we can add the right styles
        final String[] deviceString = deviceSize.name().split("_");

        for (final String device : deviceString) {
            // Case back to basic enum (PRINT, XS, SM, MD, LG)
            final DeviceSize size = DeviceSize.valueOf(device);
            switch (size) {
                case PRINT:
                    addEnumStyleName(uiObject, Responsiveness.HIDDEN_PRINT);
                    break;
                case XS:
                    addEnumStyleName(uiObject, Responsiveness.HIDDEN_XS);
                    break;
                case SM:
                    addEnumStyleName(uiObject, Responsiveness.HIDDEN_SM);
                    break;
                case MD:
                    addEnumStyleName(uiObject, Responsiveness.HIDDEN_MD);
                    break;
                case LG:
                    addEnumStyleName(uiObject, Responsiveness.HIDDEN_LG);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sets the ui object to be visible on the device size
     *
     * @param uiObject   object to be visible on the device size
     * @param deviceSize device size
     */
    public static void setVisibleOn(final UIObject uiObject,
                                    final DeviceSize deviceSize) {
        // Split the enum up by _ to get the different devices
        // Separates the SM_MD into [SM, MD] so we can add the right styles
        final String[] deviceString = deviceSize.name().split("_");

        for (final String device : deviceString) {
            // Case back to basic enum (PRINT, XS, SM, MD, LG)
            final DeviceSize size = DeviceSize.valueOf(device);
            switch (size) {
                case PRINT:
                    addEnumStyleName(uiObject, Responsiveness.VISIBLE_PRINT);
                    break;
                case XS:
                    addEnumStyleName(uiObject, Responsiveness.VISIBLE_XS);
                    break;
                case SM:
                    addEnumStyleName(uiObject, Responsiveness.VISIBLE_SM);
                    break;
                case MD:
                    addEnumStyleName(uiObject, Responsiveness.VISIBLE_MD);
                    break;
                case LG:
                    addEnumStyleName(uiObject, Responsiveness.VISIBLE_LG);
                    break;
                default:
                    break;
            }
        }
    }

    private StyleHelper() {
    }
}
