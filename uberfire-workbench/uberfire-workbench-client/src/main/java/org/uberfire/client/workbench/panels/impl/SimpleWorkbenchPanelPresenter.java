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
package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class SimpleWorkbenchPanelPresenter implements WorkbenchPanelPresenter {

    private SimpleWorkbenchPanelView view;

    private PanelManager panelManager;

    private PanelDefinition definition;

    private Event<MaximizePlaceEvent> maximizePanelEvent;

    private Event<MinimizePlaceEvent> minimizePanelEvent;

    @Inject
    public SimpleWorkbenchPanelPresenter( @Named("SimpleWorkbenchPanelView") final SimpleWorkbenchPanelView view,
                                          final PanelManager panelManager,
                                          final Event<MaximizePlaceEvent> maximizePanelEvent,
                                          final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        this.view = view;
        this.panelManager = panelManager;
        this.maximizePanelEvent = maximizePanelEvent;
        this.minimizePanelEvent = minimizePanelEvent;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        view.init( this );
    }

    public void enableDnd() {
        view.enableDnd();
    }

    @Override
    public PanelDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition( final PanelDefinition definition ) {
        this.definition = definition;
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        getPanelView().addPart( view );
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view,
                         String contextId ) {
        getPanelView().addPart( view );
    }

    @Override
    public void removePart( final PartDefinition part ) {
        view.removePart( part );
    }

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {
        getPanelView().addPanel( panel,
                                 view,
                                 position );
        definition.insertChild( position,
                                panel );
    }

    @Override
    public void removePanel() {
        view.removePanel();
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDescorator ) {
        getPanelView().changeTitle( part, title, titleDescorator );
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
        view.setFocus( hasFocus );
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        if ( !contains( part ) ) {
            return;
        }
        view.selectPart( part );
    }

    private boolean contains( final PartDefinition part ) {
        return definition.getParts().contains( part );
    }

    @Override
    public void onPartFocus( final PartDefinition part ) {
        panelManager.onPartFocus( part );
    }

    @Override
    public void onPartLostFocus() {
        panelManager.onPartLostFocus();
    }

    @Override
    public void onPanelFocus() {
        panelManager.onPanelFocus( definition );
    }

    @Override
    public void onBeforePartClose( final PartDefinition part ) {
        panelManager.onBeforePartClose( part );
    }

    @Override
    public void maximize() {
        if ( !getDefinition().isRoot() ) {
            for ( final PartDefinition part : getDefinition().getParts() ) {
                maximizePanelEvent.fire( new MaximizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public void minimize() {
        if ( !getDefinition().isRoot() ) {
            for ( final PartDefinition part : getDefinition().getParts() ) {
                minimizePanelEvent.fire( new MinimizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public WorkbenchPanelView getPanelView() {
        return view;
    }

    @Override
    public void onResize( final int width,
                          final int height ) {
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }
}
