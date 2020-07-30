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

import java.util.Optional;

import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.dashbuilder.shared.model.RuntimeModel;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service to access RuntimeModel.
 *
 */
@Remote
public interface RuntimeModelService {

    /**
     * Loads information about this installation.
     * 
     * @param runtimeModelId
     * A runtime model id to be loaded. Can be null or an empty String.
     * @return
     * 
     */
    public RuntimeServiceResponse info(String runtimeModelId);

    /**
     * 
     * Get a runtime model given an ID.
     * 
     * @param runtimeModelId
     * The Runtime model represented by runtimeModelId.
     * @return
     * An optional containing the RuntimeModel or empty if the runtime model couldn't be found or built.
     */
    Optional<RuntimeModel> getRuntimeModel(String runtimeModelId);

}