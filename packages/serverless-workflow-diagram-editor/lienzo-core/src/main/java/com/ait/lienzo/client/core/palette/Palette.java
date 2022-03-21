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

package com.ait.lienzo.client.core.palette;

import java.util.List;

import com.ait.lienzo.shared.core.types.PaletteType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public final class Palette extends AbstractPaletteBase<Palette> {

    private final NFastArrayList<PaletteItem> m_list = new NFastArrayList<>();

    public Palette() {
        super(PaletteType.PALETTE);
    }

    public int size() {
        return m_list.size();
    }

    public Palette addPaletteItem(final PaletteItem item) {
        if (null != item) {
            if (!m_list.contains(item)) {
                m_list.add(item);
            }
        }
        return this;
    }

    public Palette setPaletteItems(final List<PaletteItem> items) {
        m_list.clear();

        for (PaletteItem item : items) {
            addPaletteItem(item);
        }
        return this;
    }

    public NFastArrayList<PaletteItem> getPaletteItems() {
        return m_list;
    }
}
