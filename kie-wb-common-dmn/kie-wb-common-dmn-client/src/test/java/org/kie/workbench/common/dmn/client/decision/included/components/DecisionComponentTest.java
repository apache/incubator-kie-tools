/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.included.components;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentTest {

    private String modelName = "file1.dmn";

    private String drgElementId = "0000-1111-2222-3333";

    private String drgElementName = "Is Allowed?";

    private Class<? extends DRGElement> drgElementClass = Decision.class;

    private DecisionComponent component;

    @Before
    public void setup() {
        component = new DecisionComponent(modelName, drgElementId, drgElementName, drgElementClass);
    }

    @Test
    public void testGetFile() {
        assertEquals(modelName, component.getFile());
    }

    @Test
    public void testGetDrgElementId() {
        assertEquals(drgElementId, component.getDrgElementId());
    }

    @Test
    public void testGetName() {
        assertEquals(drgElementName, component.getName());
    }

    @Test
    public void testGetDrgElementClass() {
        assertEquals(drgElementClass, component.getDrgElementClass());
    }

    @Test
    public void testGetIcon() {

        final ImageDataUriGlyph expectedIcon = DMNSVGGlyphFactory.DECISION_PALETTE;
        final ImageDataUriGlyph actualIcon = component.getIcon();

        assertEquals(expectedIcon, actualIcon);
    }
}
