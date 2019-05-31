/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.backend.converters.tostunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.activities.CaseManagementCallActivityConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes.CaseManagementRootProcessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes.CaseManagementSubProcessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.tasks.CaseManagementTaskConverter;

public class CaseManagementConverterFactory extends BaseConverterFactory {

    public CaseManagementConverterFactory(DefinitionResolver definitionResolver, TypedFactoryManager factoryManager) {
        super(definitionResolver, factoryManager, new PropertyReaderFactory(definitionResolver));
    }

    @Override
    public CaseManagementCallActivityConverter callActivityConverter() {
        return new CaseManagementCallActivityConverter(factoryManager, propertyReaderFactory);
    }

    @Override
    public CaseManagementRootProcessConverter rootProcessConverter() {
        return new CaseManagementRootProcessConverter(factoryManager, propertyReaderFactory, definitionResolver, this);
    }

    @Override
    public CaseManagementSubProcessConverter subProcessConverter() {
        return new CaseManagementSubProcessConverter(factoryManager,
                                                     propertyReaderFactory,
                                                     definitionResolver,
                                                     this);
    }

    @Override
    public CaseManagementTaskConverter taskConverter() {
        return new CaseManagementTaskConverter(factoryManager, propertyReaderFactory);
    }
}
