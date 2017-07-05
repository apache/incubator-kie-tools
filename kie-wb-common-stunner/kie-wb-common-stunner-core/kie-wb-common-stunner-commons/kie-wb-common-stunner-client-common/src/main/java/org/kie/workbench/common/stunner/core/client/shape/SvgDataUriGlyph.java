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

package org.kie.workbench.common.stunner.core.client.shape;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A glyph that represents an SVG image.
 * <p>
 * Default renderers display this image by parsing or attaching the svg declarations
 * into the client response, this way it provides full DOM support for the SVG, as it's not
 * just being rendered as an image (as when using an ImageDataUriGlyph).
 */
public final class SvgDataUriGlyph implements Glyph {

    private final SafeUri uri;

    public static SvgDataUriGlyph create(final SafeUri uri) {
        return new SvgDataUriGlyph(uri);
    }

    private SvgDataUriGlyph(final SafeUri uri) {
        this.uri = uri;
    }

    public SafeUri getUri() {
        return uri;
    }
}
