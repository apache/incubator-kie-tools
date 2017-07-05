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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.util.Base64Util;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;

/**
 * Extracts the SVG content from the data-uri and appends the content into the DOM.
 * It renders as an SVG DOM element.
 */
@ApplicationScoped
public class SvgElementGlyphRenderer implements DOMGlyphRenderer<SvgDataUriGlyph> {

    public static final String SVG_DATA_URI_START = "data:image/svg+xml;base64,";

    private final Supplier<ImageElementRendererView> viewInstanceSupplier;

    protected SvgElementGlyphRenderer() {
        this.viewInstanceSupplier = null;
    }

    @Inject
    public SvgElementGlyphRenderer(final ManagedInstance<ImageElementRendererView> viewInstances) {
        this.viewInstanceSupplier = viewInstances::get;
    }

    SvgElementGlyphRenderer(final Supplier<ImageElementRendererView> viewInstanceSupplier) {
        this.viewInstanceSupplier = viewInstanceSupplier;
    }

    @Override
    public Class<SvgDataUriGlyph> getGlyphType() {
        return SvgDataUriGlyph.class;
    }

    @Override
    public IsElement render(final SvgDataUriGlyph glyph,
                            final double width,
                            final double height) {
        final String content = getSVGContent(glyph.getUri());
        final ImageElementRendererView view = viewInstanceSupplier.get();
        return view.setDOMContent(content,
                                  (int) width,
                                  (int) height);
    }

    private String getSVGContent(final SafeUri uri) {
        final String dataUri = uri.asString();
        if (dataUri.startsWith(SVG_DATA_URI_START)) {
            final String content = dataUri.substring(SVG_DATA_URI_START.length());
            return new String(Base64Util.decode(content));
        }
        throw new IllegalArgumentException("The image data-uri specified is not a valid SVG data " +
                                                   "for being emedded into the DOM.");
    }
}
