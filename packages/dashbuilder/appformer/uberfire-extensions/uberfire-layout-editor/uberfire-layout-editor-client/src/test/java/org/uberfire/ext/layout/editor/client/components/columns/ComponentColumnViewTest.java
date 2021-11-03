/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ComponentColumnViewTest {

    ComponentColumnView componentColumnView;
    private KebabWidget kebabWidget;
    private ComponentColumn presenter;

    @Before
    public void setup() {
        componentColumnView = spy(new ComponentColumnView());
        componentColumnView.content = mock(Div.class);
        kebabWidget = spy(new KebabWidget());
        componentColumnView.kebabWidget = kebabWidget;
        presenter = mock(ComponentColumn.class);
    }

    @Test
    public void setupKebabWidgetTest() {
        componentColumnView.init(presenter);

        doNothing().when(componentColumnView).setupEvents();
        doNothing().when(componentColumnView).setupOnResize();
        doNothing().when(componentColumnView).removeCurrentWidget();
        componentColumnView.setupWidget();

        verify(kebabWidget).init(any(),
                                 any());

        kebabWidget.removeClick(mock(Event.class));

        verify(componentColumnView).removeCurrentWidget();
        verify(presenter).remove();

        kebabWidget.editClick(mock(Event.class));

        verify(presenter).edit();
    }

    @Test
    public void clearContentTest() {
        doNothing().when(componentColumnView).removeCurrentWidget();
        componentColumnView.init(presenter);

        componentColumnView.clearContent();

        verify(componentColumnView).removeCurrentWidget();
    }
}