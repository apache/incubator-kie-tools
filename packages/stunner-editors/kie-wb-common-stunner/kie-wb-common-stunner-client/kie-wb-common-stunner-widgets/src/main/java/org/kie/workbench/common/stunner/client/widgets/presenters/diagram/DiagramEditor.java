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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram;

import org.kie.workbench.common.stunner.client.widgets.presenters.Editor;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * An editor type for diagram instances based on any subtypes for <code>Diagram</code> and <code>AbstractCanvasHandler</code>.
 * <p>
 * The goal for a diagram editor is to provide authoring features in top of a diagram viewer instance.
 * On top of the diagram viewer instance, it provides:
 * - The mandatory acceptor controls enabled for this viewer's canvas handler instance, in order
 * to provide user interactions for the different model structure updates
 * <p>
 * Notes:
 * - The interaction with the some of the controls, such as CDI contexts and events, depends on each control's implementation.
 * - Subtypes can provide additional controls.
 * <p>
 * As for viewers, in case the default Stunner's behaviors, features, views or controls do not fit a concrete
 * Definition Set requirements,different DiagramEditor types could be necessary for custom behaviors..
 * @param <D> The diagram type.
 * @param <H> The canvas handler type.
 */
public interface DiagramEditor<D extends Diagram, H extends CanvasHandler>
        extends DiagramViewer<D, H>,
                Editor<D, H, WidgetWrapperView, DiagramViewer.DiagramViewerCallback<D>> {

    /**
     * Returns a connection acceptor control instance. Allows the user to create valid connections.
     */
    ConnectionAcceptorControl<H> getConnectionAcceptorControl();

    /**
     * Returns a containment acceptor control instance. Allows the user to create valid parent/child hierarchies.
     */
    ContainmentAcceptorControl<H> getContainmentAcceptorControl();

    /**
     * Returns a docking acceptor control instance. Allows the user to perform valid dock operations
     * for allowed elements.
     */
    DockingAcceptorControl<H> getDockingAcceptorControl();

    /**
     * Returns a line splice acceptor control instance. Allows the user to splice lines by adding nodes in between connectors.
     */
    LineSpliceAcceptorControl<H> getLineSpliceAcceptorControl();
}
