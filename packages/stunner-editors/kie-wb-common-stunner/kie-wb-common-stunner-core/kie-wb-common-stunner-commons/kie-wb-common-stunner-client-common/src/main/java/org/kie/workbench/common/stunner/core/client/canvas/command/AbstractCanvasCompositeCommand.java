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
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;

public abstract class AbstractCanvasCompositeCommand
        extends AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation>
        implements CanvasCommand<AbstractCanvasHandler> {

    @Override
    protected CommandResult<CanvasViolation> doAllow(final AbstractCanvasHandler context,
                                                     final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return command.allow(context);
    }

    @Override
    protected CommandResult<CanvasViolation> doExecute(final AbstractCanvasHandler context,
                                                       final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return command.execute(context);
    }

    @Override
    protected CommandResult<CanvasViolation> doUndo(final AbstractCanvasHandler context,
                                                    final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return command.undo(context);
    }
}
