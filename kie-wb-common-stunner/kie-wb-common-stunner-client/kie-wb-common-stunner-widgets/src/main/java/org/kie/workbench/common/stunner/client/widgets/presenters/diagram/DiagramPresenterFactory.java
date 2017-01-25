/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram;

import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A diagram presenter factory.
 * It provides runtime bean resolutions for subtypes of <code>Diagram</code> instances, so
 * it provides resolutions for the canvas, handler and controls that supports the diagram's Definition
 * and Shape sets, in order to build Diagram Viewer/Editor instances at runtime.
 * @param <T> The diagram type.
 */
public interface DiagramPresenterFactory<D extends Diagram> {

    /**
     * Builds a new DiagramViewer instance.
     * @param diagram The diagram instance to be dislayed.
     */
    DiagramViewer<D, ?> newViewer(final D diagram);

    /**
     * Builds a new DiagramEditor instance.
     * @param diagram The diagram instance to be edited.
     */
    DiagramEditor<D, ?> newEditor(final D diagram);
}
