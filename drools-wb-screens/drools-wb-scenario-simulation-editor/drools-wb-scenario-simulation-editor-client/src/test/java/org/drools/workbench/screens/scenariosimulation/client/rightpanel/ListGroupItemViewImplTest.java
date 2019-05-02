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
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LIST_GROUP_ITEM;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemViewImpl.LIST_VIEW_PF_EXPAND_ACTIVE;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.HIDDEN;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private DivElement fullClassNameMock;

    @Mock
    private SpanElement faAngleRightMock;

    @Mock
    private UListElement factPropertiesMock;

    @Mock
    private LIElement factFieldMock;

    private ListGroupItemViewImpl listGroupItemView;

    @Before
    public void setup() {
        this.listGroupItemView = spy(new ListGroupItemViewImpl() {
            {
                this.factName = FACT_NAME;
                this.listGroupItem = listGroupItemMock;
                this.listGroupItemHeader = listGroupItemHeaderMock;
                this.listGroupItemContainer = listGroupItemContainerMock;
                this.faAngleRight = faAngleRightMock;
                this.fullClassName = fullClassNameMock;
                this.factProperties = factPropertiesMock;
            }
        });
        listGroupItemView.init(listGroupItemPresenterMock);
    }

    @Test
    public void onSelectedElement() {
        when(fullClassNameMock.getClassName()).thenReturn("not-empty disabled");
        listGroupItemView.onFullClassNameClick(mock(ClickEvent.class));
        verify(listGroupItemMock, never()).addClassName(eq("selected"));
        verify(listGroupItemPresenterMock, never()).onSelectedElement(eq(listGroupItemView));
        when(fullClassNameMock.getClassName()).thenReturn("empty");
        listGroupItemView.onFullClassNameClick(mock(ClickEvent.class));
        verify(listGroupItemMock, times(1)).addClassName(eq("selected"));
        verify(listGroupItemPresenterMock, times(1)).onSelectedElement(eq(listGroupItemView));
    }

    @Test
    public void onFaAngleRightClick() {
        String toReturn =  LIST_GROUP_ITEM + " " + LIST_VIEW_PF_EXPAND_ACTIVE;
        when(listGroupItemHeaderMock.getClassName()).thenReturn(toReturn);
        listGroupItemView.onFaAngleRightClick(mock(ClickEvent.class));
        verify(listGroupItemPresenterMock, times(1)).onToggleRowExpansion(eq(listGroupItemView), eq(true));
        when(listGroupItemHeaderMock.getClassName()).thenReturn(LIST_GROUP_ITEM);
        listGroupItemView.onFaAngleRightClick(mock(ClickEvent.class));
        verify(listGroupItemPresenterMock, times(1)).onToggleRowExpansion(eq(listGroupItemView), eq(false));
    }

    @Test
    public void setFactName() {
        listGroupItemView.setFactName(FACT_NAME);
        verify(fullClassNameMock, times(1)).setInnerText(eq(FACT_NAME));
    }

    @Test
    public void addFactField() {
        listGroupItemView.addFactField(factFieldMock);
        verify(factPropertiesMock, times(1)).appendChild(anyObject());
    }

    @Test
    public void closeRow() {
        listGroupItemView.closeRow();
        verify(listGroupItemHeaderMock, times(1)).removeClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(listGroupItemContainerMock, times(1)).addClassName(eq(HIDDEN));
        verify(faAngleRightMock, times(1)).removeClassName(eq(FA_ANGLE_DOWN));
    }

    @Test
    public void expandRow() {
        listGroupItemView.expandRow();
        verify(listGroupItemHeaderMock, times(1)).addClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(listGroupItemContainerMock, times(1)).removeClassName(eq(HIDDEN));
        verify(faAngleRightMock, times(1)).addClassName(eq(FA_ANGLE_DOWN));
    }
}