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

package org.kie.workbench.common.dmn.client.editors.types.shortcuts;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

@ApplicationScoped
public class DataTypeShortcuts {

    static final String SELECT_DATATYPE_MENU = ".bs-container.btn-group.bootstrap-select.open";

    private final DataTypeListShortcuts listShortcuts;

    EventListener KEY_DOWN_LISTENER = this::keyDownListener;

    EventListener CLICK_LISTENER = this::clickListener;

    private boolean loaded = false;

    private boolean enabled = true;

    @Inject
    public DataTypeShortcuts(final DataTypeListShortcuts listShortcuts) {
        this.listShortcuts = listShortcuts;
    }

    public void init(final DataTypeList dataTypeList) {
        listShortcuts.init(dataTypeList);
    }

    public void setup() {

        if (isLoaded()) {
            return;
        }

        loaded = true;
        addEventListener(KEYDOWN, KEY_DOWN_LISTENER);
        addEventListener(CLICK, CLICK_LISTENER);
    }

    public void teardown() {

        if (!isLoaded()) {
            return;
        }

        loaded = false;
        removeEventListener(KEYDOWN, KEY_DOWN_LISTENER);
        removeEventListener(CLICK, CLICK_LISTENER);
    }

    void clickListener(final Event event) {

        if (isNotEnabled()) {
            return;
        }

        if (tabContentContainsTarget(event) || dropdownMenuContainsTarget(event)) {
            final Element dataTypeElement = getDataTypeRowElement(event);
            if (dataTypeElement != null) {
                listShortcuts.highlight(dataTypeElement);
            }
            listShortcuts.focusIn();
        } else {
            listShortcuts.reset();
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    boolean isNotEnabled() {
        return !enabled;
    }

    void keyDownListener(final Event e) {

        final KeyboardEvent event = (KeyboardEvent) e;

        if (isNotEnabled()) {
            return;
        }

        switch (event.key) {
            case "Esc": /* IE/Edge specific value */
            case "Escape":
                listShortcuts.onEscape();
                return;
            case "Backspace":
                if (event.ctrlKey) {
                    listShortcuts.onCtrlBackspace();
                }
                return;
            case "Tab":
                if (isSearchBarTarget(event)) {
                    event.preventDefault();
                    listShortcuts.onTab();
                }
                return;
            case "s":
                if (event.ctrlKey) {
                    event.preventDefault();
                    listShortcuts.onCtrlS();
                }
                return;
            case "b":
                if (event.ctrlKey) {
                    event.preventDefault();
                    listShortcuts.onCtrlB();
                }
                return;
            case "u":
                if (event.ctrlKey) {
                    event.preventDefault();
                    listShortcuts.onCtrlU();
                }
                return;
            case "d":
                if (event.ctrlKey) {
                    event.preventDefault();
                    listShortcuts.onCtrlD();
                }
                return;
            case "e":
                if (event.ctrlKey) {
                    event.preventDefault();
                    listShortcuts.onCtrlE();
                }
                return;
        }

        if (isInputEvent(event) && !isSearchBarTarget(event)) {
            return;
        }

        switch (event.key) {
            case "Down": /* IE/Edge specific value */
            case "ArrowDown":
                listShortcuts.onArrowDown();
                return;
            case "Up": /* IE/Edge specific value */
            case "ArrowUp":
                listShortcuts.onArrowUp();
                return;
            case "Left": /* IE/Edge specific value */
            case "ArrowLeft":
                listShortcuts.onArrowLeft();
                return;
            case "Right": /* IE/Edge specific value */
            case "ArrowRight":
                listShortcuts.onArrowRight();
        }
    }

    boolean isSearchBarTarget(final KeyboardEvent event) {
        final Element element = getTarget(event);
        return Objects.equals(element.getAttribute("data-field"), "search-bar");
    }

    boolean isInputEvent(final KeyboardEvent event) {
        return isTargetElementAnInput(event) || isDropdownOpened();
    }

    boolean isTargetElementAnInput(final KeyboardEvent event) {
        final Element element = getTarget(event);
        return element instanceof HTMLInputElement;
    }

    boolean tabContentContainsTarget(final Event event) {
        final Element target = getTarget(event);
        final Element tabContent = getTabContent();
        return $.contains(tabContent, target);
    }

    private boolean dropdownMenuContainsTarget(final Event event) {
        final Element target = getTarget(event);
        return target.closest(SELECT_DATATYPE_MENU) != null;
    }

    private Element getDataTypeRowElement(final Event event) {
        final Element target = getTarget(event);
        return target.closest(".list-group-item");
    }

    private Element getTabContent() {
        return querySelector(".tab-content");
    }

    boolean isDropdownOpened() {
        return querySelector(SELECT_DATATYPE_MENU) != null;
    }

    boolean isLoaded() {
        return loaded;
    }

    Element querySelector(final String selector) {
        return DomGlobal.document.querySelector(selector);
    }

    void addEventListener(final String type,
                          final EventListener eventListener) {
        DomGlobal.document.addEventListener(type, eventListener);
    }

    void removeEventListener(final String type,
                             final EventListener eventListener) {
        DomGlobal.document.removeEventListener(type, eventListener);
    }

    private Element getTarget(final Event e) {
        return (Element) e.target;
    }
}
