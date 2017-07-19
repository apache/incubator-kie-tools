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
package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProcessFormGenerationTest extends BPMNFormGenerationTest<BusinessProcessFormModel> {

    @Override
    protected String getModelId() {
        return "processId";
    }

    @Override
    protected BusinessProcessFormModel getModel(String modelId,
                                                List<ModelProperty> properties) {
        return new BusinessProcessFormModel(modelId,
                                            modelId,
                                            properties);
    }

    @Override
    protected Collection<FormDefinition> getModelForms(BusinessProcessFormModel model,
                                                       ClassLoader classLoader) {
        return generator.generateProcessForms(model,
                                              classLoader);
    }
}
