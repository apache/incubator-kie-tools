package org.drools.guvnor.server.plugin;

import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.DiagramInfo;

import java.net.URL;
import java.util.List;

public interface GraphViewerPlugin {
    /**
     * Check {@link #getDiagramURL(String)} != null before invoking.
     */
    byte[] getProcessImage(String processId);

    DiagramInfo getDiagramInfo(String processId);

    List<ActiveNodeInfo> getActiveNodeInfo(String instanceId);

    /**
     * Can be null, in case no diagram is associated with the process
     */
    URL getDiagramURL(String id);

    /**
     * Collects node information (such as coordinates) for given processDefinitionId and selected activities.
     * Both arguments must be specified.
     * @param processDefinitionId process definition id which nodes information should be retrieved for
     * @param activities list of activity names treated as a filter
     * @return list of found node information. Can be empty list if no definition was found
     */
    List<ActiveNodeInfo> getNodeInfoForActivities(String processDefinitionId, List<String> activities);
}
