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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Widget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
@Dependent
public class CheckBoxFormGroupViewImpl implements IsElement,
                                                  CheckBoxFormGroupView {

    @Inject
    @DataField
    protected FieldLabel fieldLabel;

    @Inject
    @DataField
    protected HTMLDivElement helpBlock;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private Map<String, Widget> viewPartsWidget = new HashMap<>();

    public void render(Widget widget,
                       FieldDefinition field) {

        fieldLabel.renderForInput(widget,
                                  field);
        viewPartsWidget.put("Check Box Label", wrapperWidgetUtil.getWidget(this, fieldLabel.getElement()));
    }

    @Override
    public HTMLElement getHelpBlock() {
        return helpBlock;
    }
    
    @Override
    public Map<String, Widget> getViewPartsWidgets() {
        return viewPartsWidget;
    }

    @PreDestroy
    public void clear() {
        wrapperWidgetUtil.clear(this);
    }
}
