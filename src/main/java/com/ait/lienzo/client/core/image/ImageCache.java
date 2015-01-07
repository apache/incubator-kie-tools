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

package com.ait.lienzo.client.core.image;

import java.util.HashMap;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

public final class ImageCache
{
    private static final ImageCache             INSTANCE   = new ImageCache();

    private final HashMap<String, String>       m_messages = new HashMap<String, String>();

    private final HashMap<String, ImageElement> m_url_hmap = new HashMap<String, ImageElement>();

    private final HashMap<String, ImageElement> m_key_hmap = new HashMap<String, ImageElement>();

    private int                                 m_counting = -1;

    private Runnable                            m_callback = null;

    public static final ImageCache get()
    {
        return INSTANCE;
    }

    private ImageCache()
    {
    }

    public final ImageCache add(String url)
    {
        return add(url, url);
    }

    public final ImageCache add(final String key, final String url)
    {
        if (m_counting < 0)
        {
            m_counting = 0;
        }
        m_counting++;

        new ImageLoader(url)
        {
            @Override
            public void onLoad(ImageElement image)
            {
                done(key, url, image, "success");
            }

            @Override
            public void onError(String message)
            {
                done(key, url, null, message);
            }
        };
        return this;
    }

    public final ImageCache add(final String key, final ImageResource resource)
    {
        if (m_counting < 0)
        {
            m_counting = 0;
        }
        m_counting++;

        new ImageLoader(resource)
        {
            @Override
            public void onLoad(ImageElement image)
            {
                done(key, resource.getName(), image, "success");
            }

            @Override
            public void onError(String message)
            {
                done(key, resource.getName(), null, message);
            }
        };
        return this;
    }

    private final void done(String key, String url, ImageElement image, String message)
    {
        if (null != image)
        {
            m_key_hmap.put(key, image);

            m_url_hmap.put(url, image);
        }
        m_messages.put(key, message);

        m_messages.put(url, message);

        m_counting--;

        if ((null != m_callback) && (m_counting == 0))
        {
            m_callback.run();
        }
    }

    public final ImageElement getImageByKey(String key)
    {
        return m_key_hmap.get(key);
    }

    public final ImageElement getImageByURL(String url)
    {
        return m_url_hmap.get(url);
    }

    public final String getMessage(String name)
    {
        return m_messages.get(name);
    }

    public final void reset()
    {
        if (m_counting > 0)
        {
            throw new IllegalStateException("ImageCache still loading");
        }
        m_counting = -1;

        m_messages.clear();

        m_key_hmap.clear();

        m_callback = null;
    }

    public final boolean isLoaded()
    {
        if (m_counting == 0)
        {
            return true;
        }
        return false;
    }

    public final void onLoaded(Runnable callback)
    {
        if (null == callback)
        {
            throw new NullPointerException("null callback for ImageCache");
        }
        if (m_counting < 0)
        {
            throw new IllegalStateException("ImageCache has no images added");
        }
        if (m_counting == 0)
        {
            callback.run();
        }
        else
        {
            m_callback = callback;
        }
    }
}
