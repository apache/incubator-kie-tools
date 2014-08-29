package org.uberfire.client.workbench;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
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
 * Standard implementation of {@link PanelManager}.
 */
@ApplicationScoped
public class PanelManagerImpl implements PanelManager {

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

    @Inject
    private BeanFactory beanFactory;

    protected BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public PanelDefinition getRoot() {
        return this.rootPanelDef;
    }

    @Override
    public void setRoot( PerspectiveActivity activity, PanelDefinition root ) {
        checkNotNull( "root", root );

        final WorkbenchPanelPresenter oldRootPanelPresenter = mapPanelDefinitionToPresenter.remove( rootPanelDef );

        if ( !mapPanelDefinitionToPresenter.isEmpty() ) {
            String message = "Can't replace current root panel because it is not empty. The following panels remain: " + mapPanelDefinitionToPresenter;
            mapPanelDefinitionToPresenter.put( rootPanelDef, oldRootPanelPresenter );
            throw new IllegalStateException( message );
        }

        HasWidgets perspectiveContainer = layoutSelection.get().getPerspectiveContainer();
        perspectiveContainer.clear();

        getBeanFactory().destroy( oldRootPanelPresenter );

        this.rootPanelDef = root;
        WorkbenchPanelPresenter newPresenter = mapPanelDefinitionToPresenter.get( root );
        if ( newPresenter == null ) {
            newPresenter = getBeanFactory().newRootPanel( activity, root );
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

        panelPresenter.addPart( partPresenter, contextId );

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
     * subclass. The child panel argument is an empty PanelDefinition of {@link PanelDefinition#PARENT_CHOOSES_TYPE}, and its
     * size and minimum sizes have been initialized to the given amounts.
     */
    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position,
                                              final Integer height,
                                              final Integer width,
                                              final Integer minHeight,
                                              final Integer minWidth ) {
        final PanelDefinitionImpl childPanel = new PanelDefinitionImpl( PanelDefinition.PARENT_CHOOSES_TYPE );
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
    public void removeWorkbenchPanel( final PanelDefinition toRemove ) throws IllegalStateException {
        if ( !toRemove.getParts().isEmpty() ) {
            throw new IllegalStateException( "Panel still contains parts: " + toRemove.getParts() );
        }
        if ( !toRemove.getChildren().isEmpty() ) {
            throw new IllegalStateException( "Panel still contains child panels: " + toRemove.getChildren() );
        }

        final WorkbenchPanelPresenter presenterToRemove = mapPanelDefinitionToPresenter.remove( toRemove );
        if ( presenterToRemove == null ) {
            throw new IllegalArgumentException( "Couldn't find panel to remove: " + toRemove );
        }

        final PanelDefinition parentDef = toRemove.getParent();
        final WorkbenchPanelPresenter parentPresenter = mapPanelDefinitionToPresenter.get( parentDef );
        if ( parentPresenter == null ) {
            throw new IllegalArgumentException( "The given panel's parent could not be found" );
        }

        parentPresenter.removePanel( presenterToRemove );

        getBeanFactory().destroy( presenterToRemove );
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
     * Destroys the presenter bean associated with the given part and removes the part's presenter from the panel
     * presenter that contains it (which in turn removes the part definition from the panel definition and the part view
     * from the panel view).
     *
     * @param part
     *            the definition of the workbench part (screen or editor) to remove from the layout.
     */
    protected void removePart( final PartDefinition part ) {
        for ( Map.Entry<PanelDefinition, WorkbenchPanelPresenter> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final WorkbenchPanelPresenter panelPresenter = e.getValue();
            if ( panelPresenter.getDefinition().getParts().contains( part ) ) {
                panelPresenter.removePart( part );
                break;
            }
        }

        WorkbenchPartPresenter deadPartPresenter = mapPartDefinitionToPresenter.remove( part );
        getBeanFactory().destroy( deadPartPresenter );
    }

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );

        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = beanFactory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        if ( childPanel.getPanelType().equals( PanelDefinition.PARENT_CHOOSES_TYPE ) ) {
            childPanel.setPanelType( targetPanelPresenter.getDefaultChildType() );
        }

        PanelDefinition newPanel;
        if ( position == CompassPosition.ROOT ) {
            // TODO not sure if this is needed/used anymore
            newPanel = rootPanelDef;
        } else if ( position == CompassPosition.SELF ) {
            // TODO not sure if this is needed/used anymore
            newPanel = targetPanelPresenter.getDefinition();
        } else {
            final WorkbenchPanelPresenter childPanelPresenter = beanFactory.newWorkbenchPanel( childPanel );
            mapPanelDefinitionToPresenter.put( childPanel,
                                               childPanelPresenter );

            // TODO (hbraun): why no remove callback before the addPanel invocation?
            targetPanelPresenter.addPanel( childPanelPresenter,
                                           position );
            newPanel = childPanel;
        }

        onPanelFocus( newPanel );

        return newPanel;
    }

}
