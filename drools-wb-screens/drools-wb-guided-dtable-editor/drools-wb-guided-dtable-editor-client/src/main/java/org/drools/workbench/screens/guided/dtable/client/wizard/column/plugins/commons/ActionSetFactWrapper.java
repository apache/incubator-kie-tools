/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;

public class ActionSetFactWrapper implements ActionWrapper {

    private final ActionSetFieldCol52 actionCol52;

    private final BaseDecisionTableColumnPlugin plugin;

    public ActionSetFactWrapper(final BaseDecisionTableColumnPlugin plugin) {
        this.plugin = plugin;
        this.actionCol52 = newActionSetField();
    }

    private ActionSetFieldCol52 newActionSetField() {
        final GuidedDecisionTable52.TableFormat tableFormat = plugin.getPresenter().getModel().getTableFormat();

        switch (tableFormat) {
            case EXTENDED_ENTRY:
                return new ActionSetFieldCol52();
            case LIMITED_ENTRY:
                return new LimitedEntryActionSetFieldCol52();
            default:
                throw new UnsupportedOperationException("Unsupported table format: " + tableFormat);
        }
    }

    @Override
    public boolean isInsertLogical() {
        return false;
    }

    @Override
    public void setInsertLogical(final boolean insertLogical) {
        // empty
    }

    @Override
    public boolean isUpdate() {
        return getActionCol52().isUpdate();
    }

    @Override
    public void setUpdate(final boolean update) {
        getActionCol52().setUpdate(update);
    }

    @Override
    public DTCellValue52 getDefaultValue() {
        return getActionCol52().getDefaultValue();
    }

    @Override
    public void setDefaultValue(final DTCellValue52 defaultValue) {
        getActionCol52().setDefaultValue(defaultValue);
    }

    @Override
    public String getBoundName() {
        return getActionCol52().getBoundName();
    }

    @Override
    public void setBoundName(final String boundName) {
        getActionCol52().setBoundName(boundName);
    }

    @Override
    public String getFactField() {
        return getActionCol52().getFactField();
    }

    @Override
    public void setFactField(String factField) {
        getActionCol52().setFactField(factField);
    }

    @Override
    public String getFactType() {
        return "";
    }

    @Override
    public void setFactType(final String factType) {
        // empty
    }

    @Override
    public String getHeader() {
        return getActionCol52().getHeader();
    }

    @Override
    public void setHeader(String header) {
        getActionCol52().setHeader(header);
    }

    @Override
    public String getType() {
        return getActionCol52().getType();
    }

    @Override
    public void setType(final String type) {
        getActionCol52().setType(type);
    }

    @Override
    public String getValueList() {
        return getActionCol52().getValueList();
    }

    @Override
    public void setValueList(final String valueList) {
        getActionCol52().setValueList(valueList);
    }

    @Override
    public ActionSetFieldCol52 getActionCol52() {
        return actionCol52;
    }

    private GuidedDecisionTableView.Presenter presenter() {
        return plugin.getPresenter();
    }
}
