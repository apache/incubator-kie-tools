/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.editors.jbpm.inbox;

import com.google.gwt.core.client.GWT;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import javax.enterprise.event.Event;
import org.drools.guvnor.client.editors.jbpm.inbox.events.AddTaskUIEvent;
import org.drools.guvnor.client.editors.jbpm.inbox.events.InboxAction;
import org.drools.guvnor.client.editors.jbpm.inbox.events.RefreshTasksUIEvent;

@Dependent
public class InboxViewImpl extends Composite implements InboxPresenter.InboxView {

    @Inject
    private UiBinder<Widget, InboxViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    @UiField
    public HTMLPanel titlePanel;
    @UiField
    public HTMLPanel footerPanel;
    @UiField
    public Button addTaskButton;
    @UiField
    public Button refreshTasksButton;
    @UiField(provided = true)
    public DataGrid<TaskSummary> dataGrid;
    @UiField(provided = true)
    public SimplePager pager;
    @Inject
    private Event<InboxAction> inboxEvents;
    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
        public Object getKey(TaskSummary item) {
            return item == null ? null : item.getId();
        }
    };

    @PostConstruct
    public void init() {
        dataGrid = new DataGrid<TaskSummary>(KEY_PROVIDER);
        dataGrid.setWidth("100%");

        // Set the message to display when the table is empty.
        dataGrid.setEmptyTableWidget(new Label("HI!!"));

        // Attach a column sort handler to the ListDataProvider to sort the list.
//        ListHandler<TaskSummary> sortHandler =
//                new ListHandler<TaskSummary>(ContactDatabase.get().getDataProvider().getList());
//        dataGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        // Add a selection model so we can select cells.
        final SelectionModel<TaskSummary> selectionModel =
                new MultiSelectionModel<TaskSummary>(KEY_PROVIDER);
        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
                .<TaskSummary>createCheckboxManager());


        initWidget(uiBinder.createAndBindUi(this));



    }

    @UiHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        inboxEvents.select(new AddTaskUIEvent() {
            @Override
            public Class annotationType() {
                return AddTaskUIEvent.class;
            }
        })
                .fire(new InboxAction());

    }

    @UiHandler("refreshTasksButton")
    public void refreshTasksButton(ClickEvent e) {
        inboxEvents.select(new RefreshTasksUIEvent() {
            @Override
            public Class annotationType() {
                return RefreshTasksUIEvent.class;
            }
        })
                .fire(new InboxAction());
    }

    public DataGrid<TaskSummary> getDataGrid() {
        return dataGrid;
    }
}
