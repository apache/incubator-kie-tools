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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;

import static org.kie.workbench.common.forms.jbpm.client.rendering.util.DocumentSizeHelper.getFormattedDocumentSize;

@Dependent
public class DocumentPreview implements DocumentPreviewView.Presenter,
                                        IsElement {

    private final DocumentPreviewView view;
    private final TranslationService translationService;

    private DocumentPreviewState state;
    private DocumentData documentData;
    private DocumentPreviewStateActionsHandler actionsHandler;

    private boolean enabled = true;

    @Inject
    public DocumentPreview(DocumentPreviewView view, TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        view.init(this);
    }

    public void init(DocumentData documentData) {
        this.documentData = documentData;

        if (documentData.getStatus().equals(DocumentStatus.STORED)) {
            state = DocumentPreviewState.STORED;
        } else {
            state = DocumentPreviewState.PENDING;
        }

        view.render(documentData.getFileName() + " (" + getFormattedDocumentSize(documentData.getSize()) + ")");
    }

    @Override
    public String getDocumentLink() {
        return documentData.getLink();
    }

    @Override
    public String getDocumentName() {
        return documentData.getFileName();
    }

    public void setState(DocumentPreviewState state) {
        this.state = state;

        initState();
    }

    private void initState() {
        Collection<DocumentPreviewStateAction> actions;

        if (enabled && actionsHandler != null) {
            actions = actionsHandler.getCurrentStateActions();
        } else {
            actions = Collections.emptyList();
        }
        view.setState(state, actions);
    }

    public DocumentData getDocumentData() {
        return documentData;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled) {
            view.clearActions();
        } else {
            initState();
        }
    }

    public void setStateHandler(DocumentPreviewStateActionsHandler handler) {
        this.actionsHandler = handler;
        actionsHandler.setStateChangeListener(this::setState);
        initState();
    }
}
