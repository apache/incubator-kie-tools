/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;

@WithClassesToStub(FileUploadFormEncoder.class)
@RunWith(GwtMockitoTestRunner.class)
public class AttachmentFileWidgetValidationTest {

    @Mock
    private FileUpload mock;
    @Mock
    private Command errorCallback;
    @Mock
    private Element element;

    private ValidationTestAttachmentFileWidget widget;

    @Before
    public void setup() {
        String[] validFileExtensions = {"ext"};
        when(mock.getElement()).thenReturn(element);
        when(element.cast()).thenReturn(mock(InputElement.class));
        ValidationTestAttachmentFileWidget.setFileUploadMock(mock);
        widget = new ValidationTestAttachmentFileWidget(validFileExtensions);
        widget.setCallbacks(null, errorCallback);
    }

    @Test
    public void validExtension() {
        when(mock.getFilename()).thenReturn("dummy.ext");
        assertTrue(widget.isValid());
        verify(errorCallback, never()).execute();
    }

    @Test
    public void invalidExtension1() {
        when(mock.getFilename()).thenReturn("dummy.inv");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void invalidExtension2() {
        when(mock.getFilename()).thenReturn("dummy.text");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void missingExtension1() {
        when(mock.getFilename()).thenReturn("dummy");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void missingExtension2() {
        when(mock.getFilename()).thenReturn("dummyext");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void missingExtension3() {
        when(mock.getFilename()).thenReturn("dummy.");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void missingFile1() {
        when(mock.getFilename()).thenReturn("");
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void missingFile2() {
        when(mock.getFilename()).thenReturn(null);
        assertFalse(widget.isValid());
        verify(errorCallback, times(1)).execute();
    }

    @Test
    public void submitCallsIsValid() {
        widget = spy(widget);
        widget.submit(mock(Path.class), "", null, errorCallback);
        widget.submit(mock(Path.class), "", "", null, errorCallback);
        verify(widget, times(2)).isValid();
    }

    public static class ValidationTestAttachmentFileWidget extends AttachmentFileWidget {

        private static FileUpload UPLOAD_MOCK;

        public static void setFileUploadMock(FileUpload mock) {
            UPLOAD_MOCK = mock;
        }

        public ValidationTestAttachmentFileWidget(String[] validFileExtensions) {
            super(validFileExtensions);
        }

        @Override
        FileUpload createUploadWidget(boolean addFileUpload) {
            return UPLOAD_MOCK;
        }
    }
}
