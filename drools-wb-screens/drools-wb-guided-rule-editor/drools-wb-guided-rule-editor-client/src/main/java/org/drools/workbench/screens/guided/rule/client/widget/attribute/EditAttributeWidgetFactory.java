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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

public class EditAttributeWidgetFactory {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat(DATE_FORMAT);

    final boolean isReadOnly;

    public EditAttributeWidgetFactory(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public TextBox textBox(final RuleAttribute ruleAttribute, final String dataType) {
        final TextBox textBox = TextBoxFactory.getTextBox(dataType);
        initTextBoxByRuleAttribute(textBox, ruleAttribute);
        return textBox;
    }

    public DatePicker datePicker(final RuleAttribute ruleAttribute, final boolean allowEmptyValues) {
        final DatePicker datePicker = new DatePicker(allowEmptyValues);
        initDatePickerByRuleAttribute(datePicker, ruleAttribute);
        return datePicker;
    }

    protected void initTextBoxByRuleAttribute(final TextBox textBox, final RuleAttribute ruleAttribute) {
        textBox.setEnabled(!isReadOnly);
        if (!isReadOnly) {
            textBox.addValueChangeHandler(event -> ruleAttribute.setValue(textBox.getValue()));
        }
        textBox.setValue(ruleAttribute.getValue());
    }

    protected void initDatePickerByRuleAttribute(final DatePicker datePicker, final RuleAttribute ruleAttribute) {
        datePicker.addValueChangeHandler(event -> {
            final Date date = datePicker.getValue();
            final String sDate = (date == null ? null : DATE_FORMATTER.format(datePicker.getValue()));
            ruleAttribute.setValue(sDate);
        });

        datePicker.setFormat(DATE_FORMAT);
        datePicker.setValue(assertDateValue(ruleAttribute));
    }

    private Date assertDateValue(final RuleAttribute ruleAttribute) {
        if (ruleAttribute == null || ruleAttribute.getValue() == null) {
            return null;
        }

        try {
            final Date d = DATE_FORMATTER.parse(ruleAttribute.getValue());
            return d;
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
