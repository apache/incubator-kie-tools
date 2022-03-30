/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllElementChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Templated(stylesheet = "InlineNotification.css")
@Dependent
public class InlineNotification implements IsElement {

    @Inject
    @DataField("alert")
    private HTMLDivElement alert;

    @Inject
    @DataField("message")
    @Named("span")
    private HTMLElement message;

    @Inject
    @DataField("icon")
    @Named("span")
    private HTMLElement icon;

    @Inject
    @DataField("dismiss")
    private HTMLButtonElement dismiss;

    private Document document = DomGlobal.document;

    @Override
    public HTMLElement getElement() {
        return alert;
    }

    public void setMessage(final String message) {
        this.message.textContent = message;
    }

    public void setMessage(final List<String> messages) {
        removeAllElementChildren(this.message);
        final HTMLElement ul = (HTMLElement) document.createElement("ul");
        addCSSClass(ul,
                    "list-unstyled");
        for (String message : messages) {
            final HTMLElement li = (HTMLElement) document.createElement("li");
            li.textContent = message;
            ul.appendChild(li);
        }
        this.message.appendChild(ul);
    }

    public void setDismissable() {
        addCSSClass(alert,
                    "alert-dismissable");
        removeCSSClass(dismiss,
                       "hidden");
    }

    public void setType(final InlineNotificationType type) {
        Stream.of(InlineNotificationType.values()).forEach(availableType -> {
            removeCSSClass(alert, availableType.getCssClass());
            removeCSSClass(icon, availableType.getIcon());
        });
        addCSSClass(alert,
                    type.getCssClass());
        addCSSClass(icon,
                    type.getIcon());
    }

    public enum InlineNotificationType {

        SUCCESS("alert-success",
                "pficon-ok"),
        INFO("alert-info",
             "pficon-info"),
        WARNING("alert-warning",
                "pficon-warning-triangle-o"),
        DANGER("alert-danger",
               "pficon-error-circle-o");

        private String cssClass;

        private String icon;

        InlineNotificationType(final String cssClass,
                               final String icon) {
            this.cssClass = cssClass;
            this.icon = icon;
        }

        public String getCssClass() {
            return cssClass;
        }

        public String getIcon() {
            return icon;
        }
    }
}
