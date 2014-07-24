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

package com.ait.lienzo.client.core.image;

import java.util.HashMap;

public final class ImageCache
{
    private static final ImageCache        INSTANCE = new ImageCache();

    private final HashMap<String, JSImage> m_cache  = new HashMap<String, JSImage>();

    public static final ImageCache get()
    {
        return INSTANCE;
    }

    private ImageCache()
    {
    }

    public final void save(String name, JSImage image)
    {
        m_cache.put(name, image);
    }

    public final JSImage find(String name)
    {
        return m_cache.get(name);
    }
}
