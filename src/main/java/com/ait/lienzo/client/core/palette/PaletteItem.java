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

import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.PaletteType;
import com.google.gwt.json.client.JSONObject;

public final class PaletteItem extends AbstractPaletteBase<PaletteItem>
{
    public PaletteItem()
    {
        super(PaletteType.PALETTE_ITEM);
    }

    protected PaletteItem(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(PaletteType.PALETTE_ITEM, node, ctx);
    }

    public static final class PaletteItemFactory extends AbstractPalettebaseFactory<PaletteItem>
    {
        public PaletteItemFactory()
        {
            super(PaletteType.PALETTE_ITEM);
        }

        @Override
        public PaletteItem create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new PaletteItem(node, ctx);
        }
    }
}
