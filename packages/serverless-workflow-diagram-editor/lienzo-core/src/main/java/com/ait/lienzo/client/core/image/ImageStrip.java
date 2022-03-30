package com.ait.lienzo.client.core.image;

import org.gwtproject.resources.client.ImageResource;

public class ImageStrip {

    private final String name;
    private final String url;
    private final int wide;
    private final int high;
    private final int padding;
    private final Orientation orientation;

    public ImageStrip(final ImageResource resource,
                      final int wide,
                      final int high,
                      final int padding,
                      final Orientation orientation) {
        this(resource.getName(),
             resource.getSafeUri().asString(),
             wide,
             high,
             padding,
             orientation);
    }

    public ImageStrip(final String name,
                      final String url,
                      final int wide,
                      final int high,
                      final int padding,
                      final Orientation orientation) {
        this.name = name;
        this.url = url;
        this.wide = wide;
        this.high = high;
        this.padding = padding;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getWide() {
        return wide;
    }

    public int getHigh() {
        return high;
    }

    public int getPadding() {
        return padding;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL;
    }
}
