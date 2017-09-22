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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class DOMGlyphRenderers implements DOMGlyphRenderer<Glyph> {

    private final Function<Class<?>, DOMGlyphRenderer> rendererProvider;

    protected DOMGlyphRenderers() {
        this.rendererProvider = null;
    }

    @Inject
    public DOMGlyphRenderers(final @Any ManagedInstance<DOMGlyphRenderer> domGlyphRenderers) {
        this.rendererProvider = aClass -> GlyphRenderer.getRenderer(domGlyphRenderers::spliterator,
                                                                    aClass);
    }

    DOMGlyphRenderers(final Function<Class<?>, DOMGlyphRenderer> rendererProvider) {
        this.rendererProvider = rendererProvider;
    }

    @Override
    public Class<Glyph> getGlyphType() {
        return Glyph.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IsElement render(Glyph glyph,
                            double width,
                            double height) {
        return (IsElement) rendererProvider
                .apply(glyph.getClass())
                .render(glyph,
                        width,
                        height);
    }
}
