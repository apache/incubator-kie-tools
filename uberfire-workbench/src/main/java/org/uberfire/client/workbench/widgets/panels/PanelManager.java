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
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.ChangeTabContentEvent;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
@ApplicationScoped
public class PanelManager {

    private final BeanFactory factory;

    private final Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;

    private final Event<WorkbenchPartOnFocusEvent> workbenchPartOnFocusEvent;

    private final Event<WorkbenchPartLostFocusEvent> workbenchPartLostFocusEvent;

    private final Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent;

    private PartDefinition activePart = null;

    private PanelDefinition root = null;

    private PerspectiveDefinition perspective;

    private Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPartPresenter>();

    private Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();

    @Inject
    //Injected constructor for unit testing
    public PanelManager( final BeanFactory factory,
                         final Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent,
                         final Event<WorkbenchPartOnFocusEvent> workbenchPartOnFocusEvent,
                         final Event<WorkbenchPartLostFocusEvent> workbenchPartLostFocusEvent,
                         final Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent ) {
        this.factory = factory;
        this.workbenchPartBeforeCloseEvent = workbenchPartBeforeCloseEvent;
        this.workbenchPartOnFocusEvent = workbenchPartOnFocusEvent;
        this.workbenchPartLostFocusEvent = workbenchPartLostFocusEvent;
        this.selectWorkbenchPartEvent = selectWorkbenchPartEvent;
    }

    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    public void setPerspective( final PerspectiveDefinition perspective ) {
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

    public void setRoot( final PanelDefinition panel ) {
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

        onPanelFocus( panel );
    }

    public PanelDefinition addWorkbenchPart( final PlaceRequest place,
                                             final PartDefinition part,
                                             final PanelDefinition panel,
                                             final IsWidget tabWidget,
                                             final IsWidget partWidget ) {
        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            throw new IllegalArgumentException( "Unable to add Part to Panel. Panel has not been created." );
        }

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = factory.newWorkbenchPart( tabWidget,
                                                      part );
            partPresenter.setWrappedWidget( partWidget );
            mapPartDefinitionToPresenter.put( part,
                                              partPresenter );
        }

        panelPresenter.addPart( part,
                                tabWidget,
                                partPresenter.getPartView() );

        //Select newly inserted part
        selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( place ) );
        return panelPresenter.getDefinition();
    }

    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position ) {
        final PanelDefinition childPanel = new PanelDefinitionImpl();
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position,
                                              final Integer height,
                                              final Integer width,
                                              final Integer minHeight,
                                              final Integer minWidth ) {
        final PanelDefinition childPanel = new PanelDefinitionImpl();
        childPanel.setHeight( height );
        childPanel.setWidth( width );
        childPanel.setMinHeight( minHeight );
        childPanel.setMinWidth( minWidth );
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        PanelDefinition newPanel = null;

        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        switch ( position ) {
            case ROOT:
                newPanel = root;
                break;

            case SELF:
                newPanel = targetPanelPresenter.getDefinition();
                break;

            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:

                final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                mapPanelDefinitionToPresenter.put( childPanel,
                                                   childPanelPresenter );

                targetPanelPresenter.addPanel( childPanel,
                                               childPanelPresenter.getPanelView(),
                                               position );
                newPanel = childPanelPresenter.getDefinition();
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        onPanelFocus( childPanel );
        return newPanel;
    }

    public void onPartFocus( final PartDefinition part ) {
        activePart = part;
        workbenchPartOnFocusEvent.fire( new WorkbenchPartOnFocusEvent( part.getPlace() ) );
    }

    public void onPartLostFocus() {
        if ( activePart == null ) {
            return;
        }
        workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( activePart.getPlace() ) );
        this.activePart = null;
    }

    public void onPanelFocus( final PanelDefinition panel ) {
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            e.getValue().setFocus( e.getKey().equals( panel ) );
        }
    }

    public void onBeforePartClose( final PartDefinition part ) {
        workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( part.getPlace() ) );
    }

    @SuppressWarnings("unused")
    private void onSelectWorkbenchPartEvent( @Observes SelectWorkbenchPartEvent event ) {
        final PlaceRequest place = event.getPlace();
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            for ( PartDefinition part : e.getValue().getDefinition().getParts() ) {
                if ( part.getPlace().equals( place ) ) {
                    e.getValue().selectPart( part );
                    onPanelFocus( e.getKey() );
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent( @Observes WorkbenchPartCloseEvent event ) {
        final PartDefinition part = getPartForPlace( event.getPlace() );
        if ( part != null ) {
            removePart( part );
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent( @Observes WorkbenchPartDroppedEvent event ) {
        final PartDefinition part = getPartForPlace( event.getPlace() );
        if ( part != null ) {
            removePart( part );
        }
    }

    private PartDefinition getPartForPlace( final PlaceRequest place ) {
        for ( PartDefinition part : mapPartDefinitionToPresenter.keySet() ) {
            if ( part.getPlace().equals( place ) ) {
                return part;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void onChangeWorkbenchTitleEvent( @Observes ChangeTabContentEvent event ) {
        final PlaceRequest place = event.getPlaceRequest();
        final IsWidget titleWidget = event.getTitleWidget();
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition panel = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            for ( PartDefinition part : panel.getParts() ) {
                if ( place.equals( part.getPlace() ) ) {
                    presenter.changeTitle( part,
                                           titleWidget );
                }
            }
        }
    }

    private void removePart( final PartDefinition part ) {
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

    public WorkbenchPanelPresenter.View getPanelView( final PanelDefinition panel ) {
        return mapPanelDefinitionToPresenter.get( panel ).getPanelView();
    }

    public WorkbenchPartPresenter.View getPartView( final PartDefinition part ) {
        return mapPartDefinitionToPresenter.get( part ).getPartView();
    }

    private void removePanel( final PanelDefinition panelToRemove,
                              final PanelDefinition panelToSearch ) {
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

    private void removePanel( final PanelDefinition panelToRemove,
                              final PanelDefinition panelToSearch,
                              final Position position ) {

        panelToSearch.removeChild( position );

        final PanelDefinition northOrphan = panelToRemove.getChild( Position.NORTH );
        final PanelDefinition southOrphan = panelToRemove.getChild( Position.SOUTH );
        final PanelDefinition eastOrphan = panelToRemove.getChild( Position.EAST );
        final PanelDefinition westOrphan = panelToRemove.getChild( Position.WEST );
        panelToSearch.appendChild( Position.NORTH,
                                   northOrphan );
        panelToSearch.appendChild( Position.SOUTH,
                                   southOrphan );
        panelToSearch.appendChild( Position.EAST,
                                   eastOrphan );
        panelToSearch.appendChild( Position.WEST,
                                   westOrphan );
    }

}
