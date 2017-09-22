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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class LienzoGlyphRenderers implements LienzoGlyphRenderer<Glyph> {

    private final Function<Class<?>, LienzoGlyphRenderer> rendererProvider;

    @Inject
    public LienzoGlyphRenderers(final @Any ManagedInstance<LienzoGlyphRenderer> lienzoGlyphRenderers) {
        this.rendererProvider = aClass -> GlyphRenderer.getRenderer(lienzoGlyphRenderers::spliterator,
                                                                    aClass);
    }

    LienzoGlyphRenderers(final Function<Class<?>, LienzoGlyphRenderer> rendererProvider) {
        this.rendererProvider = rendererProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Group render(final Glyph glyph,
                        final double width,
                        final double height) {
        return (Group) rendererProvider.apply(glyph.getClass())
                .render(glyph,
                        width,
                        height);
    }

    @Override
    public Class<Glyph> getGlyphType() {
        return Glyph.class;
    }
}
