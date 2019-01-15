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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.VerifierWebWorkerConnection;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

public class DecisionTableAnalyzer
        implements org.kie.workbench.common.services.verifier.reporting.client.analysis.DecisionTableAnalyzer<BaseColumn> {

    private final DTableUpdateManager updateManager;
    private final VerifierWebWorkerConnection analyzer;

    private final GuidedDecisionTable52 model;
    private final EventManager eventManager = new EventManager();

    public DecisionTableAnalyzer(final GuidedDecisionTable52 model,
                                 final DTableUpdateManager updateManager,
                                 final VerifierWebWorkerConnection connection) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.updateManager = PortablePreconditions.checkNotNull("updateManager",
                                                                updateManager);
        this.analyzer = PortablePreconditions.checkNotNull("connection",
                                                           connection);
    }

    @Override
    public void analyze(final List<Coordinate> updates) {
        updateManager.update(model,
                             convert(updates));
    }

    private List<org.drools.workbench.services.verifier.plugin.client.Coordinate> convert(final List<Coordinate> updates) {
        final ArrayList<org.drools.workbench.services.verifier.plugin.client.Coordinate> result = new ArrayList<>();

        for (final Coordinate coordinate : updates) {
            result.add(new org.drools.workbench.services.verifier.plugin.client.Coordinate(coordinate.getRow(),
                                                                                           coordinate.getCol()));
        }

        return result;
    }

    @Override
    public void deleteColumns(final int firstColumnIndex,
                              final int numberOfColumns) {
        updateManager.deleteColumns(firstColumnIndex,
                                    numberOfColumns);
    }

    @Override
    public void insertColumn(final BaseColumn baseColumn) {
        updateManager.newColumn(model,
                                getColumnIndex(baseColumn));
    }

    private int getColumnIndex(final BaseColumn baseColumn) {
        List<BaseColumn> cols = model.getExpandedColumns();
        final int indexOf = cols
                .indexOf(baseColumn);
        if (indexOf < 0) {
            if (baseColumn instanceof BRLConditionColumn) {

                for (final BaseColumn column : model.getExpandedColumns()) {
                    if (column instanceof BRLConditionVariableColumn) {
                        if (((BRLConditionColumn) baseColumn).getChildColumns()
                                .contains(column)) {
                            return model.getExpandedColumns()
                                    .indexOf(column);
                        }
                    }
                }

                throw new IllegalArgumentException("Could not find BRLConditionColumn: " + baseColumn.toString());
            }
            if (baseColumn instanceof BRLActionColumn) {

                for (final BaseColumn column : model.getExpandedColumns()) {
                    if (column instanceof BRLActionVariableColumn) {
                        if (((BRLActionColumn) baseColumn).getChildColumns()
                                .contains(column)) {
                            return model.getExpandedColumns()
                                    .indexOf(column);
                        }
                    }
                }

                throw new IllegalArgumentException("Could not find BRLActionColumn: " + baseColumn.toString());
            } else if (baseColumn instanceof BRLVariableColumn) {
                return model.getExpandedColumns()
                        .indexOf(model.getBRLColumn((BRLVariableColumn) baseColumn));
            } else {
                throw new IllegalArgumentException("Could not find baseColumn: " + baseColumn.toString());
            }
        } else {
            return indexOf;
        }
    }

    @Override
    public void updateColumns(final int amountOfRows) {
        if (eventManager.rowDeleted != null) {
            updateManager.removeRule(eventManager.rowDeleted);
        } else {
            updateManager.makeRule(model,
                                   eventManager.getNewIndex());
        }

        eventManager.clear();
    }

    @Override
    public void deleteRow(final int index) {
        eventManager.rowDeleted = index;
    }

    @Override
    public void appendRow() {
        eventManager.rowAppended = true;
    }

    @Override
    public void insertRow(final int index) {
        eventManager.rowInserted = index;
    }

    @Override
    public void activate() {
        analyzer.activate();
    }

    @Override
    public void terminate() {
        analyzer.terminate();
    }

    class EventManager {

        boolean rowAppended = false;
        Integer rowInserted = null;
        Integer rowDeleted = null;

        public void clear() {

            rowAppended = false;
            rowInserted = null;
            rowDeleted = null;
        }

        int getNewIndex() {
            if (eventManager.rowAppended) {
                return model.getData()
                        .size() - 1;
            } else if (eventManager.rowInserted != null) {
                return eventManager.rowInserted;
            }

            throw new IllegalStateException("There are no active updates");
        }
    }
}
