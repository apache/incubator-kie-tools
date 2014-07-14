package org.uberfire.client.workbench;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Base class for implementations of PanelManager. This class relies on ErraiIOC field injection, so all subclasses must
 * be instantiated via the BeanManager. Subclasses instantiated with <tt>new</tt> will not behave properly.
 */
public abstract class AbstractPanelManagerImpl implements PanelManager  {

    @Inject
    protected Event<PlaceGainFocusEvent> placeGainFocusEvent;

    @Inject
    protected Event<PlaceLostFocusEvent> placeLostFocusEvent;

    @Inject
    protected Event<PanelFocusEvent> panelFocusEvent;

    @Inject
    protected Event<SelectPlaceEvent> selectPlaceEvent;

    @Inject
    protected SyncBeanManager iocManager;

    @Inject
    protected Instance<PlaceManager> placeManager;

    /**
     * Description that the current root panel was created from. Presently, this is a mutable data structure and the
     * whole UF framework tries to keep this in sync with the reality (syncing each change from DOM -> Widget ->
     * UberView -> Presenter -> Definition). This may change in the future. See UF-117.
     */
    protected PanelDefinition rootPanelDef = null;

    protected final Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPartPresenter>();

    protected final Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();

    protected PartDefinition activePart = null;

    @Inject
    LayoutSelection layoutSelection;

    protected abstract BeanFactory getBeanFactory();

    @Override
    public PanelDefinition getRoot() {
        return this.rootPanelDef;
    }

    @Override
    public void setRoot( PanelDefinition root ) {
        checkNotNull( "root", root );

        final WorkbenchPanelPresenter oldRootPanelPresenter = mapPanelDefinitionToPresenter.remove( rootPanelDef );

        if ( !mapPanelDefinitionToPresenter.isEmpty() ) {
            mapPanelDefinitionToPresenter.put( rootPanelDef, oldRootPanelPresenter );
            throw new IllegalStateException( "Can't replace current root panel because it is not empty. The following panels remain: " + mapPanelDefinitionToPresenter );
        }

        if ( !mapPartDefinitionToPresenter.isEmpty() ) {
            throw new IllegalStateException( "Can't replace current root panel because it is not empty. The following parts remain: " + mapPartDefinitionToPresenter );
        }

        HasWidgets perspectiveContainer = layoutSelection.get().getPerspectiveContainer();
        perspectiveContainer.clear();

        getBeanFactory().destroy( oldRootPanelPresenter );

        this.rootPanelDef = root;
        WorkbenchPanelPresenter newPresenter = mapPanelDefinitionToPresenter.get( root );
        if ( newPresenter == null ) {
            newPresenter = getBeanFactory().newWorkbenchPanel( root );
            mapPanelDefinitionToPresenter.put( root, newPresenter );
        }
        perspectiveContainer.add( newPresenter.getPanelView().asWidget() );
    }

    @Override
    public void addWorkbenchPart( final PlaceRequest place,
                                  final PartDefinition part,
                                  final PanelDefinition panel,
                                  final Menus menus,
                                  final UIPart uiPart ) {
        addWorkbenchPart( place, part, panel, menus, uiPart, null );
    }

