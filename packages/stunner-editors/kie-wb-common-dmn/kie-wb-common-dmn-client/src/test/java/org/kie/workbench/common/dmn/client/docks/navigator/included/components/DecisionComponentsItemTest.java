/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory.DECISION_PALETTE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsItemTest {

    @Mock
    private DecisionComponentsItem.View view;

    private DecisionComponentsItem item;

    @Before
    public void setup() {
        item = spy(new DecisionComponentsItem(view));
    }

    @Test
    public void testInit() {
        item.init();
        verify(view).init(item);
    }

    @Test
    public void testGetView() {
        assertEquals(view, item.getView());
    }

    @Test
    public void testSetDecisionComponent() {

        final DecisionComponent decisionComponent = mock(DecisionComponent.class);

        when(decisionComponent.getIcon()).thenReturn(DECISION_PALETTE);
        when(decisionComponent.getName()).thenReturn("name");
        when(decisionComponent.getFileName()).thenReturn("file");

        item.setDecisionComponent(decisionComponent);

        verify(view).setIcon(DECISION_PALETTE.getUri().asString());
        verify(view).setName(decisionComponent.getName());
        verify(view).setFile(decisionComponent.getFileName());
    }

    @Test
    public void testShow() {

        final HTMLElement viewElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(viewElement);
        viewElement.classList = mock(DOMTokenList.class);

        item.show();

        verify(viewElement.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHide() {

        final HTMLElement viewElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(viewElement);
        viewElement.classList = mock(DOMTokenList.class);

        item.hide();

        verify(viewElement.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testGetDrgElement() {

        final DecisionComponent decisionComponent = mock(DecisionComponent.class);
        final DRGElement expectedDrgElement = null;

        when(decisionComponent.getDrgElement()).thenReturn(expectedDrgElement);
        doReturn(decisionComponent).when(item).getDecisionComponent();

        final DRGElement actualDrgElement = item.getDrgElement();

        assertEquals(expectedDrgElement, actualDrgElement);
    }
}
