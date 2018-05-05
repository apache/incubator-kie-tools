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

package org.kie.workbench.common.stunner.svg.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.svg.client.shape.SVGShape;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGMutableShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

/**
 * A Shape Factory type that handles SVG Shapes.
 */
@Dependent
public class SVGShapeFactory
        implements ShapeDefFactory<Object, SVGShapeDef, SVGShape<?>> {

    private final SyncBeanManager beanManager;
    private final ShapeDefFunctionalFactory<Object, SVGShapeDef, Shape> functionalFactory;

    protected SVGShapeFactory() {
        this(null,
             null);
    }

    @Inject
    public SVGShapeFactory(final SyncBeanManager beanManager,
                           final ShapeDefFunctionalFactory<Object, SVGShapeDef, Shape> functionalFactory) {
        this.beanManager = beanManager;
        this.functionalFactory = functionalFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Set the shape instance builders for each supported svg shape definition..
        functionalFactory
                .set(SVGShapeDef.class,
                     this::newSVGShape)
                .set(SVGShapeViewDef.class,
                     this::newSVGMutableShape);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SVGShape<?> newShape(final Object instance,
                                final SVGShapeDef shapeDef) {
        return (SVGShape<?>) functionalFactory.newShape(instance,
                                                        shapeDef);
    }

    private SVGShape<?> newSVGShape(final Object instance,
                                    final SVGShapeDef shapeDef) {
        return new SVGShapeImpl(newSVGShapeView(instance,
                                                shapeDef));
    }

    @SuppressWarnings("unchecked")
    private SVGShape<?> newSVGMutableShape(final Object instance,
                                           final SVGShapeDef shapeDef) {
        final SVGShapeViewDef mutableShapeDef = (SVGShapeViewDef) shapeDef;
        final SVGShapeView view = newSVGShapeView(instance,
                                                  mutableShapeDef);
        return new SVGMutableShapeImpl<Object, SVGShapeViewDef<Object, Object>>(mutableShapeDef,
                                                                                (SVGShapeViewImpl) view);
    }

    @SuppressWarnings("unchecked")
    private SVGShapeViewImpl newSVGShapeView(final Object instance,
                                             final SVGShapeDef shapeDef) {
        final Object factory = getViewFactory(shapeDef);
        return (SVGShapeViewImpl) shapeDef.newViewInstance(factory,
                                                           instance);
    }

    Object getViewFactory(final SVGShapeDef def) {
        final Class<?> viewFactoryType = def.getViewFactoryType();
        final Object factory = beanManager.lookupBean(viewFactoryType).getInstance();
        if (null == factory) {
            throw new RuntimeException("No SVG view factory present of type [" + viewFactoryType.getName() + "]");
        }
        return factory;
    }
}
