/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.shared.core.types;

import com.ait.lienzo.tools.common.api.types.IStringValued;

public class PaletteType implements IStringValued {

    public static final PaletteType PALETTE = new PaletteType("Palette");

    public static final PaletteType PALETTE_ITEM = new PaletteType("PaletteItem");

    private final String m_value;

    protected PaletteType(final String value) {
        m_value = value;
    }

    @Override
    public final String toString() {
        return m_value;
    }

    @Override
    public final String getValue() {
        return m_value;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PaletteType)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return ((PaletteType) other).getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
