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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class FileUploadPopupViewTest extends AbstractScenarioPopupViewTest {

    @Mock
    private InputElement fileMock;

    @Mock
    private ParagraphElement uploadWarningMock;

    @Mock
    private InputElement fileTextMock;

    @Mock
    private JavaScriptObject filesMock;

    @Mock
    private JavaScriptObject castMock;

    @Mock
    private Command okButtonCommandMock;

    @Before
    public void setup() {
        super.commonSetup();
        when(fileMock.getPropertyJSO(anyString())).thenReturn(filesMock);
        when(fileMock.cast()).thenReturn(castMock);
        popupView = spy(new FileUploadPopupView() {
            {
                this.mainTitle = mainTitleMock;
                this.uploadWarning = uploadWarningMock;
                this.cancelButton = cancelButtonMock;
                this.okButton = okButtonMock;
                this.modal = modalMock;
                this.translationService = translationServiceMock;
                this.file = fileMock;
                this.fileText = fileTextMock;
            }
        });
    }

    @Test
    public void show() {
        popupView.show("mainText",
                       "okButtonText",
                       okButtonCommandMock);
        verify(fileTextMock, times(1)).setValue(eq(""));
        verify(okButtonMock, times(1)).setEnabled(eq(false));
    }

    @Test
    public void showWithUploadWarning() {
        ((FileUploadPopupView) popupView).show("mainText",
                                               "uploadWarning",
                                               "okButtonText",
                                               okButtonCommandMock);
        verify(uploadWarningMock, times(1)).setInnerText(eq("uploadWarning"));
        verify(((FileUploadPopupView) popupView), times(1)).show(eq("mainText"),
                                                                                      eq("okButtonText"),
                                                                                      eq(okButtonCommandMock));
    }

    @Test
    public void onChooseButtonClickEvent() {
        ClickEvent clickEventMock = mock(ClickEvent.class);
        ((FileUploadPopupView) popupView).onChooseButtonClickEvent(clickEventMock);
        verify(fileMock, times(1)).click();
    }

    @Test
    public void onFileChangeEvent() {
        when(fileMock.getValue()).thenReturn(VALUE);
        ChangeEvent changeEventMock = mock(ChangeEvent.class);
        ((FileUploadPopupView) popupView).onFileChangeEvent(changeEventMock);
        verify(fileMock, times(1)).getValue();
        verify(fileTextMock, times(1)).setValue(eq(VALUE));
    }
}