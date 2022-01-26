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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import jsinterop.base.Js;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
@Dependent
public class FieldSetFormGroupViewImpl implements IsElement,
                                                  FieldSetFormGroupView {

    private static final String PART_LEGEND_TEXT = "Legend Text";

    @Inject
    private FieldRequired fieldRequired;

    @Inject
    private FieldHelp fieldHelp;

    @Inject
    @Named("legend")
    @DataField
    private HTMLElement legend;

    @Inject
    @DataField
    private Span legendText;

    @DataField
    protected SimplePanel fieldContainer = new SimplePanel();

    @Inject
    @DataField
    protected HTMLDivElement formGroup;

    @Inject
    @DataField
    protected HTMLDivElement helpBlock;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private Map<String, Widget> partsWidgets = new HashMap<>();

    @Override
    public void render(Widget widget,
                       FieldDefinition field) {

        DOMUtil.addEnumStyleName(formGroup, Style.Visibility.HIDDEN);

        legendText.setText(field.getLabel());

        if (field.isRequired()) {
            legend.appendChild(fieldRequired.getElement());
        }

        if (field.getHelpMessage() != null && !field.getHelpMessage().trim().isEmpty()) {
            fieldHelp.showHelpMessage(field.getHelpMessage());
            legend.appendChild(fieldHelp.getElement());
        }

        fieldContainer.clear();

        fieldContainer.add(widget);
        
        partsWidgets.put(PART_LEGEND_TEXT, wrapperWidgetUtil.getWidget(this, Js.uncheckedCast(legendText.getElement())));
    }
    
    @Override
    public Map<String, Widget> getViewPartsWidgets() {
        return partsWidgets;
    }

    @PreDestroy
    public void clear() {
        wrapperWidgetUtil.clear(this);
    }
}
