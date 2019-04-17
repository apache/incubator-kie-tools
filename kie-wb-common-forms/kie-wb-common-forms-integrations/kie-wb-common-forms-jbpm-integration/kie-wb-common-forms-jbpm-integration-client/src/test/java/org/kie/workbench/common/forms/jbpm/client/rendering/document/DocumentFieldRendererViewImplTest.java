/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.rendering.document;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Form;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentFieldRendererViewImplTest {

    @GwtMock
    protected Form documentForm;

    @InjectMocks
    private DocumentFieldRendererViewImpl documentFieldRendererView;

    @Test
    public void testInitDocumentFieldActionWithRelativeURL() {
        documentFieldRendererView.initForm();
        final ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

        verify(documentForm).setAction(actionCaptor.capture());

        assertFalse(actionCaptor.getValue().startsWith("/"));
        assertEquals(DocumentFieldRendererViewImpl.UPLOAD_FILE_SERVLET_URL_PATTERN, actionCaptor.getValue());
    }

}
