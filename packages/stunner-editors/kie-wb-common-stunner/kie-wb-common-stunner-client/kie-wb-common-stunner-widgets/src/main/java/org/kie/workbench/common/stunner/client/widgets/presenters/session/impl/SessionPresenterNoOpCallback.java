/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public class SessionPresenterNoOpCallback<D extends Diagram>
        implements SessionPresenter.SessionPresenterCallback<D> {

    public static SessionPresenterNoOpCallback<Diagram> INSTANCE = new SessionPresenterNoOpCallback<>();

    @Override
    public void afterSessionOpened() {

    }

    @Override
    public void afterCanvasInitialized() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(ClientRuntimeError error) {

    }
}
