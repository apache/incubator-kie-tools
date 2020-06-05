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
package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.LimitedEntryDropDownManager;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.util.ConstraintValueHelper;

/**
 * A utility class to expand Condition column definitions into rows. Action
 * columns are not expanded, as the use-case is that a user-determined action
 * should be specified for each combination of Conditions. Where a column is
 * defined as having multiple values (Guvnor enum, Java enum or Decision Table
 * Value List) the number of rows is the Cartesian Product of all combinations.
 */
public class RowExpander {

    private Map<BaseColumn, ColumnValues> expandedColumns = new IdentityHashMap<BaseColumn, ColumnValues>();
    private List<ColumnValues> columns;

    private final GuidedDecisionTable52 model;
    private final ColumnUtilities columnUtilities;
    private final AsyncPackageDataModelOracle oracle;

    private static final List<DTCellValue52> EMPTY_VALUE = new ArrayList<DTCellValue52>();

    {
        EMPTY_VALUE.add(new DTCellValue52());
    }

    /**
     * Constructor
     *
     * @param model
     * @param oracle
     */
    public RowExpander(final GuidedDecisionTable52 model,
                       final AsyncPackageDataModelOracle oracle) {
        this.columns = new ArrayList<ColumnValues>();
        this.model = model;
        this.oracle = oracle;
        this.columnUtilities = new ColumnUtilities(model,
                                                   oracle);

        //Add all columns to Expander to generate row data. The AnalysisCol is not added 
        //as its data is transient, not held in the underlying Decision Table's data
        addRowNumberColumn();
        addRuleNameColumn();
        addRowDescriptionColumn();
        addConditionColumns();
        addActionColumns();
    }

    public List<ColumnValues> getColumns() {
        return this.columns;
    }

    private void addRowNumberColumn() {
        ColumnValues cv = new RowNumberColumnValues(columns,
                                                    EMPTY_VALUE,
                                                    new DTCellValue52());
        cv.setExpandColumn(false);
        this.expandedColumns.put(model.getRowNumberCol(),
                                 cv);
        this.columns.add(cv);
    }

    private void addRuleNameColumn() {
        ColumnValues cv = new RuleNameColumnValues(columns,
                                                   EMPTY_VALUE,
                                                   new DTCellValue52());
        cv.setExpandColumn(false);
        this.expandedColumns.put(model.getRuleNameColumn(),
                                 cv);
        this.columns.add(cv);
    }

    private void addRowDescriptionColumn() {
        ColumnValues cv = new RowDescriptionColumnValues(columns,
                                                         EMPTY_VALUE,
                                                         new DTCellValue52());
        cv.setExpandColumn(false);
        this.expandedColumns.put(model.getDescriptionCol(),
                                 cv);
        this.columns.add(cv);
    }

    private void addConditionColumns() {
        for (Pattern52 p : model.getPatterns()) {
            addColumn(p);
        }
    }

    private void addActionColumns() {
        for (ActionCol52 a : model.getActionCols()) {
            if (a instanceof ActionSetFieldCol52) {
                ActionSetFieldCol52 afc = (ActionSetFieldCol52) a;
                addColumn(afc);
            } else if (a instanceof ActionInsertFactCol52) {
                ActionInsertFactCol52 aif = (ActionInsertFactCol52) a;
                addColumn(aif);
            }
        }
    }

    private void addColumn(final Pattern52 p) {
        for (ConditionCol52 c : p.getChildColumns()) {
            addColumn(p,
                      c);
        }
    }

    private void addColumn(final Pattern52 p,
                           final ConditionCol52 c) {
        switch (model.getTableFormat()) {
            case EXTENDED_ENTRY:
                addExtendedEntryColumn(p,
                                       c);
                break;
            case LIMITED_ENTRY:
                addLimitedEntryColumn(c);
                break;
        }
    }

