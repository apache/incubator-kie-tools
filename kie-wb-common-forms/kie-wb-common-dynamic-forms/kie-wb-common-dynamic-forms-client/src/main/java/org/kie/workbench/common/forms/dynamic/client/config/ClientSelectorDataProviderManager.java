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

package org.kie.workbench.common.forms.dynamic.client.config;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.AbstractSelectorDataProviderManager;

@ApplicationScoped
public class ClientSelectorDataProviderManager extends AbstractSelectorDataProviderManager {
    public static final String PREFFIX = "local";

    @PostConstruct
    public void init() {
        Collection<SyncBeanDef<SelectorDataProvider>> providers = IOC.getBeanManager().lookupBeans( SelectorDataProvider.class );

        for ( SyncBeanDef<SelectorDataProvider> provider : providers ) {
            registerProvider( provider.newInstance() );
        }
    }

    @Override
    public String getPreffix() {
        return PREFFIX;
    }
}
