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
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.toolbar.ToolBar;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Presenter for WorkbenchToolBar that mediates changes to the Workbench ToolBar
 * in response to changes to the selected WorkbenchPart. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchToolBarPresenter {

    /**
     * View contract for an UberFire toolbar view.
     */
    public interface View
            extends
            IsWidget {

        /**
         * Adds a set of Tool Bar items to the view. The Presenter has already verified that the user has permission to
         * see the items in the given toolbar.
         */
        void addToolBar( final ToolBar toolBar );

        /**
         * Removes a set of Tool Bar items from the view. Has no effect if the items were not already in this view.
         */
        void removeToolBar( final ToolBar toolBar );

        /**
         * Returns the pixel height of this toolbar.
         */
        int getHeight();

        /**
         * Makes this toolbar invisible.
         */
        void hide();

        /**
         * Makes this toolbar visible.
         */
        void show();
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    //Items relating to the Workbench as a whole
    private final List<ToolBar> workbenchItems = new ArrayList<ToolBar>();

    //Transient items relating to the current Workbench Perspective
    private final List<ToolBar> workbenchPerspectiveItems = new ArrayList<ToolBar>();

    //Transient items relating to the current WorkbenchPart context
    private final Multimap<PlaceRequest, ToolBar> workbenchContextItems = ArrayListMultimap.create();

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

    /**
     * Removes all the ToolBar items that were previously added for the given place, if any.
     */
    private void removeItemsFor(final PlaceRequest place) {
    	Collection<ToolBar> removed = workbenchContextItems.removeAll(place);
        for (final ToolBar toolBar : removed) {
            view.removeToolBar(toolBar);
        }
    }

    /**
	 * Adds all the ToolBar items associated with the given place to this
	 * toolbar. The exact list of items added is remembered, and can be removed
	 * later by a call to {@link #removeItemsFor(PlaceRequest)}.
	 * <p>
	 * The toolbar items are filtered for the current user subject to their
	 * security requirements.
	 * <p>
	 * This method becomes a no-op when any of the following is true:
	 * <ul>
	 *  <li>The place doesn't have an associated {@link Activity}
	 *  <li>The place's Activity is not a {@link WorkbenchActivity}
	 *  <li>The place's WorkbenchActivity doesn't have a {@link ToolBar}
	 * </ul>
	 */
    public void addItemsFor(final PlaceRequest place) {
        final Activity activity = placeManager.getActivity(place);
        if ( activity == null ) {
            return;
        }
        if ( !( activity instanceof WorkbenchActivity ) ) {
            return;
        }
        final WorkbenchActivity wbActivity = (WorkbenchActivity) activity;

        final ToolBar toolBar = wbActivity.getToolBar();
        if ( toolBar == null ) {
            return;
        }

        final ToolBar filteredToolBar = filterToolBarItemsByPermission(toolBar);

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchContextItems.put(place, filteredToolBar);
            view.addToolBar( filteredToolBar );
        }
    }

    /**
	 * Removes the toolbar items of a WorkbenchPart when that part is closed.
	 */
    void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
    	removeItemsFor(event.getPlace());
    }

    /**
	 * Adds the toolbar items of a WorkbenchPart when that part is created.
	 * <p>
	 * TODO(UF-6): change this to observe PlaceOpenedEvent when such an event exists.
	 */
    void onWorkbenchPartOnFocus( @Observes PlaceGainFocusEvent event ) {
        if ( !workbenchContextItems.containsKey(event.getPlace()) ) {
            addItemsFor(event.getPlace());
        }
    }

    public void addWorkbenchItem( final ToolBar toolBar ) {

        final ToolBar filteredToolBar = filterToolBarItemsByPermission( toolBar );

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchItems.add( filteredToolBar );
            view.addToolBar( filteredToolBar );
        }
    }

    public void addWorkbenchPerspective( final ToolBar toolBar ) {
        final ToolBar filteredToolBar = filterToolBarItemsByPermission( toolBar );

        if ( !filteredToolBar.getItems().isEmpty() ) {
            workbenchPerspectiveItems.add( filteredToolBar );
            view.addToolBar( filteredToolBar );
        }
    }

    private ToolBar filterToolBarItemsByPermission(ToolBar toolBar) {
    	return toolBar; // TODO (UF-2)
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
}
