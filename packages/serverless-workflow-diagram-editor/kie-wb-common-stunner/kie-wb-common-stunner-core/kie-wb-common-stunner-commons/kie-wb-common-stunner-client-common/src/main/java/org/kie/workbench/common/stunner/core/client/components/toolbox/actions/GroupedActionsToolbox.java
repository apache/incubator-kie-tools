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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

public class GroupedActionsToolbox<V extends ActionsToolboxView<?>> extends ActionsToolbox {

    private HashMap<ToolboxAction<AbstractCanvasHandler>, String> connectorActions;
    private HashMap<ToolboxAction<AbstractCanvasHandler>, String> nodeActions;

    public GroupedActionsToolbox(Supplier supplier, Element element, ActionsToolboxView view) {
        super(supplier, element, view);
    }

    public int getNodeSize(final String connectorId) {
        AtomicInteger count = new AtomicInteger();
        if (null != nodeActions) {
            nodeActions.forEach((key, value) -> {
                if (value.equals(connectorId)) {
                    count.getAndIncrement();
                }
            });
        }
        return count.get();
    }

    public int getConnectorSize() {
        if (null != connectorActions) {
            return connectorActions.size();
        }
        return 0;
    }

    public HashMap<ToolboxAction<AbstractCanvasHandler>, String> getConnectorActions() {
        return connectorActions;
    }

    public void setConnectorActions(HashMap<ToolboxAction<AbstractCanvasHandler>, String> connectorActions) {
        this.connectorActions = connectorActions;
    }

    public HashMap<ToolboxAction<AbstractCanvasHandler>, String> getNodeActions() {
        return nodeActions;
    }

    public void setNodeActions(HashMap<ToolboxAction<AbstractCanvasHandler>, String> nodeActions) {
        this.nodeActions = nodeActions;
    }

    @Override
    public Iterator<ToolboxAction> iterator() {
        final List<ToolboxAction> actions = new LinkedList<>();
        nodeActions.forEach((action, definitionId) -> {
            actions.add(action);
        });

        return actions.iterator();
    }

    @Override
    // Amount of actions (connectors and nodes)
    public int size() {
        return nodeActions.size() + connectorActions.size();
    }

    @Override
    public void destroy() {
        super.destroy();
        connectorActions.clear();
        nodeActions.clear();
        connectorActions = null;
        nodeActions = null;
    }
}
