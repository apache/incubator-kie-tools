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
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.DropPlaceEvent;
import org.uberfire.client.workbench.widgets.events.MinimizePlaceEvent;
import org.uberfire.client.workbench.widgets.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.widgets.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.RestorePlaceEvent;
import org.uberfire.client.workbench.widgets.events.SelectPlaceEvent;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
@ApplicationScoped
public class PanelManager {

    @Inject
    private BeanFactory factory;

    @Inject
    private Event<BeforeClosePlaceEvent> beforeClosePlaceEvent;

    @Inject
    private Event<PlaceGainFocusEvent> placeGainFocusEvent;

    @Inject
    private Event<PlaceLostFocusEvent> placeLostFocusEvent;

    @Inject
    private Event<SelectPlaceEvent> selectPlaceEvent;

    @Inject
    private WorkbenchStatusBarPresenter statusBar;

    private PartDefinition activePart = null;

    private PanelDefinition root = null;

    private PerspectiveDefinition perspective;

    private Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPartPresenter>();

    private Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();

    //
    public PanelManager() {
    }

    //constructor for unit testing
    public PanelManager( final BeanFactory factory,
                         final Event<BeforeClosePlaceEvent> beforeClosePlaceEvent,
                         final Event<PlaceGainFocusEvent> placeGainFocusEvent,
                         final Event<PlaceLostFocusEvent> placeLostFocusEvent,
                         final Event<SelectPlaceEvent> selectPlaceEvent,
                         final WorkbenchStatusBarPresenter statusBar ) {
        this.factory = factory;
        this.beforeClosePlaceEvent = beforeClosePlaceEvent;
        this.placeGainFocusEvent = placeGainFocusEvent;
        this.placeLostFocusEvent = placeLostFocusEvent;
        this.selectPlaceEvent = selectPlaceEvent;
        this.statusBar = statusBar;
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

    public void addWorkbenchPart( final PlaceRequest place,
                                  final PartDefinition part,
                                  final PanelDefinition panel,
                                  final String title,
                                  final IsWidget titleDecoration,
                                  final IsWidget partWidget ) {
        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = factory.newWorkbenchPart( title, titleDecoration, part );
            partPresenter.setWrappedWidget( partWidget );
            mapPartDefinitionToPresenter.put( part, partPresenter );
        }

        if ( part.isMinimized() ) {
            statusBar.addMinimizedPlace( part.getPlace() );
        } else {
            final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panel );
            if ( panelPresenter == null ) {
                throw new IllegalArgumentException( "Unable to add Part to Panel. Panel has not been created." );
            }

            panelPresenter.addPart( partPresenter.getPartView() );
        }

        //The model for a Perspective is already fully populated. Don't go adding duplicates.
        if ( !panel.getParts().contains( part ) ) {
            panel.addPart( part );
        }

        //Select newly inserted part
        selectPlaceEvent.fire( new SelectPlaceEvent( place ) );
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

