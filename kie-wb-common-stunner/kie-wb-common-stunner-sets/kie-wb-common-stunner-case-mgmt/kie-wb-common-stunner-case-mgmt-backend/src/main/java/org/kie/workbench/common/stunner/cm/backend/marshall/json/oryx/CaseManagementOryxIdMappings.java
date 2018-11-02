/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend.marshall.json.oryx;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.BaseOryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;

/**
 * This class contains the mappings for the different stencil identifiers that are different from
 * the patterns used in this tool.
 */
@Dependent
@CaseManagementEditor
public class CaseManagementOryxIdMappings extends BaseOryxIdMappings {

    @Inject
    public CaseManagementOryxIdMappings(final DefinitionManager definitionManager) {
        super(definitionManager);
    }

    @Override
    protected Class<? extends BPMNDiagram> getDiagramType() {
        return CaseManagementDiagram.class;
    }

    @Override
    public Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings() {
        final Map<Class<?>, Map<Class<?>, String>> definitionMappings = super.getDefinitionMappings();
        final Map<Class<?>, String> reusableSubprocessPropertiesMap = definitionMappings.get(org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess.class);
        definitionMappings.put(ReusableSubprocess.class,
                               reusableSubprocessPropertiesMap);
        final Map<Class<?>, String> adHocSubprocessPropertiesMap = definitionMappings.get(org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess.class);
        definitionMappings.put(AdHocSubprocess.class,
                               adHocSubprocessPropertiesMap);
        final Map<Class<?>, String> embeddedSubprocessPropertiesMap = definitionMappings.get(org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess.class);
        definitionMappings.put(EmbeddedSubprocess.class,
                               embeddedSubprocessPropertiesMap);

        return definitionMappings;
    }

    @Override
    public Map<Class<?>, String> getGlobalMappings() {
        Map<Class<?>, String> globalMappings = super.getGlobalMappings();

        globalMappings.put(EmbeddedSubprocess.class, "Subprocess");
        globalMappings.put(AdHocSubprocess.class, "AdHocSubprocess");
        globalMappings.put(ReusableSubprocess.class, "ReusableSubprocess");

        return globalMappings;
    }
}
