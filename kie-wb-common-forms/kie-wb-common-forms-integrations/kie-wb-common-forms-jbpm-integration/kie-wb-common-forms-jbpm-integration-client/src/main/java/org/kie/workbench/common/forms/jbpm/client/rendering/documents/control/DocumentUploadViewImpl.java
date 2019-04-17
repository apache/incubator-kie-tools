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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.dom.DragEvent;
import elemental2.dom.Event;
import elemental2.dom.EventTarget;
import elemental2.dom.File;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js.Document;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js.Documents;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreview;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;

@Templated
public class DocumentUploadViewImpl extends Composite implements DocumentUploadView {

    private static final String DRAG_OVER_STYLE = "kie-wb-common-forms-docs-upload-dragover";

    @Inject
    @DataField
    private HTMLDivElement dropRegion;

    @Inject
    @DataField
    private HTMLDivElement emptyState;

    @Inject
    @DataField
    private HTMLDivElement dropHere;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement footer;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement maxElements;

    @Inject
    @DataField
    private HTMLInputElement fileInput;

    @Inject
    @DataField
    private HTMLButtonElement upload;

    @Inject
    @DataField
    private HTMLAnchorElement dragFilesAnchor;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    private Documents documents;

    private Presenter presenter;

    @PostConstruct
    public void init() {
        documents = Documents.get()
                .bind(dropRegion)
                .bind(fileInput)
                .onDrop(this::doUpload);
        fileInput.hidden = true;
        dropRegion.addEventListener("dragover", this::onDragOver);
        dropRegion.addEventListener("dragend", this::onDragEnd);
        dropRegion.addEventListener("dragleave", this::onDragEnd);
        dropRegion.addEventListener("drop", this::onDrop);
        dropRegion.removeChild(dropHere);
    }

    private void onDragOver(Event event) {
        if (!dropRegion.contains(dropHere)) {
            event.stopPropagation();
            event.preventDefault();
            elemental2DomUtil.removeAllElementChildren(dropRegion);
            dropRegion.classList.add(DRAG_OVER_STYLE);
            dropRegion.appendChild(dropHere);
        }
    }

    private void onDragEnd(Event event) {
        if (dropRegion.contains(dropHere)) {

            EventTarget relatedTarget = ((DragEvent)event).relatedTarget;

            if(relatedTarget.equals(dropHere) || dropHere.contains((Node) relatedTarget)) {
                return;
            }

            restore();
        }
    }

    private void onDrop(Event event) {
        restore();
    }

    private void restore() {
        dropRegion.removeChild(dropHere);

        Collection<DocumentPreview> currentPreviews = presenter.getCurrentPreviews();

        if (!currentPreviews.isEmpty()) {
            currentPreviews.forEach(this::addDocument);
        } else {
            dropRegion.appendChild(emptyState);
        }
        dropRegion.classList.remove(DRAG_OVER_STYLE);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void doUpload(final Document document, final File file) {
        presenter.doUpload(document, file);
    }

    @Override
    public void clear() {
        elemental2DomUtil.removeAllElementChildren(dropRegion);
        dropRegion.appendChild(emptyState);
    }

    @Override
    public void addDocument(DocumentPreview preview) {
        if (dropRegion.contains(emptyState)) {
            dropRegion.removeChild(emptyState);
        }
        dropRegion.appendChild(preview.getElement());
    }

    @Override
    public void removeDocument(DocumentPreview preview) {
        dropRegion.removeChild(preview.getElement());
        if (dropRegion.childElementCount == 0) {
            dropRegion.appendChild(emptyState);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        footer.hidden = !enabled;
    }

    @Override
    public void setMaxDocuments(String text) {
        if (text == null) {
            text = "";
        }
        maxElements.textContent = text;
    }

    @EventHandler("upload")
    public void onUpload(ClickEvent event) {
        openBrowse();
        upload.blur();
    }

    @EventHandler("dragFilesAnchor")
    public void onEmptyUpload(ClickEvent event) {
        openBrowse();
    }

    private void openBrowse() {
        fileInput.value = null;
        fileInput.click();
    }

    @Override
    public HasValue<List<DocumentData>> wrapped() {
        return presenter;
    }
}
