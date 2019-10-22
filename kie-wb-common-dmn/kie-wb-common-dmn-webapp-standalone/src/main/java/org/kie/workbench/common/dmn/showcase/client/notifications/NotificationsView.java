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
package org.kie.workbench.common.dmn.showcase.client.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;

public class NotificationsView extends Composite implements Notifications.View {

    interface ViewBinder extends UiBinder<Widget, NotificationsView> {

    }

    private static ProvidesKey<Notification> KEY_PROVIDER = item -> item == null ? null : item.hashCode();

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    Row logsRow;

    @UiField(provided = true)
    CellTable<Notification> logsGrid;

    @UiField(provided = true)
    SimplePager logsGridPager;

    Notifications presenter;

    @Override
    public void init(final Notifications presenter) {
        this.presenter = presenter;
        initGrid();
        initWidget(uiBinder.createAndBindUi(this));
    }

    private void initGrid() {
        // Init the logs grid.
        logsGrid = new CellTable<>(KEY_PROVIDER);
        logsGrid.setWidth("100%",
                          true);

        // Do not refresh the headers and footers every time the data is updated.
        logsGrid.setAutoHeaderRefreshDisabled(true);
        logsGrid.setAutoFooterRefreshDisabled(true);

        // Create a Pager to control the table.
        final SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        logsGridPager = new SimplePager(SimplePager.TextLocation.CENTER,
                                        pagerResources,
                                        false,
                                        0,
                                        true);

        logsGridPager.setDisplay(logsGrid);
        presenter.addDataDisplay(logsGrid);
    }

    @Override
    public Notifications.View setColumnSortHandler(final ColumnSortEvent.ListHandler<Notification> sortHandler) {
        logsGrid.addColumnSortHandler(sortHandler);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Notifications.View addColumn(final Column<Notification, String> column,
                                        final String name) {
        logsGrid.addColumn(column,
                           name);
        logsGrid.setColumnWidth(column,
                                5,
                                Style.Unit.PCT);
        return this;
    }

    @Override
    public Notifications.View removeColumn(final int index) {
        logsGrid.removeColumn(index);
        return this;
    }

    @Override
    public int getColumnCount() {
        return logsGrid.getColumnCount();
    }

    @Override
    public Notifications.View redraw() {
        logsGrid.redraw();
        return this;
    }

    @Override
    public Notifications.View clear() {
        return this;
    }
}
