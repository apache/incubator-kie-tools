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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.AbstractPaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.DefaultDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;

@Dependent
public class BS3PaletteFactoryImpl extends AbstractPaletteFactory<DefinitionSetPalette, BS3PaletteWidget>
        implements BS3PaletteFactory {

    private final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;
    private final List<BS3PaletteViewFactory> viewFactories = new LinkedList<>();

    @Inject
    public BS3PaletteFactoryImpl(final ShapeManager shapeManager,
                                 final SyncBeanManager beanManager,
                                 final ManagedInstance<DefaultDefSetPaletteDefinitionFactory> defaultPaletteDefinitionFactoryInstance,
                                 final ManagedInstance<BS3PaletteWidget> paletteInstances,
                                 final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent) {
        super(shapeManager,
              beanManager,
              defaultPaletteDefinitionFactoryInstance,
              paletteInstances);
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        Collection<SyncBeanDef<BS3PaletteViewFactory>> beanDefSets = beanManager.lookupBeans(BS3PaletteViewFactory.class);
        for (SyncBeanDef<BS3PaletteViewFactory> defSet : beanDefSets) {
            BS3PaletteViewFactory factory = defSet.getInstance();
            viewFactories.add(factory);
        }
    }

    @Override
    public BS3PaletteWidget newPalette(final String shapeSetId,
                                       final CanvasHandler canvasHandler) {

        final BS3PaletteWidget palette = super.newPalette(shapeSetId);
        if (null != canvasHandler) {
            palette.onItemDrop((definition,
                                factory,
                                x,
                                y) ->
                                       buildCanvasShapeEvent.fire(new BuildCanvasShapeEvent((AbstractCanvasHandler) canvasHandler,
                                                                                            definition,
                                                                                            factory,
                                                                                            x,
                                                                                            y)));
        }

        return palette;
    }

    @Override
    protected void beforeBindPalette(final DefinitionSetPalette paletteDefinition,
                                     final BS3PaletteWidget palette,
                                     final String shapeSetId) {
        super.beforeBindPalette(paletteDefinition,
                                palette,
                                shapeSetId);
        final String defSetId = paletteDefinition.getDefinitionSetId();
        BS3PaletteViewFactory viewFactory = getViewFactory(defSetId);
        if (null == viewFactory) {
            viewFactory = new BS3PaletteGlyphViewFactory(shapeManager);
        }
        palette.setViewFactory(viewFactory);
    }

    private BS3PaletteViewFactory getViewFactory(final String defSetId) {
        for (final BS3PaletteViewFactory factory : viewFactories) {
            if (factory.accepts(defSetId)) {
                return factory;
            }
        }
        return null;
    }
}
