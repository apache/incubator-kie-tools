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


package org.kie.workbench.common.stunner.core.client.api;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * A singleton instance for each application's client that handles the different Stunner's sessions on client side.
 * Four operations describe the lifecycle for the Stunner's client sessions:
 * <ul>
 * <li>When a session is created, its canvas controls, listeners and other behaviours should be constructed,
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
     * Creates a new session for the given type and metadata.
     */
    <S extends ClientSession> void newSession(Metadata metadata,
                                              Class<S> sessionType,
                                              Consumer<S> session);

    /**
     * Resume the session <code>session</code> and pause the current one, if any.
     */
    <S extends ClientSession> void open(S session);

    /**
     * Destroys the current active session (do not destroys this manager instance!).
     */
    <S extends ClientSession> void destroy(S session);

    /**
     * Returns the current active session.
     */
    <S extends ClientSession> S getCurrentSession();

    void handleCommandError(final CommandException exception);

    void handleClientError(final ClientRuntimeError error);
}
