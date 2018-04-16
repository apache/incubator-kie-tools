/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConflictElementTest {

    public static final String FIELD = "field";
    public static final String MESSAGE = "message";

    @Mock
    TranslationService translationService;

    @Mock
    ConflictElementView view;

    @Mock
    ConflictElement element;

    @Before
    public void init() {
        element = new ConflictElement(view,
                                      translationService);
    }

    @Test
    public void testFunctionallity() {
        verify(view).init(element);

        element.getElement();

        verify(view).getElement();

        element.showConflict(FIELD,
                             MESSAGE,
                             MESSAGE);

        verify(view).showConflict(FIELD,
                                  MESSAGE);

        // Pressing show more link for first time -> show full message
        element.onShowMoreClick();

        verify(view).setShowMoreText(any());
        verify(view).setMessage(MESSAGE + " " + MESSAGE);
        verify(translationService).getTranslation(FormEditorConstants.ShowLessLabel);

        // Pressing showMore link for second thime -> show short message
        element.onShowMoreClick();

        verify(view, times(2)).setShowMoreText(any());
        verify(view).setMessage(MESSAGE);
        verify(translationService).getTranslation(FormEditorConstants.ShowMoreLabel);
    }
}
