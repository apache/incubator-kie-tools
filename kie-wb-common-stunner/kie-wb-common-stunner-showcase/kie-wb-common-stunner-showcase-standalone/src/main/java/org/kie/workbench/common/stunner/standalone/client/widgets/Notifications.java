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

package org.kie.workbench.common.stunner.standalone.client.widgets;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.uberfire.client.mvp.UberView;

@Dependent
public class Notifications implements IsWidget {

    public interface View extends UberView<Notifications> {

        View setColumnSortHandler(final ColumnSortEvent.ListHandler<Notification> sortHandler);

        View addColumn(final com.google.gwt.user.cellview.client.Column<Notification, String> column,
                       final double pixelsWidth,
                       final String name);

        View removeColumn(final int index);

        int getColumnCount();

        View redraw();

        View clear();
    }

    private final View view;
    private final NotificationsObserver notificationsObserver;

    final ListDataProvider<Notification> logsProvider = new ListDataProvider<>();

    @Inject
    public Notifications(final View view,
                         final NotificationsObserver notificationsObserver) {
        this.view = view;
        this.notificationsObserver = notificationsObserver;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        notificationsObserver.onNotification(this::add);
        buildViewColumns();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void add(final Notification notification) {
        if (null != notification) {
            addLogEntry(notification);
            view.redraw();
        }
    }

    public void clear() {
        logsProvider.getList().clear();
        view.clear();
    }

    /*  ******************************************************************************************************
                                 VIEW CALLBACKS
     ****************************************************************************************************** */

    final ProvidesKey<Notification> KEY_PROVIDER = item -> item == null ? null : item.hashCode();

    /**
     * View callback for getting the list.
     */
    void addDataDisplay(final HasData<Notification> display) {
        logsProvider.addDataDisplay(display);
    }

    protected void buildViewColumns() {
        int columnCount = view.getColumnCount();
        while (columnCount > 0) {
            view.removeColumn(0);
            columnCount = view.getColumnCount();
        }
        // Attach a column sort handler to the ListDataProvider to sort the list.
        ColumnSortEvent.ListHandler<Notification> sortHandler = new ColumnSortEvent.ListHandler<Notification>(logsProvider.getList());
        view.setColumnSortHandler(sortHandler);
        // Log's type.
        final com.google.gwt.user.cellview.client.Column<Notification, String> typeColumn = createTypeColumn(sortHandler);
        if (typeColumn != null) {
            view.addColumn(typeColumn,
                           100,
                           "Severity");
        }
        // Log element's UUID.
        final com.google.gwt.user.cellview.client.Column<Notification, String> contextColumn = createContextColumn(sortHandler);
        if (contextColumn != null) {
            view.addColumn(contextColumn,
                           300,
                           "Context");
        }
        // Log's message.
        final com.google.gwt.user.cellview.client.Column<Notification, String> messageColumn = createMessageColumn(sortHandler);
        if (messageColumn != null) {
            view.addColumn(messageColumn,
                           600,
                           "Message");
        }
    }

    private void addLogEntry(final Notification entry) {
        List<Notification> logs = logsProvider.getList();
        logs.remove(entry);
        logs.add(entry);
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createTypeColumn(ColumnSortEvent.ListHandler<Notification> sortHandler) {
        // Log type..
        final Cell<String> typeCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> typeColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(typeCell) {
            @Override
            public String getValue(final Notification object) {
                return getNotificationTypeMessage(object);
            }
        };
        typeColumn.setSortable(true);
        sortHandler.setComparator(typeColumn,
                                  (o1, o2) -> o1.getType().compareTo(o2.getType()));
        return typeColumn;
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createContextColumn(ColumnSortEvent.ListHandler<Notification> sortHandler) {
        // Log element's context.
        final Cell<String> contextCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> contextColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(contextCell) {
            @Override
            public String getValue(final Notification object) {
                return getNotificationContextMessage(object);
            }
        };
        contextColumn.setSortable(false);
        return contextColumn;
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createMessageColumn(ColumnSortEvent.ListHandler<Notification> sortHandler) {
        // Log message.
        final Cell<String> messageCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> messageColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(messageCell) {
            @Override
            public String getValue(final Notification object) {
                return getNotificationSourceMessage(object);
            }
        };
        messageColumn.setSortable(false);
        return messageColumn;
    }

    @SuppressWarnings("unchecked")
    private String getNotificationSourceMessage(final Notification notification) {
        return (String) notification.getSource()
                .map(s -> notification.getMessage())
                .orElse("-- No source --");
    }

    @SuppressWarnings("unchecked")
    private String getNotificationContextMessage(final Notification notification) {
        return notification.getContext() != null ?
                notification.getContext().toString() :
                "-- No context --";
    }

    @SuppressWarnings("unchecked")
    private String getNotificationTypeMessage(final Notification notification) {
        return notification.getType() != null ?
                "[" + notification.getType().name() + "]" :
                "[ ]";
    }
}
