/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormLabelHelpTest {

    @Mock
    private FormLabel formLabel;

    @Mock
    private FlowPanel panel;

    private FormLabelHelp formLabelHelp;

    @Before
    public void setUp() {
        this.formLabelHelp = new FormLabelHelp(formLabel,
                                               panel);
    }

    @Test
    public void setHelpTitleNull() {
        Mockito.reset(panel);

        formLabelHelp.setHelpTitle(null);

        verify(panel,
               times(0)).add(isA(HelpIcon.class));
    }

    @Test
    public void setHelpTitleNotNull() {
        Mockito.reset(panel);

        formLabelHelp.setHelpTitle("testTitle");

        verify(panel).add(isA(HelpIcon.class));
    }

    @Test
    public void setHelpContentNull() {
        Mockito.reset(panel);

        formLabelHelp.setHelpContent(null);

        verify(panel,
               times(0)).add(isA(HelpIcon.class));
    }

    @Test
    public void setHelpContentNotNull() {
        Mockito.reset(panel);

        formLabelHelp.setHelpContent("testContent");

        verify(panel).add(isA(HelpIcon.class));
    }

    @Test
    public void setText() {
        formLabelHelp.setText("testText");

        verify(formLabel).setText("testText");
    }

    @Test
    public void getText() {
        Mockito.when(formLabel.getText()).thenReturn("testText");

        assertEquals("testText",
                     formLabelHelp.getText());
    }

    @Test
    public void setFor() {
        formLabelHelp.setFor("testFor");

        verify(formLabel).setFor("testFor");
    }
}
