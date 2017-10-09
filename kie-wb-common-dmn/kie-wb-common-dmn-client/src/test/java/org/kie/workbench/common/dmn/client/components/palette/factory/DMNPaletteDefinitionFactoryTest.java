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
package org.kie.workbench.common.dmn.client.components.palette.factory;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Categories;
import org.kie.workbench.common.dmn.client.components.palette.widget.DMNPaletteWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DMNPaletteDefinitionFactoryTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private DefinitionsPaletteBuilder paletteBuilder;

    @Mock
    private ManagedInstance<DMNPaletteWidget> palette;

    private DMNPaletteDefinitionFactory factory;

    @Before
    public void setup() {
        this.factory = new DMNPaletteDefinitionFactory(shapeManager,
                                                       paletteBuilder,
                                                       palette);
    }

    @Test
    public void checkCategoryExclusions() {
        verify(paletteBuilder).excludeCategory(Categories.DIAGRAM);
        verify(paletteBuilder).excludeCategory(Categories.CONNECTORS);
        verify(paletteBuilder).excludeCategory(Categories.MISCELLANEOUS);
    }

    @Test
    public void checkPaletteBuilderReuse() {
        assertEquals(paletteBuilder,
                     factory.newBuilder());
    }

    @Test
    public void assertDefinitionSetType() {
        assertEquals(DMNDefinitionSet.class,
                     factory.getDefinitionSetType());
    }
}
