/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.kie.workbench.common.forms.adf.rendering.FieldRendererTypesProvider;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererTypeRegistry;

@EntryPoint
@Bundle("resources/i18n/FormRenderingConstants.properties")
public class DynamicRendererEntryPoint {

    private SyncBeanManager beanManager;

    @Inject
    public DynamicRendererEntryPoint(SyncBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @PostConstruct
    public void init() {
        populateFieldRenderersRegistry();
    }

    private void populateFieldRenderersRegistry() {

        Collection<SyncBeanDef<FieldRendererTypesProvider>> providers = beanManager.lookupBeans(FieldRendererTypesProvider.class);

        providers.forEach(providerDef -> {
            FieldRendererTypesProvider provider = providerDef.newInstance();

            FieldRendererTypeRegistry.load(provider);

            beanManager.destroyBean(provider);
        });

    }
}
