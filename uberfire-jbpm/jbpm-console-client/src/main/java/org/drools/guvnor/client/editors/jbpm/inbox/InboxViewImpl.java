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

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import org.jboss.bpm.console.client.model.TaskSummary;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import java.util.Comparator;
import java.util.Set;
import org.drools.guvnor.client.editors.jbpm.inbox.events.InboxAction;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class InboxViewImpl extends Composite implements InboxPresenter.InboxView {

    @Inject
    private UiBinder<Widget, InboxViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private InboxPresenter presenter;
    @UiField
    public Button addTaskButton;
    @UiField
    public Button claimTaskButton;
    @UiField
    public Button refreshTasksButton;
    @UiField
    public Button startTaskButton;
    @UiField
    public Button completeTaskButton;
    @UiField
    public TextBox userText;
    @UiField
    public TextBox groupText;
    @UiField(provided = true)
    public DataGrid<TaskSummary> myTaskListGrid;
    @UiField(provided = true)
    public DataGrid<TaskSummary> myGroupTaskListGrid;
    
    @UiField(provided = true)
    public SimplePager pager;
    
    @UiField(provided = true)
    public SimplePager pagerGroup;
    
    private Set<TaskSummary> selectedTasks;
    private Set<TaskSummary> selectedGroupTasks;
    
    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
        public Object getKey(TaskSummary item) {
            return item == null ? null : item.getId();
        }
    };

    @PostConstruct
    public void init() {


        myTaskListGrid = new DataGrid<TaskSummary>(KEY_PROVIDER);
        myTaskListGrid.setWidth("100%");
        myTaskListGrid.setHeight("300px");

        // Set the message to display when the table is empty.
        myTaskListGrid.setEmptyTableWidget(new Label("Hooray you don't have any pending Task!!"));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        ListHandler<TaskSummary> sortHandler =
                new ListHandler<TaskSummary>(presenter.getDataProvider().getList());
        myTaskListGrid.addColumnSortHandler(sortHandler);
        myTaskListGrid.setPageSize(10);
        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(myTaskListGrid);
        pager.setPageSize(10);

        // Add a selection model so we can select cells.
        final MultiSelectionModel<TaskSummary> selectionModel =
                new MultiSelectionModel<TaskSummary>(KEY_PROVIDER);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {

                selectedTasks = selectionModel.getSelectedSet();
               
            }
        });

        myTaskListGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
                .<TaskSummary>createCheckboxManager());

        initTableColumns(selectionModel, sortHandler);
        
        myGroupTaskListGrid = new DataGrid<TaskSummary>(KEY_PROVIDER);
        myGroupTaskListGrid.setWidth("100%");
        myGroupTaskListGrid.setHeight("300px");

        // Set the message to display when the table is empty.
        myGroupTaskListGrid.setEmptyTableWidget(new Label("Hooray you don't have any Group Task to Claim!!"));
        myTaskListGrid.setPageSize(10);
        // Create a Pager to control the table.
        SimplePager.Resources pagerGroupResources = GWT.create(SimplePager.Resources.class);
        pagerGroup = new SimplePager(TextLocation.CENTER, pagerGroupResources, false, 0, true);
        pagerGroup.setDisplay(myTaskListGrid);
        pagerGroup.setPageSize(10);
        
        initWidget(uiBinder.createAndBindUi(this));

        presenter.addDataDisplay(myTaskListGrid);
    }

    @UiHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        presenter.addTask(userText.getText(), groupText.getText());
    }

    @UiHandler("refreshTasksButton")
    public void refreshTasksButton(ClickEvent e) {
        presenter.refreshTasks(userText.getText());
    }

    @UiHandler("startTaskButton")
    public void startTaskButton(ClickEvent e) {
        presenter.startTasks(selectedTasks, userText.getText());
        
       
    }

    @UiHandler("completeTaskButton")
    public void completeTaskButton(ClickEvent e) {
       presenter.completeTasks(selectedTasks, userText.getText());
       
    }
    
     @UiHandler("claimTaskButton")
    public void claimTaskButton(ClickEvent e) {
       presenter.claimTasks(selectedGroupTasks, userText.getText());
       
    }

    private void initTableColumns(final SelectionModel<TaskSummary> selectionModel,
            ListHandler<TaskSummary> sortHandler) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.


        Column<TaskSummary, Boolean> checkColumn =
                new Column<TaskSummary, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(TaskSummary object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        myTaskListGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        myTaskListGrid.setColumnWidth(checkColumn, 40, Unit.PCT);

        // First name.
        Column<TaskSummary, Number> taskIdColumn =
                new Column<TaskSummary, Number>(new NumberCell()) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getId();
                    }
                };
        taskIdColumn.setSortable(true);
        sortHandler.setComparator(taskIdColumn, new Comparator<TaskSummary>() {
            public int compare(TaskSummary o1, TaskSummary o2) {
                return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
            }
        });
        myTaskListGrid.addColumn(taskIdColumn, "Task Id");
        taskIdColumn.setFieldUpdater(new FieldUpdater<TaskSummary, Number>() {
            public void update(int index, TaskSummary object, Number value) {
                // Called when the user changes the value.
                presenter.refreshData();
            }
        });
        myTaskListGrid.setColumnWidth(taskIdColumn, 40, Unit.PCT);

        // Task name.
        Column<TaskSummary, String> taskNameColumn =
                new Column<TaskSummary, String>(new EditTextCell()) {
                    @Override
                    public String getValue(TaskSummary object) {
                        return object.getName();
                    }
                };
        taskNameColumn.setSortable(true);
        sortHandler.setComparator(taskNameColumn, new Comparator<TaskSummary>() {
            public int compare(TaskSummary o1, TaskSummary o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        myTaskListGrid.addColumn(taskNameColumn, "Task Name");
        taskNameColumn.setFieldUpdater(new FieldUpdater<TaskSummary, String>() {
            public void update(int index, TaskSummary object, String value) {
                // Called when the user changes the value.
//                
                presenter.refreshData();
            }
        });
        myTaskListGrid.setColumnWidth(taskNameColumn, 130, Unit.PCT);

        // Task priority.
        Column<TaskSummary, Number> taskPriorityColumn =
                new Column<TaskSummary, Number>(new NumberCell()) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getPriority();
                    }
                };
        taskPriorityColumn.setSortable(true);
        sortHandler.setComparator(taskPriorityColumn, new Comparator<TaskSummary>() {
            public int compare(TaskSummary o1, TaskSummary o2) {
                return Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
            }
        });
        myTaskListGrid.addColumn(taskPriorityColumn, new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Priority")));
        myTaskListGrid.setColumnWidth(taskPriorityColumn, 40, Unit.PCT);

         // Status.
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        sortHandler.setComparator(statusColumn, new Comparator<TaskSummary>() {
            public int compare(TaskSummary o1, TaskSummary o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });

        myTaskListGrid.addColumn(statusColumn, new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Status")));
        myTaskListGrid.setColumnWidth(statusColumn, 50, Unit.PCT);
        
        // User.
        Column<TaskSummary, String> userColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getActualOwner();
            }
        };
        userColumn.setSortable(true);
        sortHandler.setComparator(userColumn, new Comparator<TaskSummary>() {
            public int compare(TaskSummary o1, TaskSummary o2) {
                return o1.getActualOwner().compareTo(o2.getActualOwner());
            }
        });

        myTaskListGrid.addColumn(userColumn, new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Actual Owner")));
        myTaskListGrid.setColumnWidth(userColumn, 50, Unit.PCT);


        
        
        // Description.
        Column<TaskSummary, String> descriptionColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable(true);
        

        myTaskListGrid.addColumn(descriptionColumn, new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Description")));
        myTaskListGrid.setColumnWidth(descriptionColumn, 150, Unit.PCT);
        
    }
}
