/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DefaultValueDropDownManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.LimitedEntryDropDownManager;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.EnumDropDownUtilities;
import org.uberfire.ext.widgets.common.client.common.AbstractRestrictedEntryTextBox;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.widgets.common.client.common.NumericBigDecimalTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericBigIntegerTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericByteTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericDoubleTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericFloatTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericIntegerTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericLongTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericShortTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericTextBox;

/**
 * A Factory for Widgets to edit DTCellValues
 */
public class DTCellValueWidgetFactory {

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final ColumnUtilities columnUtilities;
    private final LimitedEntryDropDownManager dropDownManager;
    private final boolean isReadOnly;
    private final boolean allowEmptyValues;

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat format = DateTimeFormat.getFormat(DATE_FORMAT);

    public static DTCellValueWidgetFactory getInstance(GuidedDecisionTable52 model,
                                                       AsyncPackageDataModelOracle oracle,
                                                       boolean isReadOnly,
                                                       boolean allowEmptyValues) {
        switch (model.getTableFormat()) {
            case EXTENDED_ENTRY:
                return new DTCellValueWidgetFactory(model,
                                                    oracle,
                                                    new DefaultValueDropDownManager(model,
                                                                                    oracle),
                                                    isReadOnly,
                                                    allowEmptyValues);
            default:
                return new DTCellValueWidgetFactory(model,
                                                    oracle,
                                                    new LimitedEntryDropDownManager(model,
                                                                                    oracle),
                                                    isReadOnly,
                                                    allowEmptyValues);
        }
    }

    private DTCellValueWidgetFactory(GuidedDecisionTable52 model,
                                     AsyncPackageDataModelOracle oracle,
                                     LimitedEntryDropDownManager dropDownManager,
                                     boolean isReadOnly,
                                     boolean allowEmptyValues) {
        this.model = model;
        this.oracle = oracle;
        this.columnUtilities = new ColumnUtilities(model,
                                                   oracle);
        this.dropDownManager = dropDownManager;
        this.isReadOnly = isReadOnly;
        this.allowEmptyValues = allowEmptyValues;
    }

