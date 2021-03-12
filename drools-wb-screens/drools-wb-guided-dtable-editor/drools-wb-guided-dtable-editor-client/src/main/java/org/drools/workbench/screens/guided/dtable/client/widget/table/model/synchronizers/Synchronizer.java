/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.MetaData;

public interface Synchronizer<A extends MetaData, U extends MetaData, D extends MetaData, MC extends MetaData, MR extends MetaData> {

    interface MetaData {

    }

    int priority();

    void initialise(final GuidedDecisionTable52 model,
                    final GuidedDecisionTableUiModel uiModel,
                    final CellUtilities cellUtilities,
                    final ColumnUtilities columnUtilities,
                    final GridWidgetCellFactory gridWidgetCellFactory,
                    final GridWidgetColumnFactory gridWidgetColumnFactory,
                    final GuidedDecisionTableView view,
                    final BRLRuleModel rm,
                    final EventBus eventBus,
                    final GuidedDecisionTablePresenter.Access access);

    boolean handlesAppend(final MetaData metaData) throws VetoException;

    void append(final A metaData) throws VetoException;

    boolean handlesInsert(final MetaData metaData) throws VetoException;

    void insert(final A metaData) throws VetoException;

    boolean handlesUpdate(final MetaData metaData) throws VetoException;

    List<BaseColumnFieldDiff> update(final U originalMetaData,
                                     final U editedMetaData) throws VetoException;

    boolean handlesDelete(final MetaData metaData) throws VetoException;

    void delete(final D metaData) throws VetoException;

    boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException;

    void moveColumnsTo(final List<MC> metaData) throws VetoException;

    boolean handlesMoveRowsTo(final List<? extends MetaData> metaData) throws VetoException;

    void moveRowsTo(final List<MR> metaData) throws VetoException;

    boolean handlesSort() throws VetoException;

    void sort(final List<Integer> sortOrder) throws VetoException;
}
