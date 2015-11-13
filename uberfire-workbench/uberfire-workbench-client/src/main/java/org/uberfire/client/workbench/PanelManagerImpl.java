/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.*;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.debug.Debug;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

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
    protected Event<PlaceMaximizedEvent> placeMaximizedEventEvent;

    @Inject
    protected Event<PlaceMinimizedEvent> placeMinimizedEventEvent;

    @Inject
    protected Event<PlaceHiddenEvent> placeHiddenEvent;

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

    /**
     * Remembers which HasWidgets contains each existing custom panel. Items are removed from this map when the panels
     * are closed/removed.
     */
    protected final Map<PanelDefinition, HasWidgets> customPanels = new HashMap<PanelDefinition, HasWidgets>();

    protected PartDefinition activePart = null;

    @Inject
    LayoutSelection layoutSelection;

    @Inject
    private BeanFactory beanFactory;

    /**
     * Registration for the native preview handler that watches for ^M events and maximizes/restores the current panel.
     */
    private HandlerRegistration globalHandlerRegistration;

    /**
     * The currently maximized panel. Set to null when a panel is not maximized.
     */
    private WorkbenchPanelPresenter maximizedPanel = null;

    @PostConstruct
    private void setup() {
        globalHandlerRegistration = com.google.gwt.user.client.Event.addNativePreviewHandler( new NativePreviewHandler() {

            @Override
            public void onPreviewNativeEvent( NativePreviewEvent event ) {
                if ( event.getTypeInt() == com.google.gwt.user.client.Event.ONKEYPRESS &&
                        event.getNativeEvent().getCharCode() == 'm' &&
                        event.getNativeEvent().getCtrlKey() ) {
                    if ( maximizedPanel != null ) {
                        maximizedPanel.unmaximize();
                        maximizedPanel = null;
                    } else if ( activePart != null ) {
                        WorkbenchPanelPresenter activePanelPresenter = mapPanelDefinitionToPresenter.get( activePart.getParentPanel() );
                        activePanelPresenter.maximize();
                        maximizedPanel = activePanelPresenter;
                    }
                }
            }
        } );
    }

    @PreDestroy
    private void teardown() {
        globalHandlerRegistration.removeHandler();
    }

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
                                  final PartDefinition partDef,
                                  final PanelDefinition panelDef,
                                  final Menus menus,
                                  final UIPart uiPart,
                                  final String contextId,
                                  final Integer preferredWidth,
                                  final Integer preferredHeight ) {
        checkNotNull( "panel", panelDef );

        final WorkbenchPanelPresenter panelPresenter = mapPanelDefinitionToPresenter.get( panelDef );
        if ( panelPresenter == null ) {
            throw new IllegalArgumentException( "Target panel is not part of the layout" );
        }

        WorkbenchPartPresenter partPresenter = mapPartDefinitionToPresenter.get( partDef );
        if ( partPresenter == null ) {
            partPresenter = getBeanFactory().newWorkbenchPart( menus, uiPart.getTitle(), uiPart.getTitleDecoration(), partDef );
            partPresenter.setWrappedWidget( uiPart.getWidget() );
            partPresenter.setContextId( contextId );
            mapPartDefinitionToPresenter.put( partDef, partPresenter );
        }

        panelPresenter.addPart( partPresenter, contextId );
        if ( panelPresenter.getParent() instanceof DockingWorkbenchPanelPresenter ) {
            DockingWorkbenchPanelPresenter parent = (DockingWorkbenchPanelPresenter) panelPresenter.getParent();
            parent.setChildSize( panelPresenter, preferredWidth, preferredHeight );
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

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final Position position,
                                              final Integer height,
                                              final Integer width,
                                              final Integer minHeight,
                                              final Integer minWidth ) {
        final PanelDefinitionImpl childPanel = new PanelDefinitionImpl( PanelDefinition.PARENT_CHOOSES_TYPE );

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
        if ( toRemove.isRoot() ) {
            throw new IllegalArgumentException( "The root panel cannot be removed. To replace it, call setRoot()" );
        }
        if ( !toRemove.getParts().isEmpty() ) {
            throw new IllegalStateException( "Panel still contains parts: " + toRemove.getParts() );
        }

        final WorkbenchPanelPresenter presenterToRemove = mapPanelDefinitionToPresenter.remove( toRemove );
        if ( presenterToRemove == null ) {
            throw new IllegalArgumentException( "Couldn't find panel to remove: " + toRemove );
        }

        HasWidgets customContainer = customPanels.remove( toRemove );
        if ( customContainer != null ) {
            customContainer.remove( presenterToRemove.getPanelView().asWidget() );
        } else {
            final PanelDefinition parentDef = toRemove.getParent();
            final WorkbenchPanelPresenter parentPresenter = mapPanelDefinitionToPresenter.get( parentDef );
            if ( parentPresenter == null ) {
                throw new IllegalArgumentException( "The given panel's parent could not be found" );
            }

            parentPresenter.removePanel( presenterToRemove );
        }

        // we do this check last because some panel types (eg. docking panels) can "rescue" orphaned child panels
        // during the PanelPresenter.remove() call
        if ( !toRemove.getChildren().isEmpty() ) {
            throw new IllegalStateException( "Panel still contains child panels: " + toRemove.getChildren() );
        }

        getBeanFactory().destroy( presenterToRemove );
    }

    @Override
    public void onPartFocus( final PartDefinition part ) {
        activePart = part;
        panelFocusEvent.fire( new PanelFocusEvent( part.getParentPanel() ) );
        placeGainFocusEvent.fire( new PlaceGainFocusEvent( part.getPlace() ) );
    }

    @Override
    public void onPartMaximized( final PartDefinition part ) {
        placeMaximizedEventEvent.fire( new PlaceMaximizedEvent( part.getPlace() ) );
    }

    @Override
    public void onPartMinimized( final PartDefinition part ) {
        placeMinimizedEventEvent.fire( new PlaceMinimizedEvent( part.getPlace() ) );
    }

    @Override
    public void onPartHidden( final PartDefinition part ) {
        placeHiddenEvent.fire( new PlaceHiddenEvent( part.getPlace() ) );
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

    @Override
    public PanelDefinition getPanelForPlace( final PlaceRequest place ) {
        for ( PartDefinition part : mapPartDefinitionToPresenter.keySet() ) {
            if ( part.getPlace().equals( place ) ) {
                return part.getParentPanel();
            }
        }
        return null;
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

        PanelDefinition newPanel;
        if ( position == CompassPosition.ROOT ) {
            newPanel = rootPanelDef;
        } else if ( position == CompassPosition.SELF ) {
            newPanel = targetPanelPresenter.getDefinition();
        } else {
            String defaultChildType = targetPanelPresenter.getDefaultChildType();
            if ( defaultChildType == null ) {
                throw new IllegalArgumentException( "Target panel (type " + targetPanelPresenter.getClass().getName() + ")"
                        + " does not allow child panels" );
            }

            if ( childPanel.getPanelType().equals( PanelDefinition.PARENT_CHOOSES_TYPE ) ) {
                childPanel.setPanelType( defaultChildType );
            }

            final WorkbenchPanelPresenter childPanelPresenter = beanFactory.newWorkbenchPanel( childPanel );
            mapPanelDefinitionToPresenter.put( childPanel,
                                               childPanelPresenter );

            targetPanelPresenter.addPanel( childPanelPresenter,
                                           position );
            newPanel = childPanel;
        }

        onPanelFocus( newPanel );

        return newPanel;
    }

    @Override
    public PanelDefinition addCustomPanel( final HasWidgets container,
                                           final String panelType ) {
        PanelDefinition panelDef = new PanelDefinitionImpl( panelType );
        final WorkbenchPanelPresenter panelPresenter = beanFactory.newWorkbenchPanel( panelDef );
        Widget panelViewWidget = panelPresenter.getPanelView().asWidget();
        panelViewWidget.addAttachHandler( new CustomPanelCleanupHandler( panelPresenter ) );
        container.add( panelViewWidget );
        mapPanelDefinitionToPresenter.put( panelDef,
                                           panelPresenter );
        customPanels.put( panelDef, container );
        onPanelFocus( panelDef );
        return panelDef;
    }

    /**
     * Cleanup handler for custom panels that are removed from the DOM before they are removed via PlaceManager.
     *
     * @see PanelManagerImpl#addCustomPanel(HasWidgets, String)
     */
    private final class CustomPanelCleanupHandler implements AttachEvent.Handler {

        private final WorkbenchPanelPresenter panelPresenter;
        private boolean detaching;

        private CustomPanelCleanupHandler( WorkbenchPanelPresenter panelPresenter ) {
            this.panelPresenter = panelPresenter;
        }

        @Override
        public void onAttachOrDetach( AttachEvent event ) {
            if ( event.isAttached() ) {
                return;
            }
            if ( !detaching && mapPanelDefinitionToPresenter.containsKey( panelPresenter.getDefinition() ) ) {
                System.out.println("Running cleanup for " + Debug.objectId( this ));
                detaching = true;
                Scheduler.get().scheduleFinally( new ScheduledCommand() {
                    @Override
                    public void execute() {
                        try {
                            List<PartDefinition> parts = new ArrayList<PartDefinition>( panelPresenter.getDefinition().getParts() );
                            for ( PartDefinition part : parts ) {
                                placeManager.get().closePlace( part.getPlace() );
                            }

                            // in many cases, the panel will have cleaned itself up when we closed its last part in the loop above.
                            // for other custom panel use cases, the panel may still be open. we can do the cleanup here.
                            if ( mapPanelDefinitionToPresenter.containsKey( panelPresenter.getDefinition() ) ) {
                                removeWorkbenchPanel( panelPresenter.getDefinition() );
                            }
                        } finally {
                            detaching = false;
                        }
                    }
                } );
            }
        }
    }

}
