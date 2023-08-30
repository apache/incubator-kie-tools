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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentTest {

    private String fileName = "file1.dmn";

    private String drgElementName = "Is Allowed?";

    private DRGElement drgElement = new Decision();

    private DecisionComponent component;

    @Before
    public void setup() {
        drgElement = new Decision();
        component = new DecisionComponent(fileName, drgElement, true);

        drgElement.setName(new Name(drgElementName));
    }

    @Test
    public void testGetFile() {
        assertEquals(fileName, component.getFileName());
    }

    @Test
    public void testGetName() {
        assertEquals(drgElementName, component.getName());
    }

    @Test
    public void testGetIcon() {

        final ImageDataUriGlyph expectedIcon = DMNSVGGlyphFactory.DECISION_PALETTE;
        final ImageDataUriGlyph actualIcon = component.getIcon();

        assertEquals(expectedIcon, actualIcon);
    }
}
