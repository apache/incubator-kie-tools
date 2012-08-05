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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/*import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;*/
import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.common.DataDriven;
import org.drools.guvnor.client.common.IFrameWindowCallback;
import org.drools.guvnor.client.common.IFrameWindowPanel;
import org.drools.guvnor.client.common.LoadingOverlay;
import org.drools.guvnor.client.common.WidgetWindowPanel;
import org.jboss.bpm.console.client.icons.ConsoleIconBundle;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.TokenReference;
import org.jboss.bpm.console.client.process.events.InstanceEvent;
import org.jboss.bpm.console.client.process.events.SignalInstanceEvent;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.util.SimpleDateFormat;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "InstanceListView")
public class InstanceListView implements IsWidget, DataDriven {

    public final static String ID = InstanceListView.class.getName();

    private VerticalPanel instanceList = null;

    private CustomizableListBox<ProcessInstanceRef> listBox;

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
    // final BpmConsoleClientFactory clientFactory;

    public InstanceListView(/*BpmConsoleClientFactory clientFactory*/) {
    	//JLIU: TODO
/*        this.appContext = clientFactory.getApplicationContext();
        this.clientFactory = clientFactory;
        this.isRiftsawInstance = clientFactory.getApplicationContext().getConfig().getProfileName().equals("BPEL Console");
        this.controller = clientFactory.getController();*/
    }

    public Widget asWidget() {

        panel = new SimplePanel();

        //controller.addView(ID, this);
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

            listBox =
                    new CustomizableListBox<ProcessInstanceRef>(
                            new CustomizableListBox.ItemFormatter<ProcessInstanceRef>() {
                                public String format(ProcessInstanceRef processInstanceRef) {

                                    String result = "";

                                    result += processInstanceRef.getId();
                                    result += " ";
                                    result += processInstanceRef.getState().toString();
                                    result += " ";
                                    result += processInstanceRef.getStartDate() != null ? dateFormat.format(processInstanceRef.getStartDate()) : "";

                                    return result;
                                }
                            }
                    );

            listBox.setFirstLine("<b>Instance</b>, State, Start Date");

            listBox.addChangeHandler(
                    new ChangeHandler() {
                        public void onChange(ChangeEvent event) {
                            int index = listBox.getSelectedIndex();
                            if (index != -1) {
                                ProcessInstanceRef item = listBox.getItem(index);

                                // enable or disable signal button depending on current activity
                                if (isSignalable(item)) {
                                    signalBtn.setEnabled(true);
                                } else {
                                    signalBtn.setEnabled(false);
                                }

                                terminateBtn.setEnabled(true);

                                //JLIU: TODO
                                // update details
/*                                controller.handleEvent(
                                        new Event(UpdateInstanceDetailAction.ID,
                                                new InstanceEvent(currentDefinition, item)
                                        )
                                );*/
                            }
                        }
                    }
            );

            // toolbar
            final VerticalPanel toolBox = new VerticalPanel();

            toolBox.setSpacing(5);

            final MenuBar toolBar = new MenuBar();
            refreshBtn = new MenuItem(
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

            startBtn = new MenuItem(
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
                    });

            terminateBtn = new MenuItem(
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

            deleteBtn = new MenuItem(
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

            signalBtn = new MenuItem(
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
            HorizontalPanel layout = new HorizontalPanel();
            layout.add(instanceList);

            // details
            InstanceDetailView detailsView = new InstanceDetailView();
            //JLIU: TODO
/*            controller.addView(InstanceDetailView.ID, detailsView);
            controller.addAction(UpdateInstanceDetailAction.ID, new UpdateInstanceDetailAction());
            controller.addAction(ClearInstancesAction.ID, new ClearInstancesAction());
            controller.addAction(SignalExecutionAction.ID, new SignalExecutionAction(clientFactory.getApplicationContext()));*/
            layout.add(detailsView);

            panel.add(layout);

            isInitialized = true;

        }
    }

    public ProcessInstanceRef getSelection() {
        ProcessInstanceRef selection = null;
        if (listBox.getSelectedIndex() != -1) {
            selection = listBox.getItem(listBox.getSelectedIndex());
        }
        return selection;
    }

    public ProcessDefinitionRef getCurrentDefinition() {
        return this.currentDefinition;
    }
/*
    public void setController(Controller controller) {
        this.controller = controller;
    }*/

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
        listBox.clear();

        List<ProcessInstanceRef> list = instances;//pagingPanel.trim(instances);
        for (ProcessInstanceRef inst : list) {
            listBox.addItem(inst);
        }

        // layout again
        //TODO: -Rikkola-
//        panel.invalidate();
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
