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


package org.kie.workbench.common.stunner.core.client.canvas.event.command;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public abstract class AbstractCanvasCommandEvent<H extends CanvasHandler> extends AbstractCanvasHandlerEvent<H> {

    private final Command<H, CanvasViolation> command;
    private final CommandResult<CanvasViolation> result;

    public AbstractCanvasCommandEvent(final H canvasHandler,
                                      final Command<H, CanvasViolation> command,
                                      final CommandResult<CanvasViolation> result) {
        super(canvasHandler);
        this.command = command;
        this.result = result;
    }

    public Command<H, CanvasViolation> getCommand() {
        return command;
    }

    public CommandResult<CanvasViolation> getResult() {
        return result;
    }
}
