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

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.util.SimpleDateFormat;
import org.jboss.bpm.console.client.icons.ConsoleIconBundle;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.TokenReference;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dependent
@WorkbenchEditor(identifier = "InstanceListView")
public class InstanceListView implements IsWidget, DataDriven {

    public final static String ID = InstanceListView.class.getName();

    private VerticalPanel instanceList = null;

    private CellTable<ProcessInstanceRef> listBox = new CellTable<ProcessInstanceRef>();
    private ListDataProvider<ProcessInstanceRef> dataProvider = new ListDataProvider<ProcessInstanceRef>();

    private ProcessDefinitionRef currentDefinition;

    private boolean isInitialized;

    private List<ProcessInstanceRef> cachedInstances = null;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    //private ApplicationContext appContext;

    private IFrameWindowPanel iframeWindow = null;

    private boolean isRiftsawInstance;

    //private PagingPanel pagingPanel;

    SimplePanel panel;

    private MenuItem startBtn, terminateBtn, deleteBtn, signalBtn, refreshBtn;

    // elements needed to signal waiting execution
    private List<TokenReference> tokensToSignal = null;

    private WidgetWindowPanel signalWindowPanel;

    private CustomizableListBox<TokenReference> listBoxTokens = null;

    private List<TextBox> signalTextBoxes = null;

    private CustomizableListBox<String> listBoxTokenSignals;

    private ImageResource greenIcon;

