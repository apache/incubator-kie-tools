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
package org.kie.workbench.common.stunner.client.widgets.palette;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BS3PaletteWidgetImplTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private BS3PaletteWidgetView view;

    @Mock
    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    @Mock
    private ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstance;

    @Mock
    private BS3PaletteViewFactory viewFactory;

    private BS3PaletteWidgetImpl palette;

    @Before
    public void setup() {
        this.palette = new BS3PaletteWidgetImpl(shapeManager,
                                                clientFactoryServices,
                                                view,
                                                shapeGlyphDragHandler,
                                                categoryWidgetInstance);
        this.palette.init();
        this.palette.setViewFactory(viewFactory);
    }

    @Test
    public void checkDestructionReleasesResources() {
        palette.doDestroy();

        verify(categoryWidgetInstance).destroyAll();
        verify(viewFactory).destroy();
        verify(view).destroy();
    }
}
