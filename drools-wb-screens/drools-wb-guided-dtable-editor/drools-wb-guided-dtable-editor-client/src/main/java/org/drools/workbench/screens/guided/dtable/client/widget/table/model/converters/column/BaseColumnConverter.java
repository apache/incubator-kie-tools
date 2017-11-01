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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

/**
 * Converter for different BaseUiColumn types
 */
public interface BaseColumnConverter {

    /**
     * Relative priority of converter; higher priorities will be handled first
     * @return
     */
    int priority();

    /**
     * Indicate whether this BaseColumnConverter supports a given BaseUiColumn
     * @param column
     * @return
     */
    boolean handles( final BaseColumn column );

    /**
     * Initialise a BaseColumnConverter for a specific GridWidget
     * @param model The model
     * @param oracle DataModelOracle to assist with drop-downs
     * @param columnUtilities Utilities to support data-types
     * @param presenter GuidedDecisionTablePresenter for the table
     */
    void initialise( final GuidedDecisionTable52 model,
                     final AsyncPackageDataModelOracle oracle,
                     final ColumnUtilities columnUtilities,
                     final GuidedDecisionTableView.Presenter presenter );

    /**
     * Instantiate a UI Column for the Model BaseColumn
     * @param column BaseColumn to be converted
     * @param access Access state of column
     * @param gridWidget The GridWidget to which this column is associated.
     * @return
     */
    GridColumn<?> convertColumn( final BaseColumn column,
                                 final GuidedDecisionTablePresenter.Access access,
                                 final GuidedDecisionTableView gridWidget );

    /**
     * Return meta-data for the columns' header
     * @param column BaseColumn to be converted
     * @return
     */
    List<GridColumn.HeaderMetaData> makeHeaderMetaData( final BaseColumn column );

}