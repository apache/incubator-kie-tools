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

import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/*import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.ServerPlugins;*/
import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.common.PropertyGrid;
import org.drools.guvnor.client.common.WidgetWindowPanel;
import org.drools.guvnor.client.editors.jbpm.ServerPlugins;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.StringRef;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.util.SimpleDateFormat;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "InstanceDetailView")
public class InstanceDetailView extends HorizontalPanel {

    public final static String ID = InstanceDetailView.class.getName();

    private PropertyGrid grid;

    private ProcessInstanceRef currentInstance;

    private Button diagramBtn;

    private Button instanceDataBtn;

    private WidgetWindowPanel diagramWindowPanel;

    private WidgetWindowPanel instanceDataWindowPanel;

    //private ApplicationContext appContext;

    private ActivityDiagramView diagramView;

    private InstanceDataView instanceDataView;

    private boolean hasDiagramPlugin;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();
    private ProcessDefinitionRef currentDefintion;

    private boolean isRiftsawInstance;

    private CustomizableListBox<String> processEvents;
    //private final BpmConsoleClientFactory clientFactory;

    public InstanceDetailView(/*BpmConsoleClientFactory clientFactory*/) {
        //TODO: -Rikkola-
//        super("Execution details");

    	//JLIU: TODO
/*        this.clientFactory = clientFactory;
        this.controller = clientFactory.getController();

        controller.addView(ID, this);
        controller.addAction(GetProcessInstanceEventsAction.ID, new GetProcessInstanceEventsAction(clientFactory.getApplicationContext()));

        this.appContext = clientFactory.getApplicationContext();
        isRiftsawInstance = appContext.getConfig().getProfileName().equals("BPEL Console");*/

        super.setStyleName("bpm-detail-panel");

        grid = new PropertyGrid(
                new String[]{"Process:", "Instance ID:", "State", "Start Date:", "Activity:"}
        );

        this.add(grid);

        VerticalPanel buttonPanel = new VerticalPanel();
        diagramBtn = new Button(
                "Execution Path",
                new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        String diagramUrl = getCurrentDefintion().getDiagramUrl();
                        if (diagramUrl != null && !diagramUrl.equals("")) {
                            final ProcessInstanceRef selection = getCurrentInstance();
                            if (selection != null) {
                                createDiagramWindow(selection);

                                DeferredCommand.addCommand(new Command() {
                                    public void execute() {
                                    	//JLIU: TODO
/*                                        controller.handleEvent(
                                                new Event(LoadInstanceActivityImage.class.getName(), selection)
                                        );*/
                                    }
                                }
                                );

                            }
                        } else {
                            Window.alert("Incomplete deployment. No diagram associated with process");
                        }
                    }
                }
        );

        diagramBtn.setEnabled(false);
        buttonPanel.add(diagramBtn);

        instanceDataBtn = new Button("Instance Data",
                new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        if (currentInstance != null) {
                            createDataWindow(currentInstance);
                            //JLIU: TODO
/*                            controller.handleEvent(
                                    new Event(UpdateInstanceDataAction.ID, currentInstance.getId())
                            );*/
                        }
                    }
                }
        );
        instanceDataBtn.setEnabled(false);
        buttonPanel.add(instanceDataBtn);
        this.add(buttonPanel);

        // plugin availability
        this.hasDiagramPlugin =
                ServerPlugins.has("org.jboss.bpm.console.server.plugin.GraphViewerPlugin");

    }

    private void createDiagramWindow(ProcessInstanceRef inst) {

        ScrollPanel layout = new ScrollPanel();
        layout.setStyleName("bpm-window-layout");

        Label header = new Label("Instance: " + inst.getId());
        header.setStyleName("bpm-label-header");
        layout.add(header);

        final TabPanel tabPanel = new TabPanel();

        HorizontalPanel diaViewLayout = new HorizontalPanel();
        diaViewLayout.add(diagramView);

        tabPanel.add(diagramView, "View");

        processEvents = new CustomizableListBox<String>(
                new CustomizableListBox.ItemFormatter<String>() {
                    public String format(String item) {
                        return new HTML(item).getHTML();
                    }
                }
        );

        processEvents.setFirstLine("Process Events");

        VerticalPanel sourcePanel = new VerticalPanel();
        sourcePanel.add(processEvents);
        tabPanel.add(sourcePanel, "Source");

        tabPanel.selectTab(0);

        layout.add(tabPanel);

        diagramWindowPanel = new WidgetWindowPanel(
                "Process Instance Activity",
                layout, true
        );

        //JLIU: TODO
/*        controller.handleEvent(new Event(GetProcessInstanceEventsAction.ID, inst.getId()));*/
    }

    public void populateProcessInstanceEvents(List<StringRef> refs) {
        processEvents.clear();

        for (StringRef value : refs) {
            processEvents.addItem(formatResult(value.getValue()));
        }
    }

    private String formatResult(String value) {
        StringBuffer sbuffer = new StringBuffer();
        String[] split = value.split("~");
        sbuffer.append(split[0] + " : ");

        for (int i = 1; i < split.length; i++) {
            sbuffer.append("<br/>");
            sbuffer.append(split[i]);
        }

        return sbuffer.toString();
    }

    private void createDataWindow(ProcessInstanceRef inst) {
        instanceDataWindowPanel = new WidgetWindowPanel(
                "Process Instance Data: " + inst.getId(),
                instanceDataView, true
        );
    }

    //JLIU: TODO
/*    public void setController(Controller controller) {
        this.controller = controller;

        this.diagramView = new ActivityDiagramView();
        this.instanceDataView = new InstanceDataView(clientFactory);

        controller.addView(ActivityDiagramView.ID, diagramView);
        controller.addView(InstanceDataView.ID, instanceDataView);
        controller.addAction(LoadActivityDiagramAction.ID, new LoadActivityDiagramAction(clientFactory.getApplicationContext()));
        controller.addAction(LoadInstanceActivityImage.class.getName(), new LoadInstanceActivityImage(clientFactory.getApplicationContext()));
        controller.addAction(UpdateInstanceDataAction.ID, new UpdateInstanceDataAction(clientFactory.getApplicationContext()));
    }*/

    public void update(ProcessDefinitionRef def, ProcessInstanceRef instance) {
        this.currentDefintion = def;
        this.currentInstance = instance;

        String currentNodeName = instance.getRootToken() != null ?
                instance.getRootToken().getCurrentNodeName() : "n/a";

        String[] values = new String[]{
                def.getName(),
                instance.getId(),
                String.valueOf(instance.getState()),
                dateFormat.format(instance.getStartDate()),
                currentNodeName
        };

        grid.update(values);

        if (hasDiagramPlugin) {
            this.diagramBtn.setEnabled(true);
        }

        instanceDataBtn.setEnabled(true);
    }

    public void clearView() {
        grid.clear();
        this.currentDefintion = null;
        this.currentInstance = null;
        this.diagramBtn.setEnabled(false);
        instanceDataBtn.setEnabled(false);

    }

    private ProcessDefinitionRef getCurrentDefintion() {
        return currentDefintion;
    }

    private ProcessInstanceRef getCurrentInstance() {
        return currentInstance;
    }    
    
	@WorkbenchPartTitle
	public String getTitle() {
		return "InstanceDetailView";
	}

	@WorkbenchPartView
	public IsWidget getView() {
		return asWidget();
	}
}
