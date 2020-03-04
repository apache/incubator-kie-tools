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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.dom.client.DivElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListGroupItemPresenterTest extends AbstractTestToolsTest {

    private ListGroupItemPresenter listGroupItemPresenter;

    @Mock
    private ListGroupItemView listGroupItemViewMock;

    @Mock
    private DivElement divElementMock;

    @Spy
    private FieldItemPresenter fieldItemPresenterSpy = new FieldItemPresenter();

    @Mock
    private FieldItemView fieldItemViewMock;

    @Mock
    private Map<String, ListGroupItemView> listGroupItemViewMapMock;

    @Mock
    private List<ListGroupItemView> listGroupItemViewValuesMock;

    @Mock
    private TestToolsPresenter testToolsPresenterMock;

    @Before
    public void setup() {
        super.setup();
        when(viewsProviderMock.getListGroupItemView()).thenReturn(listGroupItemViewMock);
        when(viewsProviderMock.getFieldItemView()).thenReturn(fieldItemViewMock);
        when(listGroupItemViewMock.getListGroupItem()).thenReturn(divElementMock);
        when(listGroupItemViewMock.getListGroupExpansion()).thenReturn(divElementMock);
        when(listGroupItemViewMapMock.values()).thenReturn(listGroupItemViewValuesMock);
        this.listGroupItemPresenter = spy(new ListGroupItemPresenter() {
            {
                listGroupItemViewMap = listGroupItemViewMapMock;
                fieldItemPresenter = fieldItemPresenterSpy;
                viewsProvider = viewsProviderMock;
                testToolsPresenter = testToolsPresenterMock;
            }
        });
        fieldItemPresenterSpy.viewsProvider = viewsProviderMock;
    }

    @Test
    public void getDivElementByFactModel() {
        DivElement retrieved = listGroupItemPresenter.getDivElement(FACT_NAME, FACT_MODEL_TREE);
        assertNotNull(retrieved);
        assertEquals(divElementMock, retrieved);
        verify(listGroupItemPresenter, times(1)).commonGetListGroupItemView(eq(""), eq(FACT_NAME), eq(false));
        verify(listGroupItemPresenter, times(1)).populateListGroupItemView(eq(listGroupItemViewMock), eq(""), eq(FACT_NAME), eq(FACT_MODEL_TREE));
    }

    @Test
    public void getDivElementByStrings() {
        DivElement retrieved = listGroupItemPresenter.getDivElement(FULL_PACKAGE, MULTIPART_VALUE, VALUE_CLASS_NAME);
        assertNotNull(retrieved);
        assertEquals(divElementMock, retrieved);
        verify(listGroupItemPresenter, times(1)).commonGetListGroupItemView(eq(FULL_PACKAGE), eq(MULTIPART_VALUE), eq(true));
        verify(listGroupItemPresenter, times(1)).populateListGroupItemView(eq(listGroupItemViewMock), eq(MULTIPART_VALUE), eq(VALUE_CLASS_NAME));
    }

    @Test
    public void onToggleRowExpansionDisabled() {
        listGroupItemPresenter.disable();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        verify(listGroupItemViewValuesMock, never()).contains(eq(listGroupItemViewMock));
        verify(listGroupItemViewMock, never()).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(listGroupItemViewValuesMock, never()).contains(eq(listGroupItemViewMock));
        verify(listGroupItemViewMock, never()).expandRow();
    }

    @Test
    public void onToggleRowExpansionWithoutFactName() {
        listGroupItemPresenter.enable();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        verify(listGroupItemViewMock, times(1)).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(listGroupItemViewMock, times(1)).expandRow();
    }

    @Test
    public void onToggleRowExpansionWithFactName() {
        listGroupItemPresenter.enable(FACT_NAME);
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        verify(listGroupItemViewMock, times(1)).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(listGroupItemViewMock, times(1)).expandRow();
    }

    @Test
    public void onToggleRowExpansionWithFactNameHidden() {
        listGroupItemPresenter.enable(FACT_NAME);
        when(listGroupItemViewMock.isToExpand()).thenReturn(true);
        when(testToolsPresenterMock.getFactModelTreeFromFactTypeMap(anyString())).thenReturn(Optional.empty());
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(testToolsPresenterMock, times(1)).getFactModelTreeFromFactTypeMap(anyString());
        verify(testToolsPresenterMock, times(1)).getFactModelTreeFromHiddenMap(anyString());
        verify(listGroupItemViewMock, times(1)).expandRow();
    }

    @Test
    public void populateListGroupItemView() {
        listGroupItemPresenter.populateListGroupItemView(listGroupItemViewMock, "", FACT_MODEL_TREE.getFactName(), FACT_MODEL_TREE);
        verify(listGroupItemViewMock, times(1)).setFactName(eq(FACT_MODEL_TREE.getFactName()));
        Map<String, String> simpleProperties = FACT_MODEL_TREE.getSimpleProperties();
        for (String key : simpleProperties.keySet()) {
            String value = simpleProperties.get(key);
            verify(fieldItemPresenterSpy, times(1)).getLIElement(eq(FACT_MODEL_TREE.getFactName()), eq(FACT_MODEL_TREE.getFactName()), eq(key), eq(value));
        }
        verify(listGroupItemViewMock, times(simpleProperties.size())).addFactField(anyObject());
        reset(listGroupItemViewMock);
        Map<String, String> expandableProperties = FACT_MODEL_TREE.getExpandableProperties();
        for (String key : expandableProperties.keySet()) {
            String value = expandableProperties.get(key);
            verify(listGroupItemPresenter, times(1)).getDivElement(eq(""), eq(key), eq(value));
        }
        verify(listGroupItemViewMock, times(expandableProperties.size())).addExpandableFactField(anyObject());
    }

    @Test
    public void resetTest() {
        listGroupItemPresenter.reset();
        verify(listGroupItemViewMapMock, times(1)).clear();
        verify(fieldItemPresenterSpy, times(1)).reset();
    }

    @Test
    public void testSelectPropertyNoFieldAvailableJustExpression() {
        final String instance = "Applicant";
        final String property = "expression";

        when(listGroupItemViewMapMock.get(instance)).thenReturn(listGroupItemViewMock);
        fieldItemPresenterSpy.fieldItemMap = Collections.emptyMap();

        listGroupItemPresenter.selectProperty(instance, Collections.singletonList(property));

        verify(listGroupItemViewMock).showCheck(true);
    }
}