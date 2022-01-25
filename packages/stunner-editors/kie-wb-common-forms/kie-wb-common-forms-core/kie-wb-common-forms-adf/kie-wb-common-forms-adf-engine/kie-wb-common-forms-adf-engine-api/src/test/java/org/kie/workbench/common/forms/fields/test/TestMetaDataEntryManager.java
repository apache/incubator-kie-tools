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

package org.kie.workbench.common.forms.fields.test;

import org.kie.workbench.common.forms.service.shared.meta.processing.impl.AbstractMetaDataEntryManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldLabelEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldPlaceHolderEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldReadOnlyEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldRequiredEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldTypeEntryProcessor;

public class TestMetaDataEntryManager extends AbstractMetaDataEntryManager {

    public TestMetaDataEntryManager() {
        registerProcessor(new FieldLabelEntryProcessor());
        registerProcessor(new FieldPlaceHolderEntryProcessor());
        registerProcessor(new FieldPlaceHolderEntryProcessor());
        registerProcessor(new FieldReadOnlyEntryProcessor());
        registerProcessor(new FieldRequiredEntryProcessor());
        registerProcessor(new FieldTypeEntryProcessor());
    }
}
