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

package org.kie.workbench.common.forms.jbpm.model.authoring;

import java.util.List;

import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.AbstractFormModel;

public abstract class AbstractJBPMFormModel extends AbstractFormModel implements JBPMFormModel {

    protected String processId;

    public AbstractJBPMFormModel(String processId,
                                 List<ModelProperty> properties) {
        this.processId = processId;
        this.properties = properties;
    }

    protected AbstractJBPMFormModel() {
        // Only for serialization purposes
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
