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

package org.drools.workbench.services.verifier.plugin.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.builders.BuildException;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerProvider;
import org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil;
import org.junit.Before;
import org.kie.soup.project.datamodel.oracle.DataType;

public abstract class AnalyzerUpdateTestBase {

    protected AnalyzerProvider analyzerProvider;
    protected GuidedDecisionTable52 table52;
    protected Analyzer analyzer;

    private DTableUpdateManager updateManager;

    @Before
    public void setUp() throws
            Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    protected void fireUpAnalyzer() {
        if (analyzer == null) {
            analyzer = analyzerProvider.makeAnalyser(table52);
            updateManager = analyzerProvider.getUpdateManager(table52,
                                                              analyzer);
        }
        analyzer.resetChecks();
        analyzer.analyze();
    }

    protected void removeRow(final int rowIndex) {
        table52.getData()
                .remove(rowIndex);

        analyzer.removeRule(rowIndex);
    }

    protected void removeActionColumn(final int columnDataIndex,
                                      final int columnActionIndex) {
        table52.getActionCols()
                .remove(columnActionIndex);
        for (final List<DTCellValue52> row : table52.getData()) {
            row.remove(columnDataIndex);
        }

        updateManager.deleteColumns(columnDataIndex,
                                    1);
    }

    public ValueSetter setCoordinate() {
        return new ValueSetter();
    }

    protected void setValue(final int rowIndex,
                            final int columnIndex,
                            final Date value) {
        DTCellValue52 dtCellValue52 = table52.getData()
                .get(rowIndex)
                .get(columnIndex);
        dtCellValue52
                .setDateValue(value);
        updateManager.update(table52,
                             getUpdates(rowIndex,
                                        columnIndex));
    }

    protected void setValue(final int rowIndex,
                            final int columnIndex,
                            final Number value) {
        DTCellValue52 dtCellValue52 = table52.getData()
                .get(rowIndex)
                .get(columnIndex);
        dtCellValue52
                .setNumericValue(value);
        updateManager.update(table52,
                             getUpdates(rowIndex,
                                        columnIndex));
    }

    protected void setValue(final int rowIndex,
                            final int columnIndex,
                            final String value) {
        table52.getData()
                .get(rowIndex)
                .get(columnIndex)
                .setStringValue(value);
        updateManager.update(table52,
                             getUpdates(rowIndex,
                                        columnIndex));
    }

    protected void setValue(final int rowIndex,
                            final int columnIndex,
                            final Boolean value) {
        table52.getData()
                .get(rowIndex)
                .get(columnIndex)
                .setBooleanValue(value);
        updateManager.update(table52,
                             getUpdates(rowIndex,
                                        columnIndex));
    }

    protected void appendActionColumn(final int columnNumber,
                                      final ActionSetFieldCol52 actionSetField,
                                      final Comparable... cellValues) throws
            BuildException {
        table52.getActionCols()
                .add(actionSetField);

        for (int i = 0; i < cellValues.length; i++) {
            table52.getData()
                    .get(i)
                    .add(new DTCellValue52(cellValues[i]));
        }

        updateManager.newColumn(table52,
                                new ModelMetaDataEnhancer(table52).getHeaderMetaData(),
                                analyzerProvider.getFactTypes(),
                                columnNumber);
    }

    protected void insertConditionColumn(final int columnNumber,
                                         final BRLConditionColumn brlConditionColumn,
                                         final Comparable... cellValues) throws
            BuildException {
        table52.getConditions()
                .add(brlConditionColumn);

        for (int i = 0; i < cellValues.length; i++) {
            table52.getData()
                    .get(i)
                    .add(new DTCellValue52(cellValues[i]));
        }

        updateManager.newColumn(table52,
                                new ModelMetaDataEnhancer(table52).getHeaderMetaData(),
                                analyzerProvider.getFactTypes(),
                                columnNumber);
    }

    protected void insertRow(final int rowNumber,
                             final DataType.DataTypes... dataTypes) throws
            BuildException {
        table52.getData()
                .add(rowNumber,
                     newRow(dataTypes));
        updateManager.makeRule(table52,
                               new ModelMetaDataEnhancer(table52).getHeaderMetaData(),
                               analyzerProvider.getFactTypes(),
                               rowNumber);
    }

    protected void appendRow(final DataType.DataTypes... dataTypes) throws
            BuildException {

        final ArrayList<DTCellValue52> row = newRow(dataTypes);

        table52.getData()
                .add(row);
        updateManager.makeRule(table52,
                               new ModelMetaDataEnhancer(table52).getHeaderMetaData(),
                               analyzerProvider.getFactTypes(),
                               table52.getData()
                                       .size() - 1);
    }

    private ArrayList<DTCellValue52> newRow(final DataType.DataTypes[] dataTypes) {
        final ArrayList<DTCellValue52> row = new ArrayList<>();

        // Row number
        row.add(new DTCellValue52());
        // Rule Name
        row.add(new DTCellValue52());
        // Explanation
        row.add(new DTCellValue52());

        for (final DataType.DataTypes dataType : dataTypes) {
            row.add(new DTCellValue52(dataType,
                                      true));
        }
        return row;
    }

    protected List<Coordinate> getUpdates(final int x,
                                          final int y) {
        final List<Coordinate> updates = new ArrayList<>();
        updates.add(new Coordinate(x,
                                   y));
        return updates;
    }

    protected void analyze(String resourceName) throws Exception {
        final String xml = TestUtil.loadResource(resourceName);

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        // First run
        analyzer.resetChecks();
        analyzer.analyze();
    }

    public class ValueSetter {

        public ColumnValueSetter row(final int row) {
            return new ColumnValueSetter(row);
        }

        public class ColumnValueSetter {

            private int row;

            public ColumnValueSetter(final int row) {
                this.row = row;
            }

            public CellValueSetter column(final int column) {
                return new CellValueSetter(column);
            }

            public class CellValueSetter {

                private int column;

                public CellValueSetter(final int column) {
                    this.column = column;
                }

                public void toValue(final String value) {
                    setValue(row,
                             column,
                             value);
                }

                public void toValue(final Number value) {
                    setValue(row,
                             column,
                             value);
                }

                public void toValue(final Boolean value) {
                    setValue(row,
                             column,
                             value);
                }
            }
        }
    }
}
