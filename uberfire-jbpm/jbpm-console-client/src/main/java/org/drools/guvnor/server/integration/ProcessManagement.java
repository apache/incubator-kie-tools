package org.drools.guvnor.server.integration;

import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;

import java.util.List;
import java.util.Map;

public interface ProcessManagement {

    List<ProcessDefinitionRef> getProcessDefinitions();

    ProcessDefinitionRef getProcessDefinition(String definitionId);

    List<ProcessDefinitionRef> removeProcessDefinition(String definitionId);

    List<ProcessInstanceRef> getProcessInstances(String definitionId);

    ProcessInstanceRef getProcessInstance(String instanceId);

    ProcessInstanceRef newInstance(String defintionId);

    ProcessInstanceRef newInstance(String definitionId, Map<String, Object> processVars);

    Map<String, Object> getInstanceData(String instanceId);

    void setInstanceData(String instanceId, Map<String, Object> data);

    void endInstance(String instanceId, ProcessInstanceRef.RESULT result);

    void deleteInstance(String instanceId);

    void setProcessState(String instanceId, ProcessInstanceRef.STATE nextState);

    void signalExecution(String executionId, String signal);
}
