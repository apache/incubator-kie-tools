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

package com.ait.lienzo.client.core.image;

import com.ait.lienzo.tools.client.collection.NFastStringMap;
import org.kie.j2cl.tools.processors.common.resources.ImageResource;

public final class ImageCache {

    private static final ImageCache INSTANCE = new ImageCache();

    private final NFastStringMap<String> m_messages = new NFastStringMap<>();

    private final NFastStringMap<JsImageBitmap> m_url_hmap = new NFastStringMap<>();

    private final NFastStringMap<JsImageBitmap> m_key_hmap = new NFastStringMap<>();

    private int m_counting = -1;

    private Runnable m_callback = null;

    public static final ImageCache get() {
        return INSTANCE;
    }

    private ImageCache() {
    }

    public final ImageCache add(final String url) {
        return add(url, url);
    }

    public final ImageCache add(final String key, final String url) {
        if (m_counting < 0) {
            m_counting = 0;
        }
        m_counting++;

        JsImageBitmap.loadImageBitmap(url, new JsImageBitmapCallback() {
            @Override
            public void onSuccess(JsImageBitmap image) {
                done(key, url, image, "success");
            }

            @Override
            public void onError(Object error) {
                done(key, url, null, error.toString());
            }
        });

        return this;
    }

    public final ImageCache add(final String key, final ImageResource resource) {
        if (m_counting < 0) {
            m_counting = 0;
        }
        m_counting++;

        JsImageBitmap.loadImageBitmap(resource.getSrc(), new JsImageBitmapCallback() {
            @Override
            public void onSuccess(JsImageBitmap image) {
                done(key, resource.getName(), image, "success");
            }

            @Override
            public void onError(Object error) {
                done(key, resource.getName(), null, error.toString());
            }
        });

        return this;
    }

    private final void done(String key, String url, JsImageBitmap image, String message) {
        if (null != image) {
            m_key_hmap.put(key, image);

            m_url_hmap.put(url, image);
        }
        m_messages.put(key, message);

        m_messages.put(url, message);

        m_counting--;

        if ((null != m_callback) && (m_counting == 0)) {
            m_callback.run();
        }
    }

    public final JsImageBitmap getImageByKey(String key) {
        return m_key_hmap.get(key);
    }

    public final JsImageBitmap getImageByURL(String url) {
        return m_url_hmap.get(url);
    }

    public final String getMessage(String name) {
        return m_messages.get(name);
    }

    public final void reset() {
        if (m_counting > 0) {
            throw new IllegalStateException("ImageCache still loading");
        }
        m_counting = -1;

        m_messages.clear();

        m_key_hmap.clear();

        m_callback = null;
    }

    public final boolean isLoaded() {
        if (m_counting == 0) {
            return true;
        }
        return false;
    }

    public final void onLoaded(Runnable callback) {
        if (null == callback) {
            throw new NullPointerException("null callback for ImageCache");
        }
        if (m_counting < 0) {
            throw new IllegalStateException("ImageCache has no images added");
        }
        if (m_counting == 0) {
            callback.run();
        } else {
            m_callback = callback;
        }
    }
}
