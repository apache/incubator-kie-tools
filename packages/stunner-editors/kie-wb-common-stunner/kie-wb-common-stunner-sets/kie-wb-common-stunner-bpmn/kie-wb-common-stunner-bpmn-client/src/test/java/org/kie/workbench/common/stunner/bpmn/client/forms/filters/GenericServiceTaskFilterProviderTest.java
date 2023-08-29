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

import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;

public class GenericServiceTaskFilterProviderTest extends MultipleInstanceNodeFilterProviderTest {

    @Override
    protected MultipleInstanceNodeFilterProvider newFilterProvider() {
        return new GenericServiceTaskFilterProvider(sessionManager, refreshFormPropertiesEvent);
    }

    @Override
    protected Object newNonMultipleInstanceDefinition() {
        GenericServiceTask task = new GenericServiceTask();
        task.getExecutionSet().getIsMultipleInstance().setValue(false);
        return task;
    }

    @Override
    protected Object newMultipleInstanceDefinition() {
        GenericServiceTask task = new GenericServiceTask();
        task.getExecutionSet().getIsMultipleInstance().setValue(true);
        return task;
    }

    @Override
    protected Class<?> getExpectedDefinitionType() {
        return GenericServiceTask.class;
    }
}
