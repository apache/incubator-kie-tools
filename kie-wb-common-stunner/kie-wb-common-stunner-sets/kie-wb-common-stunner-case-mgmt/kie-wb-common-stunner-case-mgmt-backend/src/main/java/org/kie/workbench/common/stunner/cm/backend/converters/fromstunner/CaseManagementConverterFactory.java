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
package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.activities.CaseManagementReusableSubprocessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.processes.CaseManagementSubProcessConverter;

public class CaseManagementConverterFactory extends BaseConverterFactory {

    private CaseManagementReusableSubprocessConverter reusableSubprocessConverter;

    public CaseManagementConverterFactory(DefinitionsBuildingContext context,
                                          PropertyWriterFactory propertyWriterFactory) {
        super(context, propertyWriterFactory);

        this.reusableSubprocessConverter = new CaseManagementReusableSubprocessConverter(propertyWriterFactory);
    }

    @Override
    public CaseManagementReusableSubprocessConverter reusableSubprocessConverter() {
        return reusableSubprocessConverter;
    }

    @Override
    public SubProcessConverter subProcessConverter() {
        return new CaseManagementSubProcessConverter(context, propertyWriterFactory, this);
    }
}
