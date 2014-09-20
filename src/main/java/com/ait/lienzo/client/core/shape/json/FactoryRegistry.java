/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape.json;

import com.ait.lienzo.client.core.config.ILienzoPlugin;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.types.NFastStringMap;

/**
 * This class is a central repository for all {@link IJSONSerializable} factories.  
 * If you create a new class and you would like to be able to serialize / deserialize
 * it, you will need to register it here using {@link #registerFactory(String, IFactory)}.
 */
public final class FactoryRegistry
{
    private static FactoryRegistry            s_instance;

    private final NFastStringMap<IFactory<?>> m_factories = new NFastStringMap<IFactory<?>>();

    private FactoryRegistry()
    {
    }

    /**
     * Adds a {@link IFactory} to this registry.
     * <p>
     * Use this when you're creating your own class and you want to be able to deserialize
     * your node from a JSON string via {@link JSONDeserializer#fromString(String)}.
     * 
     * @param factory IFactory
     * @return this FactoryRegistry
     */
    public final FactoryRegistry registerFactory(IFactory<?> factory)
    {
        String type = factory.getTypeName();

        if (null == m_factories.get(type))
        {
            m_factories.put(type, factory);
        }
        else
        {
            LienzoCore.get().log("WARNING: IFactory for " + type + " was already registered. Try prefixing your type names e.g. with 'foo_' to avoid conflicts with the built-in Lienzo nodes.");
        }
        return this;
    }

    /**
     * Returns the {@link IFactory} for the specified type name.
     * 
     * @param typeName
     * @return IFactory
     */
    public final IFactory<?> getFactory(String typeName)
    {
        return m_factories.get(typeName);
    }

    /**
     * Returns the singleton FactoryRegistry.
     * @return FactoryRegistry
     */
    public static final FactoryRegistry getInstance()
    {
        if (null == s_instance)
        {
            s_instance = new FactoryRegistry();

            for (ILienzoPlugin plugin : LienzoCore.get().getPlugins())
            {
                for (IFactory<?> factory : plugin.getFactories())
                {
                    s_instance.registerFactory(factory);
                }
            }
        }
        return s_instance;
    }
}
