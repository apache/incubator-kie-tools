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

import java.util.Collection;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.client.core.image.filter.RGBIgnoreAlphaImageDataFilter;
import com.ait.lienzo.client.core.shape.AbstractImageShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import elemental2.dom.ImageData;
import org.kie.j2cl.tools.processors.common.resources.ImageResource;

/**
 * ImageProxy is used by {@link AbstractImageShape} to load and draw the image.
 */
public class ImageProxy<T extends AbstractImageShape<T>> implements ImageDataFilterable<ImageProxy<T>> {

    private JsImageBitmap m_jsimg;

    private final T m_image;

    private final ScratchPad m_normalImage;

    private final ScratchPad m_filterImage;

    private final ScratchPad m_selectImage;

    private int m_clip_xpos;

    private int m_clip_ypos;

    private int m_clip_wide;

    private int m_clip_high;

    private int m_dest_wide;

    private int m_dest_high;

    private boolean m_is_done = false;

    private boolean m_x_forms = false;

    private boolean m_fastout = false;

    private String m_message = "";

    private String m_k_color = null;

    private ImageShapeLoadedHandler<T> m_handler;

    private ImageDataFilter<?> m_ignores = new ClearFilter();

    private final ImageDataFilterChain m_filters = new ImageDataFilterChain();

    private ImageClipBounds m_obounds = null;

    /**
     * Creates an ImageProxy for the specified {@link AbstractImageShape}.
     *
     * @param image {@link AbstractImageShape}
     */
    public ImageProxy(final T image) {
        this(image,
             new ScratchPad(0, 0),
             new ScratchPad(0, 0),
             new ScratchPad(0, 0),
             null);
    }

    ImageProxy(final T image,
               final ScratchPad normalImage,
               final ScratchPad filterImage,
               final ScratchPad selectionImage,
               final JsImageBitmap imageBitmap) {
        m_image = image;
        m_normalImage = normalImage;
        m_filterImage = filterImage;
        m_selectImage = selectionImage;
        m_jsimg = imageBitmap;
    }

    public final void load(final String url) {
        m_obounds = m_image.getImageClipBounds();

        m_clip_xpos = m_obounds.getClipXPos();

        m_clip_ypos = m_obounds.getClipYPos();

        m_clip_wide = m_obounds.getClipWide();

        m_clip_high = m_obounds.getClipHigh();

        m_dest_wide = m_obounds.getDestWide();

        m_dest_high = m_obounds.getDestHigh();

        loadImageBitmap(url);
    }

    public final void load(final ImageResource resource) {
        m_obounds = m_image.getImageClipBounds();

        m_clip_xpos = m_obounds.getClipXPos();

        m_clip_ypos = m_obounds.getClipYPos();

        m_clip_wide = m_obounds.getClipWide();

        m_clip_high = m_obounds.getClipHigh();

        m_dest_wide = m_obounds.getDestWide();

        m_dest_high = m_obounds.getDestHigh();

        loadImageBitmap(resource.getSrc());
    }

    private void loadImageBitmap(final String url) {
        JsImageBitmap.loadImageBitmap(url, new JsImageBitmapCallback() {
            @Override
            public void onSuccess(JsImageBitmap image) {
                doInitialize(image);
            }

            @Override
            public void onError(Object error) {
                doneLoading(false, error.toString());
            }
        });
    }

