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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
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
 * Maciej Swiderski <swiderski.maciej@gmail.com>
 */
@Dependent
@WorkbenchEditor(identifier = "DefinitionHistoryListView")
public class DefinitionHistoryListView implements IsWidget, DataDriven {

    public final static String ID = DefinitionHistoryListView.class.getName();

//  private Controller controller;

    private VerticalPanel definitionList = null;

    private CustomizableListBox<ProcessDefinitionRef> listBox;

    private boolean isInitialized;

    private List<ProcessDefinitionRef> definitions = null;
    private PagingPanel pagingPanel;

    private SimplePanel panel;

    public Widget asWidget() {

        panel = new SimplePanel();

        listBox = createListBox();

        //TODO: -Rikkola-
//    final Controller controller = Registry.get(Controller.class);
//    controller.addView(ID, this);
//
//    controller.addAction(UpdateHistoryDefinitionAction.ID, new UpdateHistoryDefinitionAction());
//    controller.addAction(UpdateHistoryInstanceAction.ID, new UpdateHistoryInstanceAction());
//    controller.addAction(LoadHistoryDiagramAction.ID, new LoadHistoryDiagramAction());
//    controller.addAction(UpdateHistoryDefinitionsAction.ID, new UpdateHistoryDefinitionsAction());

        initialize();

//        Timer t = new Timer() {
//            @Override
//            public void run() {
//                controller.handleEvent(
//                        new Event(UpdateHistoryDefinitionsAction.ID, null)
//                );
//            }
//        };

//        t.schedule(500);

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

                        // force loading
                        //TODO: -Rikkola-
//                        controller.handleEvent(
//                                new Event(UpdateHistoryDefinitionsAction.ID, null)
//                        );
                    }
                }
        );
    }

    private CustomizableListBox<ProcessDefinitionRef> createListBox() {
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
                        }
                );
        listBox.setFirstLine("<b>Process</b>, v.");//, "Version", "Suspended"

        listBox.setFocus(true);

        listBox.addChangeHandler(
                new ChangeHandler() {
                    public void onChange(ChangeEvent event) {

                        int index = listBox.getSelectedIndex();
                        if (index != -1) {
                            ProcessDefinitionRef item = listBox.getItem(index);

                            // load history instances
                            //TODO: -Rikkola-
//                            controller.handleEvent(
//                                    new Event(
//                                            UpdateHistoryDefinitionAction.ID,
//                                            item
//                                    )
//                            );

                        }
                    }
                }
        );

        return listBox;
    }

    public void reset() {
        listBox.clear();
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

                tmp.add(def);

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

