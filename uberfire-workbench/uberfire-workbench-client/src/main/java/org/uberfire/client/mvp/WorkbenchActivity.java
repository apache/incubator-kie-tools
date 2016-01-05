/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.mvp;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * WorkbenchActivity and its subinterfaces define the interface between UberFire framework behaviour and
 * application-defined behaviour.
 * <p>
 * In the model-view-presenter (MVP) sense, an Activity is essentially an application-provided Presenter: it has a view
 * (its widget) and it defines a set of operations that can affect that view.
 * <p>
 * Applications can implement an Activity interface directly, they can subclass one of the abstract Activity
 * implementations that come with the framework, or they may rely on UberFire's annotation processors to generate
 * Activity implementations from annotated Java objects.
 * <p>
 * For example, to define a new Screen in an application, you can implement {@link WorkbenchScreenActivity}, extend
 * {@link AbstractWorkbenchScreenActivity}, or annotate a class with {@link WorkbenchScreen} and follow the rules
 * associated with that annotation.
 * <p>
 * Similarly for {@link WorkbenchEditorActivity}, {@link AbstractWorkbenchEditorActivity} and {@link WorkbenchEditor};
 * {@link PerspectiveActivity}, {@link AbstractWorkbenchPerspectiveActivity}, and {@link WorkbenchPerspective}; and so
 * on.
 */
public interface WorkbenchActivity extends ContextSensitiveActivity {

    /**
     * Invoked by the framework before this activity is closed, unless the framework has been told to "force close" the
     * activity.
     * <p>
     * The activity can cancel the close operation by returning {@code false} from this method. This is most often used
     * for implementing a "save before closing" workflow.
     *
     * @see PlaceManager#closeAllPlaces()
     * @see PlaceManager#closePlace(PlaceRequest)
     * @see PlaceManager#closePlace(String)
     *
     * @return true if the activity is ready to be closed; false if it should remain open.
     */
    boolean onMayClose();

    /**
     * Specifies custom positioning for this activity's view when the request to launch it comes without specific
     * positioning information of its own. For a newly launching activity, when this method returns a non-null value,
     * UberFire will attempt to create a new panel for this activity and add that new panel as a child of the current
     * perspective's root panel. The activity's view will then be added to the new panel.
     * <p>
     * If this method returns null, UberFire will attempt to add this activity's view directly to the root panel.
     * <p>
     * <b>Developers of cross-application reusable components should use caution with this feature.</b> If the component
     * declares a default position that is not supported by the root panel the application uses, the application's root
     * panel could throw a ClassCastException when the framework attempts to create the child panel at the given
     * position.
     *
     * @return the Position to add a new child panel to the root that accommodates this activity's view, or null if this
     *         activity's view should be added directly to the root panel.
     */
    Position getDefaultPosition();

    /**
     * Returns the PlaceRequest for the perspective that this activity should always be displayed in. When the
     * PlaceManager is asked to go to this activity, it will switching to the owning perspective first, and then show
     * this activity in it.
     *
     * @return the owning perspective's place request, or null if this activity can appear in any perspective.
     */
    PlaceRequest getOwningPlace();

    /**
     * Invoked by the UberFire framework when this activity is the current activity in the current panel. Activities may
     * choose to make their UI more prominent, or begin refreshing their data more aggressively when they are focused.
     *
     * @see #onLostFocus()
     * @see PanelManager#onPartFocus(org.uberfire.workbench.model.PartDefinition)
     */
    void onFocus();

    /**
     * Invoked by the UberFire framework when this activity is no longer the current activity in the current panel.
     * Typically undoes changes made in the corresponding {@link #onFocus()} call.
     *
     * @see #onLostFocus()
     * @see PanelManager#onPartFocus(org.uberfire.workbench.model.PartDefinition)
     */
    void onLostFocus();

    String getTitle();

    IsWidget getTitleDecoration();

    IsWidget getWidget();

    Menus getMenus();

    ToolBar getToolBar();

    String contextId();

    /**
     * Returns the amount of space that should be allocated to this activity if a new Workbench Panel is created when
     * first displaying it. Has no effect when the activity is added to a pre-existing panel, including the case where
     * the activity is added to a panel as part of a default perspective layout.
     *
     * @return the height, in pixels, that should be allocated for a new panel created to house this activity. Null
     *         means no particular height is preferred, and the framework can choose a default height.
     */
    Integer preferredHeight();

    /**
     * Returns the amount of space that should be allocated to this activity if a new Workbench Panel is created when
     * first displaying it. Has no effect when the activity is added to a pre-existing panel, including the case where
     * the activity is added to a panel as part of a default perspective layout.
     *
     * @return the width, in pixels, that should be allocated for a new panel created to house this activity. Null
     *         means no particular width is preferred, and the framework can choose a default width.
     */
    Integer preferredWidth();
}
