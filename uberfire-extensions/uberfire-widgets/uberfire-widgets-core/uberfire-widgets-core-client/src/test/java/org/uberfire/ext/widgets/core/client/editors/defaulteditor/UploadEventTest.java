/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class UploadEventTest {

    @Mock
    AbstractForm.SubmitCompleteEvent event;

    @Test
    public void testSuccessStatus() {
        when(event.getResults()).thenReturn("OK");

        assertTrue(DefaultEditorFileUploadBase.isUploadSuccessful(event));
    }

    @Test
    public void testFailedStatus() {
        when(event.getResults()).thenReturn("FAIL");

        assertFalse(DefaultEditorFileUploadBase.isUploadSuccessful(event));
    }

    @Test
    public void testUnknownStatus() {
        when(event.getResults()).thenReturn(null);

        assertFalse(DefaultEditorFileUploadBase.isUploadSuccessful(event));
    }
}
