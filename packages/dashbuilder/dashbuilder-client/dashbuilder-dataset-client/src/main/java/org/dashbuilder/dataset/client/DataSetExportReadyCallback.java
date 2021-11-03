/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.client;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.uberfire.backend.vfs.Path;

/**
 * Interface for capturing the results of a data set export request
 */
public interface DataSetExportReadyCallback {

    /**
     * The path returned after a successful export
     * @param exportFilePath The path of the exported file in the server
     */
    void exportReady(Path exportFilePath);

    /**
     * An error occurred during the export process
     * @param error The error details
     */
    void onError(ClientRuntimeError error);
}
