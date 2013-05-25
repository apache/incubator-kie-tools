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
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.PlaceGainFocusEvent;
import org.uberfire.workbench.events.PlaceLostFocusEvent;
import org.uberfire.workbench.model.toolbar.ToolBar;

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

        void addToolBar( final ToolBar toolBar );

        void removeToolBar( final ToolBar toolBar );

        int getHeight();

        void hide();

        void show();
    }

    private PlaceRequest activePlace;

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchToolBarPresenterUtils toolBarUtils;

    //Items relating to the Workbench as a whole
    private List<ToolBar> workbenchItems = new ArrayList<ToolBar>();

    //Transient items relating to the current Workbench Perspective
    private List<ToolBar> workbenchPerspectiveItems = new ArrayList<ToolBar>();

    //Transient items relating to the current WorkbenchPart context
    private List<ToolBar> workbenchContextItems = new ArrayList<ToolBar>();

    public IsWidget getView() {
        return this.view;
    }

    public int getHeight() {
        return this.view.getHeight();
    }

    public void hide() {
        this.view.hide();
    }

    public void show() {
        this.view.show();
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartLostFocus( @Observes PlaceLostFocusEvent event ) {
        if ( event.getPlace().equals( activePlace ) ) {
            clearWorkbenchContextItems();
        }
    }

    //Handle setting up the Tool Bar for the specific WorkbenchPart selected
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

            final ToolBar toolBar = wbActivity.getToolBar();
            if ( toolBar == null ) {
                return;
            }

            addWorkbenchContextItem( toolBar );
        }
    }

    public void addWorkbenchItem( final ToolBar toolBar ) {

        final ToolBar filteredToolBar = toolBarUtils.filterToolBarItemsByPermission( toolBar );

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchItems.add( filteredToolBar );
            view.addToolBar( filteredToolBar );
        }
    }

    public void addWorkbenchPerspective( final ToolBar toolBar ) {
        final ToolBar filteredToolBar = toolBarUtils.filterToolBarItemsByPermission( toolBar );

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchPerspectiveItems.add( filteredToolBar );
            view.addToolBar( filteredToolBar );
        }
    }

    public void addWorkbenchContextItem( final ToolBar toolBar ) {
        final ToolBar filteredToolBar = toolBarUtils.filterToolBarItemsByPermission( toolBar );

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchContextItems.add( filteredToolBar );
            view.addToolBar( filteredToolBar );
        }
    }

    public void clearWorkbenchItems() {
        if ( workbenchItems.isEmpty() ) {
            return;
        }
        for ( ToolBar item : workbenchItems ) {
            view.removeToolBar( item );
        }
        workbenchItems.clear();
    }

    public void clearWorkbenchPerspectiveItems() {
        if ( workbenchPerspectiveItems.isEmpty() ) {
            return;
        }
        for ( ToolBar item : workbenchPerspectiveItems ) {
            view.removeToolBar( item );
        }
        workbenchPerspectiveItems.clear();
    }

    private void clearWorkbenchContextItems() {
        activePlace = null;
        if ( workbenchContextItems.isEmpty() ) {
            return;
        }
        for ( final ToolBar toolBar : workbenchContextItems ) {
            view.removeToolBar( toolBar );
        }
        workbenchContextItems.clear();
    }

}
