/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQuery.$;

/**
 * This class implements a workaround for the kebab menu.
 * <p>
 * The kebab menu cannot be affected by the "overflow: hidden" CSS property from the dialog content.
 * Thus, this workaround moves the dropdown element to the ".modal.in" element when the menu is opened, and it
 * moves the dropdown element back to the "kebabMenu" when the menu closed.
 */
public class KebabMenuInitializer {

    private final HTMLDivElement kebabMenu;

    public KebabMenuInitializer(final HTMLDivElement kebabMenu) {
        this.kebabMenu = kebabMenu;
    }

    public void init() {
        final Element dropdown = dropdown();

        $(dropdown).on("show.bs.dropdown", moveDropDownToBody());
        $(dropdown).on("hidden.bs.dropdown", moveDropDownToKebabContainer());
    }

    JQuery.CallbackFunction moveDropDownToBody() {
        return (event) -> {
            final JavaScriptObject properties = bodyDropdownProperties(event).getJavaScriptObject();
            JQuery $ = $(modalInElement());
            JQuery css = $(event.target).css(properties);
            JQuery detach = css.detach();
            $.append(detach);
        };
    }

    JQuery.CallbackFunction moveDropDownToKebabContainer() {
        return (event) -> {
            final JavaScriptObject properties = emptyProperties().getJavaScriptObject();
            $(kebabMenu).append($(event.target).css(properties).detach());
        };
    }

    JSONObject bodyDropdownProperties(final JQueryEvent e) {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("position", new JSONString("absolute"));
        jsonObject.put("zIndex", new JSONNumber(1051)); // Bootstrap modal z-index value is 1050
        jsonObject.put("left", new JSONNumber(offsetLeft(e.target)));
        jsonObject.put("top", new JSONNumber(offsetTop(e.target)));
        return jsonObject;
    }

    JSONObject emptyProperties() {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("position", new JSONString(""));
        jsonObject.put("zIndex", new JSONString(""));
        jsonObject.put("left", new JSONString(""));
        jsonObject.put("top", new JSONString(""));
        return jsonObject;
    }

    JSONObject makeJsonObject() {
        return new JSONObject();
    }

    Element modalInElement() {
        return DomGlobal.document.querySelector(".modal.in");
    }

    Element dropdown() {
        return kebabMenu.querySelector(".dropdown");
    }

    double offsetLeft(final Element target) {
        return target.getBoundingClientRect().left + modalInElement().scrollLeft;
    }

    double offsetTop(final Element target) {
        return target.getBoundingClientRect().top + modalInElement().scrollTop;
    }
}
