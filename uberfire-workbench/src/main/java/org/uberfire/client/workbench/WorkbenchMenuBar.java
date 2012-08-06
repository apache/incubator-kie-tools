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
package org.uberfire.client.workbench;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.mvp.AbstractScreenActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A Workbench-wide MenuBar
 */
@ApplicationScoped
public class WorkbenchMenuBar extends MenuBar {

    @Inject
    private PlaceManager   placeManager;

    @Inject
    private IOCBeanManager iocManager;

    private List<MenuItem> items = new ArrayList<MenuItem>();

    @SuppressWarnings("unused")
    @AfterInitialization
    private void setup() {
        final MenuBar placesMenuBar = new MenuBar( true );
        final MenuItem placesMenu = new MenuItem( "Places",
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
            placesMenuBar.addItem( identifier,
                                   cmd );
        }

        addItem( placesMenu );
        addItem( new MenuItem( "About",
                               new Command() {

                                   @Override
                                   public void execute() {
                                       Window.alert( "Uberfire" );
                                   }

                               } ) );
    }

    private List<AbstractScreenActivity> getScreenActivities() {

        //Get Screen Activities
        final Set<AbstractScreenActivity> activities = new HashSet<AbstractScreenActivity>();
        Collection<IOCBeanDef<AbstractScreenActivity>> activityBeans = iocManager.lookupBeans( AbstractScreenActivity.class );
        for ( IOCBeanDef<AbstractScreenActivity> activityBean : activityBeans ) {
            final AbstractScreenActivity instance = (AbstractScreenActivity) activityBean.getInstance();
            activities.add( instance );
        }

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

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchActivity activity = placeManager.getActivity( event.getWorkbenchPart() );
        if ( activity == null ) {
            return;
        }
        for ( MenuItem item : items ) {
            removeItem( item );
        }
        items = activity.getMenuItems();
        for ( MenuItem item : items ) {
            addItem( item );
        }
    }

}
