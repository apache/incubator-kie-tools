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

package org.kie.workbench.common.stunner.client.widgets.presenters.session;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A session presenter factory.
 * It provides runtime bean resolutions for subtypes of <code>Diagram</code>,
 * <code>AbstractClientReadOnlySession</code> and <code>AbstractClientFullSession</code> instances.
 * @param <D> The diagram type.
 * @param <S> The read-only session type.
 * @param <E> The full session type.
 */
public interface SessionPresenterFactory<D extends Diagram, S extends AbstractClientReadOnlySession, E extends AbstractClientFullSession> {

    /**
     * Creates a new session's preview presenter instance.
     * @param <PS> The preview client session type.
     */
    <PS extends ClientSession> SessionPreview<PS, D> newPreview();

    /**
     * Creates a new session's viewer instance.
     */
    SessionViewer<S, ?, D> newViewer();

    /**
     * Creates a new session's editor instance.
     */
    SessionEditor<E, ?, D> newEditor();

    /**
     * Creates a new session's presenter for read-only goals.
     */
    SessionPresenter<S, ?, D> newPresenterViewer();

    /**
     * Creates a new session's presenter for authoring goals.
     */
    SessionPresenter<E, ?, D> newPresenterEditor();
}
