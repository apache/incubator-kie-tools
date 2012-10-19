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

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.workbench.WorkbenchServices;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

@ApplicationScoped
public class Workbench extends Composite {

    public static final int               WIDTH     = Window.getClientWidth();

    public static final int               HEIGHT    = Window.getClientHeight();

    private final VerticalPanel           container = new VerticalPanel();

    private final SimplePanel             workbench = new SimplePanel();

    private AbsolutePanel                 workbenchContainer;

    @Inject
    private PanelManager                  panelManager;

    @Inject
    private IOCBeanManager                iocManager;

    @Inject
    private WorkbenchDragAndDropManager   dndManager;

    @Inject
    private PlaceManager                  placeManager;

    @Inject
    private WorkbenchPickupDragController dragController;

    @Inject
    private WorkbenchMenuBarPresenter     menuBarPresenter;

    @Inject
    private WorkbenchToolBarPresenter     toolBarPresenter;

    @Inject
    private Caller<WorkbenchServices>     wbServices;

    @PostConstruct
    public void setup() {

        //Menu bar
        container.add( menuBarPresenter.getView() );

        //Tool bar
        container.add( toolBarPresenter.getView() );

        //Container panels for workbench
        workbenchContainer = dragController.getBoundaryPanel();
        workbenchContainer.add( workbench );
        container.add( workbenchContainer );

        initWidget( container );
    }

    @AfterInitialization
    @SuppressWarnings("unused")
    private void bootstrap() {

        //Clear environment
        workbench.clear();
        dndManager.unregisterDropControllers();

        //Size environment - Defer so Widgets have been rendered and hence sizes available
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                final int menuBarHeight = menuBarPresenter.getView().asWidget().getOffsetHeight();
                final int toolBarHeight = toolBarPresenter.getView().asWidget().getOffsetHeight();
                final int availableHeight = HEIGHT - menuBarHeight - toolBarHeight;
                workbenchContainer.setPixelSize( WIDTH,
                                                 availableHeight );
                workbench.setPixelSize( WIDTH,
                                        availableHeight );
            }

        } );

        //Add default workbench widget
        final PanelDefinition root = new PanelDefinitionImpl( true );
        panelManager.setRoot( root );
        workbench.setWidget( panelManager.getPanelView( root ) );

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        if ( defaultPerspective != null ) {
            placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
        }

        //Save Workbench state when Window is closed
        Window.addWindowClosingHandler( new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {
                final PerspectiveDefinition perspective = panelManager.getPerspective();
                if ( perspective != null ) {
                    wbServices.call( new RemoteCallback<Void>() {
                        @Override
                        public void callback(Void response) {
                            //Nothing to do. Window is closing.
                        }
                    } ).save( perspective );
                }
            }

        } );
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop : while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

}
