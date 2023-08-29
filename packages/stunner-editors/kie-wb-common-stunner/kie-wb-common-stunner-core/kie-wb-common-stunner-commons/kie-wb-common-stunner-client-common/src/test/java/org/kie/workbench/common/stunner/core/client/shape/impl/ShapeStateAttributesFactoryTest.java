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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.HIGHLIGHT;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.INVALID;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.NONE;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.SELECTED;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute.FILL_ALPHA;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute.FILL_COLOR;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute.STROKE_ALPHA;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute.STROKE_COLOR;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory.COLOR_HIGHLIGHT;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory.COLOR_INVALID;
import static org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory.COLOR_SELECTED;

@RunWith(MockitoJUnitRunner.class)
public class ShapeStateAttributesFactoryTest {

    private Function<ShapeState, ShapeStateAttributeHandler.ShapeStateAttributes> fillAttributes = ShapeStateAttributesFactory::buildFillAttributes;
    private Function<ShapeState, ShapeStateAttributeHandler.ShapeStateAttributes> strokeAttributes = ShapeStateAttributesFactory::buildStrokeAttributes;

    @Test
    public void testBuildStrokeAttributes() {
        ShapeStateAttributeHandler.ShapeStateAttributes attributes = strokeAttributes.apply(NONE);
        attributes.getValues().values().forEach(
                state -> assertEquals(null, state)
        );

        assertStrokeAttributes(strokeAttributes.apply(SELECTED), COLOR_SELECTED);
        assertStrokeAttributes(strokeAttributes.apply(HIGHLIGHT), COLOR_HIGHLIGHT);
        assertStrokeAttributes(strokeAttributes.apply(INVALID), COLOR_INVALID);
    }

    private void assertStrokeAttributes(ShapeStateAttributeHandler.ShapeStateAttributes attributes, String color) {
        assertEquals(1d, attributes.getValues().get(STROKE_ALPHA));
        assertEquals(color, attributes.getValues().get(STROKE_COLOR));
    }

    @Test
    public void testBuildFillAttributes() {
        ShapeStateAttributeHandler.ShapeStateAttributes attributes = fillAttributes.apply(NONE);
        attributes.getValues().values().forEach(
                state -> assertEquals(null, state)
        );

        assertFillAttributes(fillAttributes.apply(SELECTED), COLOR_SELECTED);
        assertFillAttributes(fillAttributes.apply(INVALID), COLOR_INVALID);
        assertFillAttributes(fillAttributes.apply(HIGHLIGHT), COLOR_HIGHLIGHT);
    }

    private void assertFillAttributes(ShapeStateAttributeHandler.ShapeStateAttributes attributes, String color) {
        assertEquals(color, attributes.getValues().get(FILL_COLOR));
        assertEquals(1d, attributes.getValues().get(FILL_ALPHA));
    }
}
