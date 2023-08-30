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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateDomainObjectPropertyValueCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateDomainObjectPropertyCommandTest {

    private static final String PROPERTY_ID = "property.id";

    private static final Object VALUE = new Object();

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private DomainObject domainObject;

    @Test
    public void testNewGraphCommand() {
        final Command<GraphCommandExecutionContext, RuleViolation> command = new UpdateDomainObjectPropertyCommand(domainObject,
                                                                                                                   PROPERTY_ID,
                                                                                                                   VALUE).newGraphCommand(canvasHandler);

        assertThat(command).isInstanceOf(UpdateDomainObjectPropertyValueCommand.class);
    }

    @Test
    public void testNewCanvasCommandExecute() {
        final CanvasCommand<AbstractCanvasHandler> command = new UpdateDomainObjectPropertyCommand(domainObject,
                                                                                                   PROPERTY_ID,
                                                                                                   VALUE).newCanvasCommand(canvasHandler);

        assertThat(command).isInstanceOf(UpdateDomainObjectPropertyCommand.RefreshPropertiesPanelCommand.class);

        assertThat(command.execute(canvasHandler)).isEqualTo(CanvasCommandResultBuilder.SUCCESS);

        verify(canvasHandler).notifyCanvasDomainObjectUpdated(eq(domainObject));
    }

    @Test
    public void testNewCanvasCommandUndo() {
        final CanvasCommand<AbstractCanvasHandler> command = new UpdateDomainObjectPropertyCommand(domainObject,
                                                                                                   PROPERTY_ID,
                                                                                                   VALUE).newCanvasCommand(canvasHandler);

        assertThat(command).isInstanceOf(UpdateDomainObjectPropertyCommand.RefreshPropertiesPanelCommand.class);

        assertThat(command.undo(canvasHandler)).isEqualTo(CanvasCommandResultBuilder.SUCCESS);

        verify(canvasHandler).notifyCanvasDomainObjectUpdated(eq(domainObject));
    }
}
