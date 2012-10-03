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
package org.uberfire.client.workbench.widgets.menu;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.annotations.DefaultPerspective;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Presenter for WorkbenchMenuBar that mediates changes to the Workbench MenuBar
 * in response to changes to the selected WorkbenchPart. The menu structure is
 * cloned and items that lack permission are removed. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenter {

    public interface View
        extends
        IsWidget {

        void addMenuItem(final MenuItem menuItem);

        void removeMenuItem(final MenuItem menuItem);
    }

    private PartDefinition                 activePart;

    @Inject
    private View                           view;

    @Inject
    private PlaceManager                   placeManager;

    @Inject
    private IOCBeanManager                 iocManager;

    @Inject
    private ActivityManager                activityManager;

    @Inject
    private WorkbenchMenuBarPresenterUtils menuBarUtils;

    //Transient items currently held with the menu bar (i.e. not the "core" entries)
    private List<MenuItem>                 items = new ArrayList<MenuItem>();

    @SuppressWarnings("unused")
    @AfterInitialization
    //Configure the default menu items
    private void setupCoreItems() {

        //Home
        final AbstractPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        if ( defaultPerspective != null ) {
            view.addMenuItem( new DefaultMenuItemCommand( "Home",
                                                          new Command() {
                                                              @Override
                                                              public void execute() {
                                                                  placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                                                              }
                                                          } ) );
        }

        //Perspectives
        final MenuBar perspectivesMenuBar = new DefaultMenuBar();
        final MenuItemSubMenu perspectivesMenu = new DefaultMenuItemSubMenu( "Perspectives",
                                                                             perspectivesMenuBar );
        final List<AbstractPerspectiveActivity> perspectives = getPerspectiveActivities();
        for ( final AbstractPerspectiveActivity perspective : perspectives ) {
            final String name = perspective.getPerspective().getName();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo( new DefaultPlaceRequest( perspective.getIdentifier() ) );
                }

            };
            final MenuItemCommand item = new DefaultMenuItemCommand( name,
                                                                     cmd );
            perspectivesMenuBar.addItem( item );
        }
        view.addMenuItem( perspectivesMenu );

        //Simple "About" dialog
        view.addMenuItem( new DefaultMenuItemCommand( "About",
                                                      new Command() {

                                                          @Override
                                                          public void execute() {
                                                              Window.alert( "Uberfire" );
                                                          }

                                                      } ) );

    }

    public IsWidget getView() {
        return this.view;
    }

    private List<AbstractPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractPerspectiveActivity> activities = activityManager.getActivities( AbstractPerspectiveActivity.class );

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractPerspectiveActivity> sortedActivities = new ArrayList<AbstractPerspectiveActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractPerspectiveActivity>() {

                              @Override
                              public int compare(AbstractPerspectiveActivity o1,
                                                 AbstractPerspectiveActivity o2) {
                                  return o1.getPerspective().getName().compareTo( o2.getPerspective().getName() );
                              }

                          } );

        return sortedActivities;
    }

    //Handle removing the WorkbenchPart menu items
    void onWorkbenchPartClose(@Observes WorkbenchPartCloseEvent event) {
        if ( event.getPart().equals( activePart ) ) {
            removeMenuItems();
        }
    }

    //Handle removing the WorkbenchPart menu items
    void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        if ( event.getDeselectedPart().equals( activePart ) ) {
            removeMenuItems();
        }
    }

    //Handle setting up the MenuBar for the specific WorkbenchPart selected
    void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchActivity activity = placeManager.getActivity( event.getPart() );
        if ( activity == null ) {
            return;
        }

        if ( !event.getPart().equals( activePart ) ) {

            removeMenuItems();

            //Add items for current WorkbenchPart
            activePart = event.getPart();
            items = new ArrayList<MenuItem>();

            final MenuBar menuBar = activity.getMenuBar();
            if ( menuBar == null ) {
                return;
            }

            for ( MenuItem item : menuBarUtils.filterMenuItemsByPermission( menuBar.getItems() ) ) {
                view.addMenuItem( item );
                items.add( item );
            }
        }
    }

    private void removeMenuItems() {
        activePart = null;
        for ( MenuItem item : items ) {
            view.removeMenuItem( item );
        }
    }

    public void addMenuItem(final MenuItem menuItem) {
        if ( menuBarUtils.filterMenuItemByPermission( menuItem ) != null ) {
            view.addMenuItem( menuItem );
        }
    }

    private AbstractPerspectiveActivity getDefaultPerspectiveActivity() {
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
        return defaultPerspective;
    }

}
