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

package org.kie.workbench.common.stunner.cm.client.forms.filters;

import org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider;
import org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProviderTest;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;

public class ProcessReusableSubProcessFilterProviderTest extends MultipleInstanceNodeFilterProviderTest {

    @Override
    protected MultipleInstanceNodeFilterProvider newFilterProvider() {
        return new ProcessReusableSubProcessFilterProvider(sessionManager, refreshFormPropertiesEvent);
    }

    @Override
    protected Object newNonMultipleInstanceDefinition() {
        ProcessReusableSubprocess subprocess = new ProcessReusableSubprocess();
        subprocess.getExecutionSet().getIsMultipleInstance().setValue(false);
        return subprocess;
    }

    @Override
    protected Object newMultipleInstanceDefinition() {
        ProcessReusableSubprocess subprocess = new ProcessReusableSubprocess();
        subprocess.getExecutionSet().getIsMultipleInstance().setValue(true);
        return subprocess;
    }

    @Override
    protected Class<?> getExpectedDefinitionType() {
        return ProcessReusableSubprocess.class;
    }
}