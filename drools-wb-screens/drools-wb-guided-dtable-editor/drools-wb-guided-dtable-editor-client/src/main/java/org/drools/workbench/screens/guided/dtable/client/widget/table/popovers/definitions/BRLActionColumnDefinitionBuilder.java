/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class BRLActionColumnDefinitionBuilder extends BaseColumnDefinitionBuilder {

    @Inject
    public BRLActionColumnDefinitionBuilder(final Caller<GuidedDecisionTableEditorService> service) {
        super(service);
    }

    @Override
    public Class getSupportedColumnType() {
        return BRLActionVariableColumn.class;
    }

    @Override
    public void generateDefinition(final GuidedDecisionTableView.Presenter dtPresenter,
                                   final BaseColumn column,
                                   final Callback<String> afterGenerationCallback) {
        if (!(column instanceof BRLActionVariableColumn)) {
            return;
        }

        final GuidedDecisionTable52 existingModel = dtPresenter.getModel();
        final BRLActionVariableColumn brlVariableColumn = (BRLActionVariableColumn) column;
        final BRLActionColumn brlColumn = existingModel.getBRLColumn(brlVariableColumn);

        final GuidedDecisionTable52 partialModel = new GuidedDecisionTable52();
        final ColumnUtilities columnUtilities = new ColumnUtilities(existingModel,
                                                                    dtPresenter.getDataModelOracle());
        partialModel.getActionCols().add(brlColumn);
        partialModel.getData().add(makeRowData(columnUtilities,
                                               brlColumn));

        generateDefinitionOnServer(partialModel,
                                   dtPresenter.getCurrentPath(),
                                   (String drl) -> afterGenerationCallback.callback(getRHS(drl)));
    }

    private List<DTCellValue52> makeRowData(final ColumnUtilities columnUtilities,
                                            final BRLActionColumn brlColumn) {
        final List<DTCellValue52> row = new ArrayList<>();
        row.add(new DTCellValue52(1));
        row.add(new DTCellValue52(""));
        row.add(new DTCellValue52("desc"));

        for (BRLActionVariableColumn brlVariableColumn : brlColumn.getChildColumns()) {
            final DataType.DataTypes dataType = columnUtilities.getDataType(brlVariableColumn);
            row.add(makeCell(dataType));
        }

        return row;
    }
}
