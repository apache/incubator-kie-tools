package com.ait.lienzo.client.core.image;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageElementProxy {

    private com.google.gwt.user.client.ui.Image imageWidget;
    private ImageElement imageElement;

    public ImageElementProxy() {
    }

    ImageElementProxy(com.google.gwt.user.client.ui.Image imageWidget,
                             ImageElement imageElement) {
        this.imageWidget = imageWidget;
        this.imageElement = imageElement;
    }

    public void load(final String url,
                     final Runnable callback) {
        assert null == imageElement;
        imageWidget = new Image();
        new ImageLoader(url,
                        imageWidget) {

            @Override
            public void onImageElementLoad(final ImageElement imageElement) {
                ImageElementProxy.this.imageElement = imageElement;
                imageElement.getWidth();
                callback.run();
            }

            @Override
            public void onImageElementError(final String errorMessage) {
                LienzoCore.get().error("Error loading Image. Message: [" + errorMessage + "]");
            }
        };
    }

    public void draw(final Context2D context) {
        context.drawImage(imageElement, 0, 0);
    }

    public void draw(final Context2D context,
                     final ImageClipBounds clipBounds) {
        final double clipX = clipBounds.getClipXPos();
        final double clipY = clipBounds.getClipYPos();
        final int width = getWidth();
        final int height = getHeight();
        final double _clipWide = clipBounds.getClipWide();
        final double clipWide = _clipWide > 0 ? _clipWide : width;
        final double _clipHigh = clipBounds.getClipHigh();
        final double clipHigh = _clipHigh > 0 ? _clipHigh : height;
        final double _destWide = clipBounds.getDestWide();
        final double destWide = _destWide > 0 ? _destWide : width;
        final double _destHigh = clipBounds.getDestHigh();
        final double destHigh = _destHigh > 0 ? _destHigh : height;
        context.drawImage(imageElement, clipX, clipY, clipWide, clipHigh, 0, 0, destWide, destHigh);
    }

    public boolean isLoaded() {
        return null != imageElement;
    }

    public int getWidth() {
        return isLoaded() ? imageElement.getWidth() : 0;
    }

    public int getHeight() {
        return isLoaded() ? imageElement.getHeight() : 0;
    }

    public void destroy() {
        RootPanel.get().remove(imageWidget);
        imageWidget.removeFromParent();
        imageElement = null;
        imageWidget = null;
    }
}
