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

package org.kie.workbench.common.forms.jbpm.model.authoring.process;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.ModelProperty;

@Portable
public class BusinessProcessFormModel extends AbstractJBPMFormModel {

    protected String processName;

    public BusinessProcessFormModel(@MapsTo("processId") String processId,
                                    @MapsTo("processName") String processName,
                                    @MapsTo("properties") List<ModelProperty> properties) {
        super(processId,
              properties);
        this.processName = processName;
    }

    private BusinessProcessFormModel() {
        // Only for serialization purposes
    }

    @Override
    public String getName() {
        return "process";
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public String getFormName() {
        return getProcessId() + BPMNVariableUtils.TASK_FORM_SUFFIX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BusinessProcessFormModel formModel = (BusinessProcessFormModel) o;

        if (!processId.equals(formModel.processId)) {
            return false;
        }
        return processName.equals(formModel.processName);
    }

    @Override
    public int hashCode() {
        int result = processId.hashCode();
        result = ~~result;
        result = 31 * result + processName.hashCode();
        result = ~~result;
        return result;
    }
}