    /**
     * Make a DTCellValue for a column
     *
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(DTColumnConfig52 c) {
        DataType.DataTypes type = columnUtilities.getTypeSafeType(c);
        return new DTCellValue52(type,
                                 allowEmptyValues);
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the pattern may be different to that to which the
     * column has been bound in the Decision Table model, i.e. when adding or
     * editing a column
     *
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(Pattern52 p,
                                      ConditionCol52 c) {
        DataType.DataTypes type = columnUtilities.getTypeSafeType(p,
                                                                  c);
        return new DTCellValue52(type,
                                 allowEmptyValues);
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the pattern may be different to that to
     * which the column has been bound in the Decision Table model, i.e. when
     * adding or editing a column
     *
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ConditionCol52 column,
                            DTCellValue52 value) {

        final boolean isMultipleSelect = isExplicitListOperator(column.getOperator());

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if (columnUtilities.hasValueList(column)) {
            final String[] valueList = columnUtilities.getValueList(column);
            return makeListBoxForColumn(DropDownData.create(valueList),
                                        pattern,
                                        column,
                                        value,
                                        isMultipleSelect);
        } else if (oracle.hasEnums(pattern.getFactType(),
                                   column.getFactField())) {
            return makeHasEnumsListBox(pattern,
                                       column,
                                       value,
                                       isMultipleSelect,
                                       column.getFactField(),
                                       pattern.getFactType());
        }

        DataType.DataTypes type = columnUtilities.getTypeSafeType(pattern,
                                                                  column);
        return makeHasNoValuesAndEnumsWidget(type,
                                             value);
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the ActionSetFieldCol52 column may be associated
     * with an unbound Pattern
     *
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(Pattern52 p,
                                      ActionSetFieldCol52 c) {
        DataType.DataTypes type = columnUtilities.getTypeSafeType(p,
                                                                  c);
        return new DTCellValue52(type,
                                 allowEmptyValues);
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the ActionSetFieldCol52 column may be
     * associated with an unbound Pattern
     *
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ActionSetFieldCol52 column,
                            DTCellValue52 value) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if (columnUtilities.hasValueList(column)) {
            final String[] valueList = columnUtilities.getValueList(column);
            return makeListBoxForColumn(DropDownData.create(valueList),
                                        pattern,
                                        column,
                                        value,
                                        false);
        } else if (oracle.hasEnums(pattern.getFactType(),
                                   column.getFactField())) {
            return makeHasEnumsListBox(pattern,
                                       column,
                                       value,
                                       false,
                                       column.getFactField(),
                                       pattern.getFactType());
        }

        DataType.DataTypes type = columnUtilities.getTypeSafeType(pattern,
                                                                  column);
        return makeHasNoValuesAndEnumsWidget(type,
                                             value);
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List").
     *
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(ActionInsertFactCol52 column,
                            DTCellValue52 value) {
        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if (columnUtilities.hasValueList(column)) {
            final String[] valueList = columnUtilities.getValueList(column);
            return makeListBoxForColumn(DropDownData.create(valueList),
                                        null,
                                        column,
                                        value,
                                        false);
        } else if (oracle.hasEnums(column.getFactType(),
                                   column.getFactField())) {
            return makeHasEnumsListBox(null,
                                       column,
                                       value,
                                       false,
                                       column.getFactField(),
                                       column.getFactType());
        }

        DataType.DataTypes type = columnUtilities.getTypeSafeType(column);
        return makeHasNoValuesAndEnumsWidget(type,
                                             value);
    }

    public IsWidget getWidget(BRLVariableColumn column,
                              DTCellValue52 value) {
        if (oracle.hasEnums(column.getFactType(),
                            column.getFactField())) {
            return makeHasEnumsListBox(null,
                                       (BaseColumn) column,
                                       value,
                                       false,
                                       column.getFactField(),
                                       column.getFactType());
        }

        DataType.DataTypes type = columnUtilities.getTypeSafeType((BaseColumn) column);
        return makeHasNoValuesAndEnumsWidget(type,
                                             value);
    }

    private ListBox makeHasEnumsListBox(Pattern52 pattern,
                                        BaseColumn column,
                                        DTCellValue52 value,
                                        boolean isMultipleSelect,
                                        String factField,
                                        String factType) {
        final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context(pattern,
                                                                                                    column);
        final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap(context);
        final DropDownData dd = oracle.getEnums(factType,
                                                factField,
                                                currentValueMap);
        //No drop-down data defined
        if (dd == null) {
            return makeListBoxForColumn(DropDownData.create(new String[0]),
                                        pattern,
                                        column,
                                        value,
                                        isMultipleSelect);
        }
        return makeListBoxForColumn(dd,
                                    pattern,
                                    column,
                                    value,
                                    isMultipleSelect);
    }

    private Widget makeHasNoValuesAndEnumsWidget(DataType.DataTypes type,
                                                 DTCellValue52 value) {
        switch (type) {
            case NUMERIC:
                return makeNumericTextBox(value);
            case NUMERIC_BIGDECIMAL:
                return makeNumericBigDecimalTextBox(value);
            case NUMERIC_BIGINTEGER:
                return makeNumericBigIntegerTextBox(value);
            case NUMERIC_BYTE:
                return makeNumericByteTextBox(value);
            case NUMERIC_DOUBLE:
                return makeNumericDoubleTextBox(value);
            case NUMERIC_FLOAT:
                return makeNumericFloatTextBox(value);
            case NUMERIC_INTEGER:
                return makeNumericIntegerTextBox(value);
            case NUMERIC_LONG:
                return makeNumericLongTextBox(value);
            case NUMERIC_SHORT:
                return makeNumericShortTextBox(value);
            case BOOLEAN:
                return makeBooleanSelector(value);
            case DATE:
                return makeDateSelector(value);
            default:
                return makeTextBox(value);
        }
    }

    private ListBox makeBooleanSelector(final DTCellValue52 value) {
        final ListBox lb = new ListBox();
        int indexTrue = 0;
        int indexFalse = 1;

        if (allowEmptyValues) {
            indexTrue = 1;
            indexFalse = 2;
            lb.addItem(GuidedDecisionTableConstants.INSTANCE.Choose(),
                       "");
        }

        lb.addItem("true");
        lb.addItem("false");
        Boolean currentItem = value.getBooleanValue();
        if (currentItem == null) {
            lb.setSelectedIndex(0);
        } else {
            lb.setSelectedIndex(currentItem ? indexTrue : indexFalse);
        }

        // Wire up update handler
        lb.setEnabled(!isReadOnly);
        if (!isReadOnly) {
            lb.addChangeHandler(new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    final String txtValue = lb.getValue(lb.getSelectedIndex());
                    Boolean boolValue = (txtValue.equals("") ? null : txtValue.equals("true"));
                    value.setBooleanValue(boolValue);
                }
            });
        }
        return lb;
    }

    private ListBox makeListBoxForColumn(final DropDownData dd,
                                         final Pattern52 basePattern,
                                         final BaseColumn baseCondition,
                                         final DTCellValue52 dcv,
                                         final boolean isMultipleSelect) {
        final ListBox lb = makeListBox(dd,
                                       isMultipleSelect,
                                       dcv);

        // Wire up update handler
        lb.setEnabled(!isReadOnly);
        if (!isReadOnly) {
            lb.addChangeHandler(new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    String value = null;
                    if (lb.isMultipleSelect()) {
                        for (int i = 0; i < lb.getItemCount(); i++) {
                            if (lb.isItemSelected(i)) {
                                if (value == null) {
                                    value = lb.getValue(i);
                                } else {
                                    value = value + "," + lb.getValue(i);
                                }
                            }
                        }
                    } else {
                        int index = lb.getSelectedIndex();
                        if (index > -1) {
                            //Set base column value
                            value = lb.getValue(index);
                        }
                    }

                    dcv.setStringValue(value);

                    //Update any dependent enumerations
                    final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context(basePattern,
                                                                                                                baseCondition);
                    Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes(context);
                    for (Integer iCol : dependentColumnIndexes) {
                        BaseColumn column = model.getExpandedColumns().get(iCol);
                        if (column instanceof LimitedEntryCol) {
                            ((LimitedEntryCol) column).setValue(null);
                        } else if (column instanceof DTColumnConfig52) {
                            ((DTColumnConfig52) column).setDefaultValue(null);
                        }
                    }
                }
            });
        }
        return lb;
    }

    private boolean isExplicitListOperator(final String operator) {
        final List<String> ops = Arrays.asList(OperatorsOracle.EXPLICIT_LIST_OPERATORS);
        return ops.contains(operator);
    }

    private ListBox makeListBox(final DropDownData dd,
                                final boolean isMultipleSelect,
                                final DTCellValue52 value) {
        final ListBox lb = new ListBox();
        lb.setMultipleSelect(isMultipleSelect);

        final EnumDropDownUtilities utilities = new EnumDropDownUtilities() {
            @Override
            protected int addItems(final ListBox listBox) {
                if (allowEmptyValues) {
                    listBox.addItem(GuidedDecisionTableConstants.INSTANCE.Choose(),
                                    "");
                }
                return allowEmptyValues ? 1 : 0;
            }

            @Override
            protected void selectItem(final ListBox listBox) {
                final int itemCount = listBox.getItemCount();
                listBox.setEnabled(itemCount > 0);
                if (itemCount > 0) {
                    listBox.setSelectedIndex(0);
                    value.setStringValue(listBox.getValue(0));
                } else {
                    value.setStringValue(null);
                }
            }
        };

        final String strValue = value.getStringValue() == null ? "" : value.getStringValue();
        utilities.setDropDownData(strValue,
                                  dd,
                                  isMultipleSelect,
                                  oracle.getResourcePath(),
                                  lb);

        return lb;
    }

    private AbstractRestrictedEntryTextBox makeNumericTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericTextBox(allowEmptyValues);
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toPlainString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> new BigDecimal(input)),
                                       (BigDecimal) null,
                                       BigDecimal.ZERO);
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigDecimalTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigDecimalTextBox(allowEmptyValues);
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toPlainString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> new BigDecimal(input)),
                                       (BigDecimal) null,
                                       BigDecimal.ZERO);
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigIntegerTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigIntegerTextBox(allowEmptyValues);
        final BigInteger numericValue = (BigInteger) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> new BigInteger(input)),
                                       (BigInteger) null,
                                       BigInteger.ZERO);
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericByteTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericByteTextBox(allowEmptyValues);
        final Byte numericValue = (Byte) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Byte.valueOf(input)),
                                       (Byte) null,
                                       new Byte("0"));
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericDoubleTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericDoubleTextBox(allowEmptyValues);
        final Double numericValue = (Double) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Double.valueOf(input)),
                                       (Double) null,
                                       new Double("0"));
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericFloatTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericFloatTextBox(allowEmptyValues);
        final Float numericValue = (Float) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Float.valueOf(input)),
                                       (Float) null,
                                       new Float("0"));
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericIntegerTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericIntegerTextBox(allowEmptyValues);
        final Integer numericValue = (Integer) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Integer.valueOf(input)),
                                       (Integer) null,
                                       0);
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericLongTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericLongTextBox(allowEmptyValues);
        final Long numericValue = (Long) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Long.valueOf(input)),
                                       (Long) null,
                                       0L);

        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericShortTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericShortTextBox(allowEmptyValues);
        final Short numericValue = (Short) value.getNumericValue();
        tb.setValue(numericValue == null ? "" : numericValue.toString());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        addNumericTextBoxChangeHandler(tb,
                                       value,
                                       (input -> Short.valueOf(input)),
                                       (Short) null,
                                       Short.valueOf("0"));
        return tb;
    }

    private void addNumericTextBoxChangeHandler(final TextBox textBox,
                                                final DTCellValue52 value,
                                                final Function<String, ? extends Number> valueOf,
                                                final Number emptyValue,
                                                final Number zeroValue) {
        if (!isReadOnly) {
            textBox.addValueChangeHandler(new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue(valueOf.apply(event.getValue()));
                    } catch (NumberFormatException nfe) {
                        if (allowEmptyValues) {
                            value.setNumericValue(emptyValue);
                            textBox.setValue("");
                        } else {
                            value.setNumericValue(zeroValue);
                            textBox.setValue("0");
                        }
                    }
                }
            });
        }
    }

    private TextBox makeTextBox(final DTCellValue52 value) {
        TextBox tb = new TextBox();
        tb.setValue(value.getStringValue());

        // Wire up update handler
        tb.setEnabled(!isReadOnly);
        if (!isReadOnly) {
            tb.addValueChangeHandler(new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    value.setStringValue(event.getValue());
                }
            });
        }
        return tb;
    }

    private Widget makeDateSelector(final DTCellValue52 value) {
        //If read-only return a label
        if (isReadOnly) {
            Label dateLabel = new Label();
            dateLabel.setText(format.format(value.getDateValue()));
            return dateLabel;
        }

        final DatePicker datePicker = new DatePicker(allowEmptyValues);

        // Wire up update handler
        datePicker.addChangeDateHandler((e) -> value.setDateValue(datePicker.getValue()));

        datePicker.setFormat(DATE_FORMAT);
        datePicker.setValue(value.getDateValue());

        return datePicker;
    }
}
