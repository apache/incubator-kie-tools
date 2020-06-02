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

package org.dashbuilder.shared.services;

import java.util.Optional;

import org.dashbuilder.shared.model.RuntimeModel;

/**
 * Responsible for downloading external models and registering it
 *
 */
public interface ExternalImportService {
    
    /**
     * Register an external model coming from an external server
     * @param externalModelUrl
     * the external model URL
     * @return
     * Optional containing the downloaded RuntimeModel or empty
     */
    Optional<RuntimeModel> registerExternalImport(String externalModelUrl);

}