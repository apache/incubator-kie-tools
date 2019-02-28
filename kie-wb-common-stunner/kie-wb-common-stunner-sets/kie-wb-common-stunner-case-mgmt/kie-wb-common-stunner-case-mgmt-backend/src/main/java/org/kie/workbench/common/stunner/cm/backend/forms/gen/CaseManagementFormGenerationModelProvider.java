/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend.forms.gen;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.forms.gen.util.CaseManagementFormGenerationModelProviderHelper;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProvider;

@ApplicationScoped
@CaseManagementEditor
public class CaseManagementFormGenerationModelProvider implements FormGenerationModelProvider<Definitions> {

    private final DefinitionUtils definitionUtils;
    private String definitionSetId;
    private CaseManagementFormGenerationModelProviderHelper formGenerationModelProviderHelper;

    // CDI proxy.
    protected CaseManagementFormGenerationModelProvider() {
        this(null, null);
    }

    @Inject
    public CaseManagementFormGenerationModelProvider(final DefinitionUtils definitionUtils,
                                                     final CaseManagementFormGenerationModelProviderHelper formGenerationModelProviderHelper) {
        this.definitionUtils = definitionUtils;
        this.formGenerationModelProviderHelper = formGenerationModelProviderHelper;
    }

    @PostConstruct
    public void init() {
        this.definitionSetId = definitionUtils.getDefinitionSetId(CaseManagementDefinitionSet.class);
    }

    @Override
    public boolean accepts(final Diagram diagram) {
        return this.definitionSetId.equals(diagram.getMetadata().getDefinitionSetId());
    }

    @Override
    public Definitions generate(Diagram diagram) throws IOException {
        return formGenerationModelProviderHelper.generate(diagram);
    }
}
