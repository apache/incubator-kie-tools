/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public interface PerspectiveActivity extends ContextSensitiveActivity {

    /**
     * Returns a new copy of the layout (panels and their parts) that should be used if no persisted state is available.
     * Each time this method is called, it must produce a new PerspectiveDefinition. This rule applies whether or not
     * the perspective is transient.
     *
     * @return the perspective layout to use when a previously saved one is not available.
     * @see #isTransient()
     * @see WorkbenchServicesProxy#loadPerspective(String, org.uberfire.mvp.ParameterizedCommand)
     */
    PerspectiveDefinition getDefaultPerspectiveLayout();

    /**
     * Returns the unique identifier for this perspective. Should match the CDI bean name (the argument to the
     * activity's {@code @Named} annotation).
     */
    String getIdentifier();

    /**
     * Returns true if this perspective should be displayed automatically when the application starts. Each application
     * needs exactly one default perspective.
     *
     * @return true if this is the default perspective; false if it is not.
     */
    boolean isDefault();

    /**
     * Returns a new copy of the menus that should be used with this perspective. Each time this method is called, it
     * must produce a new set of menus.
     *
     * @return the menus to use while this perspective is active.
     */
    Menus getMenus();

    ToolBar getToolBar();

    /**
     * Tells whether this perspective's state (layout and size of panels, parts contained in each panel) should be saved
     * per user or not.
     *
     * @return false if this perspective's state should be saved and retrieved from the server; true if this perspective
     *         should always start up in its default layout.
     * @see WorkbenchServicesProxy#loadPerspective(String, org.uberfire.mvp.ParameterizedCommand)
     */
    boolean isTransient();

}
