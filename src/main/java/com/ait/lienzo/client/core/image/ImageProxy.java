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

import java.util.Collection;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.client.core.image.filter.RGBIgnoreAlphaImageDataFilter;
import com.ait.lienzo.client.core.shape.AbstractImageShape;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

/**
 * ImageProxy is used by {@link AbstractImageShape} to load and draw the image.
 */
public class ImageProxy<T extends AbstractImageShape<T>> implements ImageDataFilterable<ImageProxy<T>>
{
    private T                                   m_image;

    private ImageElement                        m_jsimg;

    private final ScratchCanvas                 m_normalImage = new ScratchCanvas(0, 0);

    private final ScratchCanvas                 m_filterImage = new ScratchCanvas(0, 0);

    private final ScratchCanvas                 m_selectImage = new ScratchCanvas(0, 0);

    private int                                 m_clip_xpos;

    private int                                 m_clip_ypos;

    private int                                 m_clip_wide;

    private int                                 m_clip_high;

    private int                                 m_dest_wide;

    private int                                 m_dest_high;

    private boolean                             m_is_done     = false;

    private boolean                             m_x_forms     = false;

    private boolean                             m_fastout     = false;

    private String                              m_message     = "";

    private ImageShapeLoadedHandler<T>          m_handler;

    private final ImageDataFilterChain          m_filters     = new ImageDataFilterChain();

    private final RGBIgnoreAlphaImageDataFilter m_ignores;

    private ImageClipBounds                     m_obounds     = null;

    /**
     * Creates an ImageProxy for the specified {@link AbstractImageShape}.
     * 
     * @param image {@link AbstractImageShape}
     */
    public ImageProxy(T image)
    {
        m_image = image;

        m_ignores = new RGBIgnoreAlphaImageDataFilter(Color.fromColorString(m_image.getColorKey()));
    }

    /**
     * Sets the {@link ImageShapeLoadedHandler} that will be notified when the image is loaded.
     * If the image is already loaded, the handler will be invoked immediately.
     * 
     * @param handler {@link ImageShapeLoadedHandler}
     */
    public void setImageShapeLoadedHandler(ImageShapeLoadedHandler<T> handler)
    {
        m_handler = handler;

        if ((null != m_handler) && (m_is_done))
        {
            m_handler.onImageShapeLoaded(m_image);
        }
    }

