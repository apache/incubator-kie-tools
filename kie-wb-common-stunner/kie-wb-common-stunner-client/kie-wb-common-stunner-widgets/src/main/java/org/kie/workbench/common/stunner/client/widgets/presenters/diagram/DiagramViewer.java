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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram;

import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.CanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A viewer type for diagram instances based on any subtypes for <code>Diagram</code> and <code>AbstractCanvasHandler</code>.
 * <p>
 * The goal for a diagram viewer is to provide a DOM element that can be easily attached to other components
 * and encapsulates the components and logic necessary in order to open a diagram instance in a canvas.
 * It also must provide:
 * - A zoom control enabled for this viewer's canvas instance.
 * - A selection control enabled for this viewer's canvas handler instance.
 * Note the interaction with the selection control and other contexts, such as CDI, depends on its implementation.
 * Subtypes can provide additional controls.
 * <p>
 * The main usage for this component is as:
 * 1.- Load a diagram instance from backend using any of the Stunner's client services.
 * 2.- Create an injection point for <code>DiagramPresenterFactory</code>
 * 3.- Use the factory methods to create the right diagram viewer instance type for a given Diagram.
 * <p>
 * In case the default Stunner's behaviors, features and views do not fit a concrete Definition Set requirements,
 * different DiagramViewer types could be necessary for custom behaviors,
 * @param <D> The diagram type.
 * @param <H> The canvas handler type.
 */
public interface DiagramViewer<D extends Diagram, H extends CanvasHandler>
        extends CanvasViewer<D, H, WidgetWrapperView, DiagramViewer.DiagramViewerCallback<D>> {

    /**
     * The callback for the diagram viewer type.
     * @param <D>
     */
    interface DiagramViewerCallback<D extends Diagram> extends Viewer.Callback {

        void onOpen(D diagram);

        /**
         * Provide additional callback notification fired once canvas and handler have been
         * initialized by the diagram still not draw/loaded.
         */
        void afterCanvasInitialized();
    }

    /**
     * Diagram viewer types must support selection control and its cdi events, to
     * ahcieve better context integrations.
     */
    SelectionControl<H, Element> getSelectionControl();
}
