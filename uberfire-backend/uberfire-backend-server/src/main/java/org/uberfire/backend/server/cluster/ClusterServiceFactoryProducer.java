package org.uberfire.backend.server.cluster;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ClusterServiceFactoryProducer {

    private final ClusterServiceFactory factory;
    private AtomicBoolean initialized = new AtomicBoolean( false );

    ClusterServiceFactoryProducer() {
        this.factory = buildFactory();
    }

    ClusterServiceFactory buildFactory() {
        return ClusterServiceFactorySetup.buildFactory();
    }

    @Produces
    @Named("clusterServiceFactory")
    public synchronized ClusterServiceFactory clusterServiceFactory() {
        if ( factory != null && !initialized.getAndSet( true ) ) {
            factory.build( null );
        }
        return factory;
    }
}