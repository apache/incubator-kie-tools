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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label;

import org.gwtproject.user.client.ui.IsWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldLabelTest {

    private static final String INPUT_ID = "id";

    private static final String FIELD_LABEL = "label";
    private static final String FIELD_HELP = "helpMessage";
    private static final Boolean FIELD_REQUIRED = Boolean.TRUE;

    @Mock
    private FieldDefinition fieldDefinition;

    @Mock
    private FieldLabelView view;

    @Mock
    private IsWidget isWidget;

    private FieldLabel fieldLabel;

    @Before
    public void init() {
        when(fieldDefinition.getLabel()).thenReturn(FIELD_LABEL);
        when(fieldDefinition.getHelpMessage()).thenReturn(FIELD_HELP);
        when(fieldDefinition.isRequired()).thenReturn(FIELD_REQUIRED);

        fieldLabel = new FieldLabel(view);

        verify(view).init(fieldLabel);

        fieldLabel.getElement();

        verify(view).getElement();
    }

    @Test
    public void testRenderForInput() {

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            fieldLabel.renderForInput(null,
                                      fieldDefinition);
        }).withMessage("Parameter named 'isWidget' should be not null!");

        assertNeverRenderForInput();

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            fieldLabel.renderForInput(isWidget,
                                      null);
        }).withMessage("Parameter named 'fieldDefinition' should be not null!");

        assertNeverRenderForInput();

        fieldLabel.renderForInput(isWidget,
                                  fieldDefinition);

        verify(fieldDefinition).getLabel();
        verify(fieldDefinition).getHelpMessage();
        verify(fieldDefinition).isRequired();

        verify(view).renderForInput(isWidget,
                                    FIELD_LABEL,
                                    FIELD_REQUIRED,
                                    FIELD_HELP);
    }

    protected void assertNeverRenderForInput() {

        verify(fieldDefinition,
               never()).getLabel();
        verify(fieldDefinition,
               never()).getHelpMessage();
        verify(fieldDefinition,
               never()).isRequired();

        verify(view,
               never()).renderForInput(isWidget,
                                       FIELD_LABEL,
                                       FIELD_REQUIRED,
                                       FIELD_HELP);
    }

    @Test
    public void testRenderForInputId() {

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            fieldLabel.renderForInputId(null,
                                        fieldDefinition);
        }).withMessage("Parameter named 'inputId' should be not null!");

        assertNeverRenderForInputId();

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            fieldLabel.renderForInputId(INPUT_ID,
                                        null);
        }).withMessage("Parameter named 'fieldDefinition' should be not null!");

        assertNeverRenderForInputId();

        fieldLabel.renderForInputId(INPUT_ID,
                                    fieldDefinition);

        verify(fieldDefinition).getLabel();
        verify(fieldDefinition).getHelpMessage();
        verify(fieldDefinition).isRequired();

        verify(view).renderForInputId(INPUT_ID,
                                      FIELD_LABEL,
                                      FIELD_REQUIRED,
                                      FIELD_HELP);
    }

    protected void assertNeverRenderForInputId() {

        verify(fieldDefinition,
               never()).getLabel();
        verify(fieldDefinition,
               never()).getHelpMessage();
        verify(fieldDefinition,
               never()).isRequired();

        verify(view,
               never()).renderForInputId(INPUT_ID,
                                         FIELD_LABEL,
                                         FIELD_REQUIRED,
                                         FIELD_HELP);
    }
}
