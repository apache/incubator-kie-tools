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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.FormLabelHelp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FormStyleItemTest {

    @Mock
    private FlowPanel group;

    @Mock
    private FlowPanel labelContainer;

    @Mock
    private FormLabel formLabel;

    @Mock
    private FormLabelHelp formLabelHelp;

    private FormStyleItem testedItem;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(FormLabel.class, aClass -> formLabel);
        GwtMockito.useProviderForType(FormLabelHelp.class, aClass -> formLabelHelp);

        testedItem = spy(new FormStyleItem() {{
            labelContainer = FormStyleItemTest.this.labelContainer;
            group = FormStyleItemTest.this.group;
        }});
    }

    @Test
    public void testSetup() throws Exception {
        final String attribute = "attribute";
        final IsWidget widget = mock(IsWidget.class);
        final int index = 123;

        testedItem.setup(attribute, widget, index);

        verify(formLabel).setText(attribute);
        verify(labelContainer).add(formLabel);
        verify(group).add(widget);
        assertEquals(index, testedItem.index);
    }

    @Test
    public void testSetupWithHelp() throws Exception {
        final String attribute = "attribute";
        final String helpTitle = "help key";
        final String helpContent = "help content";
        final IsWidget widget = mock(IsWidget.class);
        final int index = 123;

        testedItem.setup(attribute,
                         helpTitle,
                         helpContent,
                         widget, index);

        verify(formLabelHelp).setText(attribute);
        verify(formLabelHelp).setHelpTitle(helpTitle);
        verify(formLabelHelp).setHelpContent(helpContent);
        verify(labelContainer).add(formLabelHelp);
        verify(group).add(widget);
        assertEquals(index, testedItem.index);
    }
}
