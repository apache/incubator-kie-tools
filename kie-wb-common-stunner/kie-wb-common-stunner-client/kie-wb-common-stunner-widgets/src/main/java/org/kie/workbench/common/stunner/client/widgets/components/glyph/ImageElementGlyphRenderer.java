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

import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.uberfire.mvp.Command;

/**
 * DOM element renderer for image data-uri's glyphs.
 * It renders a DOM image element.
 */
@Dependent
public class ImageElementGlyphRenderer implements DOMGlyphRenderer<ImageDataUriGlyph> {

    private final Supplier<ImageElementRendererView> viewInstanceSupplier;
    private final Command viewInstancesDestroyer;

    protected ImageElementGlyphRenderer() {
        this.viewInstanceSupplier = null;
        this.viewInstancesDestroyer = null;
    }

    @Inject
    public ImageElementGlyphRenderer(final @Any ManagedInstance<ImageElementRendererView> viewInstances) {
        this.viewInstanceSupplier = viewInstances::get;
        this.viewInstancesDestroyer = viewInstances::destroyAll;
    }

    ImageElementGlyphRenderer(final Supplier<ImageElementRendererView> viewInstanceSupplier,
                              final Command viewInstancesDestroyer) {
        this.viewInstanceSupplier = viewInstanceSupplier;
        this.viewInstancesDestroyer = viewInstancesDestroyer;
    }

    @Override
    public Class<ImageDataUriGlyph> getGlyphType() {
        return ImageDataUriGlyph.class;
    }

    @Override
    public IsElement render(final ImageDataUriGlyph glyph,
                            final double width,
                            final double height) {
        final ImageElementRendererView view = viewInstanceSupplier.get();
        return view.setImage(glyph.getUri(),
                             (int) width,
                             (int) height);
    }

    @PreDestroy
    public void destroy() {
        viewInstancesDestroyer.execute();
    }
}
