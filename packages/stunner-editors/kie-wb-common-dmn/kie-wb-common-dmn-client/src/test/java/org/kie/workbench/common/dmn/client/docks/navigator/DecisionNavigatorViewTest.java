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

package org.kie.workbench.common.dmn.client.docks.navigator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorViewTest {

    @Mock
    private HTMLDivElement divMainTree;

    @Mock
    private HTMLDivElement decisionComponentsContainer;

    @Mock
    private HTMLDivElement decisionComponents;

    @Mock
    private DecisionNavigatorTreePresenter.View treeView;

    @Mock
    private DecisionComponents.View decisionComponentsView;

    private DecisionNavigatorView view;

    @Before
    public void setup() {
        view = spy(new DecisionNavigatorView(divMainTree, decisionComponentsContainer, decisionComponents));
    }

    @Test
    public void testSetupMainTree() {

        final HTMLElement element = mock(HTMLElement.class);
        when(treeView.getElement()).thenReturn(element);

        view.setupMainTree(treeView);

        verify(divMainTree).appendChild(element);
    }

    @Test
    public void testSetupDecisionComponents() {

        final HTMLElement element = mock(HTMLElement.class);
        when(decisionComponentsView.getElement()).thenReturn(element);

        view.setupDecisionComponents(decisionComponentsView);

        verify(decisionComponents).appendChild(element);
    }

    @Test
    public void testShowDecisionComponentsContainer() {
        final DOMTokenList classList = mock(DOMTokenList.class);

        decisionComponentsContainer.classList = classList;

        view.showDecisionComponentsContainer();

        verify(classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideDecisionComponentsContainer() {
        final DOMTokenList classList = mock(DOMTokenList.class);

        decisionComponentsContainer.classList = classList;

        view.hideDecisionComponentsContainer();

        verify(classList).add(HIDDEN_CSS_CLASS);
    }
}
