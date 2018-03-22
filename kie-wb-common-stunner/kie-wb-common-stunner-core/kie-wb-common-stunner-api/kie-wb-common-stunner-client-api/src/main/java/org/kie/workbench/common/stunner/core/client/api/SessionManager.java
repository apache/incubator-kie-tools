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

package org.kie.workbench.common.stunner.core.client.api;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * A singleton instance for each application's client that handles the different Stunner's sessions on client side.
 * Four operations describe the lifecycle for the Stunner's client sessions:
 * <ul>
 * <li>When a session is opened, its canvas controls, listeners and other behaviours should be constructed,
 * registered and enabled, although each implementation can provide its own behaviours</li>
 * <li>When a session is destroyed, its canvas controls, listeners and other behaviours should be removed and
 * destroyed. The session will no longer be paused/resumed.</li>
 * <li>A session is paused when the client does not interact with any of the components that the
 * session aggregates. Implementations can do some memory cleans, if necessary.</li>
 * <li>A session is resumed when the client does interact again with any of the components that the paused
 * session aggregates. Implementations can activate again whatever previously disabled when pausing
 * here, if necessary.</li>
 * </ul>
 */
public interface SessionManager {

    /**
     * Returns the factory instance for the given session type.and targeted for the given Diagram type..
     */
    <S extends ClientSession> ClientSessionFactory<S> getSessionFactory(final Metadata metadata,
                                                                        final Class<S> sessionType);

    /**
     * Returns the current active session.
     */
    <S extends ClientSession> S getCurrentSession();

    /**
     * Opens the session <code>session</code>.
     */
    <S extends ClientSession> void open(final S session);

    /**
     * Pause the current session.
     */
    void pause();

    /**
     * Resume the session <code>session</code> and pause the current one, if any.
     */
    <S extends ClientSession> void resume(final S session);

    /**
     * Destroys the current active session (do not destroys this manager instance!).
     */
    void destroy();
}