    public void reFilter(final ImageShapeFilteredHandler<T> handler)
    {
        if ((false == (m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
        {
            m_fastout = true;

            handler.onImageShapeFiltered(m_image);
        }
        else
        {
            if (m_fastout)
            {
                m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

                m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

                m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

                m_normalImage.clear();

                m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

                m_fastout = false;
            }
            boolean did_xform = m_x_forms;

            m_x_forms = m_filters.isTransforming();

            doFiltering(m_normalImage, m_filterImage, m_filters);

            if ((false == m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
            {
                handler.onImageShapeFiltered(m_image);
            }
            else if (did_xform || m_x_forms)
            {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                handler.onImageShapeFiltered(m_image);
            }
            else
            {
                handler.onImageShapeFiltered(m_image);
            }
        }
    }

    public void unFilter(final ImageShapeFilteredHandler<T> handler)
    {
        if ((false == (m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
        {
            m_fastout = true;

            handler.onImageShapeFiltered(m_image);
        }
        else
        {
            if (m_fastout)
            {
                m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

                m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

                m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

                m_normalImage.clear();

                m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

                m_fastout = false;
            }
            doFiltering(m_normalImage, m_filterImage, null);

            if ((false == m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
            {
                handler.onImageShapeFiltered(m_image);
            }
            else if (m_x_forms)
            {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                handler.onImageShapeFiltered(m_image);
            }
            else
            {
                handler.onImageShapeFiltered(m_image);
            }
        }
    }

    @Override
    public ImageProxy<T> setFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        m_filters.setFilters(filter, filters);

        return this;
    }

    @Override
    public ImageProxy<T> addFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        m_filters.addFilters(filter, filters);

        return this;
    }

    @Override
    public ImageProxy<T> removeFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        m_filters.removeFilters(filter, filters);

        return this;
    }

    @Override
    public ImageProxy<T> clearFilters()
    {
        m_filters.clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter> getFilters()
    {
        return m_filters.getFilters();
    }

    @Override
    public ImageProxy<T> setFiltersActive(boolean active)
    {
        m_filters.setActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive()
    {
        return m_filters.areFiltersActive();
    }

    @Override
    public ImageProxy<T> setFilters(Iterable<ImageDataFilter> filters)
    {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> addFilters(Iterable<ImageDataFilter> filters)
    {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> removeFilters(Iterable<ImageDataFilter> filters)
    {
        m_filters.removeFilters(filters);

        return this;
    }

    public void load(String url)
    {
        m_obounds = m_image.getImageClipBounds();

        m_clip_xpos = m_obounds.getClipXPos();

        m_clip_ypos = m_obounds.getClipYPos();

        m_clip_wide = m_obounds.getClipWide();

        m_clip_high = m_obounds.getClipHigh();

        m_dest_wide = m_obounds.getDestWide();

        m_dest_high = m_obounds.getDestHigh();

        new ImageLoader(url)
        {
            @Override
            public void onLoad(ImageElement image)
            {
                doInitialize(image);
            }

            @Override
            public void onError(String message)
            {
                doneLoading(false, message);
            }
        };
    }

    public void load(ImageResource resource)
    {
        m_obounds = m_image.getImageClipBounds();

        m_clip_xpos = m_obounds.getClipXPos();

        m_clip_ypos = m_obounds.getClipYPos();

        m_clip_wide = m_obounds.getClipWide();

        m_clip_high = m_obounds.getClipHigh();

        m_dest_wide = m_obounds.getDestWide();

        m_dest_high = m_obounds.getDestHigh();

        new ImageLoader(resource)
        {
            @Override
            public void onLoad(ImageElement image)
            {
                doInitialize(image);
            }

            @Override
            public void onError(String message)
            {
                doneLoading(false, message);
            }
        };
    }

    private final void doInitialize(ImageElement image)
    {
        m_jsimg = image;

        if (m_clip_wide == 0)
        {
            m_clip_wide = m_jsimg.getWidth();
        }
        if (m_clip_high == 0)
        {
            m_clip_high = m_jsimg.getHeight();
        }
        if (m_dest_wide == 0)
        {
            m_dest_wide = m_clip_wide;
        }
        if (m_dest_high == 0)
        {
            m_dest_high = m_clip_high;
        }
        if ((false == (m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
        {
            m_fastout = true;

            doneLoading(true, "loaded " + m_image.getURL());
        }
        else
        {
            m_fastout = false;

            m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

            m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

            m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

            m_normalImage.clear();

            m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

            m_x_forms = m_filters.isTransforming();

            doFiltering(m_normalImage, m_filterImage, m_filters);

            if ((false == m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
            {
                doneLoading(true, "loaded " + m_image.getURL());
            }
            else
            {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                doneLoading(true, "loaded " + m_image.getURL());
            }
        }
    }

    private final void doUpdateCheck()
    {
        ImageClipBounds bounds = m_image.getImageClipBounds();

        if (m_obounds.isDifferent(bounds))
        {
            m_obounds = bounds;

            m_clip_xpos = m_obounds.getClipXPos();

            m_clip_ypos = m_obounds.getClipYPos();

            m_clip_wide = m_obounds.getClipWide();

            m_clip_high = m_obounds.getClipHigh();

            m_dest_wide = m_obounds.getDestWide();

            m_dest_high = m_obounds.getDestHigh();

            if (m_clip_wide == 0)
            {
                m_clip_wide = m_jsimg.getWidth();
            }
            if (m_clip_high == 0)
            {
                m_clip_high = m_jsimg.getHeight();
            }
            if (m_dest_wide == 0)
            {
                m_dest_wide = m_clip_wide;
            }
            if (m_dest_high == 0)
            {
                m_dest_high = m_clip_high;
            }
            if ((false == (m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()))
            {
                m_fastout = true;
            }
            else
            {
                m_fastout = false;

                m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

                m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

                m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

                m_normalImage.clear();

                m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

                m_x_forms = m_filters.isTransforming();

                doFiltering(m_normalImage, m_filterImage, m_filters);

                if ((m_image.isListening()) && (ImageSelectionMode.SELECT_NON_TRANSPARENT == m_image.getImageSelectionMode()))
                {
                    doFiltering(m_filterImage, m_selectImage, m_ignores);
                }
            }
        }
    }

    private final void doFiltering(ScratchCanvas source, ScratchCanvas target, ImageDataFilter filter)
    {
        if ((null == filter) || (false == filter.isActive()))
        {
            target.clear();

            target.getContext().putImageData(source.getContext().getImageData(0, 0, m_dest_wide, m_dest_high), 0, 0);
        }
        else
        {
            target.clear();

            target.getContext().putImageData(filter.filter(source.getContext().getImageData(0, 0, m_dest_wide, m_dest_high), false), 0, 0);
        }
    }

    /**
     * Draws the image in the {@link Context2D}.
     * 
     * @param context {@link Context2D}
     */
    public void drawImage(Context2D context)
    {
        if (isLoaded())
        {
            doUpdateCheck();

            if (context.isSelection())
            {
                if (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())
                {
                    context.setFillColor(m_image.getColorKey());

                    context.beginPath();

                    context.rect(0, 0, m_dest_wide, m_dest_high);

                    context.fill();

                    context.closePath();
                }
                else
                {
                    context.drawImage(m_selectImage.getElement(), 0, 0);
                }
            }
            else
            {
                if (m_fastout)
                {
                    context.drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);
                }
                else
                {
                    context.drawImage(m_filterImage.getElement(), 0, 0);
                }
            }
        }
    }

    /**
     * Returns whether the image has been loaded and whether the
     * selection layer image has been prepared (if needed.)
     * 
     * @return
     */
    public boolean isLoaded()
    {
        return m_is_done;
    }

    public String getLoadedMessage()
    {
        return m_message;
    }

    /**
     * Returns an ImageData object that can be used for further image processing
     * e.g. by image filters.
     * 
     * @return ImageData
     */
    public ImageData getImageData()
    {
        if (false == isLoaded())
        {
            return null;
        }
        if (m_fastout)
        {
            ScratchCanvas temp = new ScratchCanvas(m_dest_wide, m_dest_high);

            temp.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

            return temp.getContext().getImageData(0, 0, m_dest_wide, m_dest_high);
        }
        else
        {
            return m_filterImage.getContext().getImageData(0, 0, m_dest_wide, m_dest_high);
        }
    }

    /**
     * Returns the "data:" URL
     * 
     * @param mimeType If null, defaults to DataURLType.PNG
     * @return String
     */
    public String toDataURL(DataURLType mimeType)
    {
        if (false == isLoaded())
        {
            return null;
        }
        if (mimeType == null)
        {
            mimeType = DataURLType.PNG;
        }
        if (m_fastout)
        {
            ScratchCanvas temp = new ScratchCanvas(m_dest_wide, m_dest_high);

            temp.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

            return temp.toDataURL(mimeType);
        }
        else
        {
            return m_filterImage.toDataURL(mimeType);
        }

    }

    protected void doneLoading(boolean loaded, String message)
    {
        m_is_done = loaded;

        m_message = message;

        if (m_handler != null)
        {
            m_handler.onImageShapeLoaded(m_image);
        }
    }

    public int getWidth()
    {
        return m_dest_wide;
    }

    public int getHeight()
    {
        return m_dest_high;
    }

    public ImageElement getImage()
    {
        return m_jsimg;
    }
}
