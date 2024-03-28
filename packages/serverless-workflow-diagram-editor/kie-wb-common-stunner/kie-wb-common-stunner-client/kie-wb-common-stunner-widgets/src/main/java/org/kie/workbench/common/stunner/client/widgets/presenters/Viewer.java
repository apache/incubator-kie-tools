/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.widgets.presenters;

import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;

/**
 * A generic viewer type for instances of type <code>T</code>.
 *
 * @param <T> The instance type supported.
 * @param <H> The handler type.
 * @param <V> The view type.
 * @param <C> The callback type.
 */
public interface Viewer<T, H, V extends IsElement, C extends Viewer.Callback> {

    /**
     * A viewer callback type.
     */
    interface Callback {

        /**
         * Called once the instance has been loaded an displayed.
         */
        default void onSuccess() {
        }

        /**
         * Called in case of any error during loading or displaying operations.
         */
        void onError(final ClientRuntimeError error);
    }

    /**
     * Opens the <code>item </code> instance and notifies the results to the <code>callback</code> instance.
     *
     * @param item     The instance to open.
     * @param callback The operation's callback.
     */
    void open(final T item,
              final C callback);

    /**
     * Scales the current view to the given size.
     *
     * @param width  The resulting width after scale.
     * @param height The resulting height after scale.
     */
    void scale(final int width,
               final int height);

    /**
     * Clears the viewer instance's state and views.
     * The instance can be further used again.
     */
    void clear();

    /**
     * Destroys the viewer instance's state and views.
     * The instance cannot be further used again and must be eligible
     * by the garbage collector.
     */
    void destroy();

    /**
     * Returns the instace being displayed, if any.
     */
    T getInstance();

    /**
     * Returns the current view's handler.
     */
    H getHandler();

    /**
     * Returns the view.
     */
    V getView();
}
