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
package org.jboss.bpm.console.client.process;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/*import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;*/
import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.common.DataDriven;
import org.drools.guvnor.client.common.LoadingOverlay;
import org.drools.guvnor.client.common.PagingCallback;
import org.drools.guvnor.client.common.PagingPanel;
import org.drools.guvnor.client.common.WidgetWindowPanel;
import org.jboss.bpm.console.client.model.HistoryActivityInstanceRef;
import org.jboss.bpm.console.client.model.HistoryProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.process.events.HistoryActivityDiagramEvent;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.util.SimpleDateFormat;

/**
 * @author Maciej Swiderski <swiderski.maciej@gmail.com>
 */
@Dependent
@WorkbenchEditor(identifier = "HistoryInstanceListView")
public class HistoryInstanceListView implements IsWidget, DataDriven {

    public final static String ID = HistoryInstanceListView.class.getName();

    private VerticalPanel instanceList = null;

    private CustomizableListBox<HistoryProcessInstanceRef> listBoxHistory;

    private CustomizableListBox<HistoryActivityInstanceRef> listBoxInstanceActivity;

    private ProcessDefinitionRef currentDefinition;

    private boolean isInitialized;

    private List<HistoryProcessInstanceRef> cachedInstances = null;

    private List<HistoryActivityInstanceRef> cachedInstancesActivity = null;

    private List<String> executedActivities = null;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();
/*
    private ApplicationContext appContext;*/

    private PagingPanel pagingPanel;

    SimplePanel panel;

    private MenuItem diagramBtn;

    private WidgetWindowPanel diagramWindowPanel;

    private ActivityDiagramView diagramView;

    public HistoryInstanceListView(/*BpmConsoleClientFactory clientFactory*/) {
/*        this.appContext = clientFactory.getApplicationContext();
        this.controller = clientFactory.getController();*/
    }

