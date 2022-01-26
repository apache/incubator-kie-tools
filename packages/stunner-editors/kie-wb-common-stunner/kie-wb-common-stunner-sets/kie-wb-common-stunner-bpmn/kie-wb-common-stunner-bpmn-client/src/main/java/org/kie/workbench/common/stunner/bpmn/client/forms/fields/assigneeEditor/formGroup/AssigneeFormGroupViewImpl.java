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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.formGroup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
@Dependent
public class AssigneeFormGroupViewImpl implements IsElement,
                                                  AssigneeFormGroupView {

    @Inject
    @DataField
    private FieldLabel fieldLabel;

    @Inject
    @DataField
    protected HTMLDivElement fieldContainer;

    @Inject
    @DataField
    private HTMLDivElement formGroup;

    @Inject
    @DataField
    protected HTMLDivElement helpBlock;

    @Override
    public void render(Widget widget,
                       FieldDefinition fieldDefinition) {

        render(widget.getElement().getId(),
               widget,
               fieldDefinition);
    }

    @Override
    public void render(String inputId,
                       Widget widget,
                       FieldDefinition fieldDefinition) {

        fieldLabel.renderForInputId(inputId,
                                    fieldDefinition);

        DOMUtil.removeAllChildren(fieldContainer);
        DOMUtil.appendWidgetToElement(fieldContainer, widget);
    }

    @Override
    public HTMLElement getHelpBlock() {
        return helpBlock;
    }
}
