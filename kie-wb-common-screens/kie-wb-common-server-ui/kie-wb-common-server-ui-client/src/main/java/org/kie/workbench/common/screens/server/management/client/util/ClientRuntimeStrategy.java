/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;

public enum ClientRuntimeStrategy {
    SINGLETON( Constants.ClientRuntimeStrategy_Singleton, RuntimeStrategy.SINGLETON ),
    PER_REQUEST( Constants.ClientRuntimeStrategy_PerRequest, RuntimeStrategy.PER_REQUEST ),
    PER_PROCESS_INSTANCE( Constants.ClientRuntimeStrategy_PerProcessInstance, RuntimeStrategy.PER_PROCESS_INSTANCE ),
    PER_CASE( Constants.ClientRuntimeStrategy_PerCase, RuntimeStrategy.PER_CASE );

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
            case PER_CASE:
                return ClientRuntimeStrategy.PER_CASE;
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
