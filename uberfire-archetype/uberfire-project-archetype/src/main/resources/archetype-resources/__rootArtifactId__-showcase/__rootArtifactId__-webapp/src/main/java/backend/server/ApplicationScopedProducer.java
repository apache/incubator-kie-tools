package ${package}.backend.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;

@ApplicationScoped
public class ApplicationScopedProducer {

    private IOService ioService;

    @PostConstruct
    public void setup() {
        ioService = new IOServiceNio2WrapperImpl();
    }

    @PreDestroy
    public void onShutdown() {
        ioService.dispose();
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }
}
