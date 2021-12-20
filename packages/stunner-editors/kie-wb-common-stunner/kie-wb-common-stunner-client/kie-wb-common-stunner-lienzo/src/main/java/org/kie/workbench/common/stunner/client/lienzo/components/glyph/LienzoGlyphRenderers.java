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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class LienzoGlyphRenderers implements LienzoGlyphRenderer<Glyph> {

    private final ManagedInstance<LienzoGlyphRenderer> rendererInstances;
    private final List<LienzoGlyphRenderer> renderers;

    // CDI proxy.
    protected LienzoGlyphRenderers() {
        this(null);
    }

    @Inject
    public LienzoGlyphRenderers(final @Any ManagedInstance<LienzoGlyphRenderer> rendererInstances) {
        this.rendererInstances = rendererInstances;
        this.renderers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        rendererInstances.forEach(renderers::add);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Group render(final Glyph glyph,
                        final double width,
                        final double height) {
        return (Group) getRenderer(glyph.getClass())
                .render(glyph,
                        width,
                        height);
    }

    private LienzoGlyphRenderer getRenderer(final Class<?> type) {
        return GlyphRenderer.getRenderer(renderers::spliterator,
                                         type);
    }

    @Override
    public Class<Glyph> getGlyphType() {
        return Glyph.class;
    }

    @PreDestroy
    public void destroy() {
        renderers.clear();
        rendererInstances.destroyAll();
    }
}
