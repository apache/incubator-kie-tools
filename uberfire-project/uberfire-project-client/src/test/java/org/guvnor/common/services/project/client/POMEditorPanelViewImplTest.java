/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class POMEditorPanelViewImplTest {

    @Mock
    POMEditorPanelView.Presenter presenter;

    @GwtMock
    TextBox pomName;

    @GwtMock
    TextArea pomDescription;

    @GwtMock
    HelpBlock pomNameHelp;

    @GwtMock
    FormGroup pomNameGroup;

    POMEditorPanelViewImpl view;

    @Before
    public void setup() {
        view = new POMEditorPanelViewImpl();
        view.setPresenter(presenter);
        view.pomNameTextBox = pomName;
        view.pomDescriptionTextArea = pomDescription;
        view.pomNameHelpBlock = pomNameHelp;
        view.pomNameGroup = pomNameGroup;
    }

    @Test
    public void testNameChangeHanlder() {
        when(pomName.getText()).thenReturn("name");

        view.onNameChange(mock(KeyUpEvent.class));
        verify(presenter,
               times(1)).onNameChange("name");
    }

    @Test
    public void testDescriptionChangeHandler() {
        when(pomDescription.getText()).thenReturn("descr");

        view.onDescriptionChange(mock(ValueChangeEvent.class));
        verify(presenter,
               times(1)).onDescriptionChange("descr");
    }

    @Test
    public void testValidName() {
        view.setValidName(true);

        verify(pomNameGroup,
               times(1)).setValidationState(ValidationState.NONE);
        verify(pomNameHelp,
               times(1)).setText("");

        verify(pomNameGroup,
               never()).setValidationState(ValidationState.ERROR);
    }

    @Test
    public void testInvalidName() {
        view.setValidName(false);

        verify(pomNameGroup,
               times(1)).setValidationState(ValidationState.ERROR);
        verify(pomNameHelp,
               times(1)).setText(ProjectResources.CONSTANTS.invalidName());

        verify(pomNameGroup,
               never()).setValidationState(ValidationState.NONE);
    }
}
