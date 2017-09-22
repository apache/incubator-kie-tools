/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Clears the state for all canvas shapes by setting each
 * one's state the value <code>NONE</code>.
 * <p/>
 * This way selected, highlighted or invalid states for current canvas
 * shapes will be clear.
 */
@Dependent
public class ClearStatesSessionCommand extends AbstractClientSessionCommand<ClientReadOnlySession> {

    public ClearStatesSessionCommand() {
        super(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        if (null != getSession().getSelectionControl()) {
            // Clears selected elements, if any.
            getSession().getSelectionControl().clearSelection();
            // Restore shape states.
            final List<Shape> shapes = getSession().getCanvas().getShapes();
            shapes.stream().forEach(shape -> shape.applyState(ShapeState.NONE));
            // Job done, fire the callback.
            callback.onSuccess();
        }
    }
}
