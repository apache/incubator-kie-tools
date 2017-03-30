/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.bs3.BS3IconRenderer;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementBS3PaletteViewFactoryTest {

    @Mock
    private ShapeManager shapeManager;

    private CaseManagementBS3PaletteViewFactory paletteViewFactory;

    @Before
    public void setup() {
        this.paletteViewFactory = new CaseManagementBS3PaletteViewFactory(shapeManager);
    }

    @Test
    public void assertDefinitionSetType() {
        assertEquals(CaseManagementDefinitionSet.class,
                     paletteViewFactory.getDefinitionSetType());
    }

    @Test
    public void assertPaletteIconRendererType() {
        assertEquals(BS3IconRenderer.class,
                     paletteViewFactory.getPaletteIconRendererType());
    }

    @Test
    public void checkCategoryIconResources() {
        final Map<String, IconResource> iconResources = paletteViewFactory.getCategoryIconResources();
        assertNotNull(iconResources);
        assertEquals(2,
                     iconResources.size());
        assertTrue(iconResources.containsKey(CaseManagementPaletteDefinitionFactory.STAGES));
        assertTrue(iconResources.containsKey(CaseManagementPaletteDefinitionFactory.ACTIVITIES));
        assertNotNull(iconResources.get(CaseManagementPaletteDefinitionFactory.STAGES));
        assertNotNull(iconResources.get(CaseManagementPaletteDefinitionFactory.ACTIVITIES));
    }
}
