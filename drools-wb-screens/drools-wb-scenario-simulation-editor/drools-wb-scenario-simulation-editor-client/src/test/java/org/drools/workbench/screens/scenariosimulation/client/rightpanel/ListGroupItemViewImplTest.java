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

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemViewImpl.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemViewImpl.HIDDEN;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemViewImpl.LIST_VIEW_PF_EXPAND_ACTIVE;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListGroupItemViewImplTest extends AbstractRightPanelTest {

    @Mock
    private ListGroupItemPresenter mockListGroupItemPresenter;

    @Mock
    private DivElement mockListGroupItemHeader;

    @Mock
    private DivElement mockListGroupItemContainer;

    @Mock
    private DivElement mockFullClassName;

    @Mock
    private SpanElement mockFaAngleRight;

    @Mock
    private UListElement mockFactProperties;

    @Mock
    private LIElement mockFactField;

    private ListGroupItemViewImpl listGroupItemView;

    private static final String LIST_GROUP_ITEM = "list-group-item";



    @Before
    public void setup() {
        this.listGroupItemView = spy(new ListGroupItemViewImpl() {
            {
                this.factName = FACT_NAME;
                this.listGroupItemHeader = mockListGroupItemHeader;
                this.listGroupItemContainer = mockListGroupItemContainer;
                this.faAngleRight = mockFaAngleRight;
                this.fullClassName = mockFullClassName;
                this.factProperties = mockFactProperties;
            }
        });
        listGroupItemView.init(mockListGroupItemPresenter);
    }

    @Test
    public void onListGroupItemHeaderClick() {
        String toReturn =  LIST_GROUP_ITEM + " " + LIST_VIEW_PF_EXPAND_ACTIVE;
        when(mockListGroupItemHeader.getClassName()).thenReturn(toReturn);
        listGroupItemView.onListGroupItemHeaderClick(mock(ClickEvent.class));
        verify(mockListGroupItemPresenter, times(1)).onToggleRowExpansion(eq(listGroupItemView), eq(true));
        when(mockListGroupItemHeader.getClassName()).thenReturn(LIST_GROUP_ITEM);
        listGroupItemView.onListGroupItemHeaderClick(mock(ClickEvent.class));
        verify(mockListGroupItemPresenter, times(1)).onToggleRowExpansion(eq(listGroupItemView), eq(false));
    }

    @Test
    public void setFactName() {
        listGroupItemView.setFactName(FACT_NAME);
        verify(mockFullClassName, times(1)).setInnerText(eq(FACT_NAME));
    }

    @Test
    public void addFactField() {
        listGroupItemView.addFactField(mockFactField);
        verify(mockFactProperties, times(1)).appendChild(anyObject());
    }

    @Test
    public void closeRow() {
        listGroupItemView.closeRow();
        verify(mockListGroupItemHeader, times(1)).removeClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(mockListGroupItemContainer, times(1)).addClassName(eq(HIDDEN));
        verify(mockFaAngleRight, times(1)).removeClassName(eq(FA_ANGLE_DOWN));
    }

    @Test
    public void expandRow() {
        listGroupItemView.expandRow();
        verify(mockListGroupItemHeader, times(1)).addClassName(eq(LIST_VIEW_PF_EXPAND_ACTIVE));
        verify(mockListGroupItemContainer, times(1)).removeClassName(eq(HIDDEN));
        verify(mockFaAngleRight, times(1)).addClassName(eq(FA_ANGLE_DOWN));
    }
}