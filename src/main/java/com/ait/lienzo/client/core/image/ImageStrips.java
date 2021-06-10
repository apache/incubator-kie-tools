package com.ait.lienzo.client.core.image;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import java.util.function.Supplier;

public class ImageStrips {

    public static final String URL_PATTERN = "data:text/lienzo-strip,";
    public static final char URL_SEPARATOR = '~';

    private static final ImageStrips INSTANCE = new ImageStrips(() -> new ImageElementProxy());

    private final NFastStringMap<ImageStrip> strips;
    private final NFastStringMap<ImageElementProxy> proxies;
    private final Supplier<ImageElementProxy> proxySupplier;

    public static ImageStrips get() {
        return INSTANCE;
    }

    ImageStrips(final Supplier<ImageElementProxy> proxySupplier) {
        this.strips = new NFastStringMap<>();
        this.proxies = new NFastStringMap<>();
        this.proxySupplier = proxySupplier;
    }

    public ImageStrips register(final ImageStrip[] strips,
                                final Runnable loadCallback) {
        register(strips,
                 0,
                 loadCallback);
        return this;
    }

    public ImageStrips register(final ImageStrip[] strips,
                                final int index,
                                final Runnable loadCallback) {
        if (strips.length > index) {
            final ImageStrip strip = strips[index];
            register(strip,
                     () -> register(strips,
                              index + 1,
                              loadCallback));
        } else {
            loadCallback.run();
        }
        return this;
    }

    public ImageStrips register(final ImageStrip strip,
                                final Runnable loadCallback) {
        final ImageElementProxy handler = proxySupplier.get();
        handler.load(strip.getUrl(),
                     () -> {
                         registerStrip(strip, handler);
                         loadCallback.run();
                     });
        return this;
    }

    void registerStrip(final ImageStrip strip,
                      final ImageElementProxy handler) {
        strips.put(strip.getName(), strip);
        proxies.put(strip.getName(), handler);
    }

    public void remove(final ImageStrip strip) {
        final String name = strip.getName();
        remove(name);
    }

    public void remove(final String name) {
        final ImageElementProxy proxy = proxies.get(name);
        strips.remove(name);
        proxies.remove(name);
        proxy.destroy();
    }

    public ImageStrip get(final String name) {
        return strips.get(name);
    }

    public ImageStripRef getRef(final String url) {
        final String[] strip = decodeURL(url);
        return new ImageStripRef(strip[0],
                                 Integer.valueOf(strip[1]));
    }

    public ImageElementProxy newProxy(final ImageStrip strip) {
        final ImageElementProxy proxy = getProxy(strip);
        return new ImageElementProxyDelegate(proxy);
    }

    private ImageElementProxy getProxy(final ImageStrip strip) {
        return proxies.get(strip.getName());
    }

    public static String encodeURL(final String strip,
                                   final int index) {
        return URL_PATTERN + strip + URL_SEPARATOR + index;
    }

    public static boolean isURLValid(final String url) {
        return url.startsWith(URL_PATTERN);
    }

    public static String[] decodeURL(final String url) {
        if (!isURLValid(url)) {
            throw new IllegalArgumentException("The URL [" + url + "] does not reference a valid image strip.");
        }
        final String value = url.replace(URL_PATTERN, "");
        final int sep = value.lastIndexOf(URL_SEPARATOR);
        final String name = value.substring(0, sep);
        final String index = value.substring(sep + 1, value.length());
        return new String[]{name, index};
    }

    public static class ImageStripRef {

        private final String name;
        private final int index;

        public ImageStripRef(final String name,
                             final int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class ImageElementProxyDelegate extends ImageElementProxy {

        private final ImageElementProxy delegate;

        public ImageElementProxyDelegate(final ImageElementProxy delegate) {
            this.delegate = delegate;
        }

        @Override
        public void load(final String url,
                         final Runnable callback) {
            delegate.load(url, callback);
        }

        @Override
        public void draw(Context2D context) {
            delegate.draw(context);
        }

        @Override
        public void draw(Context2D context, ImageClipBounds clipBounds) {
            delegate.draw(context, clipBounds);
        }

        @Override
        public boolean isLoaded() {
            return delegate.isLoaded();
        }

        @Override
        public int getWidth() {
            return delegate.getWidth();
        }

        @Override
        public int getHeight() {
            return delegate.getHeight();
        }

        @Override
        public void destroy() {
            // Do not destroy the proxied instance.
        }

        public ImageElementProxy getDelegate() {
            return delegate;
        }
    }
}
