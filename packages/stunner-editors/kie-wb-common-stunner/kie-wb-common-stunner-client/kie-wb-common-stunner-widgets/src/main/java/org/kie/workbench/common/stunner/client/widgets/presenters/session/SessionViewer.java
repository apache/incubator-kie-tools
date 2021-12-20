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

package org.kie.workbench.common.stunner.client.widgets.presenters.session;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.CanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A viewer type for generic client session instances.
 * <p>
 * The goal for a diagram viewer is to provide a DOM element that can be easily attached to other components
 * and encapsulates the components and logic necessary in order to display a session's diagram in the session's canvas
 * instance.
 * <p>
 * The session viewer type expects the session instance already initialized with the diagram instance and all canvas,
 * handler and controls instances.
 * <p>
 * As it inherits from Viewer type, it provides by default a zoom control enabled for this viewer session's canvas instance.
 * Consider as well that the each session's type can provide its own additional controls.
 * <p>
 * Note the interaction with the selection control and other session's controls, such as CDI, depends on its implementation.
 * Subtypes can provide additional controls.
 * @param <S> The session type.
 * @param <H> The canvas handler type.
 * @param <D> The diagram type.
 */
public interface SessionViewer<S extends ClientSession, H extends CanvasHandler, D extends Diagram>
        extends CanvasViewer<S, H, IsWidget, SessionViewer.SessionViewerCallback<D>> {

    /**
     * The callback type for session viewers.
     * @param <D> The diagram type.
     */
    interface SessionViewerCallback<D extends Diagram> extends DiagramViewer.DiagramViewerCallback<D> {

        @Override
        default void onOpen(D diagram) {
            // Handled by th session instance.
        }
    }
}
