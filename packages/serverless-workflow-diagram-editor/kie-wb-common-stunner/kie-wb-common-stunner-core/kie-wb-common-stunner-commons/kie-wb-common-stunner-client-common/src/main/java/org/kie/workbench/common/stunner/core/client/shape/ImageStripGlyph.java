/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A glyph for referencing images from sprites.
 */
public final class ImageStripGlyph implements Glyph {

    private final Class<? extends ImageStrip> stripType;
    private final int index;

    public static ImageStripGlyph create(final Class<? extends ImageStrip> stripType,
                                         final int index) {
        return new ImageStripGlyph(stripType, index);
    }

    private ImageStripGlyph(final Class<? extends ImageStrip> stripType,
                            final int index) {
        this.stripType = stripType;
        this.index = index;
    }

    public Class<? extends ImageStrip> getStripType() {
        return stripType;
    }

    public int getIndex() {
        return index;
    }
}
