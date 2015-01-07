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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.image.ImageClipBounds;
import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.image.ImageShapeLoadedHandler;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.ImageSerializationMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.UriUtils;

public abstract class AbstractImageShape<T extends AbstractImageShape<T>> extends Shape<T>
{
    private final ImageProxy<T> m_proxy;

    protected AbstractImageShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);

        m_proxy = new ImageProxy<T>(upcast());
    }

    protected AbstractImageShape(final ShapeType type, final String url, final boolean listening, final ImageSelectionMode mode)
    {
        super(type);

        setURL(url);

        setListening(listening);

        setImageSelectionMode(mode);

        m_proxy = new ImageProxy<T>(upcast());
    }

    protected AbstractImageShape(final ShapeType type, final ImageResource resource, final boolean listening, final ImageSelectionMode mode)
    {
        this(type, resource.getSafeUri().asString(), listening, mode);
    }

    public final ImageProxy<T> getImageProxy()
    {
        return m_proxy;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getImageProxy().getBoundingBox();
    }

    /**
     * Returns the URL of the image. For ImageResources, this return the
     * value of ImageResource.getSafeUri().asString().
     * 
     * @return String
     */
    public String getURL()
    {
        return getAttributes().getURL();
    }

    /**
     * Sets the URL of the image. For ImageResources, this should be the
     * value of ImageResource.getSafeUri().asString().
     * 
     * @param url
     * @return Picture
     */
    protected void setURL(final String url)
    {
        getAttributes().setURL(toValidURL(url));
    }

    protected String toValidURL(String url)
    {
        if (url.startsWith("data:"))
        {
            return url;
        }
        if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
        {
            throw new NullPointerException("null or empty or invalid url");
        }
        url = UriUtils.fromString(url).asString();

        if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
        {
            throw new NullPointerException("null or empty or invalid url");
        }
        return url;
    }

    public ImageSelectionMode getImageSelectionMode()
    {
        return getAttributes().getImageSelectionMode();
    }

    public T setImageSelectionMode(final ImageSelectionMode selectionMode)
    {
        getAttributes().setImageSelectionMode(selectionMode);

        return upcast();
    }

    public ImageSerializationMode getImageSerializationMode()
    {
        return getAttributes().getSerializationMode();
    }

    public T setImageSerializationMode(final ImageSerializationMode serializationMode)
    {
        getAttributes().setSerializationMode(serializationMode);

        return upcast();
    }

    public boolean isLoaded()
    {
        return m_proxy.isLoaded();
    }

    public String getLoadedMessage()
    {
        return m_proxy.getLoadedMessage();
    }

    public ImageData getImageData()
    {
        return m_proxy.getImageData();
    }

    public String toDataURL(final DataURLType mimeType, final boolean filtered)
    {
        return m_proxy.toDataURL(mimeType, filtered);
    }

    protected void setImageShapeLoadedHandler(final ImageShapeLoadedHandler<T> handler)
    {
        m_proxy.setImageShapeLoadedHandler(handler);
    }

    @SuppressWarnings("unchecked")
    private final T upcast()
    {
        return (T) this;
    }

    public abstract ImageClipBounds getImageClipBounds();
}
