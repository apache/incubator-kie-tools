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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.File;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js.Document;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreview;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewState;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewStateAction;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreviewStateActionsHandler;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.upload.DocumentUploadManager;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentUploadTest {

    private final String DOC_1 = "doc1";
    private final String DOC_2 = "doc2";
    private final String DOC_3 = "doc3";

    @Mock
    private ManagedInstance<DocumentPreview> instance;

    @Mock
    private DocumentUploadManager uploader;

    @Mock
    private DocumentUploadView view;

    @Mock
    private Widget widget;

    @Mock
    private TranslationService translationService;

    private DocumentUpload documentUpload;

    @Before
    public void init() {
        when(instance.get()).then((Answer<DocumentPreview>) invocationOnMock -> {
            final DocumentPreview preview = mock(DocumentPreview.class);
            doAnswer(invocationOnMock1 -> {
                DocumentPreviewStateActionsHandler handler = (DocumentPreviewStateActionsHandler) invocationOnMock1.getArguments()[0];
                handler.setStateChangeListener(preview::setState);
                return null;
            }).when(preview).setStateHandler(any());
            return preview;
        });

        when(view.asWidget()).thenReturn(widget);

        documentUpload = new DocumentUpload(uploader, view, instance, translationService);

        documentUpload.init();

        verify(view).setPresenter(eq(documentUpload));
    }

    @Test
    public void testSetValues() {
        DocumentData doc1 = new DocumentData(DOC_1, DOC_1, 1024, DOC_1, System.currentTimeMillis());
        DocumentData doc2 = new DocumentData(DOC_2, DOC_2, 1024, DOC_2, System.currentTimeMillis());
        DocumentData doc3 = new DocumentData(DOC_3, DOC_3, 1024, DOC_3, System.currentTimeMillis());

        documentUpload.setValue(Arrays.asList(doc1, doc2, doc3), false);

        verify(view).clear();
        verify(instance).destroyAll();

        verify(instance, times(3)).get();
        verify(view, times(3)).addDocument(any());

        List<DocumentPreview> previews = (List<DocumentPreview>) documentUpload.getCurrentPreviews();

        Assertions.assertThat(previews)
                .isNotNull()
                .hasSize(3);

        verifyPreview(previews.get(0), doc1, true);
        verifyPreview(previews.get(1), doc2, true);
        verifyPreview(previews.get(2), doc3, true);

        documentUpload.getValue();

        verify(previews.get(0)).getDocumentData();
        verify(previews.get(1)).getDocumentData();
        verify(previews.get(2)).getDocumentData();
    }

    @Test
    public void testRemove() {
        testSetValues();

        List<DocumentPreview> previews = (List<DocumentPreview>) documentUpload.getCurrentPreviews();

        documentUpload.doRemove(previews.get(0));

        verify(instance).destroy(any());
        verify(view).removeDocument(any());

        previews = (List<DocumentPreview>) documentUpload.getCurrentPreviews();

        Assertions.assertThat(previews)
                .hasSize(2);
    }

    @Test
    public void testDrop() {
        Document doc = mock(Document.class);

        when(doc.getId()).thenReturn(DOC_1);
        when(doc.getName()).thenReturn(DOC_1);
        when(doc.getSize()).thenReturn(1024);
        when(doc.getLastModified()).thenReturn((double) System.currentTimeMillis());

        File file = mock(File.class);

        documentUpload.doUpload(doc, file);

        verify(instance).get();
        verify(view).addDocument(any());

        List<DocumentPreview> previews = (List<DocumentPreview>) documentUpload.getCurrentPreviews();

        Assertions.assertThat(previews)
                .isNotNull()
                .hasSize(1);

        ArgumentCaptor<DocumentData> documentDataArgumentCaptor = ArgumentCaptor.forClass(DocumentData.class);

        DocumentPreview preview = previews.get(0);

        verify(preview).init(documentDataArgumentCaptor.capture());

        DocumentData documentData = documentDataArgumentCaptor.getValue();

        Assertions.assertThat(documentData)
                .isNotNull()
                .hasFieldOrPropertyWithValue("contentId", DOC_1)
                .hasFieldOrPropertyWithValue("fileName", DOC_1)
                .hasFieldOrPropertyWithValue("size", Long.valueOf(1024));

        ArgumentCaptor<DocumentPreviewStateActionsHandler> handlerCaptor = ArgumentCaptor.forClass(DocumentPreviewStateActionsHandler.class);

        verify(preview).setStateHandler(handlerCaptor.capture());

        DocumentPreviewStateActionsHandler handler = handlerCaptor.getValue();

        ArgumentCaptor<Command> startUploadCaptor = ArgumentCaptor.forClass(Command.class);

        ArgumentCaptor<ParameterizedCommand> uploadResultCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

        verify(uploader).upload(eq(DOC_1), any(), startUploadCaptor.capture(), uploadResultCaptor.capture());

        startUploadCaptor.getValue().execute();

        verify(preview).setState(DocumentPreviewState.UPLOADING);

        uploadResultCaptor.getValue().execute(false);
        verify(preview).setState(DocumentPreviewState.ERROR);

        DocumentPreviewStateAction action = ((List<DocumentPreviewStateAction>) handler.getCurrentStateActions()).get(1);
        action.execute();
        verify(uploader).remove(eq(DOC_1), any());

        uploadResultCaptor.getValue().execute(true);
        verify(preview).setState(DocumentPreviewState.UPLOADED);
    }

    @Test
    public void testNotifyReadOnly() {
        testSetValues();

        documentUpload.setEnabled(false);

        verify(view).setEnabled(false);

        List<DocumentPreview> previews = (List<DocumentPreview>) documentUpload.getCurrentPreviews();

        verify(previews.get(0)).setEnabled(false);
        verify(previews.get(1)).setEnabled(false);
        verify(previews.get(2)).setEnabled(false);
    }

    @Test
    public void testSetMaxElements() {
        documentUpload.setMaxDocuments(5);
        verify(translationService).format(eq(Constants.DocumentUploadViewImplMaxDocuments), eq(5));
        verify(view).setMaxDocuments(anyString());

        documentUpload.setMaxDocuments(0);
        verify(view).setMaxDocuments(eq(""));
    }

    @Test
    public void verifyGwtMethods() {
        documentUpload.asWidget();
        verify(view).asWidget();

        documentUpload.addValueChangeHandler(mock(ValueChangeHandler.class));
        verify(view, times(2)).asWidget();
        verify(widget).addHandler(any(), any());

        documentUpload.fireEvent(mock(GwtEvent.class));
        verify(view, times(3)).asWidget();
        verify(widget).fireEvent(any());
    }

    private void verifyPreview(DocumentPreview preview, DocumentData documentData, boolean enabled) {
        verify(preview).init(documentData);
        verify(preview).setEnabled(enabled);
        verify(preview).setStateHandler(any());
    }
}
