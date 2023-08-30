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

package org.kie.workbench.common.dmn.client.events;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.AbstractSessionEvent;

@NonPortable
public class EditExpressionEvent extends AbstractSessionEvent {

    private final String nodeUUID;
    private final HasExpression hasExpression;
    private final Optional<HasName> hasName;
    private final boolean isOnlyVisualChangeAllowed;

    public EditExpressionEvent(final ClientSession session,
                               final String nodeUUID,
                               final HasExpression hasExpression,
                               final Optional<HasName> hasName,
                               final boolean isOnlyVisualChangeAllowed) {
        super(session);
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }

    public Optional<HasName> getHasName() {
        return hasName;
    }

    public boolean isOnlyVisualChangeAllowed() {
        return isOnlyVisualChangeAllowed;
    }
}
