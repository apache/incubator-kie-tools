package org.kie.workbench.common.screens.server.management.client.util;

import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;

public enum ClientRuntimeStrategy {
    SINGLETON( "Singleton", RuntimeStrategy.SINGLETON ),
    PER_REQUEST( "Per Request", RuntimeStrategy.PER_REQUEST ),
    PER_PROCESS_INSTANCE( "Per Process Instance", RuntimeStrategy.PER_PROCESS_INSTANCE );

    private final String value;
    private final RuntimeStrategy runtimeStrategy;

    ClientRuntimeStrategy( final String value,
                           final RuntimeStrategy runtimeStrategy ) {
        this.value = value;
        this.runtimeStrategy = runtimeStrategy;
    }

    public RuntimeStrategy getRuntimeStrategy() {
        return runtimeStrategy;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ClientRuntimeStrategy convert( final RuntimeStrategy runtimeStrategy ) {
        switch ( runtimeStrategy ) {
            case SINGLETON:
                return ClientRuntimeStrategy.SINGLETON;
            case PER_REQUEST:
                return ClientRuntimeStrategy.PER_REQUEST;
            case PER_PROCESS_INSTANCE:
                return ClientRuntimeStrategy.PER_PROCESS_INSTANCE;
        }
        throw new RuntimeException( "Invalid parameter" );
    }

    public static ClientRuntimeStrategy convert( final String runtimeStrategy ) {
        for ( ClientRuntimeStrategy clientRuntimeStrategy : ClientRuntimeStrategy.values() ) {
            if ( runtimeStrategy.equals( clientRuntimeStrategy.toString() ) ) {
                return clientRuntimeStrategy;
            }
        }

        return ClientRuntimeStrategy.SINGLETON;
    }

}
