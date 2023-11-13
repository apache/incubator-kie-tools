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


package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.ParameterizedCommand;

public interface CanvasHandler<D extends Diagram, C extends Canvas> {

    /**
     * Sets the handled canvas instance.
     */
    CanvasHandler<D, C> handle(C canvas);

    /**
     * Loads a diagram.instance (depends on each implementation which state, such as index/es, rules etc should
     * be kept) and draws it in the handled canvas.
     * @param diagram The Diagram instance to load and draw into the handled canvas.
     * @param loadCallback A parametrized callback as this operation can be asynchronous, eg: calling third party
     * endpoints or using the client-server bus application bug in order to perform initializations.
     * This operation returns the errors or the violated constraint occurred during the execution, if any.
     */
    void draw(D diagram,
              ParameterizedCommand<CommandResult> loadCallback);

    /**
     * The managed diagram instance.
     */
    D getDiagram();

    /**
     * The managed canvas instance.
     */
    C getCanvas();

    /**
     * Clears the canvas and the the diagram's state, but does not destroy this handler instance.
     * It can further be re-initialized in order to handle other canvas/diagram instances.
     */
    CanvasHandler<D, C> clear();

    /**
     * Destroys whatever canvas handler state is present and all its members' states, it will be no longer used.
     */
    void destroy();
}
