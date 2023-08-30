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


package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import org.kie.workbench.common.stunner.bpmn.definition.UserTask;

public class UserTaskFilterProviderTest extends MultipleInstanceNodeFilterProviderTest {

    @Override
    protected MultipleInstanceNodeFilterProvider newFilterProvider() {
        return new UserTaskFilterProvider(sessionManager, refreshFormPropertiesEvent);
    }

    @Override
    protected Object newNonMultipleInstanceDefinition() {
        UserTask userTask = new UserTask();
        userTask.getExecutionSet().getIsMultipleInstance().setValue(false);
        return userTask;
    }

    @Override
    protected Object newMultipleInstanceDefinition() {
        UserTask userTask = new UserTask();
        userTask.getExecutionSet().getIsMultipleInstance().setValue(true);
        return userTask;
    }

    @Override
    protected Class<?> getExpectedDefinitionType() {
        return UserTask.class;
    }
}
