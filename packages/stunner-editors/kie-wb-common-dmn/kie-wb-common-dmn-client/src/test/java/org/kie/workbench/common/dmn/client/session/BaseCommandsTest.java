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

package org.kie.workbench.common.dmn.client.session;

import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;

public abstract class BaseCommandsTest {

    @Mock
    protected BaseCanvasHandler canvasHandler;

    @SuppressWarnings("unchecked")
    protected CanvasCommandExecutedEvent makeCommandExecutionContext(final Command command) {
        return new CanvasCommandExecutedEvent(canvasHandler,
                                              command,
                                              CanvasCommandResultBuilder.SUCCESS);
    }

    @SuppressWarnings("unchecked")
    protected CanvasCommandUndoneEvent makeCommandUndoContext(final Command command) {
        return new CanvasCommandUndoneEvent(canvasHandler,
                                            command,
                                            CanvasCommandResultBuilder.SUCCESS);
    }

    public static class MockCommand extends AbstractCanvasGraphCommand {

        @Override
        protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
            return null;
        }

        @Override
        protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
            return null;
        }
    }

    public static class MockVetoExecutionCommand extends MockCommand implements VetoExecutionCommand {

    }

    public static class MockVetoUndoCommand extends MockCommand implements VetoUndoCommand {

    }
}
