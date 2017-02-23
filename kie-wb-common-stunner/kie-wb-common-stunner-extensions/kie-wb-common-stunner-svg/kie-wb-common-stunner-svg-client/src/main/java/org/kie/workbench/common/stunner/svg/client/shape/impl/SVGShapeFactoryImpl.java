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

package org.kie.workbench.common.stunner.svg.client.shape.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.AbstractShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

@ApplicationScoped
public class SVGShapeFactoryImpl extends AbstractShapeDefFactory<Object, ShapeView, Shape<ShapeView>, ShapeDef<Object>>
        implements SVGShapeFactory<Object, AbstractCanvasHandler> {

    private final SyncBeanManager beanManager;
    private final GlyphBuilderFactory glyphBuilderFactory;

    protected SVGShapeFactoryImpl() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public SVGShapeFactoryImpl(final SyncBeanManager beanManager,
                               final GlyphBuilderFactory glyphBuilderFactory,
                               final DefinitionManager definitionManager,
                               final FactoryManager factoryManager) {
        super(definitionManager,
              factoryManager);
        this.beanManager = beanManager;
        this.glyphBuilderFactory = glyphBuilderFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape<ShapeView> build(final Object definition,
                                  final AbstractCanvasHandler context) {
        final String id = getDefinitionId(definition);
        final SVGShapeDef proxy = (SVGShapeDef) getShapeDef(id);
        return build(definition,
                     proxy,
                     context);
    }

    @SuppressWarnings("unchecked")
    protected Shape build(final Object definition,
                          final SVGShapeDef def,
                          final AbstractCanvasHandler context) {
        final Object factory = getViewFactory(def);
        final SVGShapeView view = def.newViewInstance(factory,
                                                      definition);
        if (def instanceof SVGMutableShapeDef) {
            final SVGMutableShapeDef<Object, Object> mutableShapeDef = (SVGMutableShapeDef<Object, Object>) def;
            return new SVGMutableShapeImpl<Object, SVGMutableShapeDef<Object, Object>>(mutableShapeDef,
                                                                                       (SVGShapeViewImpl) view);
        }
        return new SVGShapeImpl(view);
    }

    @Override
    protected Glyph glyph(final Class<?> clazz,
                          final double width,
                          final double height) {
        final ShapeDef<Object> shapeDef = getShapeDef(clazz);
        final GlyphDef<Object> glyphDef = shapeDef.getGlyphDef();
        return glyphBuilderFactory
                .getBuilder(glyphDef)
                .definitionType(clazz)
                .glyphDef(glyphDef)
                .factory(this)
                .height(height)
                .width(width)
                .build();
    }

    public String getDefinitionId(final Object definition) {
        return definitionManager.adapters().forDefinition().getId(definition);
    }

    public Object getViewFactory(final SVGShapeDef def) {
        final Class<?> viewFactoryType = def.getViewFactoryType();
        final Object factory = beanManager.lookupBean(viewFactoryType).getInstance();
        if (null == factory) {
            throw new RuntimeException("No SVG view factory present of type [" + viewFactoryType.getName() + "]");
        }
        return factory;
    }
}
