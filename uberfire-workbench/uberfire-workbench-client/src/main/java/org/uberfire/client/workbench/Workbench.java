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
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.ApplicationReadyEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.services.WorkbenchServices;

import static java.util.Collections.*;
import static org.uberfire.workbench.model.PanelType.*;

@ApplicationScoped
public class Workbench
        extends Composite
        implements RequiresResize {

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
    private Caller<WorkbenchServices> wbServices;

    @Inject
    private Caller<VFSService> vfsService;

    @PostConstruct
    public void setup() {
        initWidget( container );
    }

    private void setupHeaders() {
        final Collection<IOCBeanDef<Header>> headerBeans = iocManager.lookupBeans( Header.class );
        final List<Header> instances = new ArrayList<Header>();
        for ( final IOCBeanDef<Header> headerBean : headerBeans ) {
            instances.add( headerBean.getInstance() );
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

    @SuppressWarnings("unused")
    private void bootstrap( @Observes ApplicationReadyEvent event ) {
        if ( !Window.Location.getParameterMap().containsKey( "standalone" ) ) {
            setupHeaders();
        }

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
        if ( !Window.Location.getParameterMap().containsKey( "standalone" ) ) {
            final PerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
            if ( defaultPerspective != null ) {
                placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
            }
        } else {
            handleIntegration( Window.Location.getParameterMap() );
        }

        //Save Workbench state when Window is closed
        Window.addWindowClosingHandler( new ClosingHandler() {

            @Override
            public void onWindowClosing( ClosingEvent event ) {
                final PerspectiveDefinition perspective = panelManager.getPerspective();
                if ( perspective != null ) {
                    wbServices.call( new RemoteCallback<Void>() {
                        @Override
                        public void callback( Void response ) {
                            //Nothing to do. Window is closing.
                        }
                    } ).save( perspective );
                }
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

    private void handleIntegration( final Map<String, List<String>> parameters ) {
        if ( parameters.containsKey( "perspective" ) && !parameters.get( "perspective" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( parameters.get( "perspective" ).get( 0 ) ) );
        } else if ( parameters.containsKey( "path" ) && !parameters.get( "path" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( "StandaloneEditorPerspective" ) );
            vfsService.call( new RemoteCallback<Path>() {
                @Override
                public void callback( Path response ) {
                    if ( parameters.containsKey( "editor" ) && !parameters.get( "editor" ).isEmpty() ) {
                        placeManager.goTo( new PathPlaceRequest( response, parameters.get( "editor" ).get( 0 ) ) );
                    } else {
                        placeManager.goTo( new PathPlaceRequest( response ) );
                    }
                }
            } ).get( parameters.get( "path" ).get( 0 ) );
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
        final int availableHeight;
        if ( !Window.Location.getParameterMap().containsKey( "standalone" ) ) {
            availableHeight = height - headersHeight;
        } else {
            availableHeight = height;
        }

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
