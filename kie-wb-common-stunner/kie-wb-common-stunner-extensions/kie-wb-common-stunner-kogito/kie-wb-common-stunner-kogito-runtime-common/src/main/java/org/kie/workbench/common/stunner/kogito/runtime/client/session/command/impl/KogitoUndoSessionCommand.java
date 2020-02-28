/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;

@Dependent
@Specializes
public class KogitoUndoSessionCommand extends UndoSessionCommand {

    private final Supplier<Boolean> envelopeAvailableSupplier;
    private final Supplier<StateControl> stateControlSupplier;

    @Inject
    public KogitoUndoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        this(sessionCommandManager, WindowRef::isEnvelopeAvailable, StateControl::get);
    }

    KogitoUndoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final Supplier<Boolean> envelopeAvailableSupplier,
                             final Supplier<StateControl> stateControlSupplier) {
        super(sessionCommandManager);
        this.envelopeAvailableSupplier = envelopeAvailableSupplier;
        this.stateControlSupplier = stateControlSupplier;
    }

    @Override
    protected void bindCommand() {
        //If running in Kogito we should initialize the Kogito StateControl undo/redo commands. Otherwise we should keep the key binding.
        if (isEnvelopeAvailable()) {
            stateControlSupplier.get().setUndoCommand(this::execute);
        } else {
            super.bindCommand();
        }
    }

    private boolean isEnvelopeAvailable() {
        return envelopeAvailableSupplier.get();
    }
}
