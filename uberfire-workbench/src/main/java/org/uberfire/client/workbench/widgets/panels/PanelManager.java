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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPartPresenter;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
@ApplicationScoped
public class PanelManager {

    @Inject
    private BeanFactory                                   factory;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent>             workbenchPanelOnFocusEvent;

    private PanelDefinition                               root                          = null;

    private PerspectiveDefinition                         perspective;

    private Map<PartDefinition, WorkbenchPartPresenter>   mapPartDefinitionToPresenter  = new HashMap<PartDefinition, WorkbenchPartPresenter>();

    private Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();

    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    public void setPerspective(final PerspectiveDefinition perspective) {
        final PanelDefinition perspectiveRootPanel = perspective.getRoot();
        final WorkbenchPanelPresenter rootPresenter = mapPanelDefinitionToPresenter.remove( root );
        mapPanelDefinitionToPresenter.put( perspectiveRootPanel,
                                           rootPresenter );
        rootPresenter.setDefinition( perspectiveRootPanel );
        this.root = perspectiveRootPanel;
        this.perspective = perspective;
    }

    public PanelDefinition getRoot() {
        return this.root;
    }

    public void setRoot(final PanelDefinition panel) {
        if ( !panel.isRoot() ) {
            throw new IllegalArgumentException( "Panel is not a root panel." );
        }

        if ( panel.isRoot() ) {
            if ( root == null ) {
                this.root = panel;
            } else {
                throw new IllegalArgumentException( "Root has already been set. Unable to set root." );
            }
        }

        WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            panelPresenter = factory.newWorkbenchPanel( panel );
            mapPanelDefinitionToPresenter.put( panel,
                                               panelPresenter );
        }

