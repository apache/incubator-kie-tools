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

package org.kie.workbench.common.dmn.client.docks.navigator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorViewTest {

    @Mock
    private HTMLDivElement divMainTree;

    @Mock
    private HTMLDivElement decisionComponents;

    @Mock
    private DecisionNavigatorTreePresenter.View treeView;

    @Mock
    private DecisionComponents.View decisionComponentsView;

    private DecisionNavigatorView view;

    @Before
    public void setup() {
        view = spy(new DecisionNavigatorView(divMainTree, decisionComponents));
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
}
