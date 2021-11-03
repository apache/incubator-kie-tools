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

package org.dashbuilder.backend.services;

import java.util.Optional;

import org.dashbuilder.shared.model.DashboardInfo;
import org.dashbuilder.shared.model.DashbuilderRuntimeInfo;

/**
 * Provides information about the running server.
 *
 */
public interface RuntimeInfoService {

    /**
     * Access Server information.
     */
    DashbuilderRuntimeInfo info();

    /**
     * Information about a specific runtime model. Used in MULTI mode.
     * 
     * @param modelId
     * The model ID.
     * @return
     * The dashboard information for the given runtime model id or empty if no dashboard is found.
     */
    Optional<DashboardInfo> dashboardInfo(String modelId);

}