    @Override
    public void addWorkbenchPart( final PlaceRequest place,
                                  final PartDefinition part,
                                  final PanelDefinition panel,
                                  final Menus menus,
                                  final UIPart uiPart,
                                  final String contextId ) {
        checkNotNull( "panel", panel );

        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            throw new IllegalArgumentException( "Target panel is not part of the layout" );
        }

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = getBeanFactory().newWorkbenchPart( menus, uiPart.getTitle(), uiPart.getTitleDecoration(), part );
            partPresenter.setWrappedWidget( uiPart.getWidget() );
            partPresenter.setContextId( contextId );
            mapPartDefinitionToPresenter.put( part, partPresenter );
        }

        panelPresenter.addPart( partPresenter.getPartView(), contextId );

        //The model for a Perspective is already fully populated. Don't go adding duplicates.
        if ( !panel.getParts().contains( part ) ) {
            panel.addPart( part );
        }

        //Select newly inserted part
        selectPlaceEvent.fire( new SelectPlaceEvent( place ) );
    }

    @Override
    public boolean removePartForPlace( PlaceRequest toRemove ) {
        final PartDefinition removedPart = getPartForPlace( toRemove );
        if ( removedPart != null ) {
            removePart( removedPart );
            return true;
        }
        return false;
    }

    /**
     * Calls the abstract {@link #addWorkbenchPanel(PanelDefinition, PanelDefinition, Position)} method supplied by the
     * subclass. The child panel argument is an empty PanelDefinition of the target panel's default child type.
     */
    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position ) {
        final PanelDefinitionImpl childPanel = new PanelDefinitionImpl( targetPanel.getDefaultChildPanelType() );
        childPanel.setParent(targetPanel);
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    /**
     * Calls the abstract {@link #addWorkbenchPanel(PanelDefinition, PanelDefinition, Position)} method supplied by the
     * subclass. The child panel argument is an empty PanelDefinition of the target panel's default child type, and its
     * size and minimum sizes have been initialized to the given amounts.
     */
    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position,
                                              final Integer height,
                                              final Integer width,
                                              final Integer minHeight,
                                              final Integer minWidth ) {
        final PanelDefinitionImpl childPanel = new PanelDefinitionImpl( targetPanel.getDefaultChildPanelType() );
        childPanel.setParent(targetPanel);

        childPanel.setHeight( height );
        childPanel.setWidth( width );
        childPanel.setMinHeight( minHeight );
        childPanel.setMinWidth( minWidth );
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    @Override
    public void onPartFocus( final PartDefinition part ) {
        activePart = part;
        panelFocusEvent.fire( new PanelFocusEvent( part.getParentPanel() ) );
        placeGainFocusEvent.fire( new PlaceGainFocusEvent( part.getPlace() ) );
    }

    @Override
    public void onPartLostFocus() {
        if ( activePart == null ) {
            return;
        }
        placeLostFocusEvent.fire( new PlaceLostFocusEvent( activePart.getPlace() ) );
        activePart = null;
    }

    @Override
    public void onPanelFocus( final PanelDefinition panel ) {
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            e.getValue().setFocus( e.getKey().equals( panel ) );
        }
    }

    @Override
    public void closePart( final PartDefinition part ) {
        placeManager.get().closePlace( part.getPlace() );
    }

    @SuppressWarnings("unused")
    private void onSelectPlaceEvent( @Observes SelectPlaceEvent event ) {
        final PlaceRequest place = event.getPlace();

        // TODO (hbraun): PanelDefinition is not distinct (missing hashcode)
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            WorkbenchPanelPresenter panelPresenter = e.getValue();
            for (PartDefinition part : panelPresenter.getDefinition().getParts()) {
                if (part.getPlace().equals(place)) {
                    panelPresenter.selectPart(part);
                    onPanelFocus(e.getKey());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void onDropPlaceEvent( @Observes DropPlaceEvent event ) {
        final PartDefinition part = getPartForPlace( event.getPlace() );
        if ( part != null ) {
            removePart( part );
        }
    }

    /**
     * Returns the first live (associated with an active presenter) PartDefinition whose place matches the given one.
     * 
     * @return the definition for the live part servicing the given place, or null if no such part can be found.
     */
    protected PartDefinition getPartForPlace( final PlaceRequest place ) {
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
                    mapPartDefinitionToPresenter.get( part ).setTitle( title );
                    presenter.changeTitle( part, title, titleDecoration );
                    break;
                }
            }
        }
    }

    /**
     * Destroys the presenter bean associated with the given part, removes the part definition from the panel definition
     * that contains it, removes the part from the actual parent panel presenter, and removes the panel presenter which
     * contained the part if it becomes empty in the process (unless it is the root panel: the root panel is not removed
     * even when empty).
     * <p>
     * Children in the NORTH, SOUTH, EAST, or WEST Positions of the removed panel are reconnected to the layout by
     * appending them to the same Position within the removed panel's parent. Children in other Positions within the
     * removed panel will not be reconnected, but they will be properly disposed.
     * 
     * @param part
     *            the definition of the workbench part (screen or editor) to remove from the layout.
     */
    protected void removePart( final PartDefinition part ) {
        getBeanFactory().destroy( mapPartDefinitionToPresenter.get( part ) );
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
            getBeanFactory().destroy( presenterToRemove );
            removePanel( presenterToRemove.getDefinition(),
                         rootPanelDef );
        }
    }

    /**
     * Splices out the given panel from the panel tree, preserving all children in the NORTH, SOUTH, EAST, or WEST
     * Positions of the removed panel by appending them to the same Position within the removed panel's parent. Children
     * in other Positions within the removed panel will be removed along with that panel.
     * <p>
     * TODO: this method should make some effort to check that the parent panel actually supports the NORTH, SOUTH,
     * EAST, WEST positions before attempting to reparent the orphaned children.
     * 
     * @param panelToRemove
     *            the panel to remove (children will be preserved).
     * @param panelToSearch
     *            the panel that contains the panel to remove.
     */
    private void removePanel( final PanelDefinition panelToRemove,
                              final PanelDefinition panelToSearch ) {
        final PanelDefinition northChild = panelToSearch.getChild( CompassPosition.NORTH );
        final PanelDefinition southChild = panelToSearch.getChild( CompassPosition.SOUTH );
        final PanelDefinition eastChild = panelToSearch.getChild( CompassPosition.EAST );
        final PanelDefinition westChild = panelToSearch.getChild( CompassPosition.WEST );
        if ( northChild != null ) {
            if ( northChild.equals( panelToRemove ) ) {
                mapPanelDefinitionToPresenter.remove( northChild );
                removePanel( panelToRemove,
                             panelToSearch,
                             CompassPosition.NORTH );
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
                             CompassPosition.SOUTH );
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
                             CompassPosition.EAST );
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
                             CompassPosition.WEST );
            } else {
                removePanel( panelToRemove,
                             westChild );
            }
        }
    }

    /**
     * Subroutine of {@link #removePanel(PanelDefinition, PanelDefinition)} which does the physical grafting of the
     * orphaned child panels onto their grandparents.
     */
    private void removePanel( final PanelDefinition panelToRemove,
                              final PanelDefinition panelToSearch,
                              final Position position ) {

        panelToSearch.removeChild( position );

        final PanelDefinition northOrphan = panelToRemove.getChild( CompassPosition.NORTH );
        final PanelDefinition southOrphan = panelToRemove.getChild( CompassPosition.SOUTH );
        final PanelDefinition eastOrphan = panelToRemove.getChild( CompassPosition.EAST );
        final PanelDefinition westOrphan = panelToRemove.getChild( CompassPosition.WEST );
        panelToSearch.appendChild( CompassPosition.NORTH,
                                   northOrphan );
        panelToSearch.appendChild( CompassPosition.SOUTH,
                                   southOrphan );
        panelToSearch.appendChild( CompassPosition.EAST,
                                   eastOrphan );
        panelToSearch.appendChild( CompassPosition.WEST,
                                   westOrphan );
    }


}
