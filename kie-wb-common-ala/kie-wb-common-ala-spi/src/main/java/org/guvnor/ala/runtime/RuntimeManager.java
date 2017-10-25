/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.runtime;

import org.guvnor.ala.exceptions.RuntimeOperationException;

/*
 * This interface provides a way to interact with the existing runtimes. 
 * It exposes the common lifecycle operations. An implementation of this interface
 * must be provided for each different Runtime Provider.
*/
public interface RuntimeManager {

    /*
    * Checks if the RuntimeId provided as argument is supported by the instance of the RuntimeManager
    * @param RuntimeId 
    * @return boolean true if the RuntimeId supplied represent a 
    *  Runtime that is supported by this RuntimeManager
    */
    boolean supports(final RuntimeId runtimeId);

    /*
    * Starts the Runtime
    * @param RuntimeId 
    */
    void start(final RuntimeId runtimeId) throws RuntimeOperationException;

    /*
    * Stops the Runtime
    * @param RuntimeId 
    */
    void stop(final RuntimeId runtimeId) throws RuntimeOperationException;

    /*
    * Restarts the Runtime
    * @param RuntimeId 
    */
    void restart(final RuntimeId runtimeId) throws RuntimeOperationException;

    /*
    * Refresh the Runtime
    * @param RuntimeId 
    */
    void refresh(final RuntimeId runtimeId) throws RuntimeOperationException;

    /*
    * Pause the Runtime
    * @param RuntimeId 
    */
    void pause(final RuntimeId runtimeId) throws RuntimeOperationException;
}
