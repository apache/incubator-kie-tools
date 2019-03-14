package org.kie.workbench.common.services.backend.healthcheck;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.guvnor.structure.server.config.ConfigType.GLOBAL;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.HEALTHY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.INCONCLUSIVE;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.UNHEALTHY;

@ApplicationScoped
public class IoServiceCheck implements ServiceCheck {

    private final IOService ioService;

    private final FileSystem fileSystem;

    @Inject
    public IoServiceCheck(final @Named("configIO") IOService ioService,
                          final @Named("systemFS") FileSystem fileSystem) {

        this.ioService = ioService;
        this.fileSystem = fileSystem;
    }

    @Override
    public ServiceStatus getStatus() {
        try {
            return globalDirExists() ? HEALTHY : UNHEALTHY;
        } catch (final Exception e) {
            return INCONCLUSIVE;
        }
    }

    boolean globalDirExists() {
        return ioService.exists(fileSystem.getPath(GLOBAL.getDir()));
    }

    @Override
    public String getName() {
        return "IO Service / File System";
    }
}
