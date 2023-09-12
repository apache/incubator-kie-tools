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


package org.kie.workbench.common.stunner.core.client.util;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

public class ClientUtils {

    public static String getSelectedElementUUID(ClientSession clientSession) {
        Optional<Collection<String>> selectedItems = Optional.empty();
        if (clientSession instanceof EditorSession) {
            selectedItems = Optional.ofNullable(((EditorSession) clientSession).getSelectionControl().getSelectedItems());
        } else if (clientSession instanceof ViewerSession) {
            selectedItems = Optional.ofNullable(((ViewerSession) clientSession).getSelectionControl().getSelectedItems());
        }
        return selectedItems.map(strings -> strings.stream().findFirst().orElse(null)).orElse(null);
    }

    public static Node getSelectedNode(Diagram diagram, ClientSession clientSession) {
        String uuid = getSelectedElementUUID(clientSession);
        return uuid != null ? diagram.getGraph().getNode(uuid) : null;
    }
}
