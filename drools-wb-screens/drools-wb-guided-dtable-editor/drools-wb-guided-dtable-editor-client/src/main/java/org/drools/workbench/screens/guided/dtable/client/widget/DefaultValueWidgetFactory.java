/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.Date;
import java.util.Objects;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.RuleAttributeWidget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

/**
 * Factory for Default Value widgets
 */
public class DefaultValueWidgetFactory {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat format = DateTimeFormat.getFormat(DATE_FORMAT);

    /**
     * BZ-996932: Default value changed event handler interface.
     * <p/>
     * Widgets using DefaultValueWidget should listen to value changed event by implementing this custom GWT Event Handler.
     */
    public interface DefaultValueChangedEventHandler {

        void onDefaultValueChanged(DefaultValueChangedEvent event);
    }

    /**
     * BZ-996932: Default value changed event definition.
     * <p/>
     * When default value is changed this event if fired to notified handlers.
     */
    public static class DefaultValueChangedEvent {

        private final DTCellValue52 originalDefaultValue;
        private final DTCellValue52 editedDefaultValue;

        public DefaultValueChangedEvent(DTCellValue52 originalDefaultValue,
                                        DTCellValue52 editedDefaultValue) {
            this.originalDefaultValue = originalDefaultValue;
            this.editedDefaultValue = editedDefaultValue;
        }

        public DTCellValue52 getOriginalDefaultValue() {
            return originalDefaultValue;
        }

        public DTCellValue52 getEditedDefaultValue() {
            return editedDefaultValue;
        }
    }

