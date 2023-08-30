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


package org.appformer.kogito.bridge.client.stateControl.registry.interop;

import jsinterop.annotations.JsType;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControlCommand;

/**
 * JsType that represents the TypeScript CommandRegistry available on the Envelope StateControl engine
 *
 * @param <C> anything that can be considered a Command
 */
@JsType(isNative = true)
public interface KogitoJSCommandRegistry<C> {

    /**
     * Registers a command with a give ID into the registry
     *
     * @param id      The command id
     * @param command A command to register
     */
    void register(String id, C command);

    /**
     * Peeks the last added command. Doesn't remove it.
     *
     * @return The last added Command
     */
    C peek();

    /**
     * Pops the last added command and removes it.
     *
     * @return The last added Command
     */
    C pop();

    /**
     * Sets the max number of commands that can be stored on the registry.
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
     * Returns an array containing all the commands in the registry
     *
     * @return A commands array
     */
    C[] getCommands();

    /**
     * Sets a {@link StateControlCommand} to be called when the registry changes.
     *
     * @param changeListener A {@link StateControlCommand}
     */
    void setRegistryChangeListener(StateControlCommand changeListener);
}
