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
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
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

    public WorkbenchPartPresenter newWorkbenchPart(final String title,
                                          final PartDefinition definition) {
        final WorkbenchPartPresenter part = iocManager.lookupBean( WorkbenchPartPresenter.class ).getInstance();
        part.setTitle( title );
        part.setDefinition( definition );
        return part;
    }

    public WorkbenchPanelPresenter newWorkbenchPanel(final PanelDefinition definition) {
        final WorkbenchPanelPresenter panel = iocManager.lookupBean( WorkbenchPanelPresenter.class ).getInstance();
        panel.setDefinition( definition );
        return panel;
    }

    public HorizontalSplitterPanel newHorizontalSplitterPanel(final WorkbenchPanelPresenter.View eastPanel,
                                                              final WorkbenchPanelPresenter.View westPanel,
                                                              final Position position) {
        final HorizontalSplitterPanel hsp = iocManager.lookupBean( HorizontalSplitterPanel.class ).getInstance();
        hsp.setup( eastPanel,
                   westPanel,
                   position );
        return hsp;
    }

    public VerticalSplitterPanel newVerticalSplitterPanel(final WorkbenchPanelPresenter.View northPanel,
                                                          final WorkbenchPanelPresenter.View southPanel,
                                                          final Position position) {
        final VerticalSplitterPanel vsp = iocManager.lookupBean( VerticalSplitterPanel.class ).getInstance();
        vsp.setup( northPanel,
                   southPanel,
                   position );
        return vsp;
    }

    public CompassDropController newDropController(final WorkbenchPanelPresenter.View view) {
        final CompassDropController dropController = iocManager.lookupBean( CompassDropController.class ).getInstance();
        dropController.setup( view );
        return dropController;
    }

    public void destroy(final Object o) {
        iocManager.destroyBean( o );
    }

}
