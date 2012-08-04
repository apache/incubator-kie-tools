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
package org.drools.guvnor.server.editors.jbpm.inbox;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.drools.guvnor.client.editors.jbpm.inbox.TaskServiceEntryPoint;
import org.jboss.bpm.console.client.model.TaskSummary;
import org.jboss.bpm.console.client.model.TaskSummaryHelper;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.impl.factories.TaskFactory;

/**
 *

 */
@Service
@ApplicationScoped
@Transactional
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint{
    @Inject TaskQueryService taskQueryService;
    @Inject TaskInstanceService taskInstanceService;
    
    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsBusinessAdministrator(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsExcludedOwner(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsRecipient(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsTaskInitiator(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsTaskStakeholder(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksOwned(userId));
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language));
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return TaskSummaryHelper.adapt(taskQueryService.getSubTasksByParent(parentId));
    }
    @Override
    public long addTask(String taskString, Map<String, Object> params){
        Task task = TaskFactory.evalTask(taskString, params, true);
        return taskInstanceService.addTask(task, params);
    }

    @Override
    public void start(long taskId, String user) {
        taskInstanceService.start(taskId, user);
    }
    
    @Override
    public void complete(long taskId, String user, Map<String, Object> params) {
        taskInstanceService.complete(taskId, user, params);
    }

    @Override
    public void claim(long taskId, String user) {
        taskInstanceService.claim(taskId, user);
    }
    
    
}
