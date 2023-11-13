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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public class UserTaskFilterProvider extends MultipleInstanceNodeFilterProvider {

    public UserTaskFilterProvider() {
        this(null, null);
    }

    @Inject
    public UserTaskFilterProvider(final SessionManager sessionManager,
                                  final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent) {
        super(sessionManager, refreshFormPropertiesEvent);
    }

    @Override
    public Class<UserTask> getDefinitionType() {
        return UserTask.class;
    }

    @Override
    public boolean isMultipleInstance(final Object definition) {
        final UserTask userTask = (UserTask) definition;
        return userTask.getExecutionSet().getIsMultipleInstance().getValue();
    }
}
