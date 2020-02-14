/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractProxyPopupDropDownListBoxRenderTest {

    @GwtMock
    private ListBox listBox;

    private AbstractProxyPopupDropDownListBox<String> listBoxDropDown;

    @Mock
    private AbstractProxyPopupDropDownEditCell abstractProxyPopupDropDownEditCell;

    @Before
    public void setUp() throws Exception {
        listBoxDropDown = new AbstractProxyPopupDropDownListBox<String>(abstractProxyPopupDropDownEditCell,
                                                                        "in") {

            @Override
            public String convertToString(final String value) {
                return value;
            }

            @Override
            public String convertFromString(final String value) {
                return value;
            }
        };

        doReturn(mock(AsyncPackageDataModelOracle.class)).when(abstractProxyPopupDropDownEditCell).getDataModelOracle();

        addListBoxItem(0, "Fork", "1");
        addListBoxItem(1, "Spoon", "2");
        addListBoxItem(2, "Knife", "3");
        addListBoxItem(3, "Spork", "4");
        addListBoxItem(4, "Fork, Spoon and Knife", "\"1,2,3\"");
        doReturn(5).when(listBox).getItemCount();
        listBoxDropDown.setDropDownData(DropDownData.create(new String[]{"1=Fork", "2=Spoon", "3=Knife", "4=Spork", "\"1,2,3\"=Fork, Spoon and Knife"}));
    }

    @Test
    public void appendIsCalled() {
        final SafeHtmlBuilder sb = mock(SafeHtmlBuilder.class);
        final SafeHtmlRenderer renderer = mock(SafeHtmlRenderer.class);
        final SafeHtml safeHtml = mock(SafeHtml.class);
        doReturn(safeHtml).when(renderer).render(anyString());

        listBoxDropDown.render(mock(Cell.Context.class),
                               "",
                               sb,
                               renderer);

        verify(sb).append(safeHtml);
    }

    @Test
    public void listBoxShouldHaveTheRightItems() {
        final SafeHtmlRenderer renderer = mock(SafeHtmlRenderer.class);

        listBoxDropDown.render(mock(Cell.Context.class),
                               "",
                               mock(SafeHtmlBuilder.class),
                               renderer);

        verify(listBox).addItem("Fork", "1");
        verify(listBox).addItem("Spoon", "2");
        verify(listBox).addItem("Knife", "3");
        verify(listBox).addItem("Spork", "4");
        verify(listBox).addItem("Fork, Spoon and Knife", "\"1,2,3\"");
    }

    @Test
    public void renderFork() {
        doReturn(true).when(listBox).isMultipleSelect();

        final SafeHtmlRenderer renderer = mock(SafeHtmlRenderer.class);

        listBoxDropDown.render(mock(Cell.Context.class),
                               "1",
                               mock(SafeHtmlBuilder.class),
                               renderer);

        verify(renderer).render("Fork");
    }

    @Test
    public void noExistingPairForTheValue() {
        doReturn(true).when(listBox).isMultipleSelect();

        final SafeHtmlRenderer renderer = mock(SafeHtmlRenderer.class);

        listBoxDropDown.render(mock(Cell.Context.class),
                               "10",
                               mock(SafeHtmlBuilder.class),
                               renderer);

        verify(renderer).render("10");
    }

    @Test
    public void renderForkSpoonKnife() {
        doReturn(true).when(listBox).isMultipleSelect();

        final SafeHtmlRenderer renderer = mock(SafeHtmlRenderer.class);

        listBoxDropDown.render(mock(Cell.Context.class),
                               "\"1,2,3\"",
                               mock(SafeHtmlBuilder.class),
                               renderer);

        verify(renderer).render("Fork, Spoon and Knife");
    }

    private void addListBoxItem(final int index,
                                final String text,
                                final String value) {
        doReturn(value).when(listBox).getValue(index);
        doReturn(text).when(listBox).getItemText(index);
    }
}