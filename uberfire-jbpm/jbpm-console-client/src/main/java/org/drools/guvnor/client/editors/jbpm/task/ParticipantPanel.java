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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
/*import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;*/
import org.jboss.bpm.console.client.model.ParticipantRef;
import org.jboss.bpm.console.client.model.TaskRef;
//import org.jboss.bpm.console.client.task.events.AssignEvent;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
class ParticipantPanel extends HorizontalPanel /*implements ViewInterface */{

    public final static String ID = ParticipantPanel.class.getName();

    public final static String PARTICIPANTS = "Participants";
    public final static String GROUPS = "Groups";
    public final static String USERS = "Users";

    //private Controller controller;

    private Tree tree;
    private Button assignmentBtn;
    private String selection = null;
    private TaskRef currentTask;

    public ParticipantPanel() {
        setSpacing(5);

        // particpants
        ScrollPanel treePanel = new ScrollPanel();

        treePanel.setStyleName("bpm-property-box");

        tree = new Tree();
        treePanel.add(tree);
        this.add(treePanel);

        tree.addTreeListener(
                new TreeListener() {
                    public void onTreeItemSelected(TreeItem treeItem) {
                        String name = treeItem.getText();
                        if (
                                !name.equals(PARTICIPANTS)
                                        & !name.equals(GROUPS)
                                        & !name.equals(USERS)
                                ) {
                            selection = name;
                            assignmentBtn.setEnabled(true);
                        } else {
                            selection = null;
                            assignmentBtn.setEnabled(false);
                        }
                    }

                    public void onTreeItemStateChanged(TreeItem treeItem) {

                    }
                }
        );

        // operations
        assignmentBtn = new Button(
                "Assign",
                new ClickListener() {

                    public void onClick(Widget widget) {
                        if (selection != null) {
                        	//JLIU: TODO:
/*                            controller.handleEvent(
                                    new Event(AssignTaskAction.ID, new AssignEvent(selection, currentTask))
                            );*/
                        }
                    }
                }
        );

        assignmentBtn.setStyleName("bpm-operation-ui");
        assignmentBtn.setEnabled(false);
        add(assignmentBtn);
    }
/*
    public void setController(Controller controller) {
        this.controller = controller;
    }
*/
    public void update(TaskRef task) {
        currentTask = task;

        tree.clear();

        TreeItem root = tree.addItem(PARTICIPANTS);

        // groups
        TreeItem groups = root.addItem(GROUPS);
        for (ParticipantRef gref : task.getParticipantGroups()) {
            groups.addItem(gref.getIdRef());
        }

        // users
        TreeItem users = root.addItem(USERS);
        for (ParticipantRef uref : task.getParticipantUsers()) {
            users.addItem(uref.getIdRef());
        }

        root.setState(true); // open users

        // TODO: -Rikkola-
//        this.invalidate();
    }

    public void clearView() {
        tree.clear();
        currentTask = null;
    }
}