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

package com.ait.lienzo.client.core.shape.json;

import java.util.HashSet;

import com.ait.lienzo.client.core.config.ILienzoPlugin;
import com.ait.lienzo.client.core.config.LienzoCore;

/**
 * This class is a central repository for all {@link IJSONSerializable} factories.  
 * If you create a new class and you would like to be able to serialize / deserialize
 * it, you will need to register it here using {@link #registerFactory(String, IFactory)}.
 */
public final class FactoryRegistry
{
    private static FactoryRegistry INSTANCE;

    private FactoryRegistry()
    {
    }

    /**
     * Returns the {@link IFactory} for the specified type name.
     * 
     * @param typeName
     * @return IFactory
     */
    public final IFactory<?> getFactory(final String name)
    {
        for (ILienzoPlugin plugin : LienzoCore.get().getPlugins())
        {
            final IFactory<?> factory = plugin.getFactory(name);

            if (null != factory)
            {
                return factory;
            }
        }
        return null;
    }

    /**
     * Returns the singleton FactoryRegistry.
     * @return FactoryRegistry
     */
    public static final FactoryRegistry get()
    {
        if (null == INSTANCE)
        {
            INSTANCE = new FactoryRegistry();

            final HashSet<String> seen = new HashSet<String>();

            for (ILienzoPlugin plugin : LienzoCore.get().getPlugins())
            {
                for (String name : plugin.keys())
                {
                    if (seen.contains(name))
                    {
                        LienzoCore.get().error("Factory for type " + name + " in plugin " + plugin.getNameSpace() + " has already been defined.");
                    }
                    else
                    {
                        seen.add(name);
                    }
                }
            }
        }
        return INSTANCE;
    }
}
