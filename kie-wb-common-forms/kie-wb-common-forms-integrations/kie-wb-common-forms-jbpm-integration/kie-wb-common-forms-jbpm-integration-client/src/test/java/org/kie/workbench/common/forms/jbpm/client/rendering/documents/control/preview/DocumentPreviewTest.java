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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentPreviewTest {

    private final String DOC = "doc";

    @Mock
    private DocumentPreviewStateAction action;

    @Mock
    private DocumentPreviewStateActionsHandler handler;

    @Mock
    private DocumentPreviewView view;

    @Mock
    private TranslationService translationService;

    private DocumentPreview preview;

    @Before
    public void init() {
        when(handler.getCurrentStateActions()).thenReturn(Collections.singletonList(action));

        preview = new DocumentPreview(view, translationService);

        verify(view).init(eq(preview));

        preview.getElement();

        verify(view).getElement();
    }

    @Test
    public void testInitStored() {
        testInit(DocumentStatus.STORED);
    }

    @Test
    public void testInitNew() {
        testInit(DocumentStatus.NEW);
    }

    @Test
    public void testSetStateHandler() {
        testInitNew();

        preview.setStateHandler(handler);

        verify(handler).getCurrentStateActions();
        verify(handler).setStateChangeListener(any());

        ArgumentCaptor<Collection> actionsCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(view).setState(any(), actionsCaptor.capture());

        Assertions.assertThat(actionsCaptor.getValue())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testEnable() {
        testSetStateHandler();
        preview.setEnabled(true);
        verify(handler, times(2)).getCurrentStateActions();
        verify(view, times(2)).setState(any(), anyList());
    }

    @Test
    public void testDisable() {
        testSetStateHandler();
        preview.setEnabled(false);
        verify(view).clearActions();
        verify(handler, times(1)).getCurrentStateActions();
        verify(view, times(1)).setState(any(), anyList());
    }

    @Test
    public void testNotifyStateChange() {
        testSetStateHandler();

        preview.setState(DocumentPreviewState.UPLOADED);

        verify(handler, times(2)).getCurrentStateActions();
        verify(view, times(2)).setState(any(), anyList());
    }

    @Test
    public void setSetStateDisabled() {
        testDisable();

        preview.setState(DocumentPreviewState.STORED);

        verify(handler, times(1)).getCurrentStateActions();
        verify(view, times(2)).setState(any(), anyList());
    }

    private void testInit(DocumentStatus status) {
        DocumentData data = spy(new DocumentData(DOC, DOC, 1024, DOC, System.currentTimeMillis()));

        data.setStatus(status);

        preview.init(data);

        verify(view).render(any());

        assertSame(data, preview.getDocumentData());

        preview.getDocumentName();
        verify(data, times(2)).getFileName();

        preview.getDocumentLink();
        verify(data).getLink();
    }
}
