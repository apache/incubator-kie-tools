/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.library;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;
import org.kie.workbench.common.screens.library.client.widgets.common.dropdown.DropdownHeaderWidget;
import org.kie.workbench.common.screens.library.client.widgets.common.dropdown.DropdownSeparatorWidget;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ImportProjectButtonViewTest {

    @Mock
    private ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets;

    @Mock
    private ManagedInstance<DropdownHeaderWidget> dropdownHeaderWidgets;

    @Mock
    private ManagedInstance<DropdownSeparatorWidget> dropdownSeparatorWidgets;

    @Mock
    private UnorderedList importProjectDropdownContainer;

    @InjectMocks
    private ImportProjectButtonView view;

    private DropdownHeaderWidget dropdownHeaderWidget;

    private DropdownSeparatorWidget dropdownSeparatorWidget;

    private MenuResourceHandlerWidget menuResourceHandlerWidget;

    @Before
    public void setup() {
        dropdownHeaderWidget = mock(DropdownHeaderWidget.class);
        doReturn(dropdownHeaderWidget).when(dropdownHeaderWidgets).get();
        doReturn(mock(HTMLElement.class)).when(dropdownHeaderWidget).getElement();

        dropdownSeparatorWidget = mock(DropdownSeparatorWidget.class);
        doReturn(dropdownSeparatorWidget).when(dropdownSeparatorWidgets).get();
        doReturn(mock(HTMLElement.class)).when(dropdownSeparatorWidget).getElement();

        menuResourceHandlerWidget = mock(MenuResourceHandlerWidget.class);
        doReturn(menuResourceHandlerWidget).when(menuResourceHandlerWidgets).get();
        doReturn(mock(HTMLElement.class)).when(menuResourceHandlerWidget).getElement();
    }

    @Test
    public void addOptionTest() {
        final String text = "text";
        final Command command = mock(Command.class);

        view.addOption(text,
                       command);

        verify(menuResourceHandlerWidget).init(text,
                                               null,
                                               command);
        verify(view.importProjectDropdownContainer).appendChild(menuResourceHandlerWidget.getElement());
    }

    @Test
    public void addOptionWithDescriptionTest() {
        final String text = "text";
        final String description = "description";
        final Command command = mock(Command.class);

        view.addOption(text,
                       description,
                       command);

        verify(menuResourceHandlerWidget).init(text,
                                               description,
                                               command);
        verify(view.importProjectDropdownContainer).appendChild(menuResourceHandlerWidget.getElement());
    }

    @Test
    public void addSeparatorTest() {
        view.addSeparator();

        verify(view.importProjectDropdownContainer).appendChild(dropdownSeparatorWidget.getElement());
    }

    @Test
    public void addHeaderTest() {
        view.addHeader("title");

        verify(dropdownHeaderWidget).init("title");
        verify(view.importProjectDropdownContainer).appendChild(dropdownHeaderWidget.getElement());
    }
}
