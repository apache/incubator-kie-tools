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


package org.kie.workbench.common.forms.adf.engine.client.formGeneration;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;

@ApplicationScoped
public class ClientFormGenerator extends AbstractFormGenerator {

    protected TranslationService translationService;

    @Inject
    public ClientFormGenerator(LayoutGenerator layoutGenerator,
                               TranslationService translationService) {
        super(layoutGenerator);
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {
        SyncBeanManager beanManager = IOC.getBeanManager();

        Collection<SyncBeanDef<FormElementProcessor>> processors = beanManager.lookupBeans(FormElementProcessor.class);

        processors.stream()
                .map(SyncBeanDef::getInstance)
                .forEach(processor -> {
                    registerProcessor(processor);
                    beanManager.destroyBean(processor);
                });

        Collection<SyncBeanDef<FormGenerationResourcesProvider>> builderDefs = beanManager.lookupBeans(FormGenerationResourcesProvider.class);

        builderDefs.stream()
                .map(SyncBeanDef::getInstance)
                .forEach(provider -> {
                    registerResources(provider);
                    beanManager.destroyBean(provider);
                });
    }

    @Override
    protected I18nHelper getI18nHelper(I18nSettings settings) {
        return new ClientI18nHelper(settings,
                                    translationService);
    }
}
