/*
 * Copyright 2012 JBoss Inc
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import static java.util.Collections.*;
import static org.uberfire.workbench.model.PanelType.*;

@ApplicationScoped
public class Workbench
        extends Composite
        implements RequiresResize {

    /**
     * List of classes who want to do stuff (often server communication) before the workbench shows up.
     */
    private final Set<Class<?>> startupBlockers = new HashSet<Class<?>>();

    @Inject
    private Event<ApplicationReadyEvent> appReady;

    private final FlowPanel container = new FlowPanel();

    private final FlowPanel headers = new FlowPanel();

    private final SimplePanel workbench = new SimplePanel();

    private AbsolutePanel workbenchContainer;

    @Inject
    private PanelManager panelManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchPickupDragController dragController;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private VFSServiceProxy vfsService;

    private WorkbenchCloseHandler workbenchCloseHandler = GWT.create( WorkbenchCloseHandler.class );

    private Command workbenchCloseCommand = new Command() {
        @Override
        public void execute() {
            final PerspectiveDefinition perspective = panelManager.getPerspective();
            if ( perspective != null ) {
                wbServices.save( perspective );
            }
        }

    };

    private boolean isStandaloneMode = false;
    private final Set<String> headersToKeep = new HashSet<String>();

    /**
     * Requests that the workbench does not attempt to create any UI parts until the given responsible party has
     * been removed as a startup blocker. Blockers are tracked as a set, so adding the same class more than once has no
     * effect.
     * @param responsibleParty any Class object; typically it will be the class making the call to this method.
     * Must not be null.
     */
    public void addStartupBlocker( Class<?> responsibleParty ) {
        startupBlockers.add( responsibleParty );
        System.out.println( responsibleParty.getName() + " is blocking workbench startup." );
    }

    /**
     * Causes the given responsible party to no longer block workbench initialization.
     * If the given responsible party was not already in the blocking set (either because
     * it was never added, or it has already been removed) then the method call has no effect.
     * <p/>
     * After removing the blocker, if there are no more blockers left in the blocking set, the workbench UI is
     * bootstrapped immediately. If there are still one or more blockers left in the blocking set, the workbench UI
     * remains uninitialized.
     * @param responsibleParty any Class object that was previously passed to {@link #addStartupBlocker(Class)}.
     * Must not be null.
     */
    public void removeStartupBlocker( Class<?> responsibleParty ) {
        if ( startupBlockers.remove( responsibleParty ) ) {
            System.out.println( responsibleParty.getName() + " is no longer blocking startup." );
        } else {
            System.out.println( responsibleParty.getName() + " tried to unblock startup, but it wasn't blocking to begin with!" );
        }
        startIfNotBlocked();
    }

    // package-private so tests can call in
    @AfterInitialization
    void startIfNotBlocked() {
        System.out.println( startupBlockers.size() + " workbench startup blockers remain." );
        if ( startupBlockers.isEmpty() ) {
            bootstrap();
        }
    }

    @PostConstruct
    public void setup() {
        initWidget( container );

        isStandaloneMode = Window.Location.getParameterMap().containsKey( "standalone" );

        for ( final Map.Entry<String, List<String>> parameter : Window.Location.getParameterMap().entrySet() ) {
            if ( parameter.getKey().equals( "header" ) ) {
                headersToKeep.addAll( parameter.getValue() );
            }
        }
    }

    private void setupHeaders() {
        final Collection<IOCBeanDef<Header>> headerBeans = iocManager.lookupBeans( Header.class );
        final List<Header> instances = new ArrayList<Header>();
        for ( final IOCBeanDef<Header> headerBean : headerBeans ) {
            final Header instance = headerBean.getInstance();
            if ( isStandaloneMode ) {
                if ( headersToKeep.contains( instance.getId() ) ) {
                    instances.add( instance );
                }
            } else {
                instances.add( instance );
            }
        }

        sort( instances, new Comparator<Header>() {
            @Override
            public int compare( final Header o1,
                                final Header o2 ) {
                if ( o1.getOrder() < o2.getOrder() ) {
                    return 1;
                } else if ( o1.getOrder() > o2.getOrder() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );

        for ( final Header header : instances ) {
            headers.add( header.asWidget() );
        }

        container.add( headers );
    }

    private void bootstrap() {
        setupHeaders();

        appReady.fire( new ApplicationReadyEvent() );

        //Container panels for workbench
        workbenchContainer = dragController.getBoundaryPanel();
        workbenchContainer.add( workbench );
        container.add( workbenchContainer );

        //Clear environment
        workbench.clear();
        dndManager.unregisterDropControllers();

        //Add default workbench widget
        final PanelDefinition root = new PanelDefinitionImpl( ROOT_STATIC );
        panelManager.setRoot( root );
        workbench.setWidget( panelManager.getPanelView( root ) );

        //Size environment - Defer so Widgets have been rendered and hence sizes available
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                final int width = Window.getClientWidth();
                final int height = Window.getClientHeight();
                doResizeWorkbenchContainer( width,
                                            height );
            }

        } );

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        if ( !isStandaloneMode ) {
            final PerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
            if ( defaultPerspective != null ) {
                placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
            }
        } else {
            handleStandaloneMode( Window.Location.getParameterMap() );
        }

        //Save Workbench state when Window is closed
        Window.addWindowClosingHandler( new ClosingHandler() {

            @Override
            public void onWindowClosing( ClosingEvent event ) {
                workbenchCloseHandler.onWindowClose( workbenchCloseCommand );
            }

        } );

        //Resizing the Window should resize everything
        Window.addResizeHandler( new ResizeHandler() {
            @Override
            public void onResize( ResizeEvent event ) {
                doResizeWorkbenchContainer( event.getWidth(),
                                            event.getHeight() );
            }
        } );

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

    private void handleStandaloneMode( final Map<String, List<String>> parameters ) {
        if ( parameters.containsKey( "perspective" ) && !parameters.get( "perspective" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( parameters.get( "perspective" ).get( 0 ) ) );
        } else if ( parameters.containsKey( "path" ) && !parameters.get( "path" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( "StandaloneEditorPerspective" ) );
            vfsService.get( parameters.get( "path" ).get( 0 ), new ParameterizedCommand<Path>() {
                @Override
                public void execute( final Path response ) {
                    if ( parameters.containsKey( "editor" ) && !parameters.get( "editor" ).isEmpty() ) {
                        placeManager.goTo( new PathPlaceRequest( response, parameters.get( "editor" ).get( 0 ) ) );
                    } else {
                        placeManager.goTo( new PathPlaceRequest( response ) );
                    }
                }
            } );
        }
    }

    private PerspectiveActivity getDefaultPerspectiveActivity() {
        PerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<PerspectiveActivity>> perspectives = iocManager.lookupBeans( PerspectiveActivity.class );

        for ( final IOCBeanDef<PerspectiveActivity> perspective : perspectives ) {
            final PerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    @Override
    public void onResize() {
        final int width = Window.getClientWidth();
        final int height = Window.getClientHeight();
        doResizeWorkbenchContainer( width, height );
    }

    private void doResizeWorkbenchContainer( final int width,
                                             final int height ) {
        final int headersHeight = headers.asWidget().getOffsetHeight();
        final int availableHeight = height - headersHeight;

        workbenchContainer.setPixelSize( width, availableHeight );
        workbench.setPixelSize( width, availableHeight );

        final Widget w = workbench.getWidget();
        if ( w != null ) {
            if ( w instanceof RequiresResize ) {
                ( (RequiresResize) w ).onResize();
            }
        }
    }
}