    private final void doInitialize(final JsImageBitmap image) {
        m_jsimg = image;

        if (m_clip_wide == 0) {
            m_clip_wide = m_jsimg.getWidth();
        }
        if (m_clip_high == 0) {
            m_clip_high = m_jsimg.getHeight();
        }
        if (m_dest_wide == 0) {
            m_dest_wide = m_clip_wide;
        }
        if (m_dest_high == 0) {
            m_dest_high = m_clip_high;
        }
        if ((!(m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
            m_fastout = true;

            doneLoading(true, "loaded " + m_image.getURL());
        } else {
            m_fastout = false;

            m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

            m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

            m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

            m_normalImage.clear();

            m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

            m_x_forms = m_filters.isTransforming();

            doFiltering(m_normalImage, m_filterImage, m_filters);

            if ((!m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
                doneLoading(true, "loaded " + m_image.getURL());
            } else {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                doneLoading(true, "loaded " + m_image.getURL());
            }
        }
    }

    /**
     * Returns whether the image has been loaded and whether the
     * selection layer image has been prepared (if needed.)
     *
     * @return
     */
    public boolean isLoaded() {
        return m_is_done;
    }

    public final void setColorKey(final String ckey) {
        if (null == ckey) {
            m_k_color = ckey;

            m_ignores = new ClearFilter();
        } else if (!ckey.equals(m_k_color)) {
            m_k_color = ckey;

            m_ignores = new RGBIgnoreAlphaImageDataFilter(m_k_color);
        } else {
            return;
        }
        if (isLoaded()) {
            doFiltering(m_filterImage, m_selectImage, m_ignores);

            if (m_image.isVisible()) {
                final Layer layer = m_image.getLayer();

                if (null != layer) {
                    layer.batch();
                }
            }
        }
    }

    public ImageDataFilterChain getFilterChain() {
        return m_filters;
    }

    /**
     * Sets the {@link ImageShapeLoadedHandler} that will be notified when the image is loaded.
     * If the image is already loaded, the handler will be invoked immediately.
     *
     * @param handler {@link ImageShapeLoadedHandler}
     */
    public void setImageShapeLoadedHandler(final ImageShapeLoadedHandler<T> handler) {
        m_handler = handler;

        if ((null != m_handler) && (m_is_done)) {
            m_handler.onImageShapeLoaded(m_image);
        }
    }

    public void reFilter(final ImageShapeFilteredHandler<T> handler) {
        if ((!(m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
            m_fastout = true;

            handler.onImageShapeFiltered(m_image);
        } else {
            if (m_fastout) {
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

            if ((!m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
                handler.onImageShapeFiltered(m_image);
            } else if (did_xform || m_x_forms) {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                handler.onImageShapeFiltered(m_image);
            } else {
                handler.onImageShapeFiltered(m_image);
            }
        }
    }

    public void unFilter(final ImageShapeFilteredHandler<T> handler) {
        if ((!(m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
            m_fastout = true;

            handler.onImageShapeFiltered(m_image);
        } else {
            if (m_fastout) {
                m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

                m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

                m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

                m_normalImage.clear();

                m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

                m_fastout = false;
            }
            doFiltering(m_normalImage, m_filterImage, null);

            if ((!m_image.isListening()) || (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
                handler.onImageShapeFiltered(m_image);
            } else if (m_x_forms) {
                doFiltering(m_filterImage, m_selectImage, m_ignores);

                handler.onImageShapeFiltered(m_image);
            } else {
                handler.onImageShapeFiltered(m_image);
            }
        }
    }

    @Override
    public ImageProxy<T> setFilters(final ImageDataFilter<?>... filters) {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> addFilters(final ImageDataFilter<?>... filters) {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> removeFilters(final ImageDataFilter<?>... filters) {
        m_filters.removeFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> clearFilters() {
        m_filters.clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters() {
        return m_filters.getFilters();
    }

    @Override
    public ImageProxy<T> setFiltersActive(final boolean active) {
        m_filters.setActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive() {
        return m_filters.areFiltersActive();
    }

    @Override
    public ImageProxy<T> setFilters(final Iterable<ImageDataFilter<?>> filters) {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> addFilters(final Iterable<ImageDataFilter<?>> filters) {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public ImageProxy<T> removeFilters(final Iterable<ImageDataFilter<?>> filters) {
        m_filters.removeFilters(filters);

        return this;
    }

    private final void doUpdateCheck() {
        ImageClipBounds bounds = m_image.getImageClipBounds();

        if (m_obounds == null || m_obounds.isDifferent(bounds)) {
            m_obounds = bounds;

            m_clip_xpos = m_obounds.getClipXPos();

            m_clip_ypos = m_obounds.getClipYPos();

            m_clip_wide = m_obounds.getClipWide();

            m_clip_high = m_obounds.getClipHigh();

            m_dest_wide = m_obounds.getDestWide();

            m_dest_high = m_obounds.getDestHigh();

            if (m_clip_wide == 0) {
                m_clip_wide = m_jsimg.getWidth();
            }
            if (m_clip_high == 0) {
                m_clip_high = m_jsimg.getHeight();
            }
            if (m_dest_wide == 0) {
                m_dest_wide = m_clip_wide;
            }
            if (m_dest_high == 0) {
                m_dest_high = m_clip_high;
            }
            if ((!(m_filters.isActive())) && (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode())) {
                m_fastout = true;
            } else {
                m_fastout = false;

                m_normalImage.setPixelSize(m_dest_wide, m_dest_high);

                m_filterImage.setPixelSize(m_dest_wide, m_dest_high);

                m_selectImage.setPixelSize(m_dest_wide, m_dest_high);

                m_normalImage.clear();

                m_normalImage.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

                m_x_forms = m_filters.isTransforming();

                doFiltering(m_normalImage, m_filterImage, m_filters);

                if ((m_image.isListening()) && (ImageSelectionMode.SELECT_NON_TRANSPARENT == m_image.getImageSelectionMode())) {
                    doFiltering(m_filterImage, m_selectImage, m_ignores);
                }
            }
        }
    }

    private final void doFiltering(final ScratchPad source, final ScratchPad target, final ImageDataFilter<?> filter) {
        if ((null == filter) || (!filter.isActive())) {
            target.clear();

            target.getContext().putImageData(source.getContext().getImageData(0, 0, m_dest_wide, m_dest_high), 0, 0);
        } else {
            target.clear();

            if (null != filter.getType()) {
                target.getContext().putImageData(filter.filter(source.getContext().getImageData(0, 0, m_dest_wide, m_dest_high), false), 0, 0);
            }
        }
    }

    /**
     * Draws the image in the {@link Context2D}.
     *
     * @param context {@link Context2D}
     */
    public void drawImage(final Context2D context) {
        if (isLoaded()) {
            doUpdateCheck();

            if (context.isSelection()) {
                if (ImageSelectionMode.SELECT_BOUNDS == m_image.getImageSelectionMode()) {
                    context.setFillColor(m_image.getColorKey());

                    context.beginPath();

                    context.rect(0, 0, m_dest_wide, m_dest_high);

                    context.fill();

                    context.closePath();
                } else {
                    context.drawImage(m_selectImage.getElement(), 0, 0);
                }
            } else {
                if (m_fastout) {
                    context.drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);
                } else {
                    context.drawImage(m_filterImage.getElement(), 0, 0);
                }
            }
        }
    }

    public String getLoadedMessage() {
        return m_message;
    }

    /**
     * Returns an ImageData object that can be used for further image processing
     * e.g. by image filters.
     *
     * @return ImageData
     */
    public ImageData getImageData() {
        if (!isLoaded()) {
            return null;
        }
        if (m_fastout) {
            ScratchPad temp = new ScratchPad(m_dest_wide, m_dest_high);

            temp.getContext().drawImage(m_jsimg, m_clip_xpos, m_clip_ypos, m_clip_wide, m_clip_high, 0, 0, m_dest_wide, m_dest_high);

            return temp.getContext().getImageData(0, 0, m_dest_wide, m_dest_high);
        } else {
            return m_filterImage.getContext().getImageData(0, 0, m_dest_wide, m_dest_high);
        }
    }

    public String toDataURL(final boolean filtered) {
        if (!isLoaded()) {
            return null;
        }
        if ((m_fastout) || (!filtered)) {
            final ScratchPad temp = new ScratchPad(m_jsimg.getWidth(), m_jsimg.getHeight());

            temp.getContext().drawImage(m_jsimg, 0, 0);

            return temp.toDataURL();
        } else {
            return m_filterImage.toDataURL();
        }
    }

    protected void doneLoading(final boolean loaded, final String message) {
        m_is_done = loaded;

        m_message = message;

        if (m_handler != null) {
            m_handler.onImageShapeLoaded(m_image);
        }
    }

    public int getWidth() {
        return m_dest_wide;
    }

    public int getHeight() {
        return m_dest_high;
    }

    public JsImageBitmap getImage() {
        return m_jsimg;
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, m_dest_wide, m_dest_high);
    }

    public void destroy() {
        m_image.removeFromParent();
        m_normalImage.clear();
        m_filterImage.clear();
        m_selectImage.clear();
        m_filters.clearFilters();
        m_handler = null;
        m_obounds = null;
        m_jsimg.close();
    }

    private static final class ClearFilter implements ImageDataFilter<ClearFilter> {

        @Override
        public ImageData filter(ImageData source, boolean copy) {
            return source;
        }

        @Override
        public boolean isTransforming() {
            return false;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void setActive(boolean active) {
        }

        @Override
        public ImageFilterType getType() {
            return null;
        }
    }
}
