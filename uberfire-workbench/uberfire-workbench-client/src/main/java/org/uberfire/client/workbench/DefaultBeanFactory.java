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

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * BeanFactory using Errai IOCBeanManager to instantiate (CDI) beans
 */
@ApplicationScoped
public class DefaultBeanFactory
implements BeanFactory {

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
    public WorkbenchPanelPresenter newRootPanel( PerspectiveActivity activity,
                                                 PanelDefinition root ) {
        WorkbenchPanelPresenter panel = newWorkbenchPanel( root );
        if ( panel instanceof TemplatedWorkbenchPanelPresenter ) {
            ((TemplatedWorkbenchPanelPresenter) panel).setActivity( (TemplatedActivity) activity );
        }
        return panel;
    }

    @Override
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition ) {
        Collection<IOCBeanDef<WorkbenchPanelPresenter>> beans = iocManager.lookupBeans( WorkbenchPanelPresenter.class );
        for ( IOCBeanDef<WorkbenchPanelPresenter> bean : beans ) {
            if ( bean.getBeanClass().getName().equals( definition.getPanelType() ) ) {
                final WorkbenchPanelPresenter panel = bean.getInstance();
                panel.setDefinition( definition );
                return panel;
            }
        }
        throw new IllegalArgumentException( "Unknown panel type: " + definition.getPanelType() );
    }

    @Override
    public CompassDropController newDropController( final WorkbenchPanelView<?> view ) {
        final CompassDropController dropController = iocManager.lookupBean( CompassDropController.class ).getInstance();
        dropController.setup( view );
        return dropController;
    }

    @Override
    public void destroy( final Object o ) {
        iocManager.destroyBean( o );
    }

}
