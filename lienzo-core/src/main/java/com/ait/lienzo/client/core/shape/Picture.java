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

package com.ait.lienzo.client.core.shape;

import java.util.Collection;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.image.ImageClipBounds;
import com.ait.lienzo.client.core.image.PictureFilteredHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.resources.client.ImageResource;
import jsinterop.annotations.JsProperty;

/**
 * Image Support for Canvas
 * <ul>
 *  <li>Supports {@link ImageResource}</li>
 *  <li>Supports Image based on URL</li>
 * </ul>
 * <p>
 * If the <code>listening</code> attribute is set to false, it will not be drawn in the Selection Layer,
 * which means it can not be dragged or picked. This also means, it will not respond
 * to events.
 * <p>
 * The upside is that it will not need to generate a separate Image for the Selection Layer,
 * which saves memory and time, both for generating the selection layer Image and when drawing the Picture.
 */
public class Picture extends AbstractImageShape<Picture> implements ImageDataFilterable<Picture>,
                                                                    IDestroyable {

    @JsProperty
    private int clippedImageStartX;

    @JsProperty
    private int clippedImageStartY;

    @JsProperty
    private int clippedImageWidth;

    @JsProperty
    private int clippedImageHeight;

    @JsProperty
    private int clippedImageDestinationWidth;

    @JsProperty
    private int clippedImageDestinationHeight;

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(String url) {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(createPictureLoader());

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(String url, PictureLoadedHandler loadedHandler) {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, url, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, boolean listening) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(String url, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(ImageResource resource) {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(createPictureLoader());

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler) {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, resource, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource  ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, boolean listening) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource  ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource  ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource  ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth
     * @param sh        clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource  ImageResource
     * @param sx        clippedImageStartX
     * @param sy        clippedImageStartY
     * @param sw        clippedImageWidth (0 means: use image width)
     * @param sh        clippedImageHeight (0 means: use image height)
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int sx, int sy, int sw, int sh, int dw, int dh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource  ImageResource
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource  ImageResource
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource  ImageResource
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageSelectionMode mode) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource  ImageResource
     * @param dw        clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh        clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *                  but it will be drawn faster and use less memory.
     */
    public Picture(ImageResource resource, PictureLoadedHandler loadedHandler, int dw, int dh, boolean listening, ImageSelectionMode mode, ImageDataFilter<?>... filters) {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filters);

        getImageProxy().load(resource);
    }

    @Override
    public Picture setFilters(ImageDataFilter<?>... filters) {
        getImageProxy().setFilters(filters);

        return this;
    }

    @Override
    public Picture addFilters(ImageDataFilter<?>... filters) {
        getImageProxy().addFilters(filters);

        return this;
    }

    @Override
    public Picture removeFilters(ImageDataFilter<?>... filters) {
        getImageProxy().removeFilters(filters);

        return this;
    }

    @Override
    public Picture clearFilters() {
        getImageProxy().clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters() {
        return getImageProxy().getFilters();
    }

    @Override
    public Picture setFiltersActive(boolean active) {
        getImageProxy().setFiltersActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive() {
        return getImageProxy().areFiltersActive();
    }

    @Override
    public Picture setFilters(Iterable<ImageDataFilter<?>> filters) {
        getImageProxy().setFilters(filters);

        return this;
    }

    @Override
    public Picture addFilters(Iterable<ImageDataFilter<?>> filters) {
        getImageProxy().addFilters(filters);

        return this;
    }

    @Override
    public Picture removeFilters(Iterable<ImageDataFilter<?>> filters) {
        getImageProxy().removeFilters(filters);

        return this;
    }

    /**
     * Draws the image on the canvas.
     *
     * @param context
     */
    @Override
    protected boolean prepare(Context2D context, double alpha) {
        context.save();

        if (!context.isSelection()) {
            context.setGlobalAlpha(alpha);

            if (getShadow() != null) {
                doApplyShadow(context);
            }
        }
        getImageProxy().drawImage(context);

        context.restore();

        return false;
    }

    /**
     * Returns the x coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return int
     */
    public int getClippedImageStartX() {
        return this.clippedImageStartX;
    }

    /**
     * Sets the x coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @param sx
     * @return Picture this picture
     */
    public Picture setClippedImageStartX(int sx) {
        this.clippedImageStartX = sx;

        return this;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return int
     */
    public int getClippedImageStartY() {
        return this.clippedImageStartY;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return Picture this picture
     */
    public Picture setClippedImageStartY(int clippedImageStartY) {
        this.clippedImageStartY = clippedImageStartY;

        return this;
    }

    /**
     * Returns the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     *
     * @return int
     */
    public int getClippedImageWidth() {
        return this.clippedImageWidth;
    }

    /**
     * Sets the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     *
     * @param clippedImageWidth
     * @return Picture this picture
     */
    public Picture setClippedImageWidth(int clippedImageWidth) {
        this.clippedImageWidth = clippedImageWidth;

        return this;
    }

    /**
     * Returns the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     *
     * @return int
     */
    public int getClippedImageHeight() {
        return this.clippedImageHeight;
    }

    /**
     * Sets the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     *
     * @param clippedImageHeight
     * @return Picture this picture
     */
    public Picture setClippedImageHeight(int clippedImageHeight) {
        this.clippedImageHeight = clippedImageHeight;

        return this;
    }

    /**
     * Returns the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     *
     * @return int
     */
    public int getClippedImageDestinationWidth() {
        return this.clippedImageDestinationWidth;
    }

    /**
     * Sets the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     *
     * @param clippedImageDestinationWidth
     * @return Picture
     */
    public Picture setClippedImageDestinationWidth(int clippedImageDestinationWidth) {
        this.clippedImageDestinationWidth = clippedImageDestinationWidth;

        return this;
    }

    /**
     * Returns the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p>
     * Note that further scaling can be achieved via the <code>scaleWithXY</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     *
     * @return int
     */
    public int getClippedImageDestinationHeight() {
        return this.clippedImageDestinationHeight;
    }

    /**
     * Sets the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p>
     * Note that further scaling can be achieved via the <code>scaleWithXY</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     *
     * @param clippedImageDestinationHeight
     * @return Picture
     */
    public Picture setClippedImageDestinationHeight(int clippedImageDestinationHeight) {
        this.clippedImageDestinationHeight = clippedImageDestinationHeight;

        return this;
    }

    public Picture reFilter(final PictureFilteredHandler handler) {
        getImageProxy().reFilter(picture -> handler.onPictureFiltered(picture));
        return this;
    }

    public Picture unFilter(final PictureFilteredHandler handler) {
        getImageProxy().unFilter(picture -> handler.onPictureFiltered(picture));
        return this;
    }

    @Override
    public ImageClipBounds getImageClipBounds() {
        return new ImageClipBounds(getClippedImageStartX(), getClippedImageStartY(), getClippedImageWidth(), getClippedImageHeight(), getClippedImageDestinationWidth(), getClippedImageDestinationHeight());
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.URL, Attribute.CLIPPED_IMAGE_START_X, Attribute.CLIPPED_IMAGE_START_Y, Attribute.CLIPPED_IMAGE_WIDTH, Attribute.CLIPPED_IMAGE_HEIGHT, Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH, Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT);
    }

    @Override
    public void destroy() {
        getImageProxy().destroy();
        removeFromParent();
    }

    private PictureLoadedHandler createPictureLoader() {
        return picture -> {
            if (picture.isLoaded() && picture.isVisible()) {
                Layer layer = picture.getLayer();

                if ((null != layer) && (null != layer.getViewport())) {
                    layer.batch();
                }
            }
        };
    }

    private void onLoaded(final PictureLoadedHandler handler) {
        getImageProxy().setImageShapeLoadedHandler(picture -> handler.onPictureLoaded(picture));
    }
}
