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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.drools.guvnor.client.common.CustomizableListBox;
import org.jboss.bpm.console.client.model.TaskRef;

/**
 * Base class for task lists.
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public abstract class AbstractTaskList /*implements ViewInterface*/ {

    //protected Controller controller;
    protected VerticalPanel taskList = null;
    protected CustomizableListBox<TaskRef> listBox;
    protected boolean isInitialized;
    protected String identity;
    protected List<TaskRef> cachedTasks;

    public AbstractTaskList() {
        super();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setController(/*Controller controller*/) {
        //this.controller = controller;
    }

    public TaskRef getSelection() {
        TaskRef selection = null;
        if (isInitialized() && listBox.getSelectedIndex() != -1) {
            selection = listBox.getItem(listBox.getSelectedIndex());
        }
        return selection;
    }

    public String getAssignedIdentity() {
        //TODO: -Rikkola-
//    return Registry.get(Authentication.class).getUsername();
        return null;
    }
}
