package org.kie.workbench.common.services.backend.healthcheck;

interface ServiceCheck {

    ServiceStatus getStatus();

    String getName();
}
