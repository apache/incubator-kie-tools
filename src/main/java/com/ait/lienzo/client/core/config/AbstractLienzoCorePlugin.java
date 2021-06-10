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

package com.ait.lienzo.client.core.config;

import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.tools.client.StringOps;
import java.util.function.Supplier;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import com.ait.lienzo.tools.common.api.types.IStringValued;

import elemental2.core.JsArray;
import jsinterop.base.Js;

public abstract class AbstractLienzoCorePlugin implements ILienzoPlugin
{
    private final NFastStringMap<IFactory<?>>           m_factories = new NFastStringMap<IFactory<?>>();

    private final NFastStringMap<Supplier<IFactory<?>>> m_suppliers = new NFastStringMap<Supplier<IFactory<?>>>();

    protected AbstractLienzoCorePlugin()
    {
    }

    protected final boolean addFactorySupplier(final IStringValued type, final Supplier<IFactory<?>> supplier)
    {
        return addFactorySupplier((null != type) ? type.getValue() : null, supplier);
    }

    protected final boolean addFactorySupplier(String name, final Supplier<IFactory<?>> supplier)
    {
        if (null == (name = StringOps.toTrimOrNull(name)))
        {
            return false;
        }
        if (null == supplier)
        {
            return false;
        }
        if (null != m_suppliers.get(name))
        {
            LienzoCore.get().error("Supplier for type " + name + "  has already been defined.");

            return false;
        }
        else
        {
            m_suppliers.put(name, supplier);

            return true;
        }
    }

    @Override
    public final String[] keys()
    {
        return Js.uncheckedCast(JsArray.from(m_suppliers.keys()));
    }

    @Override
    public final IFactory<?> getFactory(final IStringValued type)
    {
        return getFactory((null != type) ? type.getValue() : null);
    }

    @Override
    public final IFactory<?> getFactory(String name)
    {
        if (null == (name = StringOps.toTrimOrNull(name)))
        {
            return null;
        }
        IFactory<?> factory = m_factories.get(name);

        if (null != factory)
        {
            return factory;
        }
        final Supplier<IFactory<?>> supplier = m_suppliers.get(name);

        if (null != supplier)
        {
            factory = supplier.get();

            if (null != factory)
            {
                m_factories.put(name, factory);

                return factory;
            }
        }
        return null;
    }
}
