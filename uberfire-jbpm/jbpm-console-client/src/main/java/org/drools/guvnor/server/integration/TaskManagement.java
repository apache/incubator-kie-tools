package org.drools.guvnor.server.integration;

import org.jboss.bpm.console.client.model.TaskRef;

import java.util.List;
import java.util.Map;

public interface TaskManagement {

    /**
     * fetch a single task
     */
    TaskRef getTaskById(long taskId);

    /**
     * assign user to task
     */
    void assignTask(long taskId, String idRef, String userId);

    /**
     * unset a task assignment
     */
    void releaseTask(long taskId, String userId);

    /**
     * complete a task
     */
    void completeTask(long taskId, Map data, String userId);

    /**
     * complete a task with a given outcome (trigger)
     */
    void completeTask(long taskId, String outcome, Map data, String userId);

    /**
     * get tasks assingned to a user
     */
    List<TaskRef> getAssignedTasks(String idRef);

    /**
     * get unassigned tasks where a user participates
     */
    List<TaskRef> getUnassignedTasks(String idRef, String participationType);
}
