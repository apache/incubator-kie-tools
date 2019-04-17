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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewState;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewStateAction;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewStateActionsHandler;
import org.uberfire.mvp.ParameterizedCommand;

public class DocumentPreviewStateActionsHandlerImpl implements DocumentPreviewStateActionsHandler {

    private DocumentPreviewState currentState;
    private ParameterizedCommand<DocumentPreviewState> listener;
    private Map<DocumentPreviewState, Collection<DocumentPreviewStateAction>> actions = new HashMap<>();

    public DocumentPreviewStateActionsHandlerImpl(DocumentPreviewState currentState) {
        this.currentState = currentState;
    }

    public void addStateActions(DocumentPreviewState state, Collection<DocumentPreviewStateAction> actions) {
        this.actions.put(state, actions);
    }

    @Override
    public void setStateChangeListener(ParameterizedCommand<DocumentPreviewState> listener) {
        this.listener = listener;
        notifyStateChange(currentState);
    }

    @Override
    public DocumentPreviewState getCurrentState() {
        return currentState;
    }

    public void notifyStateChange(DocumentPreviewState state) {
        this.currentState = state;
        listener.execute(state);
    }

    @Override
    public Collection<DocumentPreviewStateAction> getCurrentStateActions() {
        return actions.getOrDefault(getCurrentState(), Collections.emptyList());
    }
}
