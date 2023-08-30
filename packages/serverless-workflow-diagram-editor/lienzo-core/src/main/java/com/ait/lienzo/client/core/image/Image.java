/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.image;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.IDestroyable;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

public class Image
        extends Shape<Image>
        implements IDestroyable {

    ImageBitmapProxy imageProxy;

    @JsProperty
    private String url;

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

    public Image(final String stripName,
                 final int index) {
        this(ImageStrips.encodeURL(stripName,
                                   index),
             image -> {
                 // Defaults to an empty callback, as the strip image has been already loaded
             });
    }

    public Image(final String url,
                 final ImageLoadCallback callback) {
        super(ShapeType.IMAGE);
        configure(url);
        load(callback);
    }

    Image() {
        super(ShapeType.IMAGE);
    }

    /**
     * Returns the x coordinate of the picture's clip region.
     * The default value is 0.
     */
    public int getClippedImageStartX() {
        return this.clippedImageStartX;
    }

    /**
     * Sets the x coordinate of the picture's clip region.
     * The default value is 0.
     */
    public Image setClippedImageStartX(int sx) {
        this.clippedImageStartX = sx;

        return this;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     */
    public int getClippedImageStartY() {
        return this.clippedImageStartY;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     */
    public Image setClippedImageStartY(int clippedImageStartY) {
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
     */
    public Image setClippedImageWidth(int clippedImageWidth) {
        this.clippedImageWidth = clippedImageWidth;

        return this;
    }

    /**
     * Returns the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     */
    public int getClippedImageHeight() {
        return this.clippedImageHeight;
    }

    /**
     * Sets the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     */
    public Image setClippedImageHeight(int clippedImageHeight) {
        this.clippedImageHeight = clippedImageHeight;

        return this;
    }

    /**
     * Returns the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     */
    public int getClippedImageDestinationWidth() {
        return this.clippedImageDestinationWidth;
    }

    /**
     * Sets the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     */
    public Image setClippedImageDestinationWidth(int clippedImageDestinationWidth) {
        this.clippedImageDestinationWidth = clippedImageDestinationWidth;

        return this;
    }

    /**
     * Returns the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p/>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p/>
     * Note that further scaling can be achieved via the <code>scaleWithXY</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     */
    public int getClippedImageDestinationHeight() {
        return this.clippedImageDestinationHeight;
    }

    /**
     * Sets the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p/>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p/>
     * Note that further scaling can be achieved via the <code>scaleWithXY</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     */
    public Image setClippedImageDestinationHeight(int clippedImageDestinationHeight) {
        this.clippedImageDestinationHeight = clippedImageDestinationHeight;

        return this;
    }

    private void destroyProxy() {
        if (null != imageProxy) {
            imageProxy.destroy();
        }
    }

    @Override
    protected boolean prepare(final Context2D context,
                              final double alpha) {
        context.save();

        if (!context.isSelection()) {
            context.setGlobalAlpha(alpha);

            if (getShadow() != null) {
                doApplyShadow(context);
            }
        }
        drawImage(context);

        context.restore();

        return false;
    }

    void drawImage(final Context2D context) {
        if (imageProxy.isLoaded()) {

            if (context.isSelection()) {
                final String color = getColorKey();

                if (null != color) {
                    context.save();

                    context.setFillColor(color);

                    context.fillRect(0, 0, getWidth(), getHeight());

                    context.restore();
                }
            } else {
                imageProxy.draw(context,
                                getImageClipBounds());
            }
        }
    }

    public ImageClipBounds getImageClipBounds() {
        return new ImageClipBounds(getClippedImageStartX(), getClippedImageStartY(), getClippedImageWidth(), getClippedImageHeight(), getClippedImageDestinationWidth(), getClippedImageDestinationHeight());
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.URL, Attribute.CLIPPED_IMAGE_START_X, Attribute.CLIPPED_IMAGE_START_Y, Attribute.CLIPPED_IMAGE_WIDTH, Attribute.CLIPPED_IMAGE_HEIGHT, Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH, Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, getWidth(), getHeight());
    }

    public double getWidth() {
        final int _destinationWidth = getClippedImageDestinationWidth();
        return _destinationWidth > 0 ? _destinationWidth : imageProxy.getWidth();
    }

    public double getHeight() {
        final int _destinationHeight = getClippedImageDestinationHeight();
        return _destinationHeight > 0 ? _destinationHeight : imageProxy.getHeight();
    }

    Image configure(final String url) {
        setURL(url); // this also validates the url, and throws exception if not valid or null
        destroyProxy();
        if (ImageStrips.isURLValid(url)) {
            final ImageStrips.ImageStripRef stripRef = ImageStrips.get().getRef(url);
            final ImageStrip strip = ImageStrips.get().get(stripRef.getName());
            imageProxy = ImageStrips.get().newProxy(strip);
            configureClipArea(strip,
                              stripRef.getIndex());
        } else {
            imageProxy = new ImageBitmapProxy();
            restoreClipArea();
        }
        return this;
    }

    Image load(final ImageLoadCallback callback) {
        if (!imageProxy.isLoaded()) {
            final String url = getURL();
            imageProxy.load(url,
                            () -> {
                                callback.onImageLoaded(Image.this);
                                performBatch();
                            });
        } else {
            callback.onImageLoaded(Image.this);
        }
        return this;
    }

    private void configureClipArea(final ImageStrip strip,
                                   final int index) {
        final int wide = strip.getWide();
        final int high = strip.getHigh();
        final int padding = strip.getPadding();
        final boolean isHorizontal = isStripOrientationHorizontal(strip);
        final int clipX = isHorizontal ? (wide + padding) * index : 0;
        final int clipY = !isHorizontal ? (high + padding) * index : 0;
        this.setClippedImageStartX(clipX);
        this.setClippedImageStartY(clipY);
        this.setClippedImageWidth(wide);
        this.setClippedImageHeight(high);
        this.setClippedImageDestinationWidth(wide);
        this.setClippedImageDestinationHeight(high);
    }

    private void restoreClipArea() {
        this.setClippedImageStartX(0);
        this.setClippedImageStartY(0);
        this.setClippedImageWidth(0);
        this.setClippedImageHeight(0);
        this.setClippedImageDestinationWidth(0);
        this.setClippedImageDestinationHeight(0);
    }

    private boolean isStripOrientationHorizontal(final ImageStrip strip) {
        return ImageStrip.Orientation.HORIZONTAL.equals(strip.getOrientation());
    }

    private void performBatch() {
        if (null != getLayer()) {
            getLayer().batch();
        }
    }

    /**
     * Returns the URL of the image. For ImageResources, this return the
     * value of ImageResource.getSafeUri().asString().
     *
     * @return String
     */
    public String getURL() {
        return this.url;
    }

    /**
     * Sets the URL of the image. For ImageResources, this should be the
     * value of ImageResource.getSafeUri().asString().
     *
     * @param url
     * @return Picture
     */
    protected void setURL(final String url) {
        this.url = Picture.toValidURL(url);
    }

    @Override
    public void destroy() {
        destroyProxy();
        removeFromParent();
    }
}
