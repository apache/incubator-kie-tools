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

import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * Represents an unique Id for the Runtime,
 * containing also a reference to the ProviderId where this Runtime belongs
 * @see Runtime
 */
public interface RuntimeId {

    /*
     * Gets the runtime Identifier 
     * @return String which contains the unique Runtime Id
     */
    String getId();

    /**
     * Gets the human readable name for the runtime. The runtime name is unique within the provider.
     * When no name was provided at runtime creation @see {@link RuntimeConfig}, the runtime name is getId().
     */
    String getName();

    /*
     * Gets the ProviderId which was used to create this runtime
     * @return ProviderId 
     * @see ProviderId
     */
    ProviderId getProviderId();
}
