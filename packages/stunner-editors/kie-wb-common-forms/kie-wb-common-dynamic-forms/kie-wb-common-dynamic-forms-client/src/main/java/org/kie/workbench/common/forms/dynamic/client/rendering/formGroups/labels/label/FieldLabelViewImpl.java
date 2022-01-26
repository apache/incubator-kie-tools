/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;

@Templated
@Dependent
public class FieldLabelViewImpl implements IsElement,
                                           FieldLabelView {

    private Presenter presenter;

    private boolean required;

    @Inject
    private FieldRequired fieldRequired;

    @Inject
    private FieldHelp fieldHelp;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement labelText;

    @PostConstruct
    public void init() {
        DOMUtil.removeAllChildren(getElement());

        required = false;
    }

    @Override
    public void renderForInputId(String inputId,
                                 String label,
                                 boolean required,
                                 String helpMessage) {

        init();

        getElement().appendChild(labelText);

        labelText.textContent = (label);

        getElement().setAttribute("for",
                                  inputId);

        setRequired(required);

        setHelpMessage(helpMessage);
    }

    @Override
    public void renderForInput(IsWidget isWidget,
                               String label,
                               boolean required,
                               String helpMessage) {

        init();

        DOMUtil.appendWidgetToElement(getElement(),
                                      isWidget);

        getElement().appendChild(labelText);

        labelText.textContent = (label);

        setRequired(required);

        setHelpMessage(helpMessage);
    }

    @Override
    public void setRequired(boolean required) {

        if (this.required == required) {
            return;
        }

        this.required = required;

        if (!required) {
            getElement().removeChild(this.fieldRequired.getElement());
        } else {
            getElement().appendChild(this.fieldRequired.getElement());
        }
    }

    protected void setHelpMessage(String helpMessage) {

        if (helpMessage == null || helpMessage.trim().isEmpty()) {
            return;
        }

        this.fieldHelp.showHelpMessage(helpMessage);

        getElement().appendChild(fieldHelp.getElement());
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }
}
