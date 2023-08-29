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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

import static org.kie.workbench.common.dmn.client.shape.def.DMNSVGShapeDefImpl.GLYPHS_PALETTE;

public class DecisionComponent {

    private final String fileName;

    private final boolean imported;

    private DRGElement drgElement;

    private static final Map<Class<?>, ImageDataUriGlyph> PALETTE_MAP = buildPaletteMap();

    public DecisionComponent(final String fileName,
                             final DRGElement drgElement,
                             final boolean imported) {
        this.fileName = fileName;
        this.drgElement = drgElement;
        this.imported = imported;
    }

    public boolean isImported() {
        return imported;
    }

    public String getFileName() {
        return fileName;
    }

    public DRGElement getDrgElement() {
        return drgElement;
    }

    public String getName() {
        return drgElement.getName().getValue();
    }

    public ImageDataUriGlyph getIcon() {
        return PALETTE_MAP.get(drgElement.getClass());
    }

    private static Map<Class<?>, ImageDataUriGlyph> buildPaletteMap() {
        final Map<Class<?>, ImageDataUriGlyph> map = new HashMap<>();

        GLYPHS_PALETTE.forEach((aClass, glyph) -> map.put(aClass, (ImageDataUriGlyph) glyph));

        return map;
    }
}
