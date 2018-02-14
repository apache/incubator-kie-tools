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

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub(FormElement.class)
@RunWith(GwtMockitoTestRunner.class)
public class FormStyleLayoutTest {

    @Mock
    private FormStyleItem item;

    private FormStyleLayout testedLayout;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(FormStyleItem.class, aClass -> item);

        testedLayout = spy(new FormStyleLayout());
    }

    @Test
    public void testAddAttributeWithHelp() throws Exception {
        final String attribute = "form attribute";
        final String helpTitle = "help title";
        final String helpContent = "help content";
        final IsWidget widget = mock(IsWidget.class);
        final int widgetCount = 123;

        doReturn(widgetCount).when(testedLayout).getWidgetCount();

        testedLayout.addAttribute(attribute,
                                  helpTitle,
                                  helpContent,
                                  widget);

        verify(item).setup(attribute,
                           helpTitle,
                           helpContent,
                           widget,
                           widgetCount);
        verify(testedLayout).add(item);
    }
}
