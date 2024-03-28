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


package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.uberfire.mvp.Command;

/**
 * DOM element renderer for a ShapeGlyph.
 * It renders a LienzoPanel that contains the shape.
 */
@Dependent
public class ElementShapeGlyphRenderer extends LienzoElementGlyphRenderer<ShapeGlyph> {

    private final LienzoGlyphRenderer<ShapeGlyph> lienzoShapeGlyphRenderer;
    private final Command viewsDestroyer;

    protected ElementShapeGlyphRenderer() {
        super(null);
        this.lienzoShapeGlyphRenderer = null;
        this.viewsDestroyer = null;
    }

    @Inject
    public ElementShapeGlyphRenderer(final LienzoGlyphRenderer<ShapeGlyph> lienzoShapeGlyphRenderer,
                                     final @Any ManagedInstance<WidgetElementRendererView> viewInstances) {
        super(viewInstances::get);
        this.lienzoShapeGlyphRenderer = lienzoShapeGlyphRenderer;
        this.viewsDestroyer = viewInstances::destroyAll;
    }

    ElementShapeGlyphRenderer(final LienzoGlyphRenderer<ShapeGlyph> lienzoShapeGlyphRenderer,
                              final Supplier<WidgetElementRendererView> viewInstances,
                              final Command viewsDestroyer) {
        super(viewInstances);
        this.lienzoShapeGlyphRenderer = lienzoShapeGlyphRenderer;
        this.viewsDestroyer = viewsDestroyer;
    }

    @Override
    public Class<ShapeGlyph> getGlyphType() {
        return ShapeGlyph.class;
    }

    @PreDestroy
    public void destroy() {
        viewsDestroyer.execute();
    }

    @Override
    protected LienzoGlyphRenderer<ShapeGlyph> getLienzoGlyphRenderer() {
        return lienzoShapeGlyphRenderer;
    }
}
