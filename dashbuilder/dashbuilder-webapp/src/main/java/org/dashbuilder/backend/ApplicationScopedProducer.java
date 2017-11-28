package org.dashbuilder.backend;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceAllImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@Startup(value = StartupType.BOOTSTRAP)
@ApplicationScoped
public class ApplicationScopedProducer {

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private IOWatchServiceAllImpl watchService;

    private IOService ioService;

    @PostConstruct
    public void setup() {
        ioService  = new IOServiceNio2WrapperImpl("1", watchService);
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }
}