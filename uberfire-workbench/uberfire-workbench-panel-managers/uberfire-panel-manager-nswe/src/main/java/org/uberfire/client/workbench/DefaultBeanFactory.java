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

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.LayoutPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SplitLayoutPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * BeanFactory using Errai IOCBeanManager to instantiate (CDI) beans
 */
@ApplicationScoped
public class DefaultBeanFactory
        implements
        BeanFactory {

    @Inject
    protected SyncBeanManager iocManager;

    @Override
    public WorkbenchPartPresenter newWorkbenchPart( final Menus menus,
                                                    final String title,
                                                    final IsWidget titleDecoration,
                                                    final PartDefinition definition ) {
        final WorkbenchPartPresenter part = iocManager.lookupBean( WorkbenchPartPresenter.class ).getInstance();
        part.setTitle( title );
        part.setMenus( menus );
        part.setTitleDecoration( titleDecoration );
        part.setDefinition( definition );
        return part;
    }

    @Override
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition ) {
        final WorkbenchPanelPresenter panel;
        switch ( definition.getPanelType() ) {
            case ROOT_TAB:
            case MULTI_TAB:
                panel = iocManager.lookupBean( MultiTabWorkbenchPanelPresenter.class ).getInstance();
                break;

            case ROOT_LIST:
            case MULTI_LIST:
                panel = iocManager.lookupBean( MultiListWorkbenchPanelPresenter.class ).getInstance();
                break;

            case ROOT_SIMPLE:
            case SIMPLE:
                panel = iocManager.lookupBean( SimpleWorkbenchPanelPresenter.class ).getInstance();
                break;

            case SIMPLE_DND:
                panel = iocManager.lookupBean( SimpleWorkbenchPanelPresenter.class ).getInstance();
                ( (SimpleWorkbenchPanelPresenter) panel ).enableDnd();
                break;

            case ROOT_STATIC:
            case STATIC:
                panel = iocManager.lookupBean( StaticWorkbenchPanelPresenter.class ).getInstance();
                break;
            case ROOT_SPLIT_LAYOUT:
                panel = iocManager.lookupBean( SplitLayoutPanelPresenter.class ).getInstance();
                break;
            case LAYOUT_PANEL:
            case ROOT_LAYOUT_PANEL:
                panel = iocManager.lookupBean( LayoutPanelPresenter.class ).getInstance();
                break;

            default:
                throw new IllegalArgumentException( "Unhandled PanelType '"+definition.getPanelType()+"': Expect subsequent errors." );
        }

        panel.setDefinition( definition );

        return panel;
    }

    @Override
    public CompassDropController newDropController( final WorkbenchPanelView view ) {
        final CompassDropController dropController = iocManager.lookupBean( CompassDropController.class ).getInstance();
        dropController.setup( view );
        return dropController;
    }

    @Override
    public void destroy( final Object o ) {
        iocManager.destroyBean( o );
    }

}
