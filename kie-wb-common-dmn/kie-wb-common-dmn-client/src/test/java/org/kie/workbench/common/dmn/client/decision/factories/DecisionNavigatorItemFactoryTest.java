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

package org.kie.workbench.common.dmn.client.decision.factories;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ROOT;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorItemFactoryTest {

    @Mock
    private DecisionNavigatorBaseItemFactory baseItemFactory;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private DecisionNavigatorItem item;

    private DecisionNavigatorItemFactory factory;

    @Before
    public void setup() {
        factory = spy(new DecisionNavigatorItemFactory(baseItemFactory));
    }

    @Test
    public void testMakeRoot() {

        when(baseItemFactory.makeItem(node, ROOT)).thenReturn(item);

        assertEquals(item, factory.makeRoot(node));
    }

    @Test
    public void testMakeItem() {

        when(baseItemFactory.makeItem(node, ITEM)).thenReturn(item);

        assertEquals(item, factory.makeItem(node));
    }
}
