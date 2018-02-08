/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Text;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.uberfire.mvp.Command;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLButtonElement")
public class Button extends HTMLButtonElement {

    @JsOverlay
    public final void setText(final String text) {
        final Text textNode = ownerDocument.createTextNode(text);
        appendChild(textNode);
    }

    @JsOverlay
    public final void setClickHandler(final Command clickHandler) {
        this.addEventListener("click",
                              (e) -> clickHandler.execute());
    }

    @JsOverlay
    public final void setType(final ButtonType type){
        this.type = type.name().toLowerCase();
    }

    @JsOverlay
    public final void setButtonStyleType(final ButtonStyleType type) {
        this.classList.add("btn", type.getCssClass());
    }

    @JsOverlay
    public final void setEnabled(final boolean enabled) {
        if (enabled) {
            classList.remove("disabled");
        } else {
            classList.add("disabled");
        }
    }

    @JsOverlay
    public final void hide(){
        classList.add("hidden");
    }

    @JsOverlay
    public final void show(){
        classList.remove("hidden");
    }

    @JsOverlay
    public final void addIcon(final String... classes){
        final HTMLElement icon = (HTMLElement) ownerDocument.createElement("span");
        icon.classList.add(classes);
        appendChild(icon);
    }

    public enum ButtonType {
        BUTTON,
        SUBMIT,
        RESET
    }

    public enum ButtonStyleType {

        DEFAULT("btn-default"),
        PRIMARY("btn-primary"),
        SUCCESS("btn-success"),
        INFO("btn-info"),
        WARNING("btn-warning"),
        DANGER("btn-danger"),
        LINK("btn-link");

        private final String cssClass;

        ButtonStyleType(final String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }

    }
}