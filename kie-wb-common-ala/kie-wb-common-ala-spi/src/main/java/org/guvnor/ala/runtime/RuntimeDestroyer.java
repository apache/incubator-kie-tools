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

/*
 * Runtime Destroyer provides a way to destroy Runtime for different providers. 
 * See also RuntimeBuilder
 * @see RuntimeBuilder
 */
public interface RuntimeDestroyer {

    /*
     * Check if your runtime is supported by this destroyer
     * @param RuntimeId
     * @return true if the destroyer supports the Runtime, false otherwise
     * @see RuntimeId
     */
    boolean supports(final RuntimeId runtimeId);

    /*
     * Destroy the specified Runtime
     * @param RuntimeId to be destroyed
     * @see RuntimeId
     */
    void destroy(final RuntimeId runtimeId);
}
