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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.mvp.AbstractScreenActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.shared.mvp.PlaceRequest;

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

        void addMenuItem(final AbstractMenuItem menuItem);

        void removeMenuItem(final AbstractMenuItem menuItem);
    }

    private final View     view;

    @Inject
    private PlaceManager   placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    public WorkbenchMenuBarPresenter(final View view) {
        this.view = view;
    }

    //Transient items currently held with the menu bar (i.e. not the "core" entries)
    private List<AbstractMenuItem> items = new ArrayList<AbstractMenuItem>();

    @SuppressWarnings("unused")
    @AfterInitialization
    //Configure the default menu items
    private void setupCoreItems() {

        //Static places
        final WorkbenchMenuBar placesMenuBar = new WorkbenchMenuBar();
        final SubMenuItem placesMenu = new SubMenuItem( "Places",
                                                        placesMenuBar );
        final List<AbstractScreenActivity> activities = getScreenActivities();
        for ( AbstractScreenActivity activity : activities ) {
            final String identifier = activity.getIdentifier();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo( new PlaceRequest( identifier ) );
                }

            };
            final CommandMenuItem item = new CommandMenuItem( identifier,
                                                              cmd );
            placesMenuBar.addItem( item );
        }
        view.addMenuItem( placesMenu );

        //Perspectives
        final WorkbenchMenuBar perspectivesMenuBar = new WorkbenchMenuBar();
        final SubMenuItem perspectivesMenu = new SubMenuItem( "Perspectives",
                                                              perspectivesMenuBar );
        final List<AbstractPerspectiveActivity> perspectives = getPerspectiveActivities();
        for ( final AbstractPerspectiveActivity perspective : perspectives ) {
            final String name = perspective.getPerspective().getName();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo( new PlaceRequest( perspective.getIdentifier() ) );
                }

            };
            final CommandMenuItem item = new CommandMenuItem( name,
                                                              cmd );
            perspectivesMenuBar.addItem( item );
        }
        view.addMenuItem( perspectivesMenu );

        //Simple "About" dialog
        view.addMenuItem( new CommandMenuItem( "About",
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

    private List<AbstractScreenActivity> getScreenActivities() {

        //Get Screen Activities
        final Set<AbstractScreenActivity> activities = activityManager.getActivities( AbstractScreenActivity.class );

        //Sort Activities so they're always in the same sequence!
        List<AbstractScreenActivity> sortedActivities = new ArrayList<AbstractScreenActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractScreenActivity>() {

                              @Override
                              public int compare(AbstractScreenActivity o1,
                                                 AbstractScreenActivity o2) {
                                  return o1.getTitle().compareTo( o2.getTitle() );
                              }

                          } );

        return sortedActivities;
    }

    private List<AbstractPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractPerspectiveActivity> activities = activityManager.getActivities(AbstractPerspectiveActivity.class);

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

    //Handle setting up the MenuBar for the specific WorkbenchPart selected
    void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchActivity activity = placeManager.getActivity( event.getWorkbenchPart() );
        if ( activity == null ) {
            return;
        }

        //Remove items from previous WorkbenchPart
        for ( AbstractMenuItem item : items ) {
            view.removeMenuItem( item );
        }

        //Add items for current WorkbenchPart
        items = new ArrayList<AbstractMenuItem>();
        for ( AbstractMenuItem item : WorkbenchMenuBarPresenterUtils.filterMenuItemsByPermission( activity.getMenuBar().getItems() ) ) {
            view.addMenuItem( item );
            items.add( item );
        }
    }

}
