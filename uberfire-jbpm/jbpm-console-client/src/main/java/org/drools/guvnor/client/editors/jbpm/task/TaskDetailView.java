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

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
/*import com.mvc4g.client.Controller;
import com.mvc4g.client.ViewInterface;*/
/*import org.jboss.bpm.console.client.LazyPanel;
import org.jboss.bpm.console.client.common.PropertyGrid;*/
import org.drools.guvnor.client.LazyPanel;
import org.drools.guvnor.client.common.PropertyGrid;
import org.jboss.bpm.console.client.model.TaskRef;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class TaskDetailView extends SimplePanel
        implements /*ViewInterface, */LazyPanel {

    public final static String ID = TaskDetailView.class.getName();

    //private Controller controller;

    private TaskRef currentTask = null;

    private PropertyGrid grid;
    private ParticipantPanel participantPanel;

    private boolean openView;

    private boolean initialzed;

    public TaskDetailView(boolean openView) {

        // render
        // TODO: -Rikkola-
//    super("Task details");

        super.setStyleName("bpm-detail-panel");

        this.openView = openView;

    }

    public boolean isInitialized() {
        return initialzed;
    }

    public void initialize() {
        if (!this.initialzed) {

            grid = new PropertyGrid(
                    new String[]{"ID:", "Process:", "Name:", "Assignee:", "Description:"}
            );

            if (openView) {
                // properties
                final DeckPanel deck = new DeckPanel();
                deck.add(grid);

                // participants
                participantPanel = new ParticipantPanel();
                //participantPanel.setController(controller);

                // selection
                final com.google.gwt.user.client.ui.ListBox dropBox = new com.google.gwt.user.client.ui.ListBox(false);
                dropBox.setStyleName("bpm-operation-ui");
                dropBox.addItem("Properties");
                dropBox.addItem("Participants");
                dropBox.addChangeListener(new ChangeListener() {
                    public void onChange(Widget sender) {
                        deck.showWidget(dropBox.getSelectedIndex());
                    }
                });

                deck.add(participantPanel);

                // TODO: -Rikkola-
//        this.getHeader().add(dropBox, Caption.CaptionRegion.RIGHT);
                this.add(deck);

                deck.showWidget(dropBox.getSelectedIndex());
            } else {
                SimplePanel p = new SimplePanel();
                p.add(grid);
                this.add(p);
            }

            this.initialzed = true;
        }
    }
/*
    public void setController(Controller controller) {
        this.controller = controller;
    }*/

    public void update(TaskRef task) {
        String description = task.getDescription() != null ? task.getDescription() : "";

        String[] values = new String[]{
                String.valueOf(task.getId()),
                task.getProcessId(),
                task.getName(),
                task.getAssignee(),
                description
        };

        grid.update(values);

        if (openView && participantPanel != null) {
            participantPanel.update(task);
        }
    }

    public void clearView() {
        grid.clear();

        if (openView && participantPanel != null) // event handling is broken. The change listener fires too early
        {
            participantPanel.clearView();
        }

        currentTask = null;
    }

}
