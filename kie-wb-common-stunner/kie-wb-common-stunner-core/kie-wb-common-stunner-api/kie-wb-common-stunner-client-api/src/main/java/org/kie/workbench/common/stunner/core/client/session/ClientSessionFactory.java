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

package org.kie.workbench.common.stunner.core.client.session;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * A factory type for client side sessions.
 * Stunner provides built-in support by default for two kind of sessions:
 * - See <a>org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession</a>
 * - See <a>org.kie.workbench.common.stunner.core.client.session.ClientFullSession</a>
 * @param <S> The session instances type that this factory produces.
 */
public interface ClientSessionFactory<S extends ClientSession> {

    /**
     * Builds and initializes a new session instance of type <code>S</code>.
     * It should not be opened at this time.
     */
    void newSession(Metadata metadata,
                    Consumer<S> sessionConsumer);

    /**
     * Return the type for the session produced.
     */
    Class<S> getSessionType();
}
