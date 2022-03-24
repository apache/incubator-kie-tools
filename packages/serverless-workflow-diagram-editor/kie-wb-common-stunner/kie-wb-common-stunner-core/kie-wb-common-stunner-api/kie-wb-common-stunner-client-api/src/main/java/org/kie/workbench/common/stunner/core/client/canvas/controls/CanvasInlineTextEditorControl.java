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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Provides element's name edition via some widget.
 */
public interface CanvasInlineTextEditorControl<C extends CanvasHandler, S extends ClientSession, E extends Element>
        extends CanvasRegistrationControl<C, E>,
                RequiresCommandManager<C>,
                CanvasControl.SessionAware<S> {

    CanvasInlineTextEditorControl<C, S, E> show(final E item);

    CanvasInlineTextEditorControl<C, S, E> hide();

    CanvasInlineTextEditorControl<C, S, E> rollback();
}
