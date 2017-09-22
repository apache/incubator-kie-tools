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
package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragUpdateEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.DefaultDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.PaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BS3PaletteFactoryImplTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    private static final String SHAPE_SET_ID = "shapeSetId";

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private ManagedInstance<DefaultDefSetPaletteDefinitionFactory> defaultPaletteDefinitionFactoryInstance;

    @Mock
    private Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;

    @Mock
    private Event<CanvasShapeDragStartEvent> canvasShapeDragStartEvent;

    @Mock
    private Event<CanvasShapeDragUpdateEvent> canvasShapeDragUpdateEvent;

    @Mock
    private SyncBeanDef<PaletteDefinitionFactory> paletteDefinitionFactorySyncBeanDef;

    @Mock
    private SyncBeanDef<BS3PaletteViewFactory> paletteViewFactorySyncBeanDef;

    @Mock
    private PaletteDefinitionFactory paletteDefinitionFactory;

    @Mock
    private BS3PaletteViewFactory paletteViewFactory;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private TypeDefinitionSetRegistry typeDefinitionSetRegistry;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private PaletteDefinitionBuilder paletteDefinitionBuilder;

    @Mock
    private BS3PaletteWidget paletteWidget;

    @Captor
    private ArgumentCaptor<PaletteDefinitionBuilder.Configuration> configurationArgumentCaptor;

    private Object definitionSet = new Object();

    private Set<String> definitions = new HashSet<>();

    private BS3PaletteFactoryImpl paletteFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.paletteFactory = new BS3PaletteFactoryImpl(shapeManager,
                                                        beanManager,
                                                        definitionManager,
                                                        defaultPaletteDefinitionFactoryInstance,
                                                        buildCanvasShapeEvent,
                                                        canvasShapeDragStartEvent,
                                                        canvasShapeDragUpdateEvent);
        final Collection<SyncBeanDef<PaletteDefinitionFactory>> paletteFactoryBeans = new ArrayList<>();
        final Collection<SyncBeanDef<BS3PaletteViewFactory>> beanDefSets = new ArrayList<>();
        paletteFactoryBeans.add(paletteDefinitionFactorySyncBeanDef);
        beanDefSets.add(paletteViewFactorySyncBeanDef);

        when(beanManager.lookupBeans(eq(PaletteDefinitionFactory.class))).thenReturn(paletteFactoryBeans);
        when(beanManager.lookupBeans(eq(BS3PaletteViewFactory.class))).thenReturn(beanDefSets);
        when(paletteDefinitionFactorySyncBeanDef.getInstance()).thenReturn(paletteDefinitionFactory);
        when(paletteViewFactorySyncBeanDef.getInstance()).thenReturn(paletteViewFactory);
        when(paletteDefinitionFactory.accepts(eq(DEFINITION_SET_ID))).thenReturn(true);
        when(paletteViewFactory.accepts(eq(DEFINITION_SET_ID))).thenReturn(true);

        final Collection<ShapeSet<?>> shapeSets = new ArrayList<>();
        shapeSets.add(shapeSet);

        when(shapeManager.getShapeSets()).thenReturn(shapeSets);
        when(shapeSet.getId()).thenReturn(SHAPE_SET_ID);
        when(shapeSet.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);

        when(definitionManager.definitionSets()).thenReturn(typeDefinitionSetRegistry);
        when(typeDefinitionSetRegistry.getDefinitionSetById(eq(DEFINITION_SET_ID))).thenReturn(definitionSet);

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getDefinitions(eq(definitionSet))).thenReturn(definitions);

        when(paletteDefinitionFactory.newBuilder(eq(DEFINITION_SET_ID))).thenReturn(paletteDefinitionBuilder);
        when(paletteDefinitionFactory.newPalette()).thenReturn(paletteWidget);
    }

    @Test
    public void checkInit() {
        paletteFactory.init();

        assertEquals(paletteViewFactory,
                     paletteFactory.getViewFactory(DEFINITION_SET_ID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNewPalette() {
        paletteFactory.init();

        paletteFactory.newPalette(SHAPE_SET_ID,
                                  canvasHandler);

        verify(paletteDefinitionBuilder).build(configurationArgumentCaptor.capture(),
                                               any(PaletteDefinitionBuilder.Callback.class));

        final PaletteDefinitionBuilder.Configuration configuration = configurationArgumentCaptor.getValue();
        assertEquals(DEFINITION_SET_ID,
                     configuration.getDefinitionSetId());
        assertEquals(definitions,
                     configuration.getDefinitionIds());

        verify(paletteWidget).onItemDrop(any(PaletteWidget.ItemDropCallback.class));
        verify(paletteWidget).onItemDragStart(any(PaletteWidget.ItemDragStartCallback.class));
        verify(paletteWidget).onItemDragUpdate(any(PaletteWidget.ItemDragUpdateCallback.class));
    }
}
