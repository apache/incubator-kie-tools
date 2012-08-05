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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.PropertyGrid;
//import org.jboss.bpm.console.client.engine.ViewDeploymentAction;
import org.drools.guvnor.client.util.ConsoleLog;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class DeploymentPanel extends HorizontalPanel {

    private PropertyGrid propGrid;

    String deploymentId = null;

    private boolean initialized;

    private void initialize() {
        if (!initialized) {
            this.propGrid = new PropertyGrid(new String[]{"Deployment ID:"});

            this.add(propGrid);
            final Button button = new Button("View Deployment", new ClickListener() {

                public void onClick(Widget widget) {
                	//JLIU: TODO
/*                    controller.handleEvent(
                            new Event(ViewDeploymentAction.ID, getSelection())
                    );*/
                }
            });

            this.add(button);

            this.initialized = true;
        }
    }

    public void update(String id) {
        initialize();

        if (id != null) {
            this.deploymentId = id;
            propGrid.update(new String[]{id});
        } else {
            ConsoleLog.warn("deploymentId is null");
        }
    }

    public void clearView() {
        initialize();
        this.deploymentId = null;
        propGrid.clear();
    }

    private String getSelection() {
        return this.deploymentId;
    }
}
