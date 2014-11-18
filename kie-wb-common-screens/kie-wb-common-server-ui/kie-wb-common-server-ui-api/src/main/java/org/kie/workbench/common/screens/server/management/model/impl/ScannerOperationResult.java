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
