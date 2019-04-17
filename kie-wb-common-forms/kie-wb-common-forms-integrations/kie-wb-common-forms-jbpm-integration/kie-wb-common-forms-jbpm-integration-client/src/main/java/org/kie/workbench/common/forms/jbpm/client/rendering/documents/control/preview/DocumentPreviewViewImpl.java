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

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Document;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DocumentPreviewViewImpl implements DocumentPreviewView,
                                                IsElement {

    private static final String DISABLED_ANCHOR_STYLE = "disabled";

    private static final String PENDING_STATE_ICON = "fa fa-hourglass-half";
    private static final String UPLOADING_STATE_ICON = "spinner spinner-xs spinner-inline";
    private static final String UPLOADED_STATE_ICON = "pficon-ok";
    private static final String UPLOAD_ERROR_STATE_ICON = "pficon-error-circle-o";
    private static final String STORED_STATE_ICON = "fa fa-file-text-o";

    private static final String CONTENT = "kie-wb-common-forms-docs-upload-content";

    @Inject
    private Document doc;

    @Inject
    private Elemental2DomUtil utils;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement state;

    @Inject
    @DataField
    private HTMLAnchorElement document;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement actions;

    private Presenter presenter;

    @Override
    public void render(String text) {
        document.textContent = text;
    }

    @Override
    public void clearActions() {
        utils.removeAllElementChildren(actions);
    }

    @Override
    public void setState(DocumentPreviewState previewState, Collection<DocumentPreviewStateAction> previewActions) {

        state.className = resolveStateStyle(previewState);

        if (previewState.equals(DocumentPreviewState.STORED)) {
            document.classList.remove(DISABLED_ANCHOR_STYLE);
            document.href = presenter.getDocumentLink();
            document.download = presenter.getDocumentName();
        } else {
            document.classList.add(DISABLED_ANCHOR_STYLE);
            document.removeAttribute("href");
            document.removeAttribute("download");
        }

        clearActions();

        previewActions.forEach(action -> {
            if (actions.lastChild != null) {
                HTMLElement separator = (HTMLElement) doc.createElement("span");
                separator.textContent = "|";
                separator.className = CONTENT;
                actions.appendChild(separator);
            }

            HTMLAnchorElement anchor = (HTMLAnchorElement) doc.createElement("a");
            anchor.textContent = action.getLabel();
            anchor.onclick = event -> {
                action.execute();
                return null;
            };

            actions.appendChild(anchor);
        });
    }

    private String resolveStateStyle(DocumentPreviewState state) {
        switch (state) {
            case STORED:
                return STORED_STATE_ICON;
            case UPLOADING:
                return UPLOADING_STATE_ICON;
            case UPLOADED:
                return UPLOADED_STATE_ICON;
            case ERROR:
                return UPLOAD_ERROR_STATE_ICON;
            default:
                return PENDING_STATE_ICON;
        }
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }
}
