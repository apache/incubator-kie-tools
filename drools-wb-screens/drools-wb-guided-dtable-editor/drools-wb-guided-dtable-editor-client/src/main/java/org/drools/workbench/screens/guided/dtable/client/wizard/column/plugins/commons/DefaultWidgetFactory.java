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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasValueOptionsPage;
import org.kie.soup.project.datamodel.oracle.DataType;

public class DefaultWidgetFactory<T extends BaseDecisionTableColumnPlugin & HasValueOptionsPage> extends BaseWidgetFactory<T> {

    public DefaultWidgetFactory(final T plugin) {
        super(plugin);
    }

    public IsWidget create() {
        if (getDefaultValue() == null) {
            setDefaultValue();
        }

        final DTCellValue52 defaultValue = prepareDefaultValue();

        return getWidget(defaultValue);
    }

    private Widget getWidget(final DTCellValue52 defaultValue) {
        final DTColumnConfig52 column = getPlugin().editingCol();
        final Pattern52 pattern = getPlugin().editingPattern();

        if (column instanceof ActionSetFieldCol52) {
            final ActionSetFieldCol52 col52 = (ActionSetFieldCol52) column;

            return factory().getWidget(pattern,
                                       col52,
                                       defaultValue);
        } else if (column instanceof ConditionCol52) {
            final ConditionCol52 col52 = (ConditionCol52) column;

            return factory().getWidget(pattern,
                                       col52,
                                       defaultValue);
        } else if (column instanceof ActionInsertFactCol52) {
            final ActionInsertFactCol52 col52 = (ActionInsertFactCol52) column;

            return factory().getWidget(col52,
                                       defaultValue);
        }

        throw new UnsupportedOperationException("The column type is not supported by the 'DefaultWidget'");
    }

    private void setDefaultValue() {
        final DTColumnConfig52 column = getPlugin().editingCol();

        column.setDefaultValue(makeNewValue());
    }

    private DTCellValue52 prepareDefaultValue() {
        final CellUtilities cellUtilities = new CellUtilities();
        final DTCellValue52 defaultValue = getDefaultValue();

        cellUtilities.convertDTCellValueType(dataType(),
                                             defaultValue);
        return defaultValue;
    }

    private DTCellValue52 getDefaultValue() {
        return getPlugin().editingCol().getDefaultValue();
    }

    private DataType.DataTypes dataType() {
        final ColumnUtilities columnUtilities = columnUtilities();
        final DTColumnConfig52 column = getPlugin().editingCol();

        if (column instanceof ActionSetFieldCol52) {
            return columnUtilities.getDataType(getPlugin().editingPattern(),
                                               (ActionSetFieldCol52) column);
        } else if (column instanceof ConditionCol52) {
            return columnUtilities.getDataType(getPlugin().editingPattern(),
                                               (ConditionCol52) column);
        } else {
            return columnUtilities.getDataType(column);
        }
    }

    private ColumnUtilities columnUtilities() {
        final GuidedDecisionTableView.Presenter presenter = getPlugin().getPresenter();

        return new ColumnUtilities(presenter.getModel(),
                                   presenter.getDataModelOracle());
    }

    private DTCellValue52 makeNewValue() {
        final DTColumnConfig52 column = getPlugin().editingCol();
        final Pattern52 pattern = getPlugin().editingPattern();

        if (column instanceof ActionSetFieldCol52) {
            return factory().makeNewValue(pattern,
                                          (ActionSetFieldCol52) column);
        } else if (column instanceof ConditionCol52) {
            return factory().makeNewValue(pattern,
                                          (ConditionCol52) column);
        } else {
            return factory().makeNewValue(column);
        }
    }
}
