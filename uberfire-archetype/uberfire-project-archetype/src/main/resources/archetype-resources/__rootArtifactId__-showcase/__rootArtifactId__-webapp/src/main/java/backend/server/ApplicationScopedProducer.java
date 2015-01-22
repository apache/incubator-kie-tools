package ${package}.backend.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;

@ApplicationScoped
public class ApplicationScopedProducer {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @PreDestroy
    public void onShutdown() {
        ioService.dispose();
    }
}
