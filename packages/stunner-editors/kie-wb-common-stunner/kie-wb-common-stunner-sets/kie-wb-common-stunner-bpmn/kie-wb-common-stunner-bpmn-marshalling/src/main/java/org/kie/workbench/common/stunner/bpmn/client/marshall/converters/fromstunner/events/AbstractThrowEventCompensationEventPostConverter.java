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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ThrowEvent;
import org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.isEmpty;

public abstract class AbstractThrowEventCompensationEventPostConverter extends AbstractCompensationEventPostConverter {

    protected void linkActivityRef(Process process,
                                   ThrowEvent throwEvent,
                                   String activityRef) {
        if (!isEmpty(activityRef)) {
            final CompensateEventDefinition compensateEvent = (CompensateEventDefinition) throwEvent.getEventDefinitions().get(0);
            final Activity activity = findActivity(process,
                                                   activityRef);
            if (activity != null) {
                compensateEvent.setActivityRef(activity);
            } else {
                Bpmn2Marshalling.logError("Referred activity: " + activityRef + " was not found for event: id: " + throwEvent.getId() + ", name: " + throwEvent.getName());
            }
        }
    }
}
