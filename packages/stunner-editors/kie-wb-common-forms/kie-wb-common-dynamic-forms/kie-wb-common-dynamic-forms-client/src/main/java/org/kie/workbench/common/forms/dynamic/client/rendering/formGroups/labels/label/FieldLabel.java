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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.model.FieldDefinition;

/**
 * Component that renders label for form fields.
 */
@Dependent
public class FieldLabel implements IsElement,
                                   FieldLabelView.Presenter {

    private FieldLabelView view;

    @Inject
    public FieldLabel(FieldLabelView view) {
        this.view = view;

        view.init(this);
    }

    /**
     * Renders a HTML label for the given inputId
     * @param inputId The id of the HTML input the label's for
     * @param fieldDefinition The settings for the label
     */
    public void renderForInputId(String inputId,
                                 FieldDefinition fieldDefinition) {
        PortablePreconditions.checkNotNull("inputId",
                                           inputId);
        PortablePreconditions.checkNotNull("fieldDefinition",
                                           fieldDefinition);

        view.renderForInputId(inputId,
                              fieldDefinition.getLabel(),
                              fieldDefinition.getRequired(),
                              fieldDefinition.getHelpMessage());
    }

    /**
     * Renders a HTML label for the given form widget and includes it on the label DOM
     * @param isWidget The form widget to add to the label.
     * @param fieldDefinition The settings for the label
     */
    public void renderForInput(IsWidget isWidget,
                               FieldDefinition fieldDefinition) {
        PortablePreconditions.checkNotNull("fieldDefinition",
                                           fieldDefinition);
        PortablePreconditions.checkNotNull("isWidget",
                                           isWidget);

        view.renderForInput(isWidget,
                            fieldDefinition.getLabel(),
                            fieldDefinition.getRequired(),
                            fieldDefinition.getHelpMessage());
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