    public Widget asWidget() {

        panel = new SimplePanel();
        panel.setWidth("100%");

        initialize();

        ConsoleIconBundle imageBundle = (ConsoleIconBundle) GWT.create(ConsoleIconBundle.class);
        greenIcon = imageBundle.greenIcon();

        return panel;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize() {
        if (!isInitialized) {
            instanceList = new VerticalPanel();

            TextColumn<ProcessInstanceRef> instanceColumn = new TextColumn<ProcessInstanceRef>() {
                @Override
                public String getValue(ProcessInstanceRef processInstanceRef) {
                    return processInstanceRef.getId();
                }
            };
            TextColumn<ProcessInstanceRef> stateColumn = new TextColumn<ProcessInstanceRef>() {
                @Override
                public String getValue(ProcessInstanceRef processInstanceRef) {
                    return processInstanceRef.getState().toString();
                }
            };
            TextColumn<ProcessInstanceRef> startDateColumn = new TextColumn<ProcessInstanceRef>() {
                @Override
                public String getValue(ProcessInstanceRef processInstanceRef) {
                    return processInstanceRef.getStartDate() != null ? dateFormat.format(processInstanceRef.getStartDate()) : "";
                }
            };
            Column<ProcessInstanceRef, String> actionColumn = new Column<ProcessInstanceRef, String>(new ButtonCell()) {
                @Override
                public String getValue(ProcessInstanceRef processInstanceRef) {
                    return "*";
                }
            };
            actionColumn.setFieldUpdater(new FieldUpdater<ProcessInstanceRef, String>() {

                public void update(int index, final ProcessInstanceRef processInstanceRef, String value) {

                    PopupPanel popupPanel = new PopupPanel();
                    VerticalPanel widgets = new VerticalPanel();
                    popupPanel.add(widgets);
                    widgets.add(new Button("Start"));
                    widgets.add(new Button("Signal"));
                    widgets.add(new Button("Delete"));
                    widgets.add(new Button("Terminate"));

                    popupPanel.show();
                }
            });

            listBox.addColumn(instanceColumn, "Instance");
            listBox.addColumn(stateColumn, "State");
            listBox.addColumn(startDateColumn, "Start Date");
            listBox.addColumn(actionColumn);

            dataProvider.addDataDisplay(listBox);

            //TODO: -Rikkola-
//            listBox.addChangeHandler(
//                    new ChangeHandler() {
//                        public void onChange(ChangeEvent event) {
//                            int index = listBox.getSelectedIndex();
//                            if (index != -1) {
//                                ProcessInstanceRef item = listBox.getItem(index);
//
//                                // enable or disable signal button depending on current activity
//                                if (isSignalable(item)) {
//                                    signalBtn.setEnabled(true);
//                                } else {
//                                    signalBtn.setEnabled(false);
//                                }
//
//                                terminateBtn.setEnabled(true);
//
//                                //JLIU: TODO
//                                // update details
///*                                controller.handleEvent(
//                                        new Event(UpdateInstanceDetailAction.ID,
//                                                new InstanceEvent(currentDefinition, item)
//                                        )
//                                );*/
//                            }
//                        }
//                    }
//            );

            // toolbar
            final VerticalPanel toolBox = new VerticalPanel();

            toolBox.setSpacing(5);

            final MenuBar toolBar = new MenuBar();
            refreshBtn = new

                    MenuItem(
                    "Refresh",
                    new Command() {

                        public void execute() {
                            //JLIU:TODO
/*                            controller.handleEvent(
                                    new Event(
                                            UpdateInstancesAction.ID,
                                            getCurrentDefinition()
                                    )
                            );*/
                        }
                    }

            );
            toolBar.addItem(refreshBtn);
            refreshBtn.setEnabled(false);
            toolBar.addSeparator();

            startBtn = new

                    MenuItem(
                    "Start",
                    new Command() {
                        public void execute() {
                            if (Window.confirm("Start new execution. Do you want to start a new execution of this process?")) {
                                String url = getCurrentDefinition().getFormUrl();
                                boolean hasForm = (url != null && !url.equals(""));
                                if (hasForm) {
                                    ProcessDefinitionRef definition = getCurrentDefinition();
                                    iframeWindow = new IFrameWindowPanel(
                                            definition.getFormUrl(), "New Process Instance: " + definition.getId()
                                    );

                                    iframeWindow.setCallback(
                                            new IFrameWindowCallback() {
                                                public void onWindowClosed() {
                                                    //JLIU:TODO
/*                                                    controller.handleEvent(
                                                            new Event(UpdateInstancesAction.ID, getCurrentDefinition())
                                                    );*/
                                                }
                                            }
                                    );

                                    iframeWindow.show();
                                } else {
                                    //JLIU: TODO
/*                                    controller.handleEvent(
                                            new Event(
                                                    StartNewInstanceAction.ID,
                                                    getCurrentDefinition()
                                            )
                                    );*/
                                }
                            }

                        }
                    }

            );

            terminateBtn = new

                    MenuItem(
                    "Terminate",
                    new Command() {
                        public void execute() {
                            if (getSelection() != null) {

                                if (Window.confirm("Terminate instance. Terminating this instance will stop further execution.")) {
                                    ProcessInstanceRef selection = getSelection();
                                    selection.setState(ProcessInstanceRef.STATE.ENDED);
                                    selection.setEndResult(ProcessInstanceRef.RESULT.OBSOLETE);
                                    //JLIU:TODO
/*                                    controller.handleEvent(
                                            new Event(
                                                    StateChangeAction.ID,
                                                    selection
                                            )
                                    );*/
                                }
                            } else {
                                Window.alert("Missing selection. Please select an instance");
                            }
                        }
                    }

            );

            deleteBtn = new

                    MenuItem(
                    "Delete",
                    new Command() {
                        public void execute() {
                            if (getSelection() != null) {
                                if (Window.confirm("Delete instance. Deleting this instance will remove any history information and associated tasks as well.")) {
                                    ProcessInstanceRef selection = getSelection();
                                    selection.setState(ProcessInstanceRef.STATE.ENDED);

                                    //JLIU:TODO
/*                                    controller.handleEvent(
                                            new Event(
                                                    DeleteInstanceAction.ID,
                                                    selection
                                            )
                                    );*/

                                } else {
                                    Window.alert("Missing selection. Please select an instance");
                                }
                            }
                        }
                    }

            );

            signalBtn = new

                    MenuItem(
                    "Signal",
                    new Command() {
                        public void execute() {
                            createSignalWindow();
                        }
                    }

            );

            if (!isRiftsawInstance)  // riftsaw doesn't support instance operations

            {
                toolBar.addItem(startBtn);
                toolBar.addItem(signalBtn);
                toolBar.addItem(deleteBtn);

                startBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                signalBtn.setEnabled(false);
            }

            // terminate works on any BPM Engine
            toolBar.addItem(terminateBtn);
            terminateBtn.setEnabled(false);

            toolBox.add(toolBar);

            instanceList.add(toolBox);
            instanceList.add(listBox);

            // cached data?
            if (this.cachedInstances != null)

            {
                bindData(this.cachedInstances);
            }

            // layout
            VerticalPanel layout = new VerticalPanel();
            instanceList.setWidth("100%");
            layout.add(instanceList);

            Label detailLabel = new Label("Execution details");
            layout.add(detailLabel);
            
            // details
            InstanceDetailView detailsView = new InstanceDetailView();
            detailsView.setWidth("100%");
            //JLIU: TODO
/*            controller.addView(InstanceDetailView.ID, detailsView);
            controller.addAction(UpdateInstanceDetailAction.ID, new UpdateInstanceDetailAction());
            controller.addAction(ClearInstancesAction.ID, new ClearInstancesAction());
            controller.addAction(SignalExecutionAction.ID, new SignalExecutionAction(clientFactory.getApplicationContext()));*/
            layout.add(detailsView);

            panel.add(layout);

            isInitialized = true;

        }
        
        
        ProcessDefinitionRef mockCurrentDefinition = new ProcessDefinitionRef();
        List<ProcessInstanceRef> mockCachedInstances = new ArrayList<ProcessInstanceRef>();
        ProcessInstanceRef mockProcessInstanceRef = new ProcessInstanceRef();
        mockProcessInstanceRef.setDefinitionId("1");
        mockProcessInstanceRef.setStartDate(new Date());
        mockProcessInstanceRef.setSuspended(true);
        mockProcessInstanceRef.setState("RUNNING");
        mockCachedInstances.add(mockProcessInstanceRef);
        ProcessInstanceRef mockProcessInstanceRef2 = new ProcessInstanceRef();
        mockProcessInstanceRef2.setDefinitionId("3");
        mockProcessInstanceRef2.setStartDate(new Date());
        mockProcessInstanceRef2.setSuspended(true);
        mockProcessInstanceRef2.setState("RUNNING");
        mockCachedInstances.add(mockProcessInstanceRef2);        
        
        Object[] data = new Object[]{mockCurrentDefinition, mockCachedInstances};
        update(data);
    }

    public ProcessInstanceRef getSelection() {
        ProcessInstanceRef selection = null;
        // TODO: -Rikkola-
//        if (listBox.getSelectedIndex() != -1) {
//            selection = listBox.getItem(listBox.getSelectedIndex());
//        }
        return selection;
    }

    public ProcessDefinitionRef getCurrentDefinition() {
        return this.currentDefinition;
    }

    public void reset() {
        this.currentDefinition = null;
        this.cachedInstances = new ArrayList<ProcessInstanceRef>();
        renderUpdate();

        startBtn.setEnabled(false);
        terminateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        signalBtn.setEnabled(false);
        refreshBtn.setEnabled(false);
    }

    public void update(Object... data) {
        this.currentDefinition = (ProcessDefinitionRef) data[0];
        this.cachedInstances = (List<ProcessInstanceRef>) data[1];

        renderUpdate();
    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(instanceList, isLoading);
    }

    private void renderUpdate() {
        if (isInitialized()) {
            bindData(this.cachedInstances);

// clear details
            //JLIU: TODO
/*            controller.handleEvent(
                    new Event(UpdateInstanceDetailAction.ID,
                            new InstanceEvent(this.currentDefinition, null)
                    )
            );*/

            startBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            refreshBtn.setEnabled(true);
        }
    }

    private void bindData(List<ProcessInstanceRef> instances) {
        dataProvider.flush();

        List<ProcessInstanceRef> list = instances;//pagingPanel.trim(instances);
        for (ProcessInstanceRef inst : list) {
            dataProvider.getList().add(inst);
        }
    }

    private boolean isSignalable(ProcessInstanceRef processInstance) {

        tokensToSignal = new ArrayList<TokenReference>();

// first check if the parent execution is signalable
        if (processInstance.getRootToken() != null && processInstance.getRootToken().canBeSignaled()) {
            tokensToSignal.add(processInstance.getRootToken());

        } else if (processInstance.getRootToken() != null && processInstance.getRootToken().getChildren() != null) {
            // next verify children
            collectSignalableTokens(processInstance.getRootToken(), tokensToSignal);
        }

        if (tokensToSignal.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void collectSignalableTokens(TokenReference tokenParent, List<TokenReference> tokensToSignal) {
        if (tokenParent.getChildren() != null) {
            for (TokenReference token : tokenParent.getChildren()) {
                if (token.canBeSignaled()) {
                    tokensToSignal.add(token);
                }

                collectSignalableTokens(token, tokensToSignal);
            }
        }
    }

    private void createSignalWindow() {
        signalTextBoxes = new ArrayList<TextBox>();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("bpm-window-layout");

        // toolbar
        final HorizontalPanel toolBox = new HorizontalPanel();

        toolBox.setSpacing(5);

        final MenuBar toolBar = new MenuBar();
        toolBar.addItem(
                "Signal",
                new Command() {

                    public void execute() {
                        int selectedToken = listBoxTokens.getSelectedIndex();
                        int selectedSignal = listBoxTokenSignals.getSelectedIndex();
                        if (selectedToken != -1 && selectedSignal != -1) {

                            //JLIU: TODO
/*                            controller.handleEvent(
                                    new Event(SignalExecutionAction.ID,
                                            new SignalInstanceEvent(getCurrentDefinition(), getSelection(), listBoxTokens.getItem(selectedToken), listBoxTokenSignals.getItem(selectedSignal), selectedToken)));
*/
                        } else {
                            Window.alert("Incomplete selection. Please select both token and signal name");
                        }

                    }
                }
        );

        toolBar.addItem(
                "Cancel",
                new Command() {
                    public void execute() {

                        signalWindowPanel.close();
                    }
                }
        );

        Label header = new Label("Available tokens to signal: ");
        header.setStyleName("bpm-label-header");
        layout.add(header);

        toolBox.add(toolBar);

        layout.add(toolBox);

        listBoxTokens = new CustomizableListBox<TokenReference>(
                new CustomizableListBox.ItemFormatter<TokenReference>() {
                    public String format(TokenReference tokenReference) {
                        String result = "";

                        result += tokenReference.getId();

                        result += " ";

                        result += tokenReference.getName() == null ? tokenReference.getCurrentNodeName() : tokenReference.getName();

                        return result;
                    }
                }
        );

        listBoxTokens.setFirstLine("Id, Name");

        listBoxTokens.addChangeHandler(
                new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        int index = listBoxTokens.getSelectedIndex();
                        if (index != -1) {
                            TokenReference item = listBoxTokens.getItem(index);
                            renderAvailableSignals(item);
                        }
                    }
                }
        );

        renderSignalListBox(-1);
        layout.add(listBoxTokens);

        Label headerSignals = new Label("Available signal names");
        headerSignals.setStyleName("bpm-label-header");
        layout.add(headerSignals);

        listBoxTokenSignals = new CustomizableListBox<String>(
                new CustomizableListBox.ItemFormatter<String>() {
                    public String format(String item) {
                        return item;
                    }
                }
        );

        listBoxTokenSignals.setFirstLine("Signal name");

        layout.add(listBoxTokenSignals);

        signalWindowPanel = new WidgetWindowPanel(
                "Signal process from wait state",
                layout, true
        );

    }

    public void renderSignalListBox(int i) {
        // remove currently signaled token
        if (i > -1) {
            tokensToSignal.remove(i);
        }

        // if available token list is empty close window
        if (tokensToSignal.isEmpty()) {
            signalWindowPanel.close();
        }

        // display all remaining token possible to signal
        listBoxTokens.clear();
        for (TokenReference token : tokensToSignal) {
            listBoxTokens.addItem(token);
        }

        // clear available signal list box
        if (listBoxTokenSignals != null) {
            listBoxTokenSignals.clear();
        }
    }

    private void renderAvailableSignals(TokenReference item) {
        listBoxTokenSignals.clear();
        for (String signal : item.getAvailableSignals()) {
            listBoxTokenSignals.addItem(signal);
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "InstanceListView";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return asWidget();
    }
}
