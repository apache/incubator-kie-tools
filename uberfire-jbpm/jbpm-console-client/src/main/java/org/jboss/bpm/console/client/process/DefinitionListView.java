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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/*import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;*/
import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.common.DataDriven;
import org.drools.guvnor.client.common.LoadingOverlay;
import org.drools.guvnor.client.common.Model;
import org.drools.guvnor.client.common.ModelCommands;
import org.drools.guvnor.client.common.ModelParts;
import org.drools.guvnor.client.common.PagingCallback;
import org.drools.guvnor.client.common.PagingPanel;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.uberfire.client.annotations.WorkbenchEditor;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Deprecated
@Dependent
@WorkbenchEditor(identifier = "DefinitionListView")
public class DefinitionListView implements IsWidget, DataDriven {

    public final static String ID = DefinitionListView.class.getName();

    private VerticalPanel definitionList = null;

    private CustomizableListBox<ProcessDefinitionRef> listBox;

    private boolean isInitialized;

    private int FILTER_NONE = 10;
    private int FILTER_ACTIVE = 20;
    private int FILTER_SUSPENDED = 30;
    private int currentFilter = FILTER_NONE;

    private List<ProcessDefinitionRef> definitions = null;
    private PagingPanel pagingPanel;

    private final SimplePanel panel = new SimplePanel();

    //private final ApplicationContext applicationContext;

    public DefinitionListView(/*BpmConsoleClientFactory clientFactory*/) {
        //applicationContext = clientFactory.getApplicationContext();
    }

