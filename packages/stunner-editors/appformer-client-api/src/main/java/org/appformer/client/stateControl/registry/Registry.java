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


package org.appformer.client.stateControl.registry;

import java.util.List;

/**
 * Represents a basic items registry.
 *
 * @param <C> anything that can be registered.
 */
public interface Registry<C> {

    /**
     * Registers an item into the registry
     *
     * @param item An item to register
     */
    void register(final C item);

    /**
     * Peeks the last added item. Doesn't remove it.
     *
     * @return The last added item
     */
    C peek();

    /**
     * Pops the last added item and removes it.
     *
     * @return The last added item
     */
    C pop();

    /**
     * Sets the max number of items that can be stored on the registry.
     *
     * @param size A positive integer
     */
    void setMaxSize(final int size);

    /**
     * Clears the registry
     */
    void clear();

    /**
     * Determines if the registry is empty or not
     *
     * @return true if empty, false if not.
     */
    boolean isEmpty();

    /**
     * Returns a {@link List} containing all the items in the registry
     *
     * @return A {@link List} of containing the registered items
     */
    List<C> getHistory();

    /**
     * Sets a {@link RegistryChangeListener} to be called when the registry changes.
     *
     * @param registryChangeListener A {@link RegistryChangeListener}
     */
    void setRegistryChangeListener(RegistryChangeListener registryChangeListener);
}
