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

package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.PaletteType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PaletteTypeTest {

    @Test
    public void testEquals() {
        PaletteType palette = PaletteType.PALETTE;
        assertFalse(palette.equals(null));
        assertTrue(palette.equals(palette));

        PaletteType paletteItem = PaletteType.PALETTE_ITEM;
        assertFalse(palette.equals(paletteItem));
        assertFalse(paletteItem.equals(palette));

        PaletteTypeExtension other = PaletteTypeExtension.OTHER;
        assertFalse(other.equals(palette));
        assertFalse(palette.equals(other));

        PaletteTypeExtension overloadPalette = PaletteTypeExtension.PALETTE;
        assertTrue(palette.equals(overloadPalette));
        assertTrue(overloadPalette.equals(palette));
    }

    private static class PaletteTypeExtension extends PaletteType {

        public static final PaletteTypeExtension OTHER = new PaletteTypeExtension("Other");
        public static final PaletteTypeExtension PALETTE = new PaletteTypeExtension("Palette");

        protected PaletteTypeExtension(String value) {
            super(value);
        }
    }
}
