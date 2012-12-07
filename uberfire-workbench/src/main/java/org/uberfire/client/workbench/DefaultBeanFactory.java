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

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.workbench.annotations.DecoratedWorkbenchPanel;
import org.uberfire.client.workbench.annotations.RootWorkbenchPanel;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.panels.DecoratedWorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.HorizontalSplitterPanel;
import org.uberfire.client.workbench.widgets.panels.RootWorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.VerticalSplitterPanel;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPartPresenter;

/**
 * BeanFactory using Errai IOCBeanManager to instantiate (CDI) beans
 */
@ApplicationScoped
public class DefaultBeanFactory
        implements
        BeanFactory {

    @Inject
    private IOCBeanManager iocManager;

    private static Annotation WORKBENCH_PANEL = new Annotation() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return DecoratedWorkbenchPanel.class;
        }
    };

    private static Annotation WORKBENCH_ROOT_PANEL = new Annotation() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return RootWorkbenchPanel.class;
        }
    };

    @Override
    public WorkbenchPartPresenter newWorkbenchPart( final IsWidget titleWidget,
                                                    final PartDefinition definition ) {
        final WorkbenchPartPresenter part = iocManager.lookupBean( WorkbenchPartPresenter.class ).getInstance();
        part.setTitleWidget( titleWidget );
        part.setDefinition( definition );
        return part;
    }

    @Override
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition ) {
        if ( definition.isRoot() ) {
            final WorkbenchPanelPresenter panel = iocManager.lookupBean( RootWorkbenchPanelPresenter.class,
                                                                         WORKBENCH_ROOT_PANEL ).getInstance();
            panel.setDefinition( definition );
            return panel;
        }
        final WorkbenchPanelPresenter panel = iocManager.lookupBean( DecoratedWorkbenchPanelPresenter.class,
                                                                     WORKBENCH_PANEL ).getInstance();
        panel.setDefinition( definition );
        return panel;
    }

    @Override
    public HorizontalSplitterPanel newHorizontalSplitterPanel( final WorkbenchPanelView eastPanel,
                                                               final WorkbenchPanelView westPanel,
                                                               final Position position,
                                                               final Integer preferredSize,
                                                               final Integer preferredMinSize ) {
        final HorizontalSplitterPanel hsp = iocManager.lookupBean( HorizontalSplitterPanel.class ).getInstance();
        hsp.setup( eastPanel,
                   westPanel,
                   position,
                   preferredSize,
                   preferredMinSize );
        return hsp;
    }

    @Override
    public VerticalSplitterPanel newVerticalSplitterPanel( final WorkbenchPanelView northPanel,
                                                           final WorkbenchPanelView southPanel,
                                                           final Position position,
                                                           final Integer preferredSize,
                                                           final Integer preferredMinSize ) {
        final VerticalSplitterPanel vsp = iocManager.lookupBean( VerticalSplitterPanel.class ).getInstance();
        vsp.setup( northPanel,
                   southPanel,
                   position,
                   preferredSize,
                   preferredMinSize );
        return vsp;
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
