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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.File;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js.Document;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreview;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewState;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewStateAction;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.handlers.DocumentPreviewStateActionsHandlerImpl;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.upload.DocumentUploadManager;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DocumentUpload implements DocumentUploadView.Presenter,
                                       IsWidget {

    private final ManagedInstance<DocumentPreview> instance;
    private final DocumentUploadManager uploader;
    private final DocumentUploadView view;
    private final TranslationService translationService;

    private List<DocumentPreview> previews = new ArrayList<>();

    private boolean enabled = true;
    private int maxDocuments = -1;

    @Inject
    public DocumentUpload(final DocumentUploadManager uploader, final DocumentUploadView view, final ManagedInstance<DocumentPreview> instance, final TranslationService translationService) {
        this.uploader = uploader;
        this.view = view;
        this.instance = instance;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        view.setPresenter(this);
    }

    @Override
    public Collection<DocumentPreview> getCurrentPreviews() {
        return previews;
    }

    @Override
    public void doUpload(final Document document, File file) {
        if (!enabled) {
            return;
        }

        DocumentData documentData = new DocumentData(document.getId(), document.getName(), document.getSize(), document.getUrl(), (long) document.getLastModified());

        DocumentPreview preview = render(documentData);

        DocumentPreviewStateActionsHandlerImpl handler = new DocumentPreviewStateActionsHandlerImpl(DocumentPreviewState.PENDING);

        DocumentPreviewStateAction abortAction = new DocumentPreviewStateAction(translationService.getTranslation(Constants.DocumentUploadViewImplAbort), () -> uploader.remove(document.getId(), () -> doRemove(preview)));

        handler.addStateActions(DocumentPreviewState.UPLOADING, Collections.singletonList(abortAction));

        DocumentPreviewStateAction removeAction = new DocumentPreviewStateAction(translationService.getTranslation(Constants.DocumentUploadViewImplRemove), () -> uploader.remove(document.getId(), () -> doRemove(preview)));

        handler.addStateActions(DocumentPreviewState.PENDING, Collections.singletonList(removeAction));
        handler.addStateActions(DocumentPreviewState.UPLOADED, Collections.singletonList(removeAction));

        final Command startUploadCallback = () -> handler.notifyStateChange(DocumentPreviewState.UPLOADING);
        final ParameterizedCommand<Boolean> onFinishUpload = success -> {
            if (success) {
                handler.notifyStateChange(DocumentPreviewState.UPLOADED);
            } else {
                handler.notifyStateChange(DocumentPreviewState.ERROR);
            }
        };

        DocumentPreviewStateAction retryAction = new DocumentPreviewStateAction(translationService.getTranslation(Constants.DocumentUploadViewImplRetry), () -> {
            uploader.remove(document.getId(), () -> uploader.upload(document.getId(), file, startUploadCallback, onFinishUpload));
        });

        handler.addStateActions(DocumentPreviewState.ERROR, Arrays.asList(removeAction, retryAction));

        preview.setStateHandler(handler);

        uploader.upload(document.getId(), file, startUploadCallback, onFinishUpload);

        ValueChangeEvent.fire(DocumentUpload.this, getValue());
    }

    protected void doRemove(DocumentPreview preview) {
        previews.remove(preview);
        view.removeDocument(preview);
        instance.destroy(preview);

        ValueChangeEvent.fire(DocumentUpload.this, getValue());
    }

    @Override
    public List<DocumentData> getValue() {
        return previews.stream().map(DocumentPreview::getDocumentData).collect(Collectors.toList());
    }

    @Override
    public void setValue(List<DocumentData> value) {
        setValue(value, false);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        previews.forEach(preview -> preview.setEnabled(enabled));
        view.setEnabled(enabled);
    }

    public void setMaxDocuments(int maxDocuments) {
        this.maxDocuments = maxDocuments;
        if (maxDocuments > 0) {
            view.setMaxDocuments(translationService.format(Constants.DocumentUploadViewImplMaxDocuments, maxDocuments));
        } else {
            view.setMaxDocuments("");
        }
    }

    @Override
    public void setValue(List<DocumentData> value, boolean fireEvents) {

        if (value == null) {
            value = new ArrayList<>();
        }

        if (getValue().containsAll(value)) {
            return;
        }

        this.clear();

        value.forEach(documentData -> {
            DocumentPreview preview = render(documentData);

            DocumentPreviewStateActionsHandlerImpl handler = new DocumentPreviewStateActionsHandlerImpl(DocumentPreviewState.STORED);

            DocumentPreviewStateAction action = new DocumentPreviewStateAction(translationService.getTranslation(Constants.DocumentUploadViewImplRemove), () -> doRemove(preview));

            handler.addStateActions(DocumentPreviewState.STORED, Collections.singletonList(action));
            preview.setStateHandler(handler);
        });

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    private DocumentPreview render(DocumentData documentData) {
        DocumentPreview preview = instance.get();
        preview.init(documentData);
        preview.setEnabled(enabled);
        previews.add(preview);
        view.addDocument(preview);
        return preview;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<DocumentData>> valueChangeHandler) {
        return view.asWidget().addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PreDestroy
    private void clear() {
        view.clear();
        previews.clear();
        instance.destroyAll();
    }
}
