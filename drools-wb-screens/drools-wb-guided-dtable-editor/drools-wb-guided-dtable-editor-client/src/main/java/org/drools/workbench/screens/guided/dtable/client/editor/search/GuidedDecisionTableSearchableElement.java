/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.uberfire.mvp.Command;

public class GuidedDecisionTableSearchableElement implements Searchable {

    private String value;

    private Integer row;

    private Integer column;

    private GuidedDecisionTableModellerView.Presenter modeller;

    private GuidedDecisionTableGridHighlightHelper highlightHelper;

    private GuidedDecisionTableView widget;

    private GuidedDecisionTable52 model;

    @Override
    public boolean matches(final String text) {
        return getValue().toUpperCase().contains(text.toUpperCase());
    }

    @Override
    public Command onFound() {
        return () -> highlightHelper.highlight(getRow(), getColumn(), getWidget(), getModeller());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GuidedDecisionTableSearchableElement that = (GuidedDecisionTableSearchableElement) o;

        if (!value.equals(that.value)) {
            return false;
        }
        if (!row.equals(that.row)) {
            return false;
        }
        return column.equals(that.column);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = ~~result;
        result = 31 * result + row.hashCode();
        result = ~~result;
        result = 31 * result + column.hashCode();
        result = ~~result;
        return result;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public void setColumn(final int column) {
        this.column = column;
    }

    public void setModeller(final GuidedDecisionTableModellerView.Presenter modeller) {
        this.modeller = modeller;
    }

    void setHighlightHelper(final GuidedDecisionTableGridHighlightHelper highlightHelper) {
        this.highlightHelper = highlightHelper;
    }

    public String getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public GuidedDecisionTableGridHighlightHelper getHighlightHelper() {
        return highlightHelper;
    }

    public GuidedDecisionTableModellerView.Presenter getModeller() {
        return modeller;
    }

    public GuidedDecisionTable52 getModel() {
        return model;
    }

    public void setModel(final GuidedDecisionTable52 model) {
        this.model = model;
    }

    public void setWidget(final GuidedDecisionTableView widget) {
        this.widget = widget;
    }

    public GuidedDecisionTableView getWidget() {
        return widget;
    }
}