        setFocus( panel );
    }

    public PanelDefinition addWorkbenchPart(final String title,
                                            final PartDefinition part,
                                            final PanelDefinition panel,
                                            final IsWidget partWidget) {
        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            throw new IllegalArgumentException( "Unable to add Part to Panel. Panel has not been created." );
        }

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = factory.newWorkbenchPart( title,
                                                      part );
            partPresenter.setWrappedWidget( partWidget );
            mapPartDefinitionToPresenter.put( part,
                                              partPresenter );
        }

        panelPresenter.addPart( title,
                                part,
                                partPresenter.getPartView() );
        setFocus( panel );
        return panelPresenter.getDefinition();
    }

    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final Position position) {
        final PanelDefinition childPanel = new PanelDefinition();
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final Position position,
                                             final Integer height,
                                             final Integer width,
                                             final Integer minHeight,
                                             final Integer minWidth) {
        final PanelDefinition childPanel = new PanelDefinition();
        childPanel.setHeight( height );
        childPanel.setWidth( width );
        childPanel.setMinHeight( minHeight );
        childPanel.setMinWidth( minWidth );
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final PanelDefinition childPanel,
                                             final Position position) {

        PanelDefinition newPanel = null;

        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        switch ( position ) {
            case ROOT :
                newPanel = root;
                break;

            case SELF :
                newPanel = targetPanelPresenter.getDefinition();
                break;

            case NORTH :
            case SOUTH :
            case EAST :
            case WEST :

                final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                mapPanelDefinitionToPresenter.put( childPanel,
                                                   childPanelPresenter );

                targetPanelPresenter.addPanel( childPanel,
                                               childPanelPresenter.getPanelView(),
                                               position );
                newPanel = childPanelPresenter.getDefinition();
                break;

            default :
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        setFocus( childPanel );
        return newPanel;
    }

    private void setFocus(final PanelDefinition panel) {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( panel ) );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final PanelDefinition panel = event.getPanel();
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            e.getValue().setFocus( e.getKey().equals( panel ) );
        }
    }

    @SuppressWarnings("unused")
    private void onSelectWorkbenchPartEvent(@Observes SelectWorkbenchPartEvent event) {
        final PartDefinition part = event.getPart();
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            if ( e.getValue().getDefinition().getParts().contains( part ) ) {
                e.getValue().selectPart( part );
            }
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent(@Observes WorkbenchPartCloseEvent event) {
        final PartDefinition part = event.getPart();
        removePart( part );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent(@Observes WorkbenchPartDroppedEvent event) {
        final PartDefinition part = event.getPart();
        removePart( part );
    }

    private void removePart(final PartDefinition part) {
        factory.destroy( mapPartDefinitionToPresenter.get( part ) );
        mapPartDefinitionToPresenter.remove( part );

        WorkbenchPanelPresenter panelToRemove = null;

        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition definition = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            if ( presenter.getDefinition().getParts().contains( part ) ) {
                presenter.removePart( part );
                if ( !definition.isRoot() && definition.getParts().size() == 0 ) {
                    panelToRemove = presenter;
                }
                break;
            }
        }
        if ( panelToRemove != null ) {
            panelToRemove.removePanel();
            factory.destroy( panelToRemove );
            mapPanelDefinitionToPresenter.remove( panelToRemove.getDefinition() );
            removePanel( panelToRemove.getDefinition(),
                         root );
        }
    }

    public WorkbenchPanelPresenter.View getPanelView(final PanelDefinition panel) {
        return mapPanelDefinitionToPresenter.get( panel ).getPanelView();
    }

    public WorkbenchPartPresenter.View getPartView(final PartDefinition part) {
        return mapPartDefinitionToPresenter.get( part ).getPartView();
    }

    private void removePanel(final PanelDefinition panelToRemove,
                             final PanelDefinition panelToSearch) {
        final PanelDefinition northChild = panelToSearch.getChild( Position.NORTH );
        final PanelDefinition southChild = panelToSearch.getChild( Position.SOUTH );
        final PanelDefinition eastChild = panelToSearch.getChild( Position.EAST );
        final PanelDefinition westChild = panelToSearch.getChild( Position.WEST );
        if ( northChild != null ) {
            if ( northChild.equals( panelToRemove ) ) {
                mapPanelDefinitionToPresenter.remove( northChild );
                removePanel( panelToRemove,
                             panelToSearch,
                             Position.NORTH );
            } else {
                removePanel( panelToRemove,
                             northChild );
            }
        }
        if ( southChild != null ) {
            if ( southChild.equals( panelToRemove ) ) {
                mapPanelDefinitionToPresenter.remove( southChild );
                removePanel( panelToRemove,
                             panelToSearch,
                             Position.SOUTH );
            } else {
                removePanel( panelToRemove,
                             southChild );
            }
        }
        if ( eastChild != null ) {
            if ( eastChild.equals( panelToRemove ) ) {
                mapPanelDefinitionToPresenter.remove( eastChild );
                removePanel( panelToRemove,
                             panelToSearch,
                             Position.EAST );
            } else {
                removePanel( panelToRemove,
                             eastChild );
            }
        }
        if ( westChild != null ) {
            if ( westChild.equals( panelToRemove ) ) {
                mapPanelDefinitionToPresenter.remove( westChild );
                removePanel( panelToRemove,
                             panelToSearch,
                             Position.WEST );
            } else {
                removePanel( panelToRemove,
                             westChild );
            }
        }
    }

    private void removePanel(final PanelDefinition panelToRemove,
                             final PanelDefinition panelToSearch,
                             final Position position) {

        panelToSearch.removeChild( position );

        PanelDefinition holder = null;

        final List<PanelDefinition> orphans = panelToRemove.getChildren();
        Iterator<PanelDefinition> itr = orphans.iterator();
        if ( itr.hasNext() ) {

            //Add the first orphans parts to where the panel was removed
            final PanelDefinition firstOrphan = itr.next();
            holder = new PanelDefinition();
            for ( PartDefinition part : firstOrphan.getParts() ) {
                holder.addPart( part );
            }

            //Update presenter and map to use new holder
            WorkbenchPanelPresenter presenter = mapPanelDefinitionToPresenter.remove( firstOrphan );
            mapPanelDefinitionToPresenter.put( holder,
                                               presenter );
            presenter.setDefinition( holder );

            //Add remaining orphans as children of new holder
            while ( itr.hasNext() ) {
                final PanelDefinition orphan = itr.next();
                holder.setChild( orphan.getPosition(),
                                 orphan );
            }
        }
        if ( holder != null ) {
            panelToSearch.setChild( position,
                                    holder );
        }

    }

}
