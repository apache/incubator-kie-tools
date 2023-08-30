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
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.AbstractFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.service.shared.FieldManager;

@ApplicationScoped
public class ClientFieldElementProcessor extends AbstractFieldElementProcessor {

    @Inject
    public ClientFieldElementProcessor(FieldManager fieldManager,
                                       PropertyValueExtractor propertyValueExtractor) {
        super(fieldManager,
              propertyValueExtractor);
    }

    @PostConstruct
    public void initialize() {
        Collection<SyncBeanDef<FieldInitializer>> initializers = IOC.getBeanManager().lookupBeans(FieldInitializer.class);
        initializers.forEach(initializerDef -> registerInitializer(initializerDef.getInstance()));
    }
}