    private void addExtendedEntryColumn(final Pattern52 p,
                                        final ConditionCol52 c) {
        ColumnValues cv = null;
        String[] values = new String[]{};
        if (columnUtilities.hasValueList(c)) {
            values = columnUtilities.getValueList(c);
            values = getSplitValues(values);
            cv = new ColumnValues(columns,
                                  convertValueList(values),
                                  c.getDefaultValue());
        } else if (oracle.hasEnums(p.getFactType(),
                                   c.getFactField())) {
            final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context(p,
                                                                                                        c);
            cv = new ColumnDynamicValues(columns,
                                         oracle,
                                         context,
                                         c.getDefaultValue());
        } else {
            cv = new ColumnValues(columns,
                                  convertValueList(values),
                                  c.getDefaultValue());
        }

        if (cv != null) {
            this.expandedColumns.put(c,
                                     cv);
            this.columns.add(cv);
        }
    }

    private void addLimitedEntryColumn(final ConditionCol52 c) {
        final List<DTCellValue52> values = new ArrayList<DTCellValue52>();
        values.add(new DTCellValue52(Boolean.TRUE));
        values.add(new DTCellValue52(Boolean.FALSE));

        final ColumnValues cv = new ColumnValues(columns,
                                                 values,
                                                 c.getDefaultValue());
        this.expandedColumns.put(c,
                                 cv);
        this.columns.add(cv);
    }

