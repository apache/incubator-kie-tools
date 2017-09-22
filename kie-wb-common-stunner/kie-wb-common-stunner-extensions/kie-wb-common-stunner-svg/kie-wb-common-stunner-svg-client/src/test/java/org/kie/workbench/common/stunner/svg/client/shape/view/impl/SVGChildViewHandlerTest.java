/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGChildViewHandlerTest {

    private static final double WIDTH = 150;
    private static final double HEIGHT = 75;

    @Mock
    SVGBasicShapeView child;

    private Group svgParentContainer;
    private IPrimitive childContainer;
    private IContainer<?, IPrimitive<?>> svgContainer;
    private SVGChildViewHandler tested;

    @Before
    public void setup() throws Exception {
        this.childContainer = spy(new Group().setID("childGroup"));
        this.svgParentContainer = new Group().setID("parentGroup");
        when(child.getContainer()).thenReturn((IContainer) childContainer);
        this.svgContainer = new Group();
        this.svgContainer.add(new Rectangle(50,
                                            50));
        this.svgContainer.add(svgParentContainer);
        this.svgContainer.add(new Circle(50));
        this.tested = new SVGChildViewHandler(svgContainer,
                                              WIDTH,
                                              HEIGHT);
    }

    @Test
    public void testAddSVGChild() {
        tested.addSVGChild("parentGroup",
                           child);
        final boolean[] hasChild = {false};
        svgParentContainer.getChildNodes().forEach(c -> {
            if ("childGroup".equals(c.getID())) {
                hasChild[0] = true;
            }
        });
        assertTrue(hasChild[0]);
        final Collection<SVGBasicShapeView> svgChildren = tested.getSVGChildren();
        assertEquals(1,
                     svgChildren.size());
        assertEquals(child,
                     svgChildren.iterator().next());
    }

    @Test
    public void testClear() {
        tested.addSVGChild("parentGroup",
                           child);
        tested.clear();
        final Collection<SVGBasicShapeView> svgChildren = tested.getSVGChildren();
        assertTrue(svgChildren.isEmpty());
    }
}