                if ( !childPanel.isMinimized() ) {
                    final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                    mapPanelDefinitionToPresenter.put( childPanel,
                                                       childPanelPresenter );

                    targetPanelPresenter.addPanel( childPanel,
                                                   childPanelPresenter.getPanelView(),
                                                   position );
                }
                newPanel = childPanel;
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        onPanelFocus( newPanel );
        return newPanel;
    }

    public void onPartFocus( final PartDefinition part ) {
        activePart = part;
        placeGainFocusEvent.fire( new PlaceGainFocusEvent( part.getPlace() ) );
    }

    public void onPartLostFocus() {
        if ( activePart == null ) {
            return;
        }
        placeLostFocusEvent.fire( new PlaceLostFocusEvent( activePart.getPlace() ) );
        this.activePart = null;
    }

    public void onPanelFocus( final PanelDefinition panel ) {
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            e.getValue().setFocus( e.getKey().equals( panel ) );
        }
    }

    public void onBeforePartClose( final PartDefinition part ) {
        beforeClosePlaceEvent.fire( new BeforeClosePlaceEvent( part.getPlace() ) );
    }

    @SuppressWarnings("unused")
    private void onSelectPlaceEvent( @Observes SelectPlaceEvent event ) {
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
    private void onClosePlaceEvent( @Observes ClosePlaceEvent event ) {
        final PartDefinition part = getPartForPlace( event.getPlace() );
        if ( part != null ) {
            removePart( part );
        }
    }

    @SuppressWarnings("unused")
    private void onDropPlaceEvent( @Observes DropPlaceEvent event ) {
        final PartDefinition part = getPartForPlace( event.getPlace() );
        if ( part != null ) {
            removePart( part );
        }
    }

    @SuppressWarnings("unused")
    private void onMinimizePlaceEvent( @Observes MinimizePlaceEvent event ) {
        final PlaceRequest placeToMinimize = event.getPlace();
        final PartDefinition partToMinimize = getPartForPlace( placeToMinimize );

        WorkbenchPanelPresenter presenterToMinimize = null;

        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition definition = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            if ( presenter.getDefinition().getParts().contains( partToMinimize ) ) {
                partToMinimize.setMinimized( true );
                presenter.removePart( partToMinimize );
                if ( presenter.getDefinition().isMinimized() ) {
                    presenterToMinimize = presenter;
                }
                break;
            }
        }
        if ( presenterToMinimize != null ) {
            presenterToMinimize.removePanel();
            factory.destroy( presenterToMinimize );
            mapPanelDefinitionToPresenter.remove( presenterToMinimize.getDefinition() );
        }
    }

    @SuppressWarnings("unused")
    private void onRestorePlaceEvent( @Observes RestorePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        final PartDefinition partToRestore = getPartForPlace( place );
        final PanelDefinition panelToRestore = partToRestore.getParentPanel();

        final Integer height = panelToRestore.getHeight();
        final Integer width = panelToRestore.getWidth();
        final Integer minHeight = panelToRestore.getMinHeight();
        final Integer minWidth = panelToRestore.getMinWidth();

        partToRestore.setMinimized( false );

        //Restore containing panel
        if ( !mapPanelDefinitionToPresenter.containsKey( panelToRestore ) ) {
            //TODO {manstis} Position needs to be looked up from model - will need "outer" panel feature :(
            PanelDefinition targetPanel = findTargetPanel( panelToRestore,
                                                           root );
            if ( targetPanel == null ) {
                targetPanel = root;
            }
            addWorkbenchPanel( targetPanel,
                               panelToRestore,
                               panelToRestore.getPosition() );
        }

        //Restore part
        final IsWidget widget = mapPartDefinitionToPresenter.get( partToRestore ).getPartView();
        final String title = mapPartDefinitionToPresenter.get( partToRestore ).getTitle();
        final IsWidget titleDecoration = mapPartDefinitionToPresenter.get( partToRestore ).getTitleDecoration();
        addWorkbenchPart( partToRestore.getPlace(),
                          partToRestore,
                          panelToRestore,
                          title,
                          titleDecoration,
                          widget );
    }

    private PanelDefinition findTargetPanel( final PanelDefinition panelToFind,
                                             final PanelDefinition panelToSearch ) {
        final PanelDefinition northChild = panelToSearch.getChild( Position.NORTH );
        final PanelDefinition southChild = panelToSearch.getChild( Position.SOUTH );
        final PanelDefinition eastChild = panelToSearch.getChild( Position.EAST );
        final PanelDefinition westChild = panelToSearch.getChild( Position.WEST );
        PanelDefinition targetPanel = null;
        if ( northChild != null ) {
            if ( northChild.equals( panelToFind ) ) {
                return panelToSearch;
            } else {
                targetPanel = findTargetPanel( panelToFind,
                                               northChild );
            }
        }
        if ( southChild != null ) {
            if ( southChild.equals( panelToFind ) ) {
                return panelToSearch;
            } else {
                targetPanel = findTargetPanel( panelToFind,
                                               southChild );
            }
        }
        if ( eastChild != null ) {
            if ( eastChild.equals( panelToFind ) ) {
                return panelToSearch;
            } else {
                targetPanel = findTargetPanel( panelToFind,
                                               eastChild );
            }
        }
        if ( westChild != null ) {
            if ( westChild.equals( panelToFind ) ) {
                return panelToSearch;
            } else {
                targetPanel = findTargetPanel( panelToFind,
                                               westChild );
            }
        }
        return targetPanel;
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
    private void onChangeTitleWidgetEvent( @Observes ChangeTitleWidgetEvent event ) {
        final PlaceRequest place = event.getPlaceRequest();
        final IsWidget titleDecoration = event.getTitleDecoration();
        final String title = event.getTitle();
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition panel = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            for ( PartDefinition part : panel.getParts() ) {
                if ( place.equals( part.getPlace() ) ) {
                    presenter.changeTitle( part, title, titleDecoration );
                }
            }
        }
    }

    private void removePart( final PartDefinition part ) {
        factory.destroy( mapPartDefinitionToPresenter.get( part ) );
        mapPartDefinitionToPresenter.remove( part );

        WorkbenchPanelPresenter presenterToRemove = null;

        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition definition = e.getKey();
            final WorkbenchPanelPresenter presenter = e.getValue();
            if ( presenter.getDefinition().getParts().contains( part ) ) {
                presenter.removePart( part );
                definition.getParts().remove( part );
                if ( !definition.isRoot() && definition.getParts().size() == 0 ) {
                    presenterToRemove = presenter;
                }
                break;
            }
        }
        if ( presenterToRemove != null ) {
            presenterToRemove.removePanel();
            factory.destroy( presenterToRemove );
            removePanel( presenterToRemove.getDefinition(),
                         root );
        }
    }

    public WorkbenchPanelView getPanelView( final PanelDefinition panel ) {
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
