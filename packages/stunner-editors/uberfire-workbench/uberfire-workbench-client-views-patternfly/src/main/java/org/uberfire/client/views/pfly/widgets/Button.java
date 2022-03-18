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

import java.util.stream.Stream;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Document;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Text;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.uberfire.mvp.Command;

@Dependent
public class Button implements IsElement {

    @Inject
    private HTMLButtonElement button;

    @Inject
    private Document document;

    private Text textNode;

    public void setText(final String text) {
        if (textNode == null) {
            textNode = document.createTextNode(text);
            getElement().appendChild(textNode);
        } else {
            textNode.textContent = text;
        }
    }

    public void setClickHandler(final Command clickHandler) {
        button.addEventListener("click",
                                (e) -> clickHandler.execute());
    }

    public void setType(final ButtonType type) {
        button.type = type.name().toLowerCase();
    }

    public void setButtonStyleType(final ButtonStyleType type) {
        removeClass("btn");
        Stream.of(ButtonStyleType.values()).forEach(buttonStyleType -> removeClass(buttonStyleType.getCssClass()));
        addClass(button,
                 "btn",
                 type.getCssClass());
    }

    public void setEnabled(final boolean enabled) {
        button.disabled = !enabled;
        if (enabled) {
            button.classList.remove("disabled");
        } else {
            button.classList.add("disabled");
        }
    }

    public void hide() {
        button.classList.add("hidden");
    }

    public void show() {
        button.classList.remove("hidden");
    }

    public void addIcon(final String... classes) {
        final HTMLElement span = (HTMLElement) document.createElement("span");
        addClass(span,
                 classes);
        button.appendChild(span);
    }

    @Override
    public HTMLElement getElement() {
        return button;
    }

    private void addClass(final HTMLElement element,
                          final String... classes) {
        for (String cssClass : classes) {
            if (element.classList.contains(cssClass) == false) {
                element.classList.add(cssClass);
            }
        }
    }

    private void removeClass(final String cssClass) {
        if (getElement().classList.contains(cssClass)) {
            getElement().classList.remove(cssClass);
        }
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