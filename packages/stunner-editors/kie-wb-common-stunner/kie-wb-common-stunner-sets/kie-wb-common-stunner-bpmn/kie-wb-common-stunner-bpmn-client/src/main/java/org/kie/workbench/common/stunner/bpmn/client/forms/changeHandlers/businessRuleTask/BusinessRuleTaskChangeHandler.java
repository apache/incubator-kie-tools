/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.changeHandlers.businessRuleTask;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.appformer.kogito.bridge.client.dmneditor.marshaller.model.DmnDecision;
import org.appformer.kogito.bridge.client.dmneditor.marshaller.model.DmnDocumentData;
import org.kie.workbench.common.stunner.bpmn.client.util.DmnResourceContentFetcher;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandler;

public class BusinessRuleTaskChangeHandler implements DomainObjectFieldChangeHandler<BusinessRuleTask> {

    protected final static String FILE_NAME_FIELD = BusinessRuleTask.EXECUTION_SET + "." + BusinessRuleTaskExecutionSet.FILE_NAME;

    private BusinessRuleTask businessRuleTask;

    private final DmnResourceContentFetcher dmnResourceContentFetcher;

    @Inject
    public BusinessRuleTaskChangeHandler(final DmnResourceContentFetcher dmnResourceContentFetcher) {
        this.dmnResourceContentFetcher = dmnResourceContentFetcher;
    }

    @Override
    public void init(BusinessRuleTask businessRuleTask) {
        this.businessRuleTask = businessRuleTask;
    }

    @Override
    public void onFieldChange(String fieldName, Object newValue) {
        if (fieldName.equals(FILE_NAME_FIELD)) {
            String fileName = (String) newValue;

            if (dmnResourceContentFetcher.getFileNames().get(newValue) != null) {
                dmnResourceContentFetcher.fetchFile(fileName, dmnDocumentData -> setData(dmnDocumentData));
            } else {
                // Reset Fields for a new typed filename
                reset();
            }
        }
    }

    public void reset() {
        businessRuleTask.getExecutionSet().getNamespace().setValue("");
        businessRuleTask.getExecutionSet().getDmnModelName().setValue("");
        businessRuleTask.getExecutionSet().getDecisionName().setValue("");
        dmnResourceContentFetcher.getDecisions().clear();
        dmnResourceContentFetcher.refreshForms();
    }

    protected void setData(DmnDocumentData dmnDocumentData) {
        businessRuleTask.getExecutionSet().getNamespace().setValue(dmnDocumentData.getNamespace());
        businessRuleTask.getExecutionSet().getDmnModelName().setValue(dmnDocumentData.getName());
        businessRuleTask.getExecutionSet().getDecisionName().setValue(""); // Clear the value
        dmnResourceContentFetcher.setDecisions(dmnDocumentData.getDecisions().stream()
                .map(DmnDecision::getName)
                .collect(Collectors.toList()));
        dmnResourceContentFetcher.refreshForms();

    }
}