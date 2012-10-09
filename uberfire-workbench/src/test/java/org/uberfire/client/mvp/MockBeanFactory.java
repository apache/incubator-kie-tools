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
package org.uberfire.client.mvp;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPartPresenter;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.panels.HorizontalSplitterPanel;
import org.uberfire.client.workbench.widgets.panels.VerticalSplitterPanel;

/**
 * Mock BeanFactory that doesn't use CDI.
 */
public class MockBeanFactory
    implements
    BeanFactory {

    @Override
    public WorkbenchPartPresenter newWorkbenchPart(final String title,
                                                   final PartDefinition definition) {
        final WorkbenchPartPresenter part = new WorkbenchPartPresenter( new MockWorkbenchPartView() );
        part.setTitle( title );
        part.setDefinition( definition );
        return part;
    }

    @Override
    public WorkbenchPanelPresenter newWorkbenchPanel(final PanelDefinition definition) {
        final WorkbenchPanelPresenter panel = new WorkbenchPanelPresenter( new MockWorkbenchPanelView(),
                                                                           null );
        panel.setDefinition( definition );
        return panel;
    }

    @Override
    public HorizontalSplitterPanel newHorizontalSplitterPanel(final WorkbenchPanelPresenter.View eastPanel,
                                                              final WorkbenchPanelPresenter.View westPanel,
                                                              final Position position,
                                                              final Integer preferredSize,
                                                              final Integer preferredMinSize) {
        return null;
    }

    @Override
    public VerticalSplitterPanel newVerticalSplitterPanel(final WorkbenchPanelPresenter.View northPanel,
                                                          final WorkbenchPanelPresenter.View southPanel,
                                                          final Position position,
                                                          final Integer preferredSize,
                                                          final Integer preferredMinSize) {
        return null;
    }

    @Override
    public CompassDropController newDropController(final WorkbenchPanelPresenter.View view) {
        return null;
    }

    @Override
    public void destroy(final Object o) {
    }

}
