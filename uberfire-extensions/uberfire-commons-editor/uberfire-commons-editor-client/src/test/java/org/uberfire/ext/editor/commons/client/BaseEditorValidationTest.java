/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BaseEditorValidationTest {

    @Mock
    protected BaseEditorView baseView;

    private BaseEditor baseEditor;

    @Test
    public void showAndHide() throws Exception {
        baseEditor = new BaseEditor() {
            @Override
            protected void loadContent() {

            }

            @Override
            protected void onValidate(final Command finished) {
                finished.execute();
            }
        };
        baseEditor.baseView = baseView;

        baseEditor.getValidateCommand().execute();

        verify(baseView).showBusyIndicator("Validating");
        verify(baseView).hideBusyIndicator();
    }

    @Test
    public void preventRerun() throws Exception {
        baseEditor = new BaseEditor() {
            @Override
            protected void loadContent() {

            }

            @Override
            protected void onValidate(final Command finished) {
                //finished.execute(); Let's not run this.
            }
        };
        baseEditor.baseView = baseView;

        baseEditor.getValidateCommand().execute();

        verify(baseView).showBusyIndicator("Validating");
        verify(baseView, never()).hideBusyIndicator();

        reset(baseView);

        baseEditor.getValidateCommand().execute();

        verify(baseView, never()).showBusyIndicator("Validating");
        verify(baseView, never()).hideBusyIndicator();
    }
}