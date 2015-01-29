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

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.types.NFastStringMap;

public final class AttributeMapper
{
    private static final AttributeMapper           INSTANCE = new AttributeMapper();

    private static final NFastStringMap<Attribute> ATTRSMAP = new NFastStringMap<Attribute>();

    public static final AttributeMapper get()
    {
        return INSTANCE;
    }

    private AttributeMapper()
    {
    }

    final void add(Attribute attr)
    {
        if (null != attr)
        {
            ATTRSMAP.put(attr.getProperty(), attr);
        }
    }

    public final Attribute find(String attribute)
    {
        return ATTRSMAP.get(attribute);
    }
}
