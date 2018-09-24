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

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListGroupItemPresenterTest extends AbstractRightPanelTest {

    private ListGroupItemPresenter listGroupItemPresenter;

    @Mock
    private ListGroupItemView mockListGroupItemView;

    @Mock
    private DivElement mockDivElement;

    @Mock
    private FieldItemPresenter mockFieldItemPresenter;

    @Mock
    private List<ListGroupItemView> mockListGroupItemViewList;

    @Before
    public void setup() {
        super.setup();
        when(mockViewsProvider.getListGroupItemView()).thenReturn(mockListGroupItemView);
        when(mockListGroupItemView.getDivElement()).thenReturn(mockDivElement);
        this.listGroupItemPresenter = spy(new ListGroupItemPresenter() {
            {
                listGroupItemViewList = mockListGroupItemViewList;
                fieldItemPresenter = mockFieldItemPresenter;
                viewsProvider = mockViewsProvider;
            }
        });
    }

    @Test
    public void getDivElement() {
        DivElement retrieved = listGroupItemPresenter.getDivElement(FACT_NAME, FACT_MODEL_TREE);
        verify(mockViewsProvider, times(1)).getListGroupItemView();
        verify(listGroupItemPresenter, times(1)).populateListGroupItemView(eq(mockListGroupItemView), eq(""), eq(FACT_NAME), eq(FACT_MODEL_TREE));
        verify(mockListGroupItemView, times(1)).init(eq(listGroupItemPresenter));
        verify(mockListGroupItemViewList, times(1)).add(eq(mockListGroupItemView));
        assertNotNull(retrieved);
        assertEquals(mockDivElement, retrieved);
    }

    @Test
    public void onToggleRowExpansion() {
        listGroupItemPresenter.setDisabled(false);
        reset(mockListGroupItemViewList);
        when(mockListGroupItemViewList.contains(mockListGroupItemView)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(mockListGroupItemView, true);
        verify(mockListGroupItemViewList, times(1)).contains(eq(mockListGroupItemView));
        verify(mockListGroupItemView, times(1)).closeRow();
        reset(mockListGroupItemViewList);
        when(mockListGroupItemViewList.contains(mockListGroupItemView)).thenReturn(true);
        reset(mockListGroupItemView);
        listGroupItemPresenter.onToggleRowExpansion(mockListGroupItemView, false);
        verify(mockListGroupItemViewList, times(1)).contains(eq(mockListGroupItemView));
        verify(mockListGroupItemView, times(1)).expandRow();
        //
        listGroupItemPresenter.setDisabled(true);
        reset(mockListGroupItemViewList);
        when(mockListGroupItemViewList.contains(mockListGroupItemView)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(mockListGroupItemView, true);
        verify(mockListGroupItemViewList, times(0)).contains(eq(mockListGroupItemView));
        verify(mockListGroupItemView, times(0)).closeRow();
        reset(mockListGroupItemViewList);
        when(mockListGroupItemViewList.contains(mockListGroupItemView)).thenReturn(true);
        reset(mockListGroupItemView);
        listGroupItemPresenter.onToggleRowExpansion(mockListGroupItemView, false);
        verify(mockListGroupItemViewList, times(0)).contains(eq(mockListGroupItemView));
        verify(mockListGroupItemView, times(0)).expandRow();
    }

    @Test
    public void populateListGroupItemView() {
        listGroupItemPresenter.populateListGroupItemView(mockListGroupItemView, "", FACT_NAME, FACT_MODEL_TREE);
        verify(mockListGroupItemView, times(1)).setFactName(eq(FACT_NAME));
        Map<String, String> simpleProperties = FACT_MODEL_TREE.getSimpleProperties();
        for (String key : simpleProperties.keySet()) {
            String value = simpleProperties.get(key);
            verify(mockFieldItemPresenter, times(1)).getLIElement(eq(FACT_NAME), eq(FACT_NAME), eq(key), eq(value));
        }
        verify(mockListGroupItemView, times(simpleProperties.size())).addFactField(anyObject());
        reset(mockListGroupItemView);
        Map<String, String> expandableProperties = FACT_MODEL_TREE.getExpandableProperties();
        for (String key : expandableProperties.keySet()) {
            String value = expandableProperties.get(key);
            verify(listGroupItemPresenter, times(1)).getDivElement(eq(""), eq(key), eq(value));
        }
        verify(mockListGroupItemView, times(expandableProperties.size())).addExpandableFactField(anyObject());
    }
}