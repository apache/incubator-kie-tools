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

package org.dashbuilder.shared.service;

import java.util.Collection;
import java.util.Optional;

import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;

import static org.dashbuilder.shared.model.DashbuilderRuntimeMode.MULTIPLE_IMPORT;

/**
 * Provides access to a saved ImportModelService.
 *
 */
public interface RuntimeModelRegistry {

    public default boolean acceptingNewImports() {
        return getMode() == MULTIPLE_IMPORT ||
               (getMode() == DashbuilderRuntimeMode.SINGLE_IMPORT && isEmpty());
    }

    /**
     * Returns the first model available. 
     * 
     * @return
     */
    Optional<RuntimeModel> single();

    /**
     * Returns if this registry has at least one model.
     * @return
     */
    boolean isEmpty();

    /**
     * Returns the registry mode
     * 
     * @return
     */
    DashbuilderRuntimeMode getMode();

    /**
     * Get a previously registered import model.
     * @param id
     * @return
     */
    Optional<RuntimeModel> get(String id);

    /**
     * Sets this runtime mode.
     * 
     * @param mode
     * The mode to be used.
     */
    void setMode(DashbuilderRuntimeMode mode);

    /**
     * Store the import from a File path;
     * @param filePath
     * The path to the file.
     */
    Optional<RuntimeModel> registerFile(String filePath);
    
    /**
     * Removes a runtime model.
     * @param runtimeModelid
     * The id of the runtime model to be removed.
     */
    void remove(String runtimeModelid);

    /**
     * List all models that are currently available.
     */
    Collection<String> availableModels();

}
