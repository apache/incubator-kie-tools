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


package org.kie.workbench.common.stunner.kogito.client.session.command.impl;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.command.Command;

@Dependent
@Specializes
public class KogitoRedoSessionCommand extends RedoSessionCommand {

    private final Supplier<Boolean> envelopeAvailableSupplier;
    private final Supplier<StateControl> stateControlSupplier;

    @Inject
    public KogitoRedoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler) {
        this(sessionCommandManager, redoCommandHandler, WindowRef::isEnvelopeAvailable, StateControl::get);
    }

    KogitoRedoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler,
                             final Supplier<Boolean> envelopeAvailableSupplier,
                             final Supplier<StateControl> stateControlSupplier) {
        super(sessionCommandManager, redoCommandHandler);
        this.envelopeAvailableSupplier = envelopeAvailableSupplier;
        this.stateControlSupplier = stateControlSupplier;
    }

    @Override
    protected void bindCommand() {
        //If running in Kogito we should initialize the Kogito StateControl undo/redo commands. Otherwise we should keep the key binding.
        if (isEnvelopeAvailable()) {
            stateControlSupplier.get().setRedoCommand(this::execute);
        } else {
            super.bindCommand();
        }
    }

    private boolean isEnvelopeAvailable() {
        return envelopeAvailableSupplier.get();
    }
}