    // BZ-996932: Added value change notifications.
    public static Widget getDefaultValueWidget(final AttributeCol52 ac,
                                               final boolean isReadOnly,
                                               final DefaultValueChangedEventHandler defaultValueChangedEventHandler) {
        Widget editor = null;
        final String attribute = ac.getAttribute();
        if (Objects.equals(attribute, Attribute.DIALECT.getAttributeName())) {
            final ListBox lb = new ListBox();
            lb.addItem(RuleAttributeWidget.DIALECTS.get(0));
            lb.addItem(RuleAttributeWidget.DIALECTS.get(1));
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(RuleAttributeWidget.DIALECTS.get(1)));
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final String stringValue = defaultValue.getStringValue();
            lb.setEnabled(!isReadOnly);
            if (!isReadOnly) {
                lb.addChangeHandler(event -> {
                    final int selectedIndex = lb.getSelectedIndex();
                    if (selectedIndex < 0) {
                        return;
                    }
                    DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                    editedDefaultValue.setStringValue(lb.getValue(selectedIndex));
                    defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                       editedDefaultValue));
                });
            }
            if (stringValue == null || stringValue.isEmpty()) {
                lb.setSelectedIndex(1);
                defaultValue.setStringValue(RuleAttributeWidget.DIALECTS.get(1));
            } else if (stringValue.equals(RuleAttributeWidget.DIALECTS.get(0))) {
                lb.setSelectedIndex(0);
            } else if (stringValue.equals(RuleAttributeWidget.DIALECTS.get(1))) {
                lb.setSelectedIndex(1);
            } else {
                lb.setSelectedIndex(1);
                defaultValue.setStringValue(RuleAttributeWidget.DIALECTS.get(1));
            }
            editor = lb;
        } else if (Objects.equals(attribute, Attribute.SALIENCE.getAttributeName())) {
            final TextBox tb = TextBoxFactory.getTextBox(DataType.TYPE_NUMERIC_INTEGER);
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(0));
            } else {
                assertIntegerDefaultValue(ac.getDefaultValue());
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Integer numericValue = (Integer) defaultValue.getNumericValue();
            tb.setValue(numericValue == null ? "" : numericValue.toString());
            tb.setEnabled(!isReadOnly);
            if (!isReadOnly) {
                tb.addValueChangeHandler(event -> {
                    DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                    try {
                        editedDefaultValue.setNumericValue(Integer.valueOf(event.getValue()));
                    } catch (NumberFormatException nfe) {
                        editedDefaultValue.setNumericValue(0);
                        tb.setValue("0");
                    } finally {
                        defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                           editedDefaultValue));
                    }
                });
            }
            editor = tb;
        } else if (Objects.equals(attribute, Attribute.DURATION.getAttributeName())) {
            final TextBox tb = TextBoxFactory.getTextBox(DataType.TYPE_NUMERIC_LONG);
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(0L));
            } else {
                assertLongDefaultValue(ac.getDefaultValue());
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Long numericValue = (Long) defaultValue.getNumericValue();
            tb.setValue(numericValue == null ? "" : numericValue.toString());
            tb.setEnabled(!isReadOnly);
            if (!isReadOnly) {
                tb.addValueChangeHandler(event -> {
                    DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                    try {
                        editedDefaultValue.setNumericValue(Long.valueOf(event.getValue()));
                    } catch (NumberFormatException nfe) {
                        editedDefaultValue.setNumericValue(0L);
                        tb.setValue("0");
                    } finally {
                        defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                           editedDefaultValue));
                    }
                });
            }
            editor = tb;
        } else if (Objects.equals(Attribute.getAttributeDataType(attribute), DataType.TYPE_STRING)) {
            final TextBox tb = TextBoxFactory.getTextBox(DataType.TYPE_STRING);
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(""));
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            tb.setValue(defaultValue.getStringValue());
            tb.setEnabled(!isReadOnly);
            if (!isReadOnly) {
                tb.addValueChangeHandler(event -> {
                    DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                    editedDefaultValue.setStringValue(tb.getValue());
                    defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                       editedDefaultValue));
                });
            }
            editor = tb;
        } else if (Objects.equals(Attribute.getAttributeDataType(attribute), DataType.TYPE_BOOLEAN)) {
            final CheckBox cb = new CheckBox();
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(Boolean.FALSE));
            } else {
                assertBooleanDefaultValue(ac.getDefaultValue());
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Boolean booleanValue = defaultValue.getBooleanValue();
            cb.setEnabled(!isReadOnly);
            if (booleanValue == null) {
                cb.setValue(false);
                defaultValue.setBooleanValue(Boolean.FALSE);
            } else {
                cb.setValue(booleanValue);
            }

            cb.addClickHandler(event -> {
                DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                editedDefaultValue.setBooleanValue(cb.getValue());
                defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                   editedDefaultValue));
            });
            editor = cb;
        } else if (Objects.equals(Attribute.getAttributeDataType(attribute), DataType.TYPE_DATE)) {
            if (ac.getDefaultValue() == null) {
                ac.setDefaultValue(new DTCellValue52(new Date()));
            } else {
                assertDateDefaultValue(ac.getDefaultValue());
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            if (isReadOnly) {
                final TextBox tb = TextBoxFactory.getTextBox(DataType.TYPE_STRING);
                tb.setValue(format.format(defaultValue.getDateValue()));
                tb.setEnabled(false);
            } else {
                final DatePicker datePicker = new DatePicker();

                // Wire up update handler
                datePicker.addChangeDateHandler(event -> {
                    DTCellValue52 editedDefaultValue = defaultValue.cloneDefaultValueCell();
                    editedDefaultValue.setDateValue(datePicker.getValue());
                    defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue,
                                                                                                       editedDefaultValue));
                });

                final Date dateValue = defaultValue.getDateValue();
                datePicker.setFormat(DATE_FORMAT);
                datePicker.setValue(dateValue);
                editor = datePicker;
            }
        }
        return editor;
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertIntegerDefaultValue(final DTCellValue52 dcv) {
        if (dcv.getNumericValue() == null) {
            try {
                dcv.setNumericValue(Integer.valueOf(dcv.getStringValue()));
            } catch (NumberFormatException nfe) {
                dcv.setNumericValue(0);
            }
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertLongDefaultValue(final DTCellValue52 dcv) {
        if (dcv.getNumericValue() == null) {
            try {
                dcv.setNumericValue(Long.valueOf(dcv.getStringValue()));
            } catch (NumberFormatException nfe) {
                dcv.setNumericValue(0L);
            }
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertBooleanDefaultValue(final DTCellValue52 dcv) {
        if (dcv.getBooleanValue() == null) {
            dcv.setBooleanValue(Boolean.valueOf(dcv.getStringValue()));
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertDateDefaultValue(final DTCellValue52 dcv) {
        if (dcv.getDateValue() == null) {
            try {
                dcv.setDateValue(format.parse(dcv.getStringValue()));
            } catch (IllegalArgumentException eae) {
                dcv.setDateValue(new Date());
            }
        }
    }
}
