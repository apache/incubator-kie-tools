/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.FormModelerContentError;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorPresenterModelErrorsTest extends FormEditorPresenterAbstractTest {

    private final String MODEL_ERROR_MESSAGE = "model error";
    private final String UNEXPECTED_ERROR_MESSAGE = "unexpected error";
    private final String DATA_OBJECT = "data object";

    enum ErrorType {
        NONE,
        MODEL,
        UNEXPECTED
    }

    protected ErrorType errorType = ErrorType.NONE;

    @Test
    public void testNoErrors() {
        loadContent();

        verify(presenter.busyIndicatorView).hideBusyIndicator();

        verify(editorHelper).initHelper(content);

        verify(view).init(presenter);
        verify(errorMessageDisplayer, never()).show(anyString(), anyString(), any());

        verify(layoutEditorMock).clear();
        verify(layoutEditorMock).init(anyString(), anyString(), anyString(), any());
        verify(layoutEditorMock).loadLayout(any());


        verify(view).setupLayoutEditor(layoutEditorMock);

        verify(modelChangesDisplayer).show(any(), any());
    }

    @Test
    public void testModelError() {
        errorType = ErrorType.MODEL;

        loadContent();

        verify(presenter.busyIndicatorView).hideBusyIndicator();

        verify(editorHelper).initHelper(content);

        verify(view).init(presenter);
        verify(errorMessageDisplayer).show(eq(MODEL_ERROR_MESSAGE), eq(MODEL_ERROR_MESSAGE), eq(DATA_OBJECT), any());

        verify(layoutEditorMock).clear();
        verify(layoutEditorMock).init(anyString(), anyString(), anyString(), any());
        verify(layoutEditorMock).loadLayout(any());


        verify(view).setupLayoutEditor(layoutEditorMock);

        verify(modelChangesDisplayer).show(any(), any());
    }

    @Test
    public void testUnexpected() {
        errorType = ErrorType.UNEXPECTED;

        loadContent();

        verify(presenter.busyIndicatorView).hideBusyIndicator();

        verify(editorHelper).initHelper(content);

        verify(view).init(presenter);
        verify(errorMessageDisplayer).show(eq(UNEXPECTED_ERROR_MESSAGE), eq(UNEXPECTED_ERROR_MESSAGE), eq(null), any());

        verify(layoutEditorMock, never()).clear();
        verify(layoutEditorMock, never()).init(anyString(), anyString(), anyString(), any());
        verify(layoutEditorMock, never()).loadLayout(any());


        verify(view, never()).setupLayoutEditor(layoutEditorMock);

        verify(modelChangesDisplayer, never()).show(any(), any());
    }

    @Override
    public FormModelerContent serviceLoad() {
        FormModelerContent content = super.serviceLoad();
        if (errorType.equals(ErrorType.MODEL)) {
            content.setError(new FormModelerContentError(MODEL_ERROR_MESSAGE, MODEL_ERROR_MESSAGE, DATA_OBJECT));
        } else if (errorType.equals(ErrorType.UNEXPECTED)) {
            content.setDefinition(null);
            content.setError(new FormModelerContentError(UNEXPECTED_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE, null));
        }
        return content;
    }
}
