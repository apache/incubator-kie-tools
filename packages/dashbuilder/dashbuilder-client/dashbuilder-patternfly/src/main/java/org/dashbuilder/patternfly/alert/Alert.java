/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.alert;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class Alert {

    @Inject
    @DataField
    HTMLDivElement alertContainer;

    @Inject
    @DataField
    @Named("i")
    HTMLElement alertIcon;

    @Inject
    @DataField
    HTMLParagraphElement alertText;

    private AlertType type;

    public HTMLElement getElement() {
        return alertContainer;
    }

    public void setup(AlertType type, String content) {
        this.type = type;
        setMessage(content);
    }

    public void setMessage(String content) {
        if (type == null) {
            type = AlertType.INFO;
        }
        alertIcon.classList.add(type.getIcon());
        alertContainer.classList.add(type.getClassName());
        alertText.textContent = content;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

}
