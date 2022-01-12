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

package org.kie.workbench.common.stunner.bpmn.forms.service.adf.processing.processors.fields;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScriptTypeFieldInitializerTest
        extends ScriptTypeModeBasedFieldInitializerTestBase {

    @Override
    public FieldDefinition mockFieldDefinition() {
        return mock(ScriptTypeFieldDefinition.class);
    }

    @Override
    public FieldInitializer newFieldInitializer() {
        return new ScriptTypeFieldInitializer();
    }

    @Override
    protected void checkModeWasSet(ScriptTypeMode mode) {
        verify(((ScriptTypeFieldDefinition) fieldDefinition),
               times(1)).setMode(mode);
    }

    @Override
    protected ScriptTypeMode getDefaultMode() {
        return ScriptTypeMode.ACTION_SCRIPT;
    }
}