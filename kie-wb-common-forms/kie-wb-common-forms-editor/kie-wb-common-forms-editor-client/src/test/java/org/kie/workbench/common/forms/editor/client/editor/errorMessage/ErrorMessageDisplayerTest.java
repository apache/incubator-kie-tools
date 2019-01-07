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

package org.kie.workbench.common.forms.editor.client.editor.errorMessage;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.model.FormModelerContentError;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorMessageDisplayerTest {

    private static final String MESSAGE = "message";
    private static final String FULL_MESSAGE = "full message";
    private static final String SOURCE_TYPE = "dataObject";

    @Mock
    private TranslationService translationService;

    @Mock
    private ErrorMessageDisplayerView view;

    @Mock
    private Command closeCommand;

    private ErrorMessageDisplayer displayer;

    @Before
    public void init() {
        when(translationService.getTranslation(MESSAGE)).thenReturn(MESSAGE);
        when(translationService.getTranslation(FULL_MESSAGE)).thenReturn(FULL_MESSAGE);
        when(translationService.getTranslation(SOURCE_TYPE)).thenReturn(SOURCE_TYPE);

        when(translationService.format(eq(MESSAGE), any())).thenReturn(MESSAGE);
        when(translationService.format(eq(FULL_MESSAGE), any())).thenReturn(FULL_MESSAGE);

        displayer = new ErrorMessageDisplayer(translationService, view);
        displayer.init();
    }

    @Test
    public void simpleMessageTest() {
        verify(view).init(displayer);

        FormModelerContentError error = new FormModelerContentError(MESSAGE, null, null, null, SOURCE_TYPE);

        displayer.show(error, closeCommand);

        verify(translationService).getTranslation(MESSAGE);
        verify(translationService).getTranslation(SOURCE_TYPE);

        verify(view).setSourceType(eq(SOURCE_TYPE));
        verify(view).displayShowMoreAnchor(false);
        verify(view, never()).setShowMoreLabel(any());

        verify(view).show(eq(MESSAGE));

        displayer.notifyShowMorePressed();
        verify(view, times(1)).show(any());
        verify(translationService, never()).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, never()).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(1)).show(any());
        verify(translationService, never()).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, never()).setShowMoreLabel(any());
    }

    @Test
    public void fullMessageTest() {
        verify(view).init(displayer);

        FormModelerContentError error = new FormModelerContentError(MESSAGE, new String[1], FULL_MESSAGE, new String[1], SOURCE_TYPE);
        displayer.show(error, closeCommand);

        verify(translationService).format(eq(MESSAGE), any());
        verify(translationService).format(eq(FULL_MESSAGE), any());
        verify(translationService).getTranslation(eq(SOURCE_TYPE));

        verify(view).setSourceType(eq(SOURCE_TYPE));
        verify(view).displayShowMoreAnchor(true);

        verify(translationService, times(1)).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, times(1)).setShowMoreLabel(any());

        verify(view).show(MESSAGE);

        displayer.notifyShowMorePressed();
        verify(view, times(1)).show(eq(FULL_MESSAGE));
        verify(translationService, times(1)).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, times(2)).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(2)).show(eq(MESSAGE));
        verify(translationService, times(2)).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, times(3)).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(2)).show(eq(FULL_MESSAGE));
        verify(translationService, times(2)).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, times(4)).setShowMoreLabel(any());

        displayer.notifyClose();

        verify(closeCommand, never()).execute();

        when(view.isClose()).thenReturn(true);

        displayer.notifyClose();

        verify(closeCommand).execute();
    }
}
