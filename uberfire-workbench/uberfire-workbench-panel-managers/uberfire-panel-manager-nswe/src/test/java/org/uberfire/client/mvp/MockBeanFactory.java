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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.NSWEExtendedBeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.HorizontalSplitterPanel;
import org.uberfire.client.workbench.panels.impl.VerticalSplitterPanel;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenterDefault;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Mock BeanFactory that doesn't use CDI.
 */
public class MockBeanFactory
        implements
        NSWEExtendedBeanFactory {

    @Override
    public WorkbenchPartPresenter newWorkbenchPart( final Menus menus,
                                                    final String title,
                                                    final IsWidget titleDecoration,
                                                    final PartDefinition definition ) {
        final WorkbenchPartPresenterDefault part = new WorkbenchPartPresenterDefault( new MockWorkbenchPartView());
        part.init(  );
        part.setTitle( title );
        part.setTitleDecoration( titleDecoration );
        part.setDefinition( definition );
        return part;
    }

    @Override
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition ) {
//        final MultiTabWorkbenchPanelPresenter panel = new MultiTabWorkbenchPanelPresenter( new MockWorkbenchPanelView(),
//                                                                                   null,
//                                                                                   null,
//                                                                                   null );
//        panel.setDefinition( definition );
        return null;
    }

    @Override
    public CompassDropController newDropController( final WorkbenchPanelView view ) {
        return null;
    }

    @Override
    public void destroy( final Object o ) {
    }

    @Override
    public HorizontalSplitterPanel newHorizontalSplitterPanel( WorkbenchPanelView eastPanel,
                                                               WorkbenchPanelView westPanel,
                                                               Position position,
                                                               Integer preferredSize,
                                                               Integer preferredMinSize ) {
        return null;
    }

    @Override
    public VerticalSplitterPanel newVerticalSplitterPanel( WorkbenchPanelView northPanel,
                                                           WorkbenchPanelView southPanel,
                                                           Position position,
                                                           Integer preferredSize,
                                                           Integer preferredMinSize ) {
        return null;
    }
}
