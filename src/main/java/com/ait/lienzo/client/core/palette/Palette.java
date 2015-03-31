/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.PaletteType;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public final class Palette extends AbstractPaletteBase<Palette>
{
    private final NFastArrayList<PaletteItem> m_list = new NFastArrayList<PaletteItem>();

    public Palette()
    {
        super(PaletteType.PALETTE);
    }

    protected Palette(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(PaletteType.PALETTE, node, ctx);
    }

    public int size()
    {
        return m_list.size();
    }

    public Palette addPaletteItem(final PaletteItem item)
    {
        if (null != item)
        {
            if (false == m_list.contains(item))
            {
                m_list.add(item);
            }
        }
        return this;
    }

    public Palette setPaletteItems(final List<PaletteItem> items)
    {
        m_list.clear();

        for (PaletteItem item : items)
        {
            addPaletteItem(item);
        }
        return this;
    }

    public NFastArrayList<PaletteItem> getPaletteItems()
    {
        return m_list;
    }

    @Override
    public JSONObject toJSONObject()
    {
        final int size = size();

        final JSONArray list = new JSONArray();

        for (int i = 0; i < size; i++)
        {
            final PaletteItem item = m_list.get(i);

            if (null != item)
            {
                final JSONObject make = item.toJSONObject();

                if (null != make)
                {
                    list.set(list.size(), make);
                }
            }
        }
        final JSONObject object = super.toJSONObject();

        object.put("items", list);

        return object;
    }

    public static final class PaletteFactory extends AbstractPalettebaseFactory<Palette>
    {
        public PaletteFactory()
        {
            super(PaletteType.PALETTE);
        }

        @Override
        public Palette create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            Palette palette = new Palette(node, ctx);

            JSONDeserializer.get().deserializePaletteItems(palette, node, ctx);

            return palette;
        }
    }
}
