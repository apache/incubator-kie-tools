package org.kie.workbench.common.screens.server.management.model;

import org.guvnor.common.services.project.model.GAV;

public interface ContainerRef {

    String getServerId();

    String getId();

    GAV getReleasedId();

    ContainerStatus getStatus();

    ScannerStatus getScannerStatus();

    void setStatus( ContainerStatus serverStatus );
}
