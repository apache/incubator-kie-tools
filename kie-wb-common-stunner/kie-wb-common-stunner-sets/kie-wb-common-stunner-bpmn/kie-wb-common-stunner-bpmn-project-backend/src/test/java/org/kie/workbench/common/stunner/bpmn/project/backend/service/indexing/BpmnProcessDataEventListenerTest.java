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
package org.kie.workbench.common.stunner.bpmn.project.backend.service.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.project.backend.indexing.BpmnProcessDataEventListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BpmnProcessDataEventListenerTest {

    @Test
    public void testAddDistinctProcessVariables() throws Exception {
        Resource processResource = new Resource("org.jbpm.test.testProcess.bpmn2",
                                                ResourceType.BPMN2);
        BpmnProcessDataEventListener dataEventListener = new BpmnProcessDataEventListener();
        try {
            dataEventListener.addDistinctProcessVariables(getProcessVariables(),
                                                          processResource);
            Set<String> adderVars = dataEventListener.getUniqueVariables();
            assertNotNull(adderVars);
            assertEquals(3,
                         adderVars.size());
        } catch (IllegalArgumentException e) {
            fail("Unable to add process variables: " + e.getMessage());
        }
    }

    private List<Variable> getProcessVariables() {
        List<Variable> processVariables = new ArrayList<>();
        Variable firstName = new Variable();
        firstName.setName("firstName");
        firstName.setType(new StringDataType());
        processVariables.add(firstName);

        Variable lastName = new Variable();
        lastName.setName("lastName");
        lastName.setType(new StringDataType());
        processVariables.add(lastName);

        Variable address = new Variable();
        address.setName("address");
        address.setType(new StringDataType());
        processVariables.add(address);

        // add first name again (could be a subprocess variable also called firstName
        Variable subprocessFirstName = new Variable();
        subprocessFirstName.setName("firstName");
        subprocessFirstName.setType(new StringDataType());
        processVariables.add(subprocessFirstName);

        return processVariables;
    }
}
