/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.model.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;

@Portable
public class ScannerOperationResult {

    private ScannerStatus scannerStatus;
    private String message;
    private Long pollInterval;

    public ScannerOperationResult() {
    }

    public ScannerOperationResult( final ScannerStatus scannerStatus,
                                   final String message,
                                   final Long pollInterval ) {
        this.scannerStatus = scannerStatus;
        this.message = message;
        this.pollInterval = pollInterval;
    }

    public ScannerStatus getScannerStatus() {
        return scannerStatus;
    }

    public String getMessage() {
        return message;
    }

    public Long getPollInterval() {
        return pollInterval;
    }
}
