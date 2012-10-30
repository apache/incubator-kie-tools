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
package org.uberfire.client.workbench.widgets.toolbar;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Presenter for WorkbenchToolBar that mediates changes to the Workbench ToolBar
 * in response to changes to the selected WorkbenchPart. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchToolBarPresenter {

    public interface View
            extends
            IsWidget {

        void addToolBarItem( final ToolBarItem item );

        void removeToolBarItem( final ToolBarItem item );
    }

    private PlaceRequest activePlace;

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchToolBarPresenterUtils toolBarUtils;

    //Items relating to the Workbench as a whole
    private List<ToolBarItem> workbenchItems = new ArrayList<ToolBarItem>();

    //Transient items relating to the current Workbench Perspective
    private List<ToolBarItem> workbenchPerspectiveItems = new ArrayList<ToolBarItem>();

    //Transient items relating to the current WorkbenchPart context
    private List<ToolBarItem> workbenchContextItems = new ArrayList<ToolBarItem>();

    public IsWidget getView() {
        return this.view;
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartClose( @Observes WorkbenchPartCloseEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartLostFocus( @Observes WorkbenchPartLostFocusEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle setting up the Tool Bar for the specific WorkbenchPart selected
    void onWorkbenchPartOnFocus( @Observes WorkbenchPartOnFocusEvent event ) {
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

            final ToolBar toolBar = wbActivity.getToolBar();
            if ( toolBar == null ) {
                return;
            }

            for ( ToolBarItem item : toolBar.getItems() ) {
                addWorkbenchContextItem( item );
            }
        }
    }

    public void addWorkbenchItem( final ToolBarItem item ) {
        if ( toolBarUtils.filterToolBarItemByPermission( item ) != null ) {
            workbenchItems.add( item );
            view.addToolBarItem( item );
        }
    }

    public void addWorkbenchPerspectiveItem( final ToolBarItem item ) {
        if ( toolBarUtils.filterToolBarItemByPermission( item ) != null ) {
            workbenchPerspectiveItems.add( item );
            view.addToolBarItem( item );
        }
    }

    public void addWorkbenchContextItem( final ToolBarItem item ) {
        if ( toolBarUtils.filterToolBarItemByPermission( item ) != null ) {
            workbenchContextItems.add( item );
            view.addToolBarItem( item );
        }
    }

    public void clearWorkbenchItems() {
        if ( workbenchItems.isEmpty() ) {
            return;
        }
        for ( ToolBarItem item : workbenchItems ) {
            view.removeToolBarItem( item );
        }
        workbenchItems.clear();
    }

    public void clearWorkbenchPerspectiveItems() {
        if ( workbenchPerspectiveItems.isEmpty() ) {
            return;
        }
        for ( ToolBarItem item : workbenchPerspectiveItems ) {
            view.removeToolBarItem( item );
        }
        workbenchPerspectiveItems.clear();
    }

    private void clearWorkbenchContextItems() {
        activePlace = null;
        if ( workbenchContextItems.isEmpty() ) {
            return;
        }
        for ( ToolBarItem item : workbenchContextItems ) {
            view.removeToolBarItem( item );
        }
        workbenchContextItems.clear();
    }

}
