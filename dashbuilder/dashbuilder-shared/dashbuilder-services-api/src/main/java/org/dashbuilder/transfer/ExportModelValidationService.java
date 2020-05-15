/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.transfer;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Perform validations on an ExportModel
 *
 */
@Remote
public interface ExportModelValidationService {

    /**
     * Check if there is datasets not exported for the given export model
     * 
     * @param exportModel
     * The export model to be validated
     * @return
     * A map of pages with missing dependencies and the list of dependencies for each page.
     */
    Map<String, List<String>> checkMissingDatasets(DataTransferExportModel exportModel);

}