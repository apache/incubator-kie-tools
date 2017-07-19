/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerView;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractFormGroupDisplayerView extends Composite implements FormGroupDisplayerView {

    @Inject
    @DataField
    protected Label fieldLabel;

    @Inject
    @DataField
    protected Div fieldContainer;

    @Inject
    @DataField
    protected Div helpBlock;

    @Inject
    protected Document document;

    @Override
    public void render(Widget widget,
                       FieldDefinition field) {
        this.getElement().setId(generateFormGroupId(field));
        fieldLabel.setHtmlFor(widget.getElement().getId());
        fieldLabel.setTextContent(field.getLabel());
        if (field.getRequired()) {
            fieldLabel.appendChild(getRequiredElement(document));
        }
        DOMUtil.appendWidgetToElement(fieldContainer,
                                      widget);
        helpBlock.setId(generateHelpBlockId(field));
    }
}