    public Widget asWidget() {
        panel = new SimplePanel();

/*        controller.addView(ID, this);*/
        initialize();

        return panel;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize() {
        if (!isInitialized) {
            instanceList = new VerticalPanel();

            // create history list box elements
            listBoxHistory = createHistoryListBox();
            // create list of activities executed for currently selected history process instance
            this.listBoxInstanceActivity = createHistoryActivitiesListBox();

            // toolbar
            final HorizontalPanel toolBox = new HorizontalPanel();

            toolBox.setSpacing(5);

            final MenuBar toolBar = new MenuBar();
            toolBar.addItem(
                    "Refresh",
                    new Command() {

                        public void execute() {

                        	//JLIU: TODO
/*                            controller.handleEvent(
                                    new Event(
                                            UpdateHistoryDefinitionAction.ID,
                                            getCurrentDefinition()
                                    )
                            );*/
                        }
                    }
            );

            diagramBtn = new MenuItem(
                    "Diagram",
                    new Command() {
                        public void execute() {
                            String diagramUrl = currentDefinition.getDiagramUrl();
                            if (currentDefinition != null && executedActivities != null) {
                                HistoryActivityDiagramEvent eventData = new HistoryActivityDiagramEvent(currentDefinition, executedActivities);
                                if (diagramUrl != null && !diagramUrl.equals("")) {
                                    createDiagramWindow();
                                    //JLIU: TODO
/*                                    controller.handleEvent(
                                            new Event(LoadHistoryDiagramAction.ID, eventData)
                                    );*/

                                } else {
                                    Window.alert("Incomplete deployment, No diagram associated with process");
                                }
                            }
                        }
                    }
            );

            // terminate works on any BPM Engine
            toolBar.addItem(diagramBtn);
            diagramBtn.setEnabled(false);

            toolBox.add(toolBar);

            instanceList.add(toolBox);
            instanceList.add(listBoxHistory);

            pagingPanel = new

                    PagingPanel(
                    new PagingCallback() {
                        public void rev
                                () {
                            renderUpdate();
                        }

                        public void ffw() {
                            renderUpdate();
                        }
                    }
            );
            instanceList.add(pagingPanel);
            instanceList.add(listBoxInstanceActivity);

            // cached data?
            if (this.cachedInstances != null) {
                bindData(this.cachedInstances);
            }

            // layout
            SimplePanel layout = new SimplePanel();
            layout.add(instanceList);

            panel.add(layout);

            isInitialized = true;

            this.executedActivities = new ArrayList<String>();

        }
    }

    public HistoryProcessInstanceRef getSelection() {
        HistoryProcessInstanceRef selection = null;
        if (listBoxHistory.getSelectedIndex() != -1) {
            selection = listBoxHistory.getItem(listBoxHistory.getSelectedIndex());
        }
        return selection;
    }

    public ProcessDefinitionRef getCurrentDefinition() {
        return this.currentDefinition;
    }

/*    public void setController(Controller controller) {
        this.controller = controller;

        this.diagramView = new ActivityDiagramView();

        controller.addView(ActivityDiagramView.ID, diagramView);
    }*/

    public void reset() {
        this.currentDefinition = null;
        this.cachedInstances = new ArrayList<HistoryProcessInstanceRef>();
        renderUpdate();

        diagramBtn.setEnabled(false);
    }

    public void update(Object... data) {
        if (data[0] instanceof ProcessDefinitionRef) {
            // fill in list box for finished process instances for current definition
            this.currentDefinition = (ProcessDefinitionRef) data[0];
            this.cachedInstances = (List<HistoryProcessInstanceRef>) data[1];

            //if(isInitialized()) pagingPanel.reset();
            renderUpdate();

            //clear activity list box
            listBoxInstanceActivity.clear();
            diagramBtn.setEnabled(false);
        } else {
            // fill in list box of activities executed for currently selected process instance
            this.cachedInstancesActivity = (List<HistoryActivityInstanceRef>) data[0];

            renderHistoryActivityList();
        }
    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(instanceList, isLoading);
    }

    private void renderUpdate() {
        if (isInitialized()) {
            bindData(this.cachedInstances);

        }
    }

    private void bindData(List<HistoryProcessInstanceRef> instances) {
        listBoxHistory.clear();

        List<HistoryProcessInstanceRef> list = pagingPanel.trim(instances);
        for (HistoryProcessInstanceRef inst : list) {
            listBoxHistory.addItem(inst);
        }

        // layout again
        //TODO: -Rikkola-
//        panel.invalidate();
    }

    private void renderHistoryActivityList() {

        if (this.cachedInstancesActivity != null) {

            listBoxInstanceActivity.clear();
            this.executedActivities.clear();

            for (HistoryActivityInstanceRef def : cachedInstancesActivity) {

                listBoxInstanceActivity.addItem(def);
                this.executedActivities.add(def.getActivityName());

            }
        }
    }

    protected CustomizableListBox<HistoryProcessInstanceRef> createHistoryListBox() {
        listBoxHistory = new CustomizableListBox<HistoryProcessInstanceRef>(
                new CustomizableListBox.ItemFormatter<HistoryProcessInstanceRef>() {
                    public String format(HistoryProcessInstanceRef historyProcessInstanceRef) {
                        String result = "";

                        result += historyProcessInstanceRef.getProcessInstanceId();

                        result += " ";

                        result += historyProcessInstanceRef.getState().toString();

                        result += " ";

                        result += historyProcessInstanceRef.getStartTime() != null ? dateFormat.format(historyProcessInstanceRef.getStartTime()) : "";

                        result += " ";

                        result += historyProcessInstanceRef.getEndTime() != null ? dateFormat.format(historyProcessInstanceRef.getEndTime()) : "";

                        result += " ";

                        result += String.valueOf(historyProcessInstanceRef.getDuration());

                        return result;
                    }
                }
        );

        listBoxHistory.setFirstLine("<b>Instance</b>, State, Start Date, End Date, Duration");

        listBoxHistory.addChangeHandler(
                new ChangeHandler() {
                    public void onChange(ChangeEvent event) {

                        int index = listBoxHistory.getSelectedIndex();
                        if (index != -1) {
                            HistoryProcessInstanceRef item = listBoxHistory.getItem(index);

                            // update details
                            //JLIU: TODO
/*                            controller.handleEvent(new Event(UpdateHistoryInstanceAction.ID, item.getProcessInstanceId()));*/

                            diagramBtn.setEnabled(true);
                        }
                    }
                }
        );

        return listBoxHistory;
    }

    private CustomizableListBox<HistoryActivityInstanceRef> createHistoryActivitiesListBox() {
        final CustomizableListBox<HistoryActivityInstanceRef> listBox =
                new CustomizableListBox<HistoryActivityInstanceRef>(
                        new CustomizableListBox.ItemFormatter<HistoryActivityInstanceRef>() {
                            public String format(HistoryActivityInstanceRef historyActivityInstanceRef) {
                                String result = "";

                                result += historyActivityInstanceRef.getActivityName();
                                result += " ";
                                result += historyActivityInstanceRef.getStartTime() != null ? dateFormat.format(historyActivityInstanceRef.getStartTime()) : "";
                                result += " ";
                                result += historyActivityInstanceRef.getEndTime() != null ? dateFormat.format(historyActivityInstanceRef.getEndTime()) : "";
                                result += " ";
                                result += String.valueOf(historyActivityInstanceRef.getDuration());

                                return result;
                            }
                        }
                );

        listBox.setFirstLine("ActivityName, StartTime, EndTime, Duration");

        return listBox;
    }

    private void createDiagramWindow() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("bpm-window-layout");

        Label header = new Label("Instance: ");
        header.setStyleName("bpm-label-header");
        layout.add(header);

        layout.add(diagramView);

        diagramWindowPanel = new WidgetWindowPanel(
                "Process Instance Activity",
                layout, true
        );

    }
}

