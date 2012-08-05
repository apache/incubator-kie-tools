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

import org.uberfire.client.annotations.WorkbenchEditor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
//import org.jboss.bpm.console.client.BpmConsoleClientFactory;

/**
 * Combined view of process and instance data in a single screen
 */
@Dependent
@WorkbenchEditor(identifier = "MergedProcessView")
public class MergedProcessView implements IsWidget {

    //private BpmConsoleClientFactory clientFactory;

    public MergedProcessView(/*BpmConsoleClientFactory clientFactory*/) {
        //this.clientFactory = clientFactory;
    }

    public Widget asWidget() {

        final SplitLayoutPanel splitPanel = new SplitLayoutPanel();
        splitPanel.setHeight("100%");

        splitPanel.addWest(new DefinitionListView(), 250);

        splitPanel.add(new InstanceListView());

        return splitPanel;
    }
}