    private String[] getSplitValues(final String[] values) {
        final String[] splitValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            String[] splut = ConstraintValueHelper.splitValue(v);
            splitValues[i] = splut[0];
        }
        return splitValues;
    }

    private static List<DTCellValue52> convertValueList(final String[] values) {
        final List<DTCellValue52> convertedValues = new ArrayList<DTCellValue52>();
        for (String value : values) {
            convertedValues.add(new DTCellValue52(value));
        }
        return convertedValues;
    }

    private void addColumn(final ActionSetFieldCol52 a) {
        final ColumnValues cv = new ColumnValues(columns,
                                                 EMPTY_VALUE,
                                                 model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ? a.getDefaultValue() : new DTCellValue52(Boolean.FALSE));
        cv.setExpandColumn(false);
        this.expandedColumns.put(a,
                                 cv);
        this.columns.add(cv);
    }

    private void addColumn(final ActionInsertFactCol52 a) {
        final ColumnValues cv = new ColumnValues(columns,
                                                 EMPTY_VALUE,
                                                 model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ? a.getDefaultValue() : new DTCellValue52(Boolean.FALSE));
        cv.setExpandColumn(false);
        this.expandedColumns.put(a,
                                 cv);
        this.columns.add(cv);
    }

    /**
     * Rather than return a List of rows as the expanded form we expose an
     * Iterator with which the expanded form can be retrieved. This decision was
     * to avoid potentially hugh transient Lists being created; as the results
     * from this class will be transformed into other representations.
     *
     * @return
     */
    public RowIterator iterator() {
        return new RowIterator();
    }

    /**
     * Indicate whether the provided column should be expanded or not. If the
     * column was not part of the Decision Table used in the Constructor no
     * action is taken.
     *
     * @param column
     * @param expandColumn
     */
    public void setExpandColumn(final BaseColumn column,
                                final boolean expandColumn) {
        final ColumnValues cv = this.expandedColumns.get(column);
        if (cv == null) {
            return;
        }
        cv.setExpandColumn(expandColumn);
    }

    /**
     * An iterator that retrieves the expanded rows one at a time
     */
    public class RowIterator
            implements
            Iterator<List<DTCellValue52>> {

        //Check if all columns have had their value lists consumed
        @Override
        public boolean hasNext() {
            for (ColumnValues cv : columns) {
                if (!cv.isAllValuesUsed()) {
                    return true;
                }
            }
            return false;
        }

        //Build a row from the columns current values and advance the first column. Columns 
        //check whether all their values have been used and advance the subsequent column
        //so a ripple effect can be observed, with one column advancing the next, which
        //advances the next and so on...
        @Override
        public List<DTCellValue52> next() {

            //We have a row that is potentially partially populated as the dependent enum data has not been set
            //So ask columns to update their value lists based on the current row definition. This will force
            //the dependent enumeration value lists to be populated.
            boolean refreshRow = false;
            List<DTCellValue52> row;
            do {
                refreshRow = false;
                row = new ArrayList<DTCellValue52>();
                for (ColumnValues cv : columns) {
                    row.add(cv.getCurrentValue());
                }
                for (ColumnValues cv : columns) {
                    if (cv instanceof ColumnDynamicValues) {
                        final ColumnDynamicValues cdv = (ColumnDynamicValues) cv;
                        refreshRow = refreshRow || cdv.assertValueList(row);
                    }
                }
            } while (refreshRow);

            //Advance the first column to the next value
            columns.get(columns.size() - 1).advanceColumnValue();
            return row;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported on RowIterator");
        }
    }

    /**
     * Container for a columns values
     */
    static class ColumnValues {

        List<DTCellValue52> values;
        List<DTCellValue52> originalValues;
        List<ColumnValues> columns;
        DTCellValue52 value;
        DTCellValue52 defaultValue;
        Iterator<DTCellValue52> iterator;
        boolean expandColumn = true;
        boolean isAllValuesUsed = false;

        ColumnValues(final List<ColumnValues> columns,
                     final List<DTCellValue52> values,
                     final DTCellValue52 defaultValue) {
            this.columns = columns;
            this.defaultValue = defaultValue;
            this.values = values;
            this.originalValues = this.values;

            //If no values were provided add the default and record that all values have been used
            if (this.values == null || this.values.isEmpty()) {
                this.values = new ArrayList<DTCellValue52>();
                this.values.add(defaultValue);
                this.originalValues = this.values;
                this.isAllValuesUsed = true;
            }

            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        void setExpandColumn(final boolean expandColumn) {
            this.expandColumn = expandColumn;
            if (expandColumn) {
                this.values = this.originalValues;
                this.isAllValuesUsed = false;
            } else {
                this.values = new ArrayList<DTCellValue52>();
                this.values.add(defaultValue);
                this.isAllValuesUsed = true;
            }
            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        /**
         * Get the current value of the column
         *
         * @return
         */
        DTCellValue52 getCurrentValue() {
            return this.value;
        }

        /**
         * Advance to the next value for the column, resetting to the beginning
         * of the list if all values have been used. The reset operation also
         * advances the next columns value.
         *
         * @return
         */
        void advanceColumnValue() {
            if (iterator.hasNext()) {
                value = iterator.next();
            } else {
                isAllValuesUsed = true;
                this.iterator = this.values.iterator();
                this.value = iterator.next();
                int myIndex = columns.indexOf(this);
                if (myIndex > 0) {
                    columns.get(myIndex - 1).advanceColumnValue();
                }
            }
        }

        /**
         * Have all values in the columns list been used
         *
         * @return
         */
        boolean isAllValuesUsed() {
            return this.isAllValuesUsed;
        }
    }

    /**
     * Container for a columns values that are dynamically generated
     */
    static class ColumnDynamicValues extends ColumnValues {

        private final LimitedEntryDropDownManager.Context context;
        private final AsyncPackageDataModelOracle oracle;
        private boolean initialiseValueList = true;

        ColumnDynamicValues(final List<ColumnValues> columns,
                            final AsyncPackageDataModelOracle oracle,
                            final LimitedEntryDropDownManager.Context context,
                            final DTCellValue52 defaultValue) {
            super(columns,
                  EMPTY_VALUE,
                  defaultValue);
            this.oracle = oracle;
            this.context = context;

            //Check if there is an enumeration
            final DropDownData dd = oracle.getEnums(context.getBasePattern().getFactType(),
                                                    ((ConditionCol52) context.getBaseColumn()).getFactField(),
                                                    new HashMap<String, String>());
            if (dd != null) {
                this.values = convertValueList(getSplitValues(dd.getFixedList()));
                this.originalValues = this.values;
                this.initialiseValueList = false;
                this.isAllValuesUsed = false;
            }

            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        private String[] getSplitValues(final String[] values) {
            final String[] splitValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                final String v = values[i];
                final String[] splut = ConstraintValueHelper.splitValue(v);
                splitValues[i] = splut[0];
            }
            return splitValues;
        }

        /**
         * Assert that the Value List is correct for data contained in the row
         *
         * @param row
         * @returns true if the Value List has changed and the row should be
         * refreshed
         */
        boolean assertValueList(final List<DTCellValue52> row) {
            if (!this.expandColumn) {
                return false;
            }
            final boolean refreshRow = this.initialiseValueList;
            if (refreshRow) {
                final Map<String, String> currentValueMap = new HashMap<String, String>();
                for (int iCol = 0; iCol < this.columns.size(); iCol++) {
                    final ColumnValues cv = this.columns.get(iCol);
                    if (cv instanceof ColumnDynamicValues) {
                        final ColumnDynamicValues cdv = (ColumnDynamicValues) cv;
                        if (cdv.context.getBasePattern().equals(this.context.getBasePattern())) {
                            final ConditionCol52 cc = (ConditionCol52) cdv.context.getBaseColumn();
                            final DTCellValue52 value = row.get(iCol);
                            if (value != null) {
                                currentValueMap.put(cc.getFactField(),
                                                    value.getStringValue());
                            }
                        }
                    }
                }
                this.initialiseValueList = false;
                final DropDownData dd = oracle.getEnums(context.getBasePattern().getFactType(),
                                                        ((ConditionCol52) context.getBaseColumn()).getFactField(),
                                                        currentValueMap);
                if (dd != null) {
                    this.values = convertValueList(getSplitValues(dd.getFixedList()));
                    this.originalValues = this.values;
                    this.isAllValuesUsed = false;
                } else {
                    this.values = new ArrayList<DTCellValue52>();
                    this.values.add(defaultValue);
                    this.originalValues = this.values;
                    this.isAllValuesUsed = true;
                }

                //Initialise value to the first in the list
                this.iterator = this.values.iterator();
                this.value = iterator.next();
            }
            return refreshRow;
        }

        /**
         * Advance to the next value for the column, resetting to the beginning
         * of the list if all values have been used. The reset operation also
         * advances the next columns value.
         *
         * @return
         */
        void advanceColumnValue() {
            if (iterator.hasNext()) {
                value = iterator.next();
            } else {
                isAllValuesUsed = true;
                this.initialiseValueList = true;
                this.iterator = values.iterator();
                value = iterator.next();
                int myIndex = columns.indexOf(this);
                if (myIndex > 0) {
                    columns.get(myIndex - 1).advanceColumnValue();
                }
            }
        }
    }

    /**
     * Container for Row Number column values
     */
    static class RowNumberColumnValues extends ColumnValues {

        RowNumberColumnValues(final List<ColumnValues> columns,
                              final List<DTCellValue52> values,
                              final DTCellValue52 defaultValue) {
            super(columns,
                  values,
                  defaultValue);
        }

        @Override
        DTCellValue52 getCurrentValue() {
            //GUVNOR-1960: Always return a new instance
            return new DTCellValue52();
        }
    }

    static class RuleNameColumnValues extends ColumnValues {

        RuleNameColumnValues(final List<ColumnValues> columns,
                                   final List<DTCellValue52> values,
                                   final DTCellValue52 defaultValue) {
            super(columns,
                  values,
                  defaultValue);
        }

        @Override
        DTCellValue52 getCurrentValue() {
            //GUVNOR-1960: Always return a new instance
            return new DTCellValue52();
        }
    }

    /**
     * Container for Row Description column values
     */
    static class RowDescriptionColumnValues extends ColumnValues {

        RowDescriptionColumnValues(final List<ColumnValues> columns,
                                   final List<DTCellValue52> values,
                                   final DTCellValue52 defaultValue) {
            super(columns,
                  values,
                  defaultValue);
        }

        @Override
        DTCellValue52 getCurrentValue() {
            //GUVNOR-1960: Always return a new instance
            return new DTCellValue52();
        }
    }
}
