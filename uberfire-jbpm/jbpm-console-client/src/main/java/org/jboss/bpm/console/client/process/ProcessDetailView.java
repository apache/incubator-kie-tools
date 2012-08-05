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

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.PropertyGrid;
import org.drools.guvnor.client.editors.jbpm.ServerPlugins;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.uberfire.client.annotations.WorkbenchEditor;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "ProcessDetailView")
public class ProcessDetailView extends VerticalPanel {

    public final static String ID = ProcessDetailView.class.getName();

    private PropertyGrid grid;

    private ProcessDefinitionRef currentProcess;

    private DeploymentPanel deploymentPanel;

    private boolean showDeployment;

    public ProcessDetailView() {
        //TODO -Rikkola-
//    super("Process details");
        super.setStyleName("bpm-detail-panel");

        grid = new PropertyGrid(
                new String[]{"ID:", "Key:", "Name:", "Suspended:", "Package:", "Description:"}
        );

        // properties
        final DeckPanel deck = new DeckPanel();
        deck.add(grid);

        // selection
        final com.google.gwt.user.client.ui.ListBox dropBox = new com.google.gwt.user.client.ui.ListBox(false);
        dropBox.setStyleName("bpm-operation-ui");
        dropBox.addItem("Properties");

        // deployment info
        if (ServerPlugins.has("org.jboss.bpm.console.server.plugin.ProcessEnginePlugin")) {
            dropBox.addItem("Deployment");
            deploymentPanel = new DeploymentPanel();
            deck.add(deploymentPanel);
        }

        dropBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                deck.showWidget(dropBox.getSelectedIndex());
            }
        });

        //TODO: -Rikkola-
//    this.getHeader().add(dropBox, Caption.CaptionRegion.RIGHT);
        this.add(deck);

        deck.showWidget(dropBox.getSelectedIndex());

        this.add(deck);

    }
    
    //JLIU:TODO
/*
    public void setController(Controller controller) {
        this.controller = controller;
        if (deploymentPanel != null)//if(ServerPlugins.has("org.jboss.bpm.console.server.plugin.ProcessEnginePlugin"))
        {
            deploymentPanel.setController(controller);
        }
    }*/

    public void update(ProcessDefinitionRef process) {
        this.currentProcess = process;

        String[] values = new String[]{
                process.getId(),
                process.getKey(),
                process.getName(),
                String.valueOf(process.isSuspended()),
                process.getPackageName(),
                process.getDescription()
        };

        grid.update(values);

        if (ServerPlugins.has("org.jboss.bpm.console.server.plugin.ProcessEnginePlugin")) {
            deploymentPanel.update(process.getDeploymentId());
        }
    }

    public void clearView() {
        grid.clear();
        if (ServerPlugins.has("org.jboss.bpm.console.server.plugin.ProcessEnginePlugin")) {
            deploymentPanel.clearView();
        }
        this.currentProcess = null;
    }
}
