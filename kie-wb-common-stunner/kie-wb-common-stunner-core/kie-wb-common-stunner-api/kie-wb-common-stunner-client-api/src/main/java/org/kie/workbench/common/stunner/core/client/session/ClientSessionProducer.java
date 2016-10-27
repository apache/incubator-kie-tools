/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;

/**
 * It produces the client sessions.
 * Different clients requires different canvas controls and specific behaviours ( eg: mobile / desktop ).
 * @param <C> The canvas type.
 * @param <H> The canvas handler type.
 */
public interface ClientSessionProducer<C extends Canvas, H extends CanvasHandler> {

    /**
     * Creates a new read-only client session.
     */
    ClientReadOnlySession<C, H> newReadOnlySession();

    /**
     * Creates a new client session with all features.
     */
    ClientFullSession<C, H> newFullSession();

}
