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

public abstract class ImageLoader
{
    private final JSImage m_js_img;

    private boolean       m_loaded = false;

    public ImageLoader(String url)
    {
        m_js_img = JSImage.make(url, this);
    }

    /**
     * Return true if the image was already loaded.
     * @return boolean
     */
    public final boolean isLoaded()
    {
        return m_loaded;
    }

    /**
     * Get width
     * @return int
     */
    public final int getWidth()
    {
        return m_js_img.getWidth();
    }

    /**
     * Get Height.
     * @return int
     */
    public final int getHeight()
    {
        return m_js_img.getHeight();
    }

    /**
     * Get the JSO
     * @return {@link JSImage}
     */
    public final JSImage getJSImage()
    {
        return m_js_img;
    }

    @SuppressWarnings("unused")
    private final void onLoadedHelper()
    {
        m_loaded = true;

        onLoaded(this);
    }

    @SuppressWarnings("unused")
    private final void onErrorHelper(String message)
    {
        m_loaded = false;

        onError(this, message);
    }

    public abstract void onLoaded(ImageLoader loader);

    public abstract void onError(ImageLoader loader, String message);
}
