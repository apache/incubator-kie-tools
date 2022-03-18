/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Icon;
import org.jboss.errai.common.client.dom.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListSelectorHeaderItemViewImplTest {

    private Span text;
    private Icon icon;
    private ListSelectorHeaderItemViewImpl headerItemView;

    @Before
    public void setUp() {
        text = mock(Span.class);
        icon = mock(Icon.class);
        headerItemView = new ListSelectorHeaderItemViewImpl(text, icon);
    }

    @Test
    public void testSetText() {
        final String text = "TEXT";
        headerItemView.setText(text);

        verify(this.text).setTextContent(text);
    }

    @Test
    public void testSetIconClass() {
        final String iconClass = "class";
        final Element element = mock(Element.class);
        when(this.icon.getElement()).thenReturn(element);

        headerItemView.setIconClass(iconClass);

        verify(element).setClassName(iconClass);
    }

}
