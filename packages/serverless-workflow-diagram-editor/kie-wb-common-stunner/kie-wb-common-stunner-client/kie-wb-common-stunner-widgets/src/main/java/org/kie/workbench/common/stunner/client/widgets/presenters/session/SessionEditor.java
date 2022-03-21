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
import org.kie.workbench.common.stunner.client.widgets.presenters.Editor;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * An editor type for generic client session instances.
 * <p>
 * The goal for a diagram editor is to provide a DOM element that can be easily attached to other components
 * and encapsulates the components and logic necessary in order to provide authoring features for the session's diagram
 * instance.
 * <p>
 * The session editor type expects the session instance already initialized with the diagram instance and all canvas,
 * handler and controls instances.
 * <p>
 * Notes:
 * - The interaction with the editor different controls, such as CDI context interactions, depends on
 * each control's implementation.
 * - Subtypes can provide additional controls.
 * @param <S> The session type.
 * @param <H> The canvas handler type.
 * @param <D> The diagram type.
 */
public interface SessionEditor<S extends ClientSession, H extends CanvasHandler, D extends Diagram>
        extends SessionViewer<S, H, D>,
                Editor<S, H, IsWidget, SessionViewer.SessionViewerCallback<D>> {

}
