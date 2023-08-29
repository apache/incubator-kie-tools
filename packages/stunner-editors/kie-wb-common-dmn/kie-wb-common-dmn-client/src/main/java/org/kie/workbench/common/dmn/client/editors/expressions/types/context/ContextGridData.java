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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DelegatingGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

public class ContextGridData extends DelegatingGridData {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Supplier<Optional<Context>> expression;
    private final Command canvasOperation;

    public ContextGridData(final DMNGridData delegate,
                           final SessionManager sessionManager,
                           final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                           final Supplier<Optional<Context>> expression,
                           final Command canvasOperation) {
        super(delegate);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expression = expression;
        this.canvasOperation = canvasOperation;
    }

    // --- Intercepted methods delegated to commands ---

    @Override
    public void moveRowTo(final int index,
                          final GridRow row) {
        moveRowsTo(index,
                   Collections.singletonList(row));
    }

    @Override
    public void moveRowsTo(final int index,
                           final List<GridRow> rows) {
        expression.get().ifPresent(context -> {
            final AbstractCanvasHandler handler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
            final MoveRowsCommand command = new MoveRowsCommand(context,
                                                                delegate,
                                                                index,
                                                                rows,
                                                                canvasOperation);
            if (isAllowed(sessionCommandManager.allow(handler,
                                                      command))) {
                sessionCommandManager.execute(handler,
                                              command);
            }
        });
    }

    private boolean isAllowed(final CommandResult<CanvasViolation> result) {
        return !CommandResult.Type.ERROR.equals(result.getType());
    }
}
