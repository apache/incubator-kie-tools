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
package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.activities;

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities.ReusableSubprocessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementCallActivityPropertyWriter;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class CaseManagementReusableSubprocessConverter
        extends ReusableSubprocessConverter {

    public CaseManagementReusableSubprocessConverter(PropertyWriterFactory propertyWriterFactory) {
        super(propertyWriterFactory);
    }

    @Override
    public PropertyWriter toFlowElement(Node<View<BaseReusableSubprocess>, ?> n) {
        CaseManagementCallActivityPropertyWriter p = (CaseManagementCallActivityPropertyWriter) super.toFlowElement(n);

        ReusableSubprocess definition = (ReusableSubprocess) n.getContent().getDefinition();

        ReusableSubprocessTaskExecutionSet executionSet = definition.getExecutionSet();

        p.setCase(executionSet.getIsCase().getValue());

        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());

        return p;
    }
}
