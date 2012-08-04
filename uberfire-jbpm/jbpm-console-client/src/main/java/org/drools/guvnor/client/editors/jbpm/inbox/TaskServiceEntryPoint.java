/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.guvnor.client.editors.jbpm.inbox;

import org.jboss.bpm.console.client.model.TaskSummary;
import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;


/**
 *
 */
@Remote
public interface TaskServiceEntryPoint {

    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult);

    List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language);

    List<TaskSummary> getTasksOwned(String userId);

    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);
    
    public long addTask(String taskString, Map<String, Object> params);

    public void start(long taskId, String user);
    
    public void claim(long taskId, String user);
    
    public void complete(long taskId, String user, Map<String, Object> params);
  
    public void release(long taskId, String user);
}
