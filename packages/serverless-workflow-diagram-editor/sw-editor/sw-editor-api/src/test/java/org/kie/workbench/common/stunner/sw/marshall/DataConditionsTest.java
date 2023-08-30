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


package org.kie.workbench.common.stunner.sw.marshall;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.sw.definition.ContinueAs;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class DataConditionsTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        DataConditionTransition dataCondition = new DataConditionTransition();
        dataCondition.setCondition("${ try(.customerCount) != null and .customerCount > .quota.maxConsumedEvents }");
        dataCondition.setEnd(new StateEnd()
                                     .setContinueAs(new ContinueAs()
                                                            .setData("${ del(.customerCount) }")
                                                            .setVersion("1.0")
                                                            .setWorkflowId("notifycustomerworkflow")));

        return new Workflow()
                .setId("workflow1")
                .setStart("WaitForCustomerEvent")
                .setStates(new State[]{
                        new EventState()
                                .setName("WaitForCustomerEvent")
                                .setTransition("CheckEventQuota"),
                        new SwitchState()
                                .setName("CheckEventQuota")
                                .setDataConditions(new DataConditionTransition[]{dataCondition})
                                .setDefaultCondition(new DefaultConditionTransition()
                                                             .setTransition("WaitForCustomerEvent"))
                });
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        assertDefinitionReferencedInNode(workflow, "workflow1");
        assertEquals(4, countChildren("workflow1"));
    }
}
