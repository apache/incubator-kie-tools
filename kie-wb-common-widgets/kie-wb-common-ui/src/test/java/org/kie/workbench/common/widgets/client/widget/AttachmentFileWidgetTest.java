/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm.SubmitCompleteHandler;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm.SubmitHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AttachmentFileWidgetTest {

    @Spy
    @InjectMocks
    private AttachmentFileWidgetTestWrapper editor;

    @GwtMock
    private Form form;

    @Mock
    private FileUpload fileUpload;

    @Mock
    private FileUploadFormEncoder formEncoder;

    @Mock
    private Command successCallback;

    @Mock
    private Command errorCallback;

    @Mock
    private Path path;

    @Mock
    Form.SubmitCompleteEvent event;

    @Captor
    ArgumentCaptor<SubmitCompleteHandler> submitCompleteCaptor;

    @Before
    public void setup() {
        editor.forceInitForm(false);
    }

    @Test
    public void formCharsetAdded() {
        verify(formEncoder,
               times(1)).addUtf8Charset(form);
    }

    @Test
    public void formSubmitHandlersSet() {
        verify(form,
               never()).addSubmitHandler(any(SubmitHandler.class));
        verify(form,
               times(1)).addSubmitCompleteHandler(any(SubmitCompleteHandler.class));
    }

    @Test
    public void formSubmitValidState() {
        editor.setValid(true);
        editor.submit(path,
                      "filename",
                      "targetUrl",
                      successCallback,
                      errorCallback);
        verify(form,
               times(1)).submit();
    }

    @Test
    public void formSubmitInvalidState() {
        editor.setValid(false);
        editor.submit(path,
                      "filename",
                      "targetUrl",
                      successCallback,
                      errorCallback);
        verify(form,
               never()).submit();
    }

    @Test
    public void testSubmitCompleteInvalidXlsContent() throws Exception {
        when(event.getResults()).thenReturn("DecisionTableParseException");
        editor.setValid(true);
        editor.submit(path,
                      "filename",
                      "targetUrl",
                      successCallback,
                      errorCallback);
        verify(form).addSubmitCompleteHandler(submitCompleteCaptor.capture());
        submitCompleteCaptor.getValue().onSubmitComplete(event);
        assertEquals(1,
                     editor.getShownMessages().size());
        assertEquals(CommonConstants.INSTANCE.UploadGenericError(),
                     editor.getShownMessages().get(0));
    }

    @Test
    public void testSelectedFileName() {
        doReturn(fileUpload).when(editor).createUploadWidget(anyBoolean());
        editor.setup(true);

        when(fileUpload.getFilename()).thenReturn("abcd");

        assertEquals("abcd", editor.getFilenameSelectedToUpload());
    }
}
