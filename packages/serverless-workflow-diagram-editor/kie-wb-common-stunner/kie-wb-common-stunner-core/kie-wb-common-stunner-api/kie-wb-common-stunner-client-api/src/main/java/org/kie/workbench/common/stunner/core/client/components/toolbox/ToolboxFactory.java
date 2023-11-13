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


package org.kie.workbench.common.stunner.core.client.components.toolbox;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * It builds toolbox instances for a given element of type <code>E</code>, in the specified
 * canvas handler of type <code>H</code>.
 * @param <H> The canvas handler type.
 * @param <E> The element type.
 */
public interface ToolboxFactory<H extends CanvasHandler, E extends Element> {

    /**
     * Builds a toolbox instance for the specified <code>element</code> and
     * <code>canvasHandler</code>.
     * @param canvasHandler The canvas handler.
     * @param element The element.
     * @return As toolbox items could depend on the current context (states, rules, etc),
     * the result can be either a toolbox instance or empty.
     */
    public Optional<Toolbox<?>> build(H canvasHandler,
                                      E element);
}
