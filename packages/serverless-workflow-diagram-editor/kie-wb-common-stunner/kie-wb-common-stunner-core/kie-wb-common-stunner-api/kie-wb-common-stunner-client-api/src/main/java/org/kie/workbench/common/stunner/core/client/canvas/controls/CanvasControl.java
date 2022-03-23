/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.core.client.session.ClientSession;

/**
 * A canvas control.
 * It can implement <code>IsWidget</code> if the control have to include views outside the canvas itself,
 * such as floating widgets.
 */
public interface CanvasControl<C> {

    interface SessionAware<S extends ClientSession> {

        void bind(S session);
    }

    /**
     * This method is called when the control is initialized for a given context.
     */
    void init(C context);

    /**
     * This method is called when the control is being destroyed.
     */
    void destroy();
}
