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

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.AbstractFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class BackendFieldElementProcessor extends AbstractFieldElementProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BackendFieldElementProcessor.class);

    @Inject
    public BackendFieldElementProcessor(FieldManager fieldManager,
                                        PropertyValueExtractor propertyValueExtractor,
                                        Instance<FieldInitializer<? extends FieldDefinition>> initializers) {
        super(fieldManager,
              propertyValueExtractor);

        initializers.forEach(fieldInitializer -> registerInitializer(fieldInitializer));
    }
}
