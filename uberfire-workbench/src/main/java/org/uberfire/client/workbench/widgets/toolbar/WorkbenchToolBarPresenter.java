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

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;

import com.google.gwt.user.client.ui.IsWidget;

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

        void addToolBarItem(final ToolBarItem item);

        void removeToolBarItem(final ToolBarItem item);
    }

    private PartDefinition                 activePart;

    @Inject
    private View                           view;

    @Inject
    private PlaceManager                   placeManager;

    @Inject
    private WorkbenchToolBarPresenterUtils toolBarUtils;

    //Transient items currently held with the tool bar (i.e. not the "core" entries)
    private List<ToolBarItem>              items = new ArrayList<ToolBarItem>();

    @SuppressWarnings("unused")
    @AfterInitialization
    //Configure the default menu items
    private void setupCoreItems() {
    }

    public IsWidget getView() {
        return this.view;
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartClose(@Observes WorkbenchPartCloseEvent event) {
        if ( event.getPart().equals( activePart ) ) {
            removeToolBarItems();
        }
    }

    //Handle removing the WorkbenchPart Tool Bar items
    void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        if ( event.getDeselectedPart().equals( activePart ) ) {
            removeToolBarItems();
        }
    }

    //Handle setting up the Tool Bar for the specific WorkbenchPart selected
    void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchActivity activity = placeManager.getActivity( event.getPart() );
        if ( activity == null ) {
            return;
        }

        if ( !event.getPart().equals( activePart ) ) {
            removeToolBarItems();

            //Add items for current WorkbenchPart
            activePart = event.getPart();
            items = new ArrayList<ToolBarItem>();

            final ToolBar toolBar = activity.getToolBar();
            if ( toolBar == null ) {
                return;
            }

            for ( ToolBarItem item : toolBarUtils.filterToolBarItemsByPermission( toolBar.getItems() ) ) {
                view.addToolBarItem( item );
                items.add( item );
            }
        }
    }

    private void removeToolBarItems() {
        activePart = null;
        for ( ToolBarItem item : items ) {
            view.removeToolBarItem( item );
        }
    }

    public void addMenuItem(final ToolBarItem item) {
        if ( toolBarUtils.filterToolBarItemByPermission( item ) != null ) {
            view.addToolBarItem( item );
        }
    }

}
