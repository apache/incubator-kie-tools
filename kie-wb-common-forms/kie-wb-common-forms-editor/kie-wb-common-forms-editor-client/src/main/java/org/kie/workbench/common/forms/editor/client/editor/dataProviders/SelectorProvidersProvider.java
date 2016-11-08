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

package org.kie.workbench.common.forms.editor.client.editor.dataProviders;

import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.BackendSelectorDataProviderService;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;

@Dependent
public class SelectorProvidersProvider implements SystemSelectorDataProvider {

    protected SelectorData data = new SelectorData();

    @Inject
    private SelectorDataProviderManager clientProvider;

    @Inject
    private Caller<BackendSelectorDataProviderService> selectorProviderService;

    @PostConstruct
    protected void doInit() {
        final Map<String, String> providers = new TreeMap<String, String>();

        providers.putAll( clientProvider.availableProviders() );

        selectorProviderService.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> backendProviders ) {
                if ( backendProviders != null ) {
                    providers.putAll( backendProviders );
                }
            }
        } ).availableProviders();

        data.setValues( providers );
    }

    @Override
    public String getProviderName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData( FormRenderingContext context ) {
        return data;
    }
}
