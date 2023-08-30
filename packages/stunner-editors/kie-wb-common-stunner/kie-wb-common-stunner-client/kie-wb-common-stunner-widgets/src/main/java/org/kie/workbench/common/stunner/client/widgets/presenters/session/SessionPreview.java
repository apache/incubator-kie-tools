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


package org.kie.workbench.common.stunner.client.widgets.presenters.session;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A preview presenter type for generic client session instances.
 * <p/>
 * The goal for the session preview presenter is to replicate the active session's diagram, canvas and handler states
 * into another different canvas instance, which is handled by a proxy canvas handler type. Having different canvas
 * instances replicated can be useful for goals such as:
 * - The session's diagram potentially being replicated in the preview's canvas
 * - Possibility to execute commands against the preview's canvas handler's instance to update the preview's canvas state
 * without affecting the session's canvas.
 * - Possibility to use different canvas control instances for the preview's presenter, so for example, different
 * mediators (zoom, pan) and other behaviors can be done separately on each canvas.
 * <p/>
 * As it inherits from a Viewer type, it provides by default a zoom control enabled for this viewer session's canvas instance.
 * @param <S> The session type.
 * @param <D> The diagram type.
 */
public interface SessionPreview<S extends ClientSession, D extends Diagram> extends SessionViewer<S, AbstractCanvasHandler, D> {

}