    public Widget asWidget() {

        panel.clear();

        listBox = createListBox();
/*
        controller.addView(ID, this);

        controller.addAction(UpdateInstancesAction.ID, new UpdateInstancesAction(applicationContext));
        controller.addAction(StartNewInstanceAction.ID, new StartNewInstanceAction(applicationContext));
        controller.addAction(StateChangeAction.ID, new StateChangeAction(applicationContext));
        controller.addAction(DeleteDefinitionAction.ID, new DeleteDefinitionAction(applicationContext));
        controller.addAction(DeleteInstanceAction.ID, new DeleteInstanceAction(applicationContext));
        controller.addAction(UpdateDefinitionsAction.ID, new UpdateDefinitionsAction(applicationContext));*/

        initialize();

        Timer t = new Timer() {
            @Override
            public void run() {
            	//JLIU: TODO
/*                controller.handleEvent(
                        new Event(UpdateDefinitionsAction.ID, null)
                );*/
            }
        };

        t.schedule(500);

        return panel;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize() {
        if (!isInitialized) {

            definitionList = new VerticalPanel();

            // toolbar

            final HorizontalPanel toolBox = new HorizontalPanel();

            // toolbar
            final MenuBar toolBar = new MenuBar();

            toolBar.addItem(
                    "Refresh",
                    new Command() {
                        public void execute() {
                            reload();
                        }
                    }
            );

            toolBox.add(toolBar);

            // filter
            VerticalPanel filterPanel = new VerticalPanel();
            filterPanel.setStyleName("mosaic-ToolBar");
            final com.google.gwt.user.client.ui.ListBox dropBox = new com.google.gwt.user.client.ui.ListBox(false);
            dropBox.setStyleName("bpm-operation-ui");
            dropBox.addItem("All");
            dropBox.addItem("Active");
            dropBox.addItem("Retired");

            dropBox.addChangeHandler(new ChangeHandler() {

                public void onChange(ChangeEvent changeEvent) {
                    switch (dropBox.getSelectedIndex()) {
                        case 0:
                            currentFilter = FILTER_NONE;
                            break;
                        case 1:
                            currentFilter = FILTER_ACTIVE;
                            break;
                        case 2:
                            currentFilter = FILTER_SUSPENDED;
                            break;
                        default:
                            throw new IllegalArgumentException("No such index");
                    }

                    renderFiltered();
                }
            });
            filterPanel.add(dropBox);

            toolBox.add(filterPanel);

            definitionList.add(toolBox);
            definitionList.add(listBox);
            pagingPanel = new PagingPanel(
                    new PagingCallback() {
                        public void rev() {
                            renderFiltered();
                        }

                        public void ffw() {
                            renderFiltered();
                        }
                    }
            );
            definitionList.add(pagingPanel);

            // layout
            //MosaicPanel layout = new MosaicPanel(new BorderLayout());
            //layout.add(definitionList, new BorderLayoutData(BorderLayout.Region.CENTER));

            // details
            /*ProcessDetailView detailsView = new ProcessDetailView();
         controller.addView(ProcessDetailView.ID, detailsView);
         controller.addAction(UpdateProcessDetailAction.ID, new UpdateProcessDetailAction());
         layout.add(detailsView, new BorderLayoutData(BorderLayout.Region.SOUTH, 10,200));*/

            //panel.add(layout);

            panel.add(definitionList);

            // deployments model listener
            ErraiBus.get().subscribe(Model.SUBJECT,
                    new MessageCallback() {
                        public void callback(Message message) {
                            switch (ModelCommands.valueOf(message.getCommandType())) {
                                case HAS_BEEN_UPDATED:
                                    if (message.get(String.class, ModelParts.CLASS).equals(Model.DEPLOYMENT_MODEL)) {
                                        reload();
                                    }
                                    break;
                            }
                        }
                    });

            isInitialized = true;
        }
    }

    private void reload() {
        DeferredCommand.addCommand(
                new Command() {
                    public void execute() {
                        listBox.clear();

                        //JLIU: TODO
                        // force loading
/*                        controller.handleEvent(
                                new Event(UpdateDefinitionsAction.ID, null)
                        );*/
                    }
                }
        );
    }

    private CustomizableListBox createListBox() {
        final CustomizableListBox<ProcessDefinitionRef> listBox =
                new CustomizableListBox<ProcessDefinitionRef>(
                        new CustomizableListBox.ItemFormatter<ProcessDefinitionRef>() {
                            public String format(ProcessDefinitionRef processDefinitionRef) {
                                String result = "";

                                String name = processDefinitionRef.getName();
                                String s = name.indexOf("}") > 0 ?
                                        name.substring(name.lastIndexOf("}") + 1, name.length()) : name;

                                String color = processDefinitionRef.isSuspended() ? "#CCCCCC" : "#000000";
                                String text = "<div style=\"color:" + color + "\">" + s + "</div>";

                                result += new HTML(text);
                                result += " ";
                                result += String.valueOf(processDefinitionRef.getVersion());
                                result += " ";
                                result += String.valueOf(processDefinitionRef.isSuspended());

                                return result;
                            }
                        });

        listBox.setFirstLine("<b>Process</b>, v.");//, "Version", "Suspended"

        listBox.setFocus(true);

        listBox.addChangeHandler(
                new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        int index = listBox.getSelectedIndex();
                        if (index != -1) {
                            ProcessDefinitionRef item = listBox.getItem(index);

                            //JLIU: TODO
/*                            // load instances
                            controller.handleEvent(
                                    new Event(
                                            UpdateInstancesAction.ID,
                                            item
                                    )
                            );*/

                        }
                    }
                }
        );

        return listBox;
    }

    public void reset() {
        listBox.clear();

        //JLIU: TODO
/*        // clear instance panel
        controller.handleEvent(new Event(ClearInstancesAction.ID, null));*/

        // clear details
        /*controller.handleEvent(
            new Event(UpdateProcessDetailAction.ID, null)
        );*/

    }

    public void update(Object... data) {
        this.definitions = (List<ProcessDefinitionRef>) data[0];
        pagingPanel.reset();
        renderFiltered();
    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(panel, isLoading);
    }

    private void renderFiltered() {
        if (this.definitions != null) {
            reset();

            List<ProcessDefinitionRef> tmp = new ArrayList<ProcessDefinitionRef>();
            for (ProcessDefinitionRef def : definitions) {
                if (FILTER_NONE == currentFilter) {
                    tmp.add(def);
                } else {
                    boolean showSuspended = (FILTER_SUSPENDED == currentFilter);
                    if (def.isSuspended() == showSuspended) {
                        tmp.add(def);
                    }
                }
            }

            for (ProcessDefinitionRef def : (List<ProcessDefinitionRef>) pagingPanel.trim(tmp)) {
                listBox.addItem(def);
            }
        }
    }

    public ProcessDefinitionRef getSelection() {
        ProcessDefinitionRef selection = null;
        if (isInitialized() && listBox.getSelectedIndex() != -1) {
            selection = listBox.getItem(listBox.getSelectedIndex());
        }
        return selection;
    }

}
