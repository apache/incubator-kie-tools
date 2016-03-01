package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;

public enum ClientRuntimeStrategy {
    SINGLETON( Constants.ClientRuntimeStrategy_Singleton, RuntimeStrategy.SINGLETON ),
    PER_REQUEST( Constants.ClientRuntimeStrategy_PerRequest, RuntimeStrategy.PER_REQUEST ),
    PER_PROCESS_INSTANCE( Constants.ClientRuntimeStrategy_PerProcessInstance, RuntimeStrategy.PER_PROCESS_INSTANCE );

    private final String valueTranslationKey;
    private final RuntimeStrategy runtimeStrategy;

    ClientRuntimeStrategy( final String valueTranslationKey,
                           final RuntimeStrategy runtimeStrategy ) {
        this.valueTranslationKey = valueTranslationKey;
        this.runtimeStrategy = runtimeStrategy;
    }

    public RuntimeStrategy getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public String getValue( final TranslationService translationService ) {
        return translationService.format( valueTranslationKey );
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

    public static ClientRuntimeStrategy convert( final String runtimeStrategy, final TranslationService translationService ) {
        for ( ClientRuntimeStrategy clientRuntimeStrategy : ClientRuntimeStrategy.values() ) {
            if ( runtimeStrategy.equals( clientRuntimeStrategy.getValue( translationService ) ) ) {
                return clientRuntimeStrategy;
            }
        }

        return ClientRuntimeStrategy.SINGLETON;
    }

    public static List<String> listRuntimeStrategiesValues( final TranslationService translationService ) {
        List<String> runtimeStrategyValues = new ArrayList<String>();

        for ( ClientRuntimeStrategy clientRuntimeStrategy : ClientRuntimeStrategy.values() ) {
            runtimeStrategyValues.add( clientRuntimeStrategy.getValue( translationService ) );
        }

        return runtimeStrategyValues;
    }

}
