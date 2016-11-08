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

package org.kie.workbench.common.forms.dynamic.service.shared;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;


public abstract class AbstractSelectorDataProviderManager implements SelectorDataProviderManager {

    protected Map<String, SelectorDataProvider> providers = new HashMap<>();

    protected void registerProvider( SelectorDataProvider provider ) {
        providers.put( getPreffix() + SEPARATOR + provider.getClass().getName(), provider );
    }

    @Override
    public Map<String, String> availableProviders() {
        Map<String, String> result = new HashMap<>();

        for ( String key : providers.keySet() ) {
            SelectorDataProvider provider = providers.get( key );
            if ( !(provider instanceof SystemSelectorDataProvider ) ) {
                result.put( key, provider.getProviderName() );
            }
        }
        return result;
    }

    @Override
    public SelectorData getDataFromProvider( FormRenderingContext context, String provider ) {

        SelectorDataProvider dataProvider = providers.get( provider );

        if ( dataProvider == null ) {
            return null;
        }

        return dataProvider.getSelectorData( context );
    }


    public abstract String getPreffix();
}
