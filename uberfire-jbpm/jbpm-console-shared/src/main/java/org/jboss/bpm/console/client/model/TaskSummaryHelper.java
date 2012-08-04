/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.bpm.console.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 *

 */
public class TaskSummaryHelper {
    public static List<TaskSummary> adapt(List<org.jbpm.task.query.TaskSummary> taskSums){
        List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>(taskSums.size());
        for(org.jbpm.task.query.TaskSummary taskSum : taskSums){
            taskSummaries.add(new TaskSummary(taskSum.getId(), 
                                taskSum.getProcessInstanceId(), 
                                taskSum.getName(), 
                                taskSum.getSubject(), 
                                taskSum.getDescription(), 
                                (taskSum.getStatus() != null)?taskSum.getStatus().name():"", 
                                taskSum.getPriority(),
                                taskSum.isSkipable(),
                                (taskSum.getActualOwner() != null)?taskSum.getActualOwner().getId():"", 
                                (taskSum.getCreatedBy() != null)?taskSum.getCreatedBy().getId():"", 
                                taskSum.getCreatedOn(),
                                taskSum.getActivationTime(),
                                taskSum.getExpirationTime(), 
                                taskSum.getProcessId(), 
                                taskSum.getProcessSessionId()));
        }
        return taskSummaries;
    }
}
