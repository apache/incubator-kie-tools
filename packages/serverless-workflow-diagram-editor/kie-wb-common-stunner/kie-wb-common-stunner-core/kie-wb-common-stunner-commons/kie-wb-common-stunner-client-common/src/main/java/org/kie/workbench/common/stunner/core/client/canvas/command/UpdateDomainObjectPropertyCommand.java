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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateDomainObjectPropertyValueCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class UpdateDomainObjectPropertyCommand extends AbstractCanvasGraphCommand {

    private final DomainObject domainObject;
    private final String propertyId;
    private final Object value;

    public class RefreshPropertiesPanelCommand extends AbstractCanvasCommand {

        @Override
        public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
            context.notifyCanvasDomainObjectUpdated(domainObject);
            return CanvasCommandResultBuilder.SUCCESS;
        }

        @Override
        public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
            context.notifyCanvasDomainObjectUpdated(domainObject);
            return CanvasCommandResultBuilder.SUCCESS;
        }
    }

    public UpdateDomainObjectPropertyCommand(final DomainObject domainObject,
                                             final String propertyId,
                                             final Object value) {
        this.domainObject = domainObject;
        this.propertyId = propertyId;
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new UpdateDomainObjectPropertyValueCommand(domainObject,
                                                          propertyId,
                                                          value);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new RefreshPropertiesPanelCommand();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [domainObject=" + domainObject.getDomainObjectUUID() + "," +
                "propertyId=" + propertyId + "," +
                "value=" + value + "]";
    }
}
