package org.kie.workbench.common.services.backend.healthcheck;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.HEALTHY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.INCONCLUSIVE;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.UNHEALTHY;

@ApplicationScoped
public class IoServiceCheck implements ServiceCheck {

    private final IOService ioService;

    @Inject
    public IoServiceCheck(final @Named("configIO") IOService ioService) {

        this.ioService = ioService;
    }

    @Override
    public ServiceStatus getStatus() {
        try {
            return systemFsExists() ? HEALTHY : UNHEALTHY;
        } catch (final Exception e) {
            return INCONCLUSIVE;
        }
    }

    boolean systemFsExists() {
        try {
            final FileSystem fileSystem = getFileSystem();
            if (fileSystem == null) {
                return false;
            }

            return fileSystem.isOpen();
        } catch (final Exception e) {
            return false;
        }
    }

    FileSystem getFileSystem() {
        return ioService.getFileSystem(URI.create("default://system/system"));
    }

    @Override
    public String getName() {
        return "IO Service / File System";
    }
}
