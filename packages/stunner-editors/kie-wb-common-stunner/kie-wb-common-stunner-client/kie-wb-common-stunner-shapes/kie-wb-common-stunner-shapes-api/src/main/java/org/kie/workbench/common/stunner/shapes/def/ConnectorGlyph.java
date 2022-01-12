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
package org.kie.workbench.common.stunner.shapes.def;

import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * The glyph type for a generic connector.
 * The default renderer displays it as an arrow.
 */
public final class ConnectorGlyph implements Glyph {

    private final String color;

    public static ConnectorGlyph create(final String color) {
        return new ConnectorGlyph(color);
    }

    public static ConnectorGlyph create() {
        return new ConnectorGlyph("#000000");
    }

    private ConnectorGlyph(final String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
