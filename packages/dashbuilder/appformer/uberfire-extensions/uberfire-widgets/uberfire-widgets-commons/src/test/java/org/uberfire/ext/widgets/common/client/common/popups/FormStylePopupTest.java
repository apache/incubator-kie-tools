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

package org.uberfire.ext.widgets.common.client.common.popups;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.FormStyleItem;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub({BaseModal.class, Text.class})
@RunWith(GwtMockitoTestRunner.class)
public class FormStylePopupTest {

    @Mock
    private FormStyleLayout formStyleLayout;

    private FormStylePopup testedPopup;

    @Before
    public void setUp() throws Exception {
        testedPopup = spy(new FormStylePopup("form") {{
            form = formStyleLayout;
        }});
    }

    @Test
    public void testAddAttributeWithHelp() throws Exception {
        final String attribute = "form attribute";
        final String helpTitle = "help title";
        final String helpContent = "help content";
        final Widget widget = mock(Widget.class);
        final FormStyleItem expectedItem = mock(FormStyleItem.class);
        final int expectedAttributeIndex = 123;

        doReturn(expectedItem).when(formStyleLayout).addAttribute(attribute,
                                                                  helpTitle,
                                                                  helpContent,
                                                                  widget);
        doReturn(expectedAttributeIndex).when(expectedItem).getIndex();

        testedPopup.addAttributeWithHelp(attribute,
                                         helpTitle,
                                         helpContent,
                                         widget);

        verify(formStyleLayout).addAttribute(attribute,
                                             helpTitle,
                                             helpContent,
                                             widget);
        verify(formStyleLayout).setAttributeVisibility(expectedAttributeIndex, true);
    }
}
