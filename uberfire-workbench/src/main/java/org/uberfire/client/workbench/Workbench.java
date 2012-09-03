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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.annotations.DefaultPerspective;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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

    //@Inject
    //private BeanFactory                   factory;

    @Inject
    private WorkbenchMenuBarPresenter     menuBarPresenter;

    @PostConstruct
    public void setup() {

        //Menu bar
        container.add( menuBarPresenter.getView() );

        //Container panels for workbench
        workbenchContainer = dragController.getBoundaryPanel();
        workbenchContainer.add( workbench );
        container.add( workbenchContainer );

        initWidget( container );

        //Schedule creation of the default perspective
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                //We need to defer execution until the browser has completed initial layout
                final Timer t = new Timer() {
                    public void run() {
                        bootstrap();
                    }
                };
                t.schedule( 500 );
            }

        } );

    }

    private void bootstrap() {

        //Clear environment
        workbench.clear();
        dndManager.unregisterDropControllers();

        //Size environment
        final int menuBarHeight = menuBarPresenter.getView().asWidget().getOffsetHeight();
        workbenchContainer.setPixelSize( WIDTH,
                                         HEIGHT - menuBarHeight );
        workbench.setPixelSize( WIDTH,
                                HEIGHT - menuBarHeight );

        //Add default workbench widget
        final PanelDefinition panel = panelManager.addWorkbenchPanel( new PanelDefinition( true ),
                                                                      Position.ROOT );
        workbench.setWidget( panelManager.getPanelView( panel ) );

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        AbstractPerspectiveActivity defaultPerspective = null;

        Collection<IOCBeanDef<AbstractPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractPerspectiveActivity.class );
        Iterator<IOCBeanDef<AbstractPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop : while ( perspectivesIterator.hasNext() ) {
            IOCBeanDef<AbstractPerspectiveActivity> perspective = perspectivesIterator.next();
            Set<Annotation> annotations = perspective.getQualifiers();
            for ( Annotation a : annotations ) {
                if ( a instanceof DefaultPerspective ) {
                    defaultPerspective = perspective.getInstance();
                    break outer_loop;
                }
            }
        }

        //If a default perspective was found load it up!
        if ( defaultPerspective != null ) {
            placeManager.goTo( new PlaceRequest( defaultPerspective.getIdentifier() ) );
        }
    }

}
