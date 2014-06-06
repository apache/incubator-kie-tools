package org.uberfire.client.workbench;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.RestorePlaceEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

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
    protected WorkbenchStatusBarPresenter statusBar;

    @Inject
    protected SyncBeanManager iocManager;

    @Inject
    protected Instance<PlaceManager> placeManager;

    /**
     * Description of the current perspective's root panel.
     * TODO: this should always be the same as <tt>perspective.getRoot()</tt>. Tracking it separately probably does more harm than good!
     */
    protected PanelDefinition rootPanelDef = null;

    protected PerspectiveDefinition perspective;

    protected final Map<PartDefinition, WorkbenchPartPresenter> mapPartDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPartPresenter>();

    protected final Map<PanelDefinition, WorkbenchPanelPresenter> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanelPresenter>();

    protected PartDefinition activePart = null;

    /**
     * The panel within which the current perspective's root view resides. This panel lasts the lifetime of the app; it's
     * cleared and repopulated with the new perspective's root view each time
     * {@link #setPerspective(PerspectiveDefinition)} gets called. The actual panel that's used for this is specified by
     * the concrete subclass's constructor.
     */
    protected final Panel perspectiveRootContainer;

    /**
     * The panel within which the current perspective's header widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setHeaderContents(java.util.List)} gets called. The actual panel that's used for this is specified by the
     * concrete subclass's constructor.
     */
    protected final Panel headerPanel;

    /**
     * The panel within which the current perspective's footer widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setFooterContents(java.util.List)} gets called. The actual panel that's used for this is specified by the
     * concrete subclass's constructor.
     */
    protected final Panel footerPanel;

    /**
     * Constructor for subclasses. Allows specifying which Panel the perspective portion of the workbench layout lives
     * in.
     * 
     * @param perspectiveRootContainer
     *            Container for (direct parent of) the current perspective's root panel view. Subclasses can pass in any
     *            Panel they like, as long as it supports the {@link Panel#clear()} and {@link Panel#add(IsWidget)}
     *            methods.
     * @param headerPanel
     *            Subclasses can pass in any Panel they like, as long as it supports the {@link Panel#clear()} and
     *            {@link Panel#add(IsWidget)} methods.
     * @param footerPanel
     *            Subclasses can pass in any Panel they like, as long as it supports the {@link Panel#clear()} and
     *            {@link Panel#add(IsWidget)} methods.
     */
    protected AbstractPanelManagerImpl( Panel perspectiveRootContainer, Panel headerPanel, Panel footerPanel ) {
        this.perspectiveRootContainer = perspectiveRootContainer;
        this.headerPanel = headerPanel;
        this.footerPanel = footerPanel;
    }

    @Override
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @Override
    public void setPerspective( final PerspectiveDefinition newPerspectiveDef ) {
        if ( this.perspective == null ) {
            arrangePanelsInDOM();
        }


        final WorkbenchPanelPresenter oldRootPanelPresenter = mapPanelDefinitionToPresenter.remove( rootPanelDef );

        //TODO oldRootPanelPresenter.dispose() (or onClose() or onRemove()), UNLESS this can be done by a DOM event hook in the view
        //         - cleanup listeners; take impl from existing removePanel() method but leave out the remove-from-parent bit

        perspectiveRootContainer.clear();
        getBeanFactory().destroy( rootPanelDef );

        final PanelDefinition newRootPanelDef = newPerspectiveDef.getRoot();
        this.rootPanelDef = newRootPanelDef;
        this.perspective = newPerspectiveDef;
        WorkbenchPanelPresenter newPresenter = getWorkbenchPanelPresenter( newRootPanelDef );
        if ( newPresenter == null ) {
            newPresenter = getBeanFactory().newWorkbenchPanel( newRootPanelDef );
            mapPanelDefinitionToPresenter.put( newRootPanelDef, newPresenter );
        }
        perspectiveRootContainer.add( newPresenter.getPanelView() );
    }

    protected abstract BeanFactory getBeanFactory();

    /**
     * Adds the header, footer, and perspective root panels to the DOM. This method is called once only, and the call
     * comes as a side effect to the first call to {@link #setPerspective(PerspectiveDefinition)}.
     */
    protected abstract void arrangePanelsInDOM();

    @Override
    public PanelDefinition getRoot() {
        return this.rootPanelDef;
    }

    @Override
    public void setHeaderContents( List<Header> headers ) {
        headerPanel.clear();
        for ( Header h : headers ) {
            headerPanel.add( h );
        }
    }

    @Override
    public void setFooterContents( List<Footer> footers ) {
        footerPanel.clear();
        for ( Footer f : footers ) {
            footerPanel.add( f );
        }
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

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = getBeanFactory().newWorkbenchPart( menus, uiPart.getTitle(), uiPart.getTitleDecoration(), part );
            partPresenter.setWrappedWidget( uiPart.getWidget() );
            partPresenter.setContextId( contextId );
            mapPartDefinitionToPresenter.put( part, partPresenter );
        }

        if ( part.isMinimized() ) {
            statusBar.addMinimizedPlace( part.getPlace() );
        } else {
            final WorkbenchPanelPresenter panelPresenter = getWorkbenchPanelPresenter( panel );
            if ( panelPresenter == null ) {
                throw new IllegalArgumentException( "Unable to add Part to Panel. Panel has not been created." );
            }

            panelPresenter.addPart( partPresenter.getPartView(), contextId );
        }

        //The model for a Perspective is already fully populated. Don't go adding duplicates.
        if ( !panel.getParts().contains( part ) ) {
            panel.addPart( part );
        }

        //Select newly inserted part
        selectPlaceEvent.fire( new SelectPlaceEvent( place ) );
    }

    protected WorkbenchPanelPresenter getWorkbenchPanelPresenter( PanelDefinition panel ) {
        return mapPanelDefinitionToPresenter.get( panel );
    }

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position ) {
        final PanelDefinition childPanel = new PanelDefinitionImpl( targetPanel.getDefaultChildPanelType() );
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position,
                                              final Integer height,
                                              final Integer width,
                                              final Integer minHeight,
                                              final Integer minWidth ) {
        final PanelDefinition childPanel = new PanelDefinitionImpl( targetPanel.getDefaultChildPanelType() );
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
            getBeanFactory().destroy( presenterToMinimize );
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
                                                           rootPanelDef );
            if ( targetPanel == null ) {
                targetPanel = rootPanelDef;
            }
            addWorkbenchPanel( targetPanel,
                               panelToRestore,
                               panelToRestore.getPosition() );
        }

        //Restore part
        final WorkbenchPartPresenter presenter = mapPartDefinitionToPresenter.get( partToRestore );
        addWorkbenchPart( partToRestore.getPlace(),
                          partToRestore,
                          panelToRestore,
                          presenter.getMenus(),
                          new UIPart( presenter.getTitle(), presenter.getTitleDecoration(), presenter.getPartView() ) );
    }

    private PanelDefinition findTargetPanel( final PanelDefinition panelToFind,
                                             final PanelDefinition panelToSearch ) {
        final PanelDefinition northChild = panelToSearch.getChild( CompassPosition.NORTH );
        final PanelDefinition southChild = panelToSearch.getChild( CompassPosition.SOUTH );
        final PanelDefinition eastChild = panelToSearch.getChild( CompassPosition.EAST );
        final PanelDefinition westChild = panelToSearch.getChild( CompassPosition.WEST );
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
                    mapPartDefinitionToPresenter.get( part ).setTitle( title );
                    presenter.changeTitle( part, title, titleDecoration );
                    break;
                }
            }
        }
    }

    private void removePart( final PartDefinition part ) {
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
