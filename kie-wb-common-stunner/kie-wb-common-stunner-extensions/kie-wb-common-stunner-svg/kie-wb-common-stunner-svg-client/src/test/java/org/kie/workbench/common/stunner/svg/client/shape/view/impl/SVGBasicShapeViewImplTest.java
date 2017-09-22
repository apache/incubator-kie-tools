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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGBasicShapeViewImplTest {

    @Mock
    SVGBasicShapeView child;

    private Rectangle theShape;
    private Group svgParent;
    private IPrimitive childContainer;
    private SVGBasicShapeViewImpl tested;

    @Before
    public void setup() throws Exception {
        this.theShape = new Rectangle(50,
                                      50);
        this.svgParent = new Group().setID("theParent");
        this.childContainer = spy(new Group().setID("childGroup"));
        when(child.getContainer()).thenReturn((IContainer) childContainer);
        this.tested = new SVGBasicShapeViewImpl("svg-test1",
                                                theShape,
                                                100,
                                                340);
        tested.addChild(svgParent);
    }

    @Test
    public void testGetters() {
        assertEquals("svg-test1",
                     tested.getName());
        assertEquals(theShape,
                     tested.getShape());
    }

    @Test
    public void testSVGChild() {
        tested.addSVGChild("theParent",
                           child);
        final Collection<SVGBasicShapeView> svgChildren = tested.getSVGChildren();
        assertEquals(1,
                     svgChildren.size());
        assertEquals(child,
                     svgChildren.iterator().next());
    }
}
