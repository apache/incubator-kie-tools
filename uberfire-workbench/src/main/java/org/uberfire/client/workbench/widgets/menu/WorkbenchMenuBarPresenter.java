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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.widgets.events.PlaceLostFocusEvent;
import org.uberfire.shared.mvp.PlaceRequest;

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

        void addMenuItem( final MenuItem menuItem );

        void removeMenuItem( final MenuItem menuItem );
    }

    private PlaceRequest activePlace;

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenterUtils menuBarUtils;

    //Items relating to the Workbench as a whole
    private List<MenuItem> workbenchItems = new ArrayList<MenuItem>();

    //Transient items relating to the current Workbench Perspective
    private List<MenuItem> workbenchPerspectiveItems = new ArrayList<MenuItem>();

    //Transient items relating to the current WorkbenchPart context
    private List<MenuItem> workbenchContextItems = new ArrayList<MenuItem>();

    public IsWidget getView() {
        return this.view;
    }

    //Handle removing the WorkbenchPart menu items
    void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle removing the WorkbenchPart menu items
    void onWorkbenchPartLostFocus( @Observes PlaceLostFocusEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle setting up the MenuBar for the specific WorkbenchPart selected
    void onWorkbenchPartOnFocus( @Observes PlaceGainFocusEvent event ) {
        final Activity activity = placeManager.getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( !( activity instanceof WorkbenchActivity ) ) {
            return;
        }
        final WorkbenchActivity wbActivity = (WorkbenchActivity) activity;

        if ( !event.getPlace().equals( activePlace ) ) {

            clearWorkbenchContextItems();

            //Add items for current WorkbenchPart
            activePlace = event.getPlace();

            final MenuBar menuBar = wbActivity.getMenuBar();
            if ( menuBar == null ) {
                return;
            }

            for ( MenuItem item : menuBar.getItems() ) {
                addWorkbenchContextItem( item );
            }
        }
    }

    public void addWorkbenchItem( final MenuItem menuItem ) {
        if ( menuBarUtils.filterMenuItemByPermission( menuItem ) != null ) {
            workbenchItems.add( menuItem );
            view.addMenuItem( menuItem );
        }
    }

    public void addWorkbenchPerspectiveItem( final MenuItem item ) {
        if ( menuBarUtils.filterMenuItemByPermission( item ) != null ) {
            workbenchPerspectiveItems.add( item );
            view.addMenuItem( item );
        }
    }

    public void addWorkbenchContextItem( final MenuItem menuItem ) {
        if ( menuBarUtils.filterMenuItemByPermission( menuItem ) != null ) {
            workbenchContextItems.add( menuItem );
            view.addMenuItem( menuItem );
        }
    }

    public void clearWorkbenchItems() {
        if ( workbenchItems.isEmpty() ) {
            return;
        }
        for ( MenuItem item : workbenchItems ) {
            view.removeMenuItem( item );
        }
        workbenchItems.clear();
    }

    public void clearWorkbenchPerspectiveItems() {
        if ( workbenchPerspectiveItems.isEmpty() ) {
            return;
        }
        for ( MenuItem item : workbenchPerspectiveItems ) {
            view.removeMenuItem( item );
        }
        workbenchPerspectiveItems.clear();
    }

    private void clearWorkbenchContextItems() {
        activePlace = null;
        if ( workbenchContextItems.isEmpty() ) {
            return;
        }
        for ( MenuItem item : workbenchContextItems ) {
            view.removeMenuItem( item );
        }
        workbenchContextItems.clear();
    }
}
