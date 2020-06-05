/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

@Dependent
public class RuleNameColumnConverter extends BaseColumnConverterImpl {

    private static final double MIN_WIDTH = 150.0;

    @Override
    public boolean handles(final BaseColumn column) {
        return column instanceof RuleNameColumn;
    }

    @Override
    public GridColumn<?> convertColumn(final BaseColumn column,
                                       final GuidedDecisionTablePresenter.Access access,
                                       final GuidedDecisionTableView gridWidget) {
        final GridColumn<?> uiColumn = newRuleNameColumn(makeHeaderMetaData(column),
                                                         Math.max(column.getWidth(),
                                                                  MIN_WIDTH),
                                                         true,
                                                         true,
                                                         access,
                                                         gridWidget);
        uiColumn.setMovable(false);
        uiColumn.setFloatable(true);
        uiColumn.setVisible(!column.isHideColumn());
        uiColumn.setMinimumWidth(MIN_WIDTH);
        return uiColumn;
    }

    @Override
    public List<GridColumn.HeaderMetaData> makeHeaderMetaData(final BaseColumn column) {
        return Collections.singletonList(new BaseHeaderMetaData(GuidedDecisionTableConstants.INSTANCE.RuleName(),
                                                                RuleNameColumn.class.getName()));
    }
}
