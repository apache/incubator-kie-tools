/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getChildCanvasIndex;

public class CaseManagementDeleteCanvasNodeCommand extends DeleteCanvasNodeCommand {

    private final int index;

    public CaseManagementDeleteCanvasNodeCommand(Node candidate) {
        super(candidate);

        this.index = getChildCanvasIndex(getParent(candidate), candidate);
    }

    public CaseManagementDeleteCanvasNodeCommand(Node candidate,
                                                 Node parent,
                                                 int index) {
        super(candidate, parent);
        this.index = index;
    }

    @Override
    protected AbstractCanvasCommand createUndoCommand(Node parent, Node candidate, String ssid) {
        return new CaseManagementAddChildNodeCanvasCommand(parent, candidate, ssid, index);
    }
}
