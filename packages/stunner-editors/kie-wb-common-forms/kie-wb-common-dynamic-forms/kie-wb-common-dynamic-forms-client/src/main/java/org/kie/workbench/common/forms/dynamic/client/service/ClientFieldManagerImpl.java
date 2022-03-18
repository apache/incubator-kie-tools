/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.dynamic.client.service;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldManager;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;

@ApplicationScoped
public class ClientFieldManagerImpl extends AbstractFieldManager {

    @Inject
    public ClientFieldManagerImpl(MetaDataEntryManager metaDataEntryManager) {
        super(metaDataEntryManager);
    }

    @PostConstruct
    protected void init() {
        Collection<SyncBeanDef<FieldProvider>> providers = IOC.getBeanManager().lookupBeans(FieldProvider.class);

        for (SyncBeanDef<FieldProvider> provider : providers) {
            registerFieldProvider(provider.newInstance());
        }
    }
}
