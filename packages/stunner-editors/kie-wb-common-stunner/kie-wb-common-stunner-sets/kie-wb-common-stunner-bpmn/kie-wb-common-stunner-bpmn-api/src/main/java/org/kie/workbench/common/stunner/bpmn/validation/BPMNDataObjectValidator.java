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


package org.kie.workbench.common.stunner.bpmn.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public abstract class BPMNDataObjectValidator implements DomainValidator {

    private static final String ALLOWED_CHARS = "^[a-zA-Z0-9\\-\\_\\ \\+\\/\\*\\?\\'\\.]*$";

    @Override
    public String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }

    @Override
    public void validate(Diagram diagram, Consumer<Collection<DomainViolation>> resultConsumer) {
        Iterator<Element> it = diagram.getGraph().nodes().iterator();
        Map<String, String> dataObjectsMap = new HashMap<>();
        List<DomainViolation> violations = new ArrayList<>();
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View && ((View) element.getContent()).getDefinition() instanceof DataObject) {
                DataObject dataObject = (DataObject) ((View) element.getContent()).getDefinition();

                String name = dataObject.getName().getValue();
                String type = dataObject.getType().getValue().getType();
                String containedType = dataObjectsMap.get(name);

                if (containedType != null && !type.equals(containedType)) { // If already defined with different type
                    BPMNViolation bpmnViolation = new BPMNViolation(getMessageDataObjectWithTypeSameName() + " : " + name, Violation.Type.WARNING, element.getUUID());
                    violations.add(bpmnViolation);
                } else {
                    dataObjectsMap.put(name, type);
                }

                if (!name.matches(ALLOWED_CHARS)) {
                    BPMNViolation bpmnViolation = new BPMNViolation(getMessageDataObjectIllegalName() + " : " + name, Violation.Type.WARNING, element.getUUID());
                    violations.add(bpmnViolation);
                }
            }
        }
        resultConsumer.accept(violations);
    }

    public abstract String getMessageDataObjectWithTypeSameName();

    public abstract String getMessageDataObjectWithName();

    public abstract String getMessageDataObjectIllegalName();
}