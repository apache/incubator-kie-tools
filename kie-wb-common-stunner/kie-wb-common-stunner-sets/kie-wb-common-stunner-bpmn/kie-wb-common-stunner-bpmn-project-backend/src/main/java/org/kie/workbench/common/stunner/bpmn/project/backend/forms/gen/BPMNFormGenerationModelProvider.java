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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.gen;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.project.backend.forms.gen.util.BPMNFormGenerationModelProviderHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProvider;

@ApplicationScoped
public class BPMNFormGenerationModelProvider
        implements FormGenerationModelProvider<Definitions> {

    private final DefinitionUtils definitionUtils;
    private String definitionSetId;
    private BPMNFormGenerationModelProviderHelper formGenerationModelProviderHelper;

    // CDI proxy.
    protected BPMNFormGenerationModelProvider() {
        this(null,
             null);
    }

    @Inject
    public BPMNFormGenerationModelProvider(final DefinitionUtils definitionUtils,
                                           final BPMNFormGenerationModelProviderHelper formGenerationModelProviderHelper) {
        this.definitionUtils = definitionUtils;
        this.formGenerationModelProviderHelper = formGenerationModelProviderHelper;
    }

    @PostConstruct
    public void init() {
        this.definitionSetId = definitionUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }

    @Override
    public boolean accepts(final Diagram diagram) {
        return this.definitionSetId.equals(diagram.getMetadata().getDefinitionSetId());
    }

    @Override
    public Definitions generate(final Diagram diagram) throws IOException {
        return formGenerationModelProviderHelper.generate(diagram);
    }
}
