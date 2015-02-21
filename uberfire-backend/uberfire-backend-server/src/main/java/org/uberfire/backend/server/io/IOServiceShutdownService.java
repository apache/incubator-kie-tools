package org.uberfire.backend.server.io;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

@ApplicationScoped
@Startup(StartupType.EAGER)
public class IOServiceShutdownService {

    @Inject
    private Instance<IOService> ioServices;

    @PreDestroy
    public void onShutdown() {
        for ( IOService ioService : ioServices ) {
            ioService.dispose();
        }
        SimpleAsyncExecutorService.shutdownInstances();
    }

}
