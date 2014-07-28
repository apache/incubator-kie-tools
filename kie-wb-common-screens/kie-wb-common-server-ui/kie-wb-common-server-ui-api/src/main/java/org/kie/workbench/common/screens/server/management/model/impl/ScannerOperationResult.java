package org.kie.workbench.common.screens.server.management.model.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;

@Portable
public class ScannerOperationResult {

    private boolean opStatus;
    private ScannerStatus scannerStatus;

    public ScannerOperationResult() {
    }

    public ScannerOperationResult( final boolean opStatus,
                                   final ScannerStatus scannerStatus ) {
        this.opStatus = opStatus;
        this.scannerStatus = scannerStatus;
    }

    public boolean opStatus() {
        return opStatus;
    }

    public ScannerStatus getScannerStatus() {
        return scannerStatus;
    }
}
