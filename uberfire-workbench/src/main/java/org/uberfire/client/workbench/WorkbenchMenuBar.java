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
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;

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

    private List<MenuItem> items = new ArrayList<MenuItem>();

    @SuppressWarnings("unused")
    @AfterInitialization
    private void setup() {
        addItem( new MenuItem( "About",
                               new Command() {

                                   @Override
                                   public void execute() {
                                       Window.alert( "Uberfire" );
                                   }

                               } ) );
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
