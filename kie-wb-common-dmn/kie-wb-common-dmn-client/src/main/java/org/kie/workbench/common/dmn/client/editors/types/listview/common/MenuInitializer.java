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
import org.uberfire.client.views.pfly.selectpicker.JQuery;
import org.uberfire.client.views.pfly.selectpicker.JQueryEvent;

import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

/**
 * This class implements a workaround for menus.
 * <p>
 * Menus in a dialog cannot be affected by the "overflow: hidden" CSS property from the dialog content.
 * Thus, this workaround moves the dropdown element to the "body" element when the menu is opened, and it
 * moves the dropdown element back to the "menu" when the menu closed.
 */
public class MenuInitializer {

    private final Element menu;
    private final String dropDownClass;

    public MenuInitializer(final Element menu,
                           final String dropDownClass) {
        this.menu = menu;
        this.dropDownClass = dropDownClass;
    }

    public void init() {
        final Element dropdown = dropdown();

        $(dropdown).on("show.bs.dropdown", moveDropDownToBody());
        $(dropdown).on("hidden.bs.dropdown", moveDropDownToKebabContainer());
    }

    JQuery.CallbackFunction moveDropDownToBody() {
        return (event) -> {
            final JavaScriptObject properties = bodyDropdownProperties(event).getJavaScriptObject();
            JQuery $ = $(body());
            JQuery css = $(event.target).css(properties);
            JQuery detach = css.detach();
            $.append(detach);
        };
    }

    JQuery.CallbackFunction moveDropDownToKebabContainer() {
        return (event) -> {
            final JavaScriptObject properties = emptyProperties().getJavaScriptObject();
            $(menu).append($(event.target).css(properties).detach());
        };
    }

    JSONObject bodyDropdownProperties(final JQueryEvent e) {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("position", new JSONString("absolute"));
        jsonObject.put("left", new JSONNumber(offsetLeft(e.target)));
        jsonObject.put("top", new JSONNumber(offsetTop(e.target)));
        jsonObject.put("z-index", new JSONNumber(1051)); // The '.modal.in' CSS has a z-index of '1050', so the dropdown element needs a higher value.
        return jsonObject;
    }

    JSONObject emptyProperties() {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("position", new JSONString(""));
        jsonObject.put("left", new JSONString(""));
        jsonObject.put("top", new JSONString(""));
        jsonObject.put("z-index", new JSONString(""));
        return jsonObject;
    }

    JSONObject makeJsonObject() {
        return new JSONObject();
    }

    Element body() {
        return DomGlobal.document.body;
    }

    Element dropdown() {
        return menu.querySelector(dropDownClass);
    }

    double offsetLeft(final Element target) {
        return target.getBoundingClientRect().left + body().scrollLeft;
    }

    double offsetTop(final Element target) {
        return target.getBoundingClientRect().top + body().scrollTop;
    }
}
