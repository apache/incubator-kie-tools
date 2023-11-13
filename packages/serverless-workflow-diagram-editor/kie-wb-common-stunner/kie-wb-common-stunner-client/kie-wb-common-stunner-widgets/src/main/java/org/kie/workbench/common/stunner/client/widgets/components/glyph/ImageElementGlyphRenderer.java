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

import io.crysknife.client.IsElement;
import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
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
