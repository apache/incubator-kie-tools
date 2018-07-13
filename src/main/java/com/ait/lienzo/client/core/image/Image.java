package com.ait.lienzo.client.core.image;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.IDestroyable;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class Image
        extends Shape<Image>
        implements IDestroyable {

    ImageElementProxy imageProxy;

    public Image(final String stripName,
                 final int index) {
        this(ImageStrips.encodeURL(stripName,
                                   index),
             new ImageLoadCallback() {
                 @Override
                 public void onImageLoaded(Image image) {
                     // Defaults to an empty callback, as the strip image has been already loaded
                 }
             });
    }

    public Image(final String url,
                 final ImageLoadCallback callback) {
        super(ShapeType.IMAGE);
        configure(url);
        load(callback);
    }

    private Image(final JSONObject node,
                  final ValidationContext ctx) throws ValidationException {
        super(ShapeType.IMAGE, node, ctx);
    }

    Image() {
        super(ShapeType.IMAGE);
    }

    /**
     * Returns the x coordinate of the picture's clip region.
     * The default value is 0.
     */
    public int getClippedImageStartX() {
        return getAttributes().getClippedImageStartX();
    }

    /**
     * Sets the x coordinate of the picture's clip region.
     * The default value is 0.
     */
    public Image setClippedImageStartX(int sx) {
        getAttributes().setClippedImageStartX(sx);

        return this;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     */
    public int getClippedImageStartY() {
        return getAttributes().getClippedImageStartY();
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     */
    public Image setClippedImageStartY(int clippedImageStartY) {
        getAttributes().setClippedImageStartY(clippedImageStartY);

        return this;
    }

    /**
     * Returns the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     * @return int
     */
    public int getClippedImageWidth() {
        return getAttributes().getClippedImageWidth();
    }

    /**
     * Sets the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     */
    public Image setClippedImageWidth(int clippedImageWidth) {
        getAttributes().setClippedImageWidth(clippedImageWidth);

        return this;
    }

    /**
     * Returns the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     */
    public int getClippedImageHeight() {
        return getAttributes().getClippedImageHeight();
    }

    /**
     * Sets the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     */
    public Image setClippedImageHeight(int clippedImageHeight) {
        getAttributes().setClippedImageHeight(clippedImageHeight);

        return this;
    }

    /**
     * Returns the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     */
    public int getClippedImageDestinationWidth() {
        return getAttributes().getClippedImageDestinationWidth();
    }

    /**
     * Sets the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     */
    public Image setClippedImageDestinationWidth(int clippedImageDestinationWidth) {
        getAttributes().setClippedImageDestinationWidth(clippedImageDestinationWidth);

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
     * Note that further scaling can be achieved via the <code>scale</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     */
    public int getClippedImageDestinationHeight() {
        return getAttributes().getClippedImageDestinationHeight();
    }

    /**
     * Sets the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p/>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p/>
     * Note that further scaling can be achieved via the <code>scale</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     */
    public Image setClippedImageDestinationHeight(int clippedImageDestinationHeight) {
        getAttributes().setClippedImageDestinationHeight(clippedImageDestinationHeight);

        return this;
    }

    private void destroyProxy() {
        if (null != imageProxy) {
            imageProxy.destroy();
        }
    }

    @Override
    protected boolean prepare(final Context2D context,
                              final Attributes attr,
                              final double alpha) {
        context.save();

        if (!context.isSelection()) {
            context.setGlobalAlpha(alpha);

            if (attr.hasShadow()) {
                doApplyShadow(context, attr);
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
        return new BoundingBox(0, 0, getWidth(), getHeight());
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
        getAttributes().setURL(url);
        if (null != url && url.trim().length() > 0) {
            destroyProxy();
            if (ImageStrips.isURLValid(url)) {
                final ImageStrips.ImageStripRef stripRef = ImageStrips.get().getRef(url);
                final ImageStrip strip = ImageStrips.get().get(stripRef.getName());
                imageProxy = ImageStrips.get().newProxy(strip);
                configureClipArea(strip,
                                  stripRef.getIndex());
            } else {
                imageProxy = new ImageElementProxy();
                restoreClipArea();
            }
        }
        return this;
    }

    Image load(final ImageLoadCallback callback) {
        if (!imageProxy.isLoaded()) {
            final String url = getAttributes().getURL();
            imageProxy.load(url,
                            new Runnable() {
                                @Override
                                public void run() {
                                    callback.onImageLoaded(Image.this);
                                    performBatch();
                                }
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

    @Override
    public JSONObject toJSONObject() {
        JSONObject attr = new JSONObject(getAttributes().getJSO());

        attr.put("url", new JSONString(getAttributes().getURL()));

        JSONObject object = new JSONObject();

        object.put("type", new JSONString(getShapeType().getValue()));

        if (hasMetaData()) {
            final MetaData meta = getMetaData();

            if (false == meta.isEmpty()) {
                object.put("meta", new JSONObject(meta.getJSO()));
            }
        }
        object.put("attributes", attr);
        return object;
    }

    @Override
    public void destroy() {
        destroyProxy();
        removeFromParent();
    }

    public static class ImageFactory extends ShapeFactory<Image> {

        public ImageFactory() {
            super(ShapeType.IMAGE);
            addAttribute(Attribute.URL, true);
            addAttribute(Attribute.CLIPPED_IMAGE_START_X);
            addAttribute(Attribute.CLIPPED_IMAGE_START_Y);
            addAttribute(Attribute.CLIPPED_IMAGE_WIDTH);
            addAttribute(Attribute.CLIPPED_IMAGE_HEIGHT);
            addAttribute(Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH);
            addAttribute(Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT);
        }

        @Override
        public Image create(JSONObject node, ValidationContext ctx) throws ValidationException {
            return new Image(node, ctx);
        }

        @Override
        public boolean isPostProcessed() {
            return true;
        }

        @Override
        public void process(IJSONSerializable<?> node, ValidationContext ctx) throws ValidationException {
            if (node instanceof Image) {
                final Image self = (Image) node;
                self.configure(self.getAttributes().getURL())
                    .load(new ImageLoadCallback() {
                        @Override
                        public void onImageLoaded(Image image) {
                            image.performBatch();
                        }
                    });
            }
        }
    }
}
