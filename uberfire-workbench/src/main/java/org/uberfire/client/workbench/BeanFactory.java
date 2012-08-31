/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.panels.HorizontalSplitterPanel;
import org.uberfire.client.workbench.widgets.panels.VerticalSplitterPanel;

/**
 * A convenience class to create new instances of managed beans.
 */
@ApplicationScoped
public class BeanFactory {

    @Inject
    private IOCBeanManager iocManager;

    public WorkbenchPart newWorkbenchPart() {
        final WorkbenchPart part = iocManager.lookupBean( WorkbenchPart.class ).getInstance();
        return part;
    }

    public WorkbenchPanel newWorkbenchPanel() {
        final WorkbenchPanel panel = iocManager.lookupBean( WorkbenchPanel.class ).getInstance();
        return panel;
    }

    public WorkbenchPanel newWorkbenchPanel(final WorkbenchPart part) {
        final WorkbenchPanel panel = iocManager.lookupBean( WorkbenchPanel.class ).getInstance();
        panel.addPart( part );
        return panel;
    }

    public HorizontalSplitterPanel newHorizontalSplitterPanel(final WorkbenchPanel eastPanel,
                                                              final WorkbenchPanel westPanel,
                                                              final Position position) {
        final HorizontalSplitterPanel hsp = iocManager.lookupBean( HorizontalSplitterPanel.class ).getInstance();
        hsp.setup( eastPanel,
                   westPanel,
                   position );
        return hsp;
    }

    public VerticalSplitterPanel newVerticalSplitterPanel(final WorkbenchPanel northPanel,
                                                          final WorkbenchPanel southPanel,
                                                          final Position position) {
        final VerticalSplitterPanel vsp = iocManager.lookupBean( VerticalSplitterPanel.class ).getInstance();
        vsp.setup( northPanel,
                   southPanel,
                   position );
        return vsp;
    }

    public CompassDropController newDropController(final WorkbenchPanel panel) {
        final CompassDropController dropController = iocManager.lookupBean( CompassDropController.class ).getInstance();
        dropController.setup( panel );
        return dropController;
    }

    public void destroy(final Object o) {
        iocManager.destroyBean( o );
    }

}
