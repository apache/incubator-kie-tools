/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.tooltip;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

@Dependent
public class DefinitionGlyphTooltipImpl
        extends GlyphTooltipImpl
        implements DefinitionGlyphTooltip<Group> {

    DefinitionManager definitionManager;
    ShapeManager shapeManager;
    FactoryManager factoryManager;

    private String prefix;
    private String suffix;

    protected DefinitionGlyphTooltipImpl() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DefinitionGlyphTooltipImpl(final DefinitionManager definitionManager,
                                      final ShapeManager shapeManager,
                                      final FactoryManager factoryManager,
                                      final View view) {
        super(view);
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.shapeManager = shapeManager;
    }

    @Override
    public DefinitionGlyphTooltip<Group> setPrefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public DefinitionGlyphTooltip<Group> setSuffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public DefinitionGlyphTooltipImpl showTooltip(final String definitionId,
                                                  final double x,
                                                  final double y,
                                                  final GlyphTooltip.Direction direction) {
        final String title = getTitle(definitionId);
        if (null != title) {
            this.show(getTitleToShow(title),
                      x,
                      y,
                      direction);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DefinitionGlyphTooltipImpl showGlyph(final String defSetId,
                                                final String definitionId,
                                                final double x,
                                                final double y,
                                                final double width,
                                                final double height,
                                                final GlyphTooltip.Direction direction) {
        final String title = getTitle(definitionId);
        if (null != title) {
            final ShapeFactory<?, ?, ?> factory = shapeManager.getDefaultShapeSet(defSetId).getShapeFactory();
            final Glyph glyph = factory.glyph(definitionId,
                                              width,
                                              height);
            this.show(glyph,
                      getTitleToShow(title),
                      x,
                      y,
                      direction);
        }
        return this;
    }

    // TODO: Do not create model instances here.
    private String getTitle(final String id) {
        if (null != id && id.trim().length() > 0) {
            final Object def = factoryManager.newDefinition(id);
            if (null != def) {
                return definitionManager.adapters().forDefinition().getTitle(def);
            }
        }
        return null;
    }

    private String getTitleToShow(final String text) {
        return (null != prefix && prefix.trim().length() > 0 ? prefix : "")
                + text
                + (null != suffix && suffix.trim().length() > 0 ? suffix : "");
    }
}
