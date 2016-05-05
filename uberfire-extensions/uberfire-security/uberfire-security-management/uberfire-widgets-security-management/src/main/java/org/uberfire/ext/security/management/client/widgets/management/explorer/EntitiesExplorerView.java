/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;

import java.util.Set;

/**
 * <p>Entities explorer view methods signatures.</p>
 *           
 * @since 0.8.0
 */
public interface EntitiesExplorerView extends IsWidget {

    /**
     * <p>Configures the explorer's list widget.</p>
     * @param entityType A string that represents the current exploring type for the entities (eg: user, group). Used in buttons and texts in the explorer.
     * @param entitiesListView The view for entities list widget.
     * @see <a>org.uberfire.ext.security.management.client.widgets.management.EntitiesListView</a>
     * @return The view instance.
     */
    EntitiesExplorerView configure(final String entityType, final EntitiesList.View entitiesListView);

    /**
     * <p>Shows the list.</p>
     * @param context The view context. 
     * @see <a>org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView.ViewContext</a>
     * @param callback The view callback methods.
     * @see <a>org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView.ViewCallback</a>
     * @return The view instance.
     */
    EntitiesExplorerView show(final ViewContext context, final ViewCallback callback);
    
    /**
     * <p>Shows a meesage in the entities explorer view. No entities will be displayed, just the message.</p>
     * @param labelType The type of the label for the message.
     * @param message The message.
     * @return The view instance.
     */
    EntitiesExplorerView showMessage(final LabelType labelType, final String message);

    /**
     * <p>Clears the current search.</p>
     * @return The view instance.
     */
    EntitiesExplorerView clearSearch();
    
    /**
     * <p>Clears the view.</p>
     * @return The view instance.
     */
    EntitiesExplorerView clear();

    /**
     * <p>The context for the entities explorer view.</p>
     * <p>The view uses this callback methods for internal features management.</p>
     */
    interface ViewContext {

        /**
         * <p>Allows enabling or disabling the entities search feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities explorer widget enables the search feature.</li>
         *              <li><code>false</code> - The entities explorer widget disables the search feature.</li>
         *          </ul>
         */
        boolean canSearch();

        /**
         * <p>Allows enabling or disabling the entities creation feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities explorer widget enables the create feature.</li>
         *              <li><code>false</code> - The entities explorer widget disables the create feature.</li>
         *              <li><code>null</code> - The entities explorer widget will determine if create is enabled by using the UserSysteManager API.</li>
         *          </ul>
         */
        boolean canCreate();

        /**
         * <p>Allows enabling or disabling the entities read feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities explorer widget enables the read feature.</li>
         *              <li><code>false</code> - The entities explorer widget disables the read feature.</li>
         *          </ul>
         */
        boolean canRead();

        /**
         * <p>Allows enabling or disabling the entities delete feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities explorer widget enables the delete feature.</li>
         *              <li><code>false</code> - The entities explorer widget disables the delete feature.</li>
         *          </ul>
         */
        boolean canDelete();

        /**
         * <p>Allows enabling or disabling the entities selection feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities explorer widget enables the selection feature.</li>
         *              <li><code>false</code> - The entities explorer widget disables the selection feature.</li>
         *          </ul>
         */
        boolean canSelect();

        /**
         * <p>If <code>canSelect()</code> is enabled, the entity identifiers specified in the collection values will be mark as selected.</p>
         * @return <p>A collection of the selected entity identifiers present in the entities collection returned by <code>getEntities()</code>,</p>
         */
        Set<String> getSelectedEntities();

        /**
         * <p>The entity identifiers that cannot be used.</p>
         */
        Set<String> getConstrainedEntities();

    }

    /**
     * <p>Callback methods for view's user actions.</p>
     */
    interface ViewCallback {

        /**
         * <p>Search entities.</p>
         * @param pattern The search pattern.
         */
        void onSearch(final String pattern);

        /**
         * <p>User requests a refresh.</p>
         */
        void onRefresh();

    }
    
}
