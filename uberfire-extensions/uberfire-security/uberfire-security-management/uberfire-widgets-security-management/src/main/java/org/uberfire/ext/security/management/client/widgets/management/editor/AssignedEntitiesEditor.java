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

package org.uberfire.ext.security.management.client.widgets.management.editor;

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView;
import org.uberfire.mvp.Command;

/**
 * <p>An editor view contract for modifying the assigned entities.</p>
 * <p>This view wraps the given <code>EntitiesExplorerView</code> instance for exploring the users, groups or roles and adds editing features.</p>
 * @param <T> The presenter instance.
 * 
 * @since 0.8.0
 */
public interface AssignedEntitiesEditor<T> extends UberView<T> {

    /**
     * Sets the explorer view being wrapped and the editor's title..
     * @param explorerView The explorer view widget.
     * @return The view implementation.
     */
    AssignedEntitiesEditor<T> configure(final EntitiesExplorerView explorerView);

    /**
     * Configures the editor close button's text and click callback.
     * @param closeText The button's text..
     * @param closeCallback The button's click handler callback.
     * @return The view implementation.
     */
    AssignedEntitiesEditor<T> configureClose(final String closeText, final Command closeCallback);

    /**
     * Configures the editor save button's text and click callback.
     * @param saveText The button's text..
     * @param saveCallback The button's click handler callback.
     * @return The view implementation.
     */
    AssignedEntitiesEditor<T> configureSave(final String saveText, final Command saveCallback);

    /**
     * Show the editor view.
     * @param header The editor's header.
     * @return The view implementation.
     */
    AssignedEntitiesEditor<T> show(final String header);

    /**
     * Hide the editor view.
     * @return The view implementation.
     */
    AssignedEntitiesEditor<T> hide();

}