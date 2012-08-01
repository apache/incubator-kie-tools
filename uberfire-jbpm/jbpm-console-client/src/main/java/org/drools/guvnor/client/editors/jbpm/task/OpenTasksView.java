/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.guvnor.client.editors.jbpm.task;

import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.common.DataDriven;
import org.drools.guvnor.client.common.PagingCallback;
import org.drools.guvnor.client.common.PagingPanel;
import org.drools.guvnor.client.util.SimpleDateFormat;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "OpenTasksView")
public class OpenTasksView extends AbstractTaskList implements IsWidget, DataDriven {

    public final static String ID = OpenTasksView.class.getName();

    //JLIU
    //private TaskDetailView detailsView;

    //private final ApplicationContext appContext;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    private PagingPanel pagingPanel;

    private DockPanel panel;

    private static boolean actionSetup = false;

    public OpenTasksView(/*BpmConsoleClientFactory clientFactory*/) {
/*        this.appContext = clientFactory.getApplicationContext();
        this.controller = clientFactory.getController();*/
    }

/*    public static void registerCommonActions(ApplicationContext applicationContext, Controller controller) {
        if (!actionSetup) {
            // create and register actions
            controller.addAction(LoadTasksAction.ID, new LoadTasksAction(applicationContext));
            controller.addAction(LoadTasksParticipationAction.ID, new LoadTasksParticipationAction(applicationContext));
            controller.addAction(ClaimTaskAction.ID, new ClaimTaskAction(applicationContext));
            controller.addAction(ReleaseTaskAction.ID, new ReleaseTaskAction(applicationContext));
            controller.addAction(UpdateDetailsAction.ID, new UpdateDetailsAction());
            controller.addAction(AssignTaskAction.ID, new AssignTaskAction(applicationContext));
            controller.addAction(ReloadAllTaskListsAction.ID, new ReloadAllTaskListsAction(applicationContext));

            actionSetup = true;
        }
    }*/

    public Widget asWidget() {
        panel = new DockPanel();

        initialize();

        //registerCommonActions(appContext, controller);

        // ----

        /*TaskDetailView assignedDetailView = new TaskDetailView(false);
   controller.addView("AssignedDetailView", assignedDetailView);
   assignedDetailView.initialize();
   registerView(controller, tabPanel, AssignedTasksView.ID, new AssignedTasksView(appContext, assignedDetailView));*/

        //JLIU
/*        controller.addView(OpenTasksView.ID, this);

        // ----

        panel.add(detailsView, DockPanel.SOUTH);*/
        panel.add(taskList, DockPanel.CENTER);

        return panel;
    }

    public void initialize() {
        if (!isInitialized) {
            taskList = new VerticalPanel();

            listBox =
                    new CustomizableListBox<TaskRef>(
                            new CustomizableListBox.ItemFormatter<TaskRef>() {

                                public String format(TaskRef taskRef) {
                                    String result = "";

                                    result += String.valueOf(taskRef.getPriority());

                                    result += " ";

                                    result += taskRef.getProcessId();

                                    result += " ";

                                    result += taskRef.getName();

                                    result += " ";

                                    result += String.valueOf(taskRef.getCurrentState());

                                    result += " ";

                                    result += taskRef.getDueDate() != null ? dateFormat.format(taskRef.getDueDate()) : "";

                                    return result;
                                }
                            }
                    );

            listBox.setFirstLine("Priority, Process, Task Name, Status, Due Date");

            //JLIU
/*            listBox.addChangeHandler(
                    new ChangeHandler() {
                        public void onChange(ChangeEvent event) {
                            TaskRef task = getSelection(); // first call always null?
                            if (task != null) {
                                controller.handleEvent(
                                        new Event(UpdateDetailsAction.ID, new DetailViewEvent("OpenDetailView", task))
                                );
                            }
                        }
                    }
            );*/

            // toolbar
            final VerticalPanel toolBox = new VerticalPanel();
            toolBox.setSpacing(5);

            final MenuBar toolBar = new MenuBar();
            toolBar.addItem(
                    "Refresh",
                    new Command() {
                        public void execute() {
                            reload();
                        }
                    }
            );

            toolBar.addItem(
                    "Claim",
                    new Command() {
                        public void execute() {
                        	//JLIU
/*                            TaskRef selection = getSelection();

                            if (selection != null) {
                                controller.handleEvent(
                                        new Event(
                                                ClaimTaskAction.ID,
                                                new TaskIdentityEvent(appContext.getAuthentication().getUsername(), selection)
                                        )
                                );
                            } else {
                                Window.alert("Missing selection. Please select a task");
                            }*/
                        }
                    }
            );

            toolBox.add(toolBar);

            this.taskList.add(toolBox);
            this.taskList.add(listBox);

            pagingPanel = new PagingPanel(
                    new PagingCallback() {
                        public void rev() {
                            renderUpdate();
                        }

                        public void ffw() {
                            renderUpdate();
                        }
                    }
            );

            this.taskList.add(pagingPanel);

            // ----

            // create and register views
            //JLIU
/*            detailsView = new TaskDetailView(true);
            controller.addView("OpenDetailView", detailsView);
            detailsView.initialize();

            // deployments model listener
            ErraiBus.get().subscribe(Model.SUBJECT,
                    new MessageCallback() {
                        public void callback(Message message) {
                            switch (ModelCommands.valueOf(message.getCommandType())) {
                                case HAS_BEEN_UPDATED:
                                    if (message.get(String.class, ModelParts.CLASS).equals(Model.PROCESS_MODEL)) {
                                        reload();
                                    }
                                    break;
                            }
                        }
                    });*/

            Timer t = new Timer() {
                @Override
                public void run() {
                    // force loading
                    reload();
                }
            };

            t.schedule(500);

            isInitialized = true;
        }
    }

    private void reload() {
        // force loading
/*        controller.handleEvent(
                new Event(LoadTasksParticipationAction.ID, getAssignedIdentity())
        );*/
    }

    public void reset() {
        listBox.clear();

        // clear details
/*        controller.handleEvent(
                new Event(UpdateDetailsAction.ID, new DetailViewEvent("OpenDetailView", null))
        );*/
    }

    public void update(Object... data) {
        this.identity = (String) data[0];
        this.cachedTasks = (List<TaskRef>) data[1];
        pagingPanel.reset();
        renderUpdate();
    }

    public void setLoading(boolean isLoading) {
/*        if (panel.isVisible()) {
            LoadingOverlay.on(taskList, isLoading);
        }*/
    }

    private void renderUpdate() {
        // lazy init
        initialize();

        reset();

        List<TaskRef> trimmed = pagingPanel.trim(cachedTasks);
        for (TaskRef task : trimmed) {
            if (TaskRef.STATE.OPEN == task.getCurrentState()) {
                listBox.addItem(task);
            }
        }
    }
    
    @IsDirty
    public boolean isDirty() {
    	return false;
        //return view.isDirty();
    }

    @OnMayClose
    public boolean onMayClose() {
        return Window.confirm( "Are you sure you want to close?" );
    }

    @OnReveal
    public void onReveal() {
        //view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        //view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Module Editor []";
    }

    @WorkbenchPartView
    public IsWidget getView() {
    	return asWidget();
    }
}
