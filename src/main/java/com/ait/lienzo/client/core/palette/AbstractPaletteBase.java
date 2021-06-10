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

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.AbstractFactory;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.PaletteType;
import com.ait.lienzo.tools.client.collection.MetaData;

public abstract class AbstractPaletteBase<T extends AbstractPaletteBase<T>> implements IJSONSerializable<T>
{
    private final MetaData    m_meta;

    private final PaletteType m_type;

    protected AbstractPaletteBase(final PaletteType type)
    {
        m_type = type;

        m_meta = new MetaData();
    }
    
    public final MetaData getMetaData()
    {
        return m_meta;
    }

    @Override
    public IFactory<?> getFactory()
    {
        return LienzoCore.get().getFactory(m_type);
    }

    protected abstract static class AbstractPalettebaseFactory<T extends AbstractPaletteBase<T>> extends AbstractFactory<T>
    {
        protected AbstractPalettebaseFactory(final PaletteType type)
        {
            super(type.getValue());
        }
    }
}
