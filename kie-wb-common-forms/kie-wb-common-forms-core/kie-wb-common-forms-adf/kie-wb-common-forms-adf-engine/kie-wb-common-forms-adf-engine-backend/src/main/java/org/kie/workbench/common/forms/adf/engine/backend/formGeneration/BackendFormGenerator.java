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

package org.kie.workbench.common.forms.adf.engine.backend.formGeneration;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;

@Dependent
public class BackendFormGenerator extends AbstractFormGenerator {

    @Inject
    public BackendFormGenerator(LayoutGenerator layoutGenerator,
                                Instance<FormElementProcessor<? extends FormElement>> elementProcessors,
                                Instance<FormGenerationResourcesProvider> settingsBuilderProviders) {
        super(layoutGenerator);

        elementProcessors.forEach(formElementProcessor -> registerProcessor(formElementProcessor));
        settingsBuilderProviders.forEach(formDefinitionSettingsBuilderProvider -> {
            registerResources(formDefinitionSettingsBuilderProvider);
            settingsBuilderProviders.destroy(formDefinitionSettingsBuilderProvider);
        });
    }

    @Override
    protected I18nHelper getI18nHelper(I18nSettings settings) {
        return new BackendI18nHelper(settings);
    }
}
