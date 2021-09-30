/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.external.service;

import java.util.List;

import org.dashbuilder.external.model.ExternalComponent;

public interface ComponentLoader {
    
    public static final String DESCRIPTOR_FILE = "manifest.json";

    /**
     * Load the list of components externally created by users .
     * 
     * @return
     * The list of external components.
     */
    List<ExternalComponent> loadExternal();

    /**
     * Load external components that are provided (built-in) by Dashbuilder.
     * @return
     * The list of provided external components
     */
    List<ExternalComponent> loadProvided();

    /**
     * The filesystem directory for external components.
     * 
     * @return
     */
    String getExternalComponentsDir();

    /**
     * The internal path for provided components.
     * 
     * @return
     */
    String getProvidedComponentsPath();

    boolean isExternalComponentsEnabled();

}