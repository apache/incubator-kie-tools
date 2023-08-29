/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchEntry;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssigneeLiveSearchEntryCreationEditorTest {

    @Mock
    private AssigneeLiveSearchEntryCreationEditorView view;

    @Mock
    private TranslationService translationService;

    @Mock
    private ParameterizedCommand<LiveSearchEntry<String>> okCommand;

    @Mock
    private Command cancelCommand;

    @Mock
    private ParameterizedCommand<String> customEntryCommand;

    private AssigneeLiveSearchEntryCreationEditor editor;

    @Before
    public void init() {
        editor = new AssigneeLiveSearchEntryCreationEditor(view, translationService);

        editor.init(okCommand, cancelCommand);

        editor.setCustomEntryCommand(customEntryCommand);
    }

    @Test
    public void testGeneral() {

        verify(view).init(editor);

        editor.getElement();
        verify(view).getElement();

        editor.getFieldLabel();
        verify(translationService).getTranslation(StunnerBPMNConstants.ASSIGNEE_LABEL);

        editor.clear();
        verify(view).clear();
    }

    @Test
    public void testOnCancel() {

        editor.onCancel();
        verify(view).clear();
        verify(cancelCommand).execute();
    }

    @Test
    public void testAcceptSuccess() {

        when(view.getValue()).thenReturn("value");

        editor.onAccept();
        verify(view).getValue();
        verify(view).clearErrors();
        verify(translationService, never()).getTranslation(StunnerBPMNConstants.ASSIGNEE_CANNOT_BE_EMPTY);
        verify(view, never()).showError(anyString());
        verify(customEntryCommand).execute(eq("value"));
        verify(okCommand).execute(any());
    }

    @Test
    public void testAcceptFailure() {

        editor.onAccept();
        verify(view).getValue();
        verify(view).clearErrors();
        verify(translationService).getTranslation(StunnerBPMNConstants.ASSIGNEE_CANNOT_BE_EMPTY);
        verify(view).showError(any());
        verify(customEntryCommand, never()).execute(any());
        verify(okCommand, never()).execute(any());
    }
}
