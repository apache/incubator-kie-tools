/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LIST_GROUP_ITEM;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemViewImpl.LIST_VIEW_PF_EXPAND_ACTIVE;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.HIDDEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListGroupItemViewImplTest extends AbstractTestToolsTest {

    @Mock
    private ListGroupItemPresenter listGroupItemPresenterMock;

    @Mock
    private DivElement listGroupItemMock;

    @Mock
    private DivElement listGroupItemHeaderMock;

    @Mock
    private DivElement listGroupItemContainerMock;

    @Mock
    private DivElement listGroupElementMock;

    @Mock
    private SpanElement fullClassNameMock;

    @Mock
    private SpanElement faAngleRightMock;

    @Mock
    private SpanElement checkElementMock;

    @Mock
    private Style checkElementStyleMock;

    @Mock
    private UListElement factPropertiesMock;

    @Mock
    private LIElement factFieldMock;

    private ListGroupItemViewImpl listGroupItemViewSpy;

    @Before
    public void setup() {
        this.listGroupItemViewSpy = spy(new ListGroupItemViewImpl() {
            {
                this.factName = FACT_NAME;
                this.listGroupItem = listGroupItemMock;
                this.listGroupItemHeader = listGroupItemHeaderMock;
                this.listGroupItemContainer = listGroupItemContainerMock;
                this.listGroupElement = listGroupElementMock;
                this.faAngleRight = faAngleRightMock;
                this.fullClassName = fullClassNameMock;
                this.factProperties = factPropertiesMock;
                this.checkElement = checkElementMock;
            }
        });
        listGroupItemViewSpy.init(listGroupItemPresenterMock);
        when(checkElementMock.getStyle()).thenReturn(checkElementStyleMock);
    }

    @Test
    public void onFullClassNameClick() {
        when(listGroupElementMock.getClassName()).thenReturn("not-empty disabled");
        listGroupItemViewSpy.onFullClassNameClick(mock(ClickEvent.class));
        verify(listGroupItemMock, never()).addClassName(eq(ConstantHolder.SELECTED));
        verify(listGroupItemPresenterMock, never()).onSelectedElement(eq(listGroupItemViewSpy));
        //
        when(listGroupElementMock.getClassName()).thenReturn("empty");
        listGroupItemViewSpy.onFullClassNameClick(mock(ClickEvent.class));
        verify(listGroupItemMock, times(1)).addClassName(eq(ConstantHolder.SELECTED));
        verify(listGroupItemPresenterMock, times(1)).onSelectedElement(eq(listGroupItemViewSpy));
    }

    @Test
    public void unselect() {
        listGroupItemViewSpy.unselect();
        verify(listGroupItemMock, times(1)).removeClassName(eq(ConstantHolder.SELECTED));
        verify(listGroupItemViewSpy, times(1)).showCheck(eq(false));
    }

    @Test
    public void showCheck() {
        listGroupItemViewSpy.showCheck(true);
        verify(checkElementStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
        //
        reset();
        listGroupItemViewSpy.showCheck(false);
        verify(checkElementStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void isCheckShown() {
        when(checkElementStyleMock.getDisplay()).thenReturn(Style.Display.NONE.getCssName());
        assertFalse(listGroupItemViewSpy.isCheckShown());
        //
        reset();
        when(checkElementStyleMock.getDisplay()).thenReturn(Style.Display.BLOCK.getCssName());
        assertTrue(listGroupItemViewSpy.isCheckShown());
    }

    @Test
    public void onFaAngleRightClick() {
        String toReturn =  LIST_GROUP_ITEM + " " + LIST_VIEW_PF_EXPAND_ACTIVE;
        when(listGroupItemHeaderMock.getClassName()).thenReturn(toReturn);
        listGroupItemViewSpy.onFaAngleRightClick(mock(ClickEvent.class));
        verify(listGroupItemPresenterMock, times(1)).onToggleRowExpansion(eq(listGroupItemViewSpy), eq(true));
        when(listGroupItemHeaderMock.getClassName()).thenReturn(LIST_GROUP_ITEM);
        listGroupItemViewSpy.onFaAngleRightClick(mock(ClickEvent.class));
        verify(listGroupItemPresenterMock, times(1)).onToggleRowExpansion(eq(listGroupItemViewSpy), eq(false));
    }

    @Test
    public void setFactName() {
        listGroupItemViewSpy.setFactName(FACT_NAME);
        verify(fullClassNameMock, times(1)).setInnerText(eq(FACT_NAME));
    }

    @Test
    public void addFactField() {
        listGroupItemViewSpy.addFactField(factFieldMock);
        verify(factPropertiesMock, times(1)).appendChild(anyObject());
    }

    @Test
    public void closeRow() {
        listGroupItemViewSpy.closeRow();
        verify(listGroupItemHeaderMock, times(1)).removeClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(listGroupItemContainerMock, times(1)).addClassName(eq(HIDDEN));
        verify(faAngleRightMock, times(1)).removeClassName(eq(FA_ANGLE_DOWN));
    }

    @Test
    public void expandRow() {
        listGroupItemViewSpy.expandRow();
        verify(listGroupItemHeaderMock, times(1)).addClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(listGroupItemContainerMock, times(1)).removeClassName(eq(HIDDEN));
        verify(faAngleRightMock, times(1)).addClassName(eq(FA_ANGLE_DOWN));
    }
}