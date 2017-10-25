/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.m2repo.client.widgets;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import org.guvnor.m2repo.client.editor.JarDetailPopup;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

@Dependent
public class ArtifactListViewImpl extends Composite implements ArtifactListView {

    interface ArtifactListViewImplWidgetBinder
            extends
            UiBinder<Widget, ArtifactListViewImpl> {

    }

    private ArtifactListViewImplWidgetBinder uiBinder = GWT.create(ArtifactListViewImplWidgetBinder.class);

    @UiField(provided = true)
    final PagedTable<JarListPageRow> dataGrid = new PagedTable<JarListPageRow>();

    protected ArtifactListPresenter presenter;

    public ArtifactListViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setup(final ColumnType... _columns) {
        final Set<ColumnType> columns = new HashSet<ColumnType>(Arrays.asList(_columns));
        dataGrid.setEmptyTableCaption(M2RepoEditorConstants.INSTANCE.NoArtifactAvailable());

        if (columns.contains(ColumnType.NAME)) {
            final Column<JarListPageRow, String> nameColumn = new Column<JarListPageRow, String>(new TextCell()) {
                @Override
                public String getValue(JarListPageRow row) {
                    return row.getName();
                }
            };
            nameColumn.setSortable(true);
            nameColumn.setDataStoreName(JarListPageRequest.COLUMN_NAME);
            addColumn(nameColumn,
                      M2RepoEditorConstants.INSTANCE.Name());
        }

        if (columns.contains(ColumnType.GAV)) {
            final Column<JarListPageRow, String> gavColumn = new Column<JarListPageRow, String>(new TextCell()) {
                @Override
                public String getValue(JarListPageRow row) {
                    return row.getGav().toString();
                }
            };
            gavColumn.setSortable(true);
            gavColumn.setDataStoreName(JarListPageRequest.COLUMN_GAV);
            addColumn(gavColumn,
                      M2RepoEditorConstants.INSTANCE.GAV());
        }

        if (columns.contains(ColumnType.LAST_MODIFIED)) {
            final Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
                @Override
                public Date getValue(JarListPageRow row) {
                    return row.getLastModified();
                }
            };
            lastModifiedColumn.setSortable(true);
            lastModifiedColumn.setDataStoreName(JarListPageRequest.COLUMN_LAST_MODIFIED);
            addColumn(lastModifiedColumn,
                      M2RepoEditorConstants.INSTANCE.LastModified(),
                      false);
        }

        dataGrid.addColumnSortHandler(new ColumnSortEvent.AsyncHandler(dataGrid));
    }

    @Override
    public void setContentHeight(String s) {
        dataGrid.setHeight(s);
    }

    @Override
    public void init(final ArtifactListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addColumn(final Column<JarListPageRow, ?> column,
                          final String caption) {
        dataGrid.addColumn(column,
                           caption);
    }

    @Override
    public void addColumn(final Column<JarListPageRow, ?> column,
                          final String caption,
                          final boolean visible) {
        dataGrid.addColumn(column,
                           caption,
                           visible);
    }

    @Override
    public void addColumn(final Column<JarListPageRow, ?> column,
                          final String caption,
                          final double width,
                          final Style.Unit unit) {
        dataGrid.addColumn(column,
                           caption);
        dataGrid.setColumnWidth(column,
                                width,
                                unit);
    }

    @Override
    public void addColumn(final Column<JarListPageRow, ?> column,
                          final String caption,
                          final boolean visible,
                          final double width,
                          final Style.Unit unit) {
        dataGrid.addColumn(column,
                           caption,
                           visible);
        dataGrid.setColumnWidth(column,
                                width,
                                unit);
    }

    @Override
    public void showPom(String pomText) {
        JarDetailPopup popup = new JarDetailPopup(pomText);
        popup.show();
    }

    @Override
    public HasData<JarListPageRow> getDisplay() {
        return dataGrid;
    }

    @Override
    public ColumnSortList getColumnSortList() {
        return dataGrid.getColumnSortList();
    }

    @Override
    public String getRefreshNotificationMessage() {
        return M2RepoEditorConstants.INSTANCE.RefreshedSuccessfully();
    }
}
