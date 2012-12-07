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
package org.uberfire.client.workbench.widgets.panels;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.annotations.RootWorkbenchPanel;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.widgets.events.MinimizePlaceEvent;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@RootWorkbenchPanel
public class RootWorkbenchPanelPresenter implements WorkbenchPanelPresenter {

    private WorkbenchPanelView view;

    private PanelManager panelManager;

    private PanelDefinition definition;

    private List<PartDefinition> orderedParts = new ArrayList<PartDefinition>();

    private Event<MaximizePlaceEvent> maximizePanelEvent;

    private Event<MinimizePlaceEvent> minimizePanelEvent;

    @Inject
    public RootWorkbenchPanelPresenter( @RootWorkbenchPanel final WorkbenchPanelView view,
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

    public PanelDefinition getDefinition() {
        return definition;
    }

    public void setDefinition( final PanelDefinition definition ) {
        this.definition = definition;
    }

    public void addPart( final PartDefinition part,
                         final IsWidget titleWidget,
                         final WorkbenchPartPresenter.View view ) {
        getPanelView().addPart( titleWidget,
                                view );
        if ( !orderedParts.contains( part ) ) {
            orderedParts.add( part );
        }
    }

    public void removePart( final PartDefinition part ) {
        if ( !contains( part ) ) {
            return;
        }
        if ( orderedParts.contains( part ) ) {
            final int indexOfPartToRemove = orderedParts.indexOf( part );
            view.removePart( indexOfPartToRemove );
            orderedParts.remove( part );
        }
    }

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {
        getPanelView().addPanel( panel,
                                 view,
                                 position );
        definition.insertChild( position,
                                panel );
    }

    public void removePanel() {
        view.removePanel();
    }

    public void changeTitle( final PartDefinition part,
                             final IsWidget titleWidget ) {
        if ( !contains( part ) ) {
            return;
        }
        if ( orderedParts.contains( part ) ) {
            final int indexOfPartToChangeTabContent = orderedParts.indexOf( part );
            getPanelView().changeTitle( indexOfPartToChangeTabContent,
                                        titleWidget );
        }
    }

    public void setFocus( final boolean hasFocus ) {
        view.setFocus( hasFocus );
    }

    public void selectPart( final PartDefinition part ) {
        if ( !contains( part ) ) {
            return;
        }
        if ( orderedParts.contains( part ) ) {
            final int indexOfPartToSelect = orderedParts.indexOf( part );
            view.selectPart( indexOfPartToSelect );
        }
    }

    private boolean contains( final PartDefinition part ) {
        return definition.getParts().contains( part );
    }

    public void onPartFocus( final PartDefinition part ) {
        panelManager.onPartFocus( part );
    }

    public void onPartLostFocus() {
        panelManager.onPartLostFocus();
    }

    public void onPanelFocus() {
        panelManager.onPanelFocus( definition );
    }

    public void onBeforePartClose( final PartDefinition part ) {
        panelManager.onBeforePartClose( part );
    }

    public void maximize() {
        if ( !getDefinition().isRoot() ) {
            for ( PartDefinition part : getDefinition().getParts() ) {
                maximizePanelEvent.fire( new MaximizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    public void minimize() {
        if ( !getDefinition().isRoot() ) {
            for ( PartDefinition part : getDefinition().getParts() ) {
                minimizePanelEvent.fire( new MinimizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    public WorkbenchPanelView getPanelView() {
        return view;
    }

    public void onResize( final int width,
                          final int height ) {
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }

}
