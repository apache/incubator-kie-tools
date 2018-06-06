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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;

public class EditAttributeWidgetFactory {

    final boolean isReadOnly;

    public EditAttributeWidgetFactory(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public TextBox textBox(final RuleAttribute ruleAttribute, final String dataType) {
        final TextBox textBox = TextBoxFactory.getTextBox(dataType);
        initTextBoxByRuleAttribute(textBox, ruleAttribute);
        return textBox;
    }

    protected void initTextBoxByRuleAttribute(final TextBox textBox, final RuleAttribute ruleAttribute) {
        textBox.setEnabled(!isReadOnly);
        if (!isReadOnly) {
            textBox.addValueChangeHandler(event -> ruleAttribute.setValue(textBox.getValue()));
        }
        textBox.setValue(ruleAttribute.getValue());
    }
}
