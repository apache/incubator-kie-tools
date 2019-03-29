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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.drools.workbench.services.verifier.plugin.client.builders.BuildException;
import org.drools.workbench.services.verifier.plugin.client.builders.BuilderFactory;
import org.drools.workbench.services.verifier.plugin.client.builders.VerifierColumnUtilities;
import org.drools.workbench.services.verifier.plugin.client.util.NullEqualityOperator;

public class DTableUpdateManager {

    private static final int ROW_NUMBER_COLUMN = 0;
    private static final int DESCRIPTION_COLUMN = 1;

    private Index index;
    private final Analyzer analyzer;
    private final AnalyzerConfiguration configuration;

    public DTableUpdateManager(final Index index,
                               final Analyzer analyzer,
                               final AnalyzerConfiguration configuration) {
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.analyzer = PortablePreconditions.checkNotNull("analyzer",
                                                           analyzer);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public void removeRule(final Integer rowDeleted) {
        analyzer.removeRule(PortablePreconditions.checkNotNull("rowDeleted",
                                                               rowDeleted));
    }

    public void update(final GuidedDecisionTable52 model,
                       final List<Coordinate> coordinates) throws
            UpdateException {
        PortablePreconditions.checkNotNull("model",
                                           model);
        PortablePreconditions.checkNotNull("coordinates",
                                           coordinates);

        final Set<Integer> canBeUpdated = new HashSet<>();

        for (final Coordinate coordinate : coordinates) {
            if (coordinate.getCol() != ROW_NUMBER_COLUMN
                    && coordinate.getCol() != DESCRIPTION_COLUMN) {

                if (getCellUpdateManager(coordinate,
                                         model).update()) {
                    canBeUpdated.add(coordinate.getRow());
                }
            }
        }

        boolean hadUpdates = !canBeUpdated.isEmpty();

        if (hadUpdates) {
            analyzer.update(canBeUpdated);
            analyzer.analyze();
        }
    }

    private CellUpdateManagerBase getCellUpdateManager(final Coordinate coordinate,
                                                       final GuidedDecisionTable52 model) throws
            UpdateException {
        final BaseColumn baseColumn = model.getExpandedColumns()
                .get(coordinate.getCol());

        if (isConditionColumnWithSpecialOperator(baseColumn)) {
            return new NullEqualityOperatorCellUpdateManager(index,
                                                             model,
                                                             coordinate);
        } else {
            return new RegularCellUpdateManager(index,
                                                configuration,
                                                model,
                                                coordinate);
        }
    }

    private boolean isConditionColumnWithSpecialOperator(final BaseColumn baseColumn) {
        return baseColumn instanceof ConditionCol52
                &&
                NullEqualityOperator.contains(((ConditionCol52) baseColumn).getOperator());
    }

    public void newColumn(final GuidedDecisionTable52 model,
                          final HeaderMetaData headerMetaData,
                          final FactTypes factTypes,
                          final int columnIndex) throws
            BuildException {

        PortablePreconditions.checkNotNull("model",
                                           model);
        PortablePreconditions.checkNotNull("headerMetaData",
                                           headerMetaData);
        PortablePreconditions.checkNotNull("fieldTypes",
                                           factTypes);
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);

        final BuilderFactory builderFactory = new BuilderFactory(new VerifierColumnUtilities(model,
                                                                                             headerMetaData,
                                                                                             factTypes),
                                                                 index,
                                                                 model,
                                                                 headerMetaData,
                                                                 configuration);
        final Column column = builderFactory
                .getColumnBuilder()
                .with(columnIndex)
                .build();

        analyzer.newColumn(column);

        int rowIndex = 0;

        for (final List<DTCellValue52> row : model.getData()) {
            final BaseColumn baseColumn = model.getExpandedColumns()
                    .get(columnIndex);

            final Rule rule = index.getRules()
                    .where(Rule.index()
                                   .is(rowIndex))
                    .select()
                    .first();

            builderFactory.getCellBuilder()
                    .with(columnIndex)
                    .with(baseColumn)
                    .with(rule)
                    .with(row)
                    .build();

            rowIndex++;
        }

        analyzer.resetChecks();
        analyzer.analyze();
    }

    public void deleteColumns(final int firstColumnIndex,
                              final int numberOfColumns) {

        PortablePreconditions.checkNotNull("firstColumnIndex",
                                           firstColumnIndex);
        PortablePreconditions.checkNotNull("numberOfColumns",
                                           numberOfColumns);

        analyzer.deleteColumn(firstColumnIndex);

        analyzer.resetChecks();
        analyzer.analyze();
    }

    public void makeRule(final GuidedDecisionTable52 model,
                         final HeaderMetaData headerMetaData,
                         final FactTypes factTypes,
                         final int rowIndex) throws
            BuildException {

        PortablePreconditions.checkNotNull("model",
                                           model);
        PortablePreconditions.checkNotNull("fieldTypes",
                                           factTypes);
        PortablePreconditions.checkNotNull("index",
                                           rowIndex);

        final Rule rule = new BuilderFactory(new VerifierColumnUtilities(model,
                                                                         headerMetaData,
                                                                         factTypes),
                                             this.index,
                                             model,
                                             headerMetaData,
                                             configuration)
                .getRuleBuilder()
                .with(rowIndex)
                .build();

        analyzer.newRule(rule);
        analyzer.analyze();
    }
}
