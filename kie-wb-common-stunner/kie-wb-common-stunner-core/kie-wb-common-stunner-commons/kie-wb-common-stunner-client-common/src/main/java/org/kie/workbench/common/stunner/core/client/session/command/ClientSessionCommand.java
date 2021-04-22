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
package org.kie.workbench.common.stunner.core.client.session.command;

import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.mvp.Command;

public interface ClientSessionCommand<S extends ClientSession> extends CanvasControl.SessionAware<S> {

    interface Callback<V> {

        void onSuccess();

        void onError(final V error);
    }

    ClientSessionCommand<S> listen(final Command statusCallback);

    <V> void execute(final Callback<V> callback);

    boolean isEnabled();

    void destroy();
}
