package org.uberfire.commons.cluster;

import org.uberfire.commons.message.MessageHandlerResolver;

public interface ClusterServiceFactory {

    ClusterService build( final MessageHandlerResolver resolver );
}
