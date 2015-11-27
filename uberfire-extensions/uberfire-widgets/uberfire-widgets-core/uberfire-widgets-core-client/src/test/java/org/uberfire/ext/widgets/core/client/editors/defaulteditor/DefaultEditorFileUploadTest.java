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

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm.SubmitCompleteHandler;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm.SubmitHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultEditorFileUploadTest {

    @InjectMocks
    private DefaultEditorFileUploadBaseTestWrapper editor;

    @GwtMock
    private Form form;

    @Mock
    private FileUploadFormEncoder formEncoder;

    @Mock
    private Command successCallback;

    @Mock
    private Command errorCallback;

    @Before
    public void setup() {
        editor.forceInitForm();
    }

    @Test
    public void formCharsetAdded() {
        verify( formEncoder,
                times( 1 ) ).addUtf8Charset( form );
    }

    @Test
    public void formSubmitHandlersSet() {
        verify( form,
                never() ).addSubmitHandler( any( SubmitHandler.class ) );
        verify( form,
                times( 1 ) ).addSubmitCompleteHandler( any( SubmitCompleteHandler.class ) );
    }

    @Test
    public void formSubmitValidState() {
        editor.setValid( true );
        editor.upload( successCallback,
                       errorCallback );
        verify( form,
                times( 1 ) ).submit();
    }

    @Test
    public void formSubmitInvalidState() {
        editor.setValid( false );
        editor.upload( successCallback,
                       errorCallback );
        verify( form,
                never() ).submit();
    }

}
