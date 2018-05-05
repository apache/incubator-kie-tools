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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;

@Dependent
public class LienzoShapeGlyphRenderer implements LienzoGlyphRenderer<ShapeGlyph> {

    private final FactoryManager factoryManager;
    private final Function<ShapeView<?>, BoundingBox> boundingBoxProvider;
    private final Function<ShapeView<?>, Group> groupProvider;

    protected LienzoShapeGlyphRenderer() {
        this(null);
    }

    @Inject
    public LienzoShapeGlyphRenderer(final FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
        this.boundingBoxProvider = LienzoShapeGlyphRenderer::getBoundingBox;
        this.groupProvider = LienzoShapeGlyphRenderer::getGroup;
    }

    LienzoShapeGlyphRenderer(final FactoryManager factoryManager,
                             final Function<ShapeView<?>, BoundingBox> boundingBoxProvider,
                             final Function<ShapeView<?>, Group> groupProvider) {
        this.factoryManager = factoryManager;
        this.boundingBoxProvider = boundingBoxProvider;
        this.groupProvider = groupProvider;
    }

    @Override
    public Class<ShapeGlyph> getGlyphType() {
        return ShapeGlyph.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Group render(final ShapeGlyph glyph,
                        final double width,
                        final double height) {
        final String definitionId = glyph.getDefinitionId();
        final Supplier<ShapeFactory> factorySupplier = glyph.getFactorySupplier();
        final Shape shape = factorySupplier.get().newShape(factoryManager.newDefinition(definitionId));
        final ShapeView<?> view = shape.getShapeView();
        final BoundingBox bb = boundingBoxProvider.apply(view);
        Group group = groupProvider.apply(view);

        if (null == group) {
            throw new RuntimeException("Shape view [" + view.toString() + "] not supported for " +
                                               "this shape glyph builder [" + this.getClass().getName());
        }

        if (view instanceof HasTitle) {
            final HasTitle hasTitle = (HasTitle) view;
            hasTitle.setTitle(null);
        }

        // Create a copy of this view.
        group = group.copy();
        // Scale, if necessary, to the given glyph size.
        final double[] scale = LienzoUtils.getScaleFactor(bb.getWidth(),
                                                          bb.getHeight(),
                                                          width,
                                                          height);
        group.setScale(scale[0],
                       scale[1]);

        return group;
    }

    private static BoundingBox getBoundingBox(final ShapeView<?> view) {
        if (view instanceof WiresShape) {
            return ((WiresShape) view).getPath().getBoundingBox();
        } else if (view instanceof WiresConnector) {
            final WiresConnector wiresConnector = (WiresConnector) view;
            return wiresConnector.getGroup().getBoundingBox();
        }
        return null;
    }

    private static Group getGroup(final ShapeView<?> view) {
        if (view instanceof WiresShape) {
            return ((WiresShape) view).getGroup();
        } else if (view instanceof WiresConnector) {
            final WiresConnector wiresConnector = (WiresConnector) view;
            return wiresConnector.getGroup();
        }
        return null;
    }
}
