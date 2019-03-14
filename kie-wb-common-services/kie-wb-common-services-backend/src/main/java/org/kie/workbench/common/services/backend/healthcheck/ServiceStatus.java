package org.kie.workbench.common.services.backend.healthcheck;

enum ServiceStatus {

    NOT_READY(false, false),
    READY(true, false),
    HEALTHY(true, true),
    INCONCLUSIVE(false, false),
    UNHEALTHY(true, false);

    private final boolean ready;
    private final boolean healthy;

    ServiceStatus(final boolean ready,
                  final boolean healthy) {

        this.ready = ready;
        this.healthy = healthy;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isHealthy() {
        return healthy;
    }
}
