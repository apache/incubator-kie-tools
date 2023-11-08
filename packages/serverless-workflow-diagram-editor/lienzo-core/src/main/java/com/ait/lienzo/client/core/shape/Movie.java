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
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.IndefiniteAnimation;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.i18n.MessageConstants;
import com.ait.lienzo.client.core.image.JsImageBitmap;
import com.ait.lienzo.client.core.image.JsImageBitmapCallback;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.MovieEndedHandler;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.MediaError;
import elemental2.dom.TextMetrics;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.safehtml.shared.UriUtils;

/**
 * Movie provides a mechanism for viewing and controlling videos in a Canvas.
 * Due to discrepancies in the adoption of the Canvas specification by different vendors,
 * you should provide multiple formats of the video to ensure portability.
 */
public class Movie extends Shape<Movie> implements ImageDataFilterable<Movie> {

    private static final int MOVIE_ERROR_HIGH = 360;

    private static final int MOVIE_ERROR_WIDE = 640;

    private boolean m_inits = true;

    private boolean m_ended = true;

    private boolean m_pause = true;

    private boolean m_xorig = false;

    private String m_error = null;

    private JsImageBitmap m_postr = null;

    private MovieEndedHandler m_onend = null;

    private final HTMLVideoElement m_video = Js.uncheckedCast(DomGlobal.document.createElement("video"));

    private final MovieAnimation m_animate;

    private final ImageDataFilterChain m_filters = new ImageDataFilterChain();

    private final ScratchPad m_canvas = new ScratchPad(0, 0);

    @JsProperty
    private String url;

    @JsProperty
    private double width = -1;

    @JsProperty
    private double height = -1;

    @JsProperty
    private double volume = 0.5;

    @JsProperty
    private boolean autoPlay;

    @JsProperty
    private boolean loop;

    @JsProperty
    private double playBackRate = 1.0;

    @JsProperty
    private boolean showPoster;

    private TextUtils textUtils = new TextUtils();

    public interface VideoElementOnLoad {

        void onLoad(Movie movie, HTMLVideoElement elem);
    }

    /**
     * Constructor. Creates an instance of a movie.
     *
     * @param url
     */
    public Movie(final String url) {
        this(url, (VideoElementOnLoad) null);
    }

    /**
     * Constructor. Creates an instance of a movie.
     *
     * @param url
     */
    public Movie(final String url, VideoElementOnLoad onLoad) {
        super(ShapeType.MOVIE);

        this.url = url;

        m_animate = doInitialize(onLoad);
    }

    public Movie(final String url, final ImageDataFilter<?>... filters) {
        this(url, (VideoElementOnLoad) null, filters);
    }

    public Movie(final String url, VideoElementOnLoad onLoad, final ImageDataFilter<?>... filters) {
        super(ShapeType.MOVIE);

        this.url = url;

        m_animate = doInitialize(onLoad);

        setFilters(filters);
    }

    private final MovieAnimation doInitialize(VideoElementOnLoad onLoad) {
        if (null != m_video) {
            setErrorHandler(this, m_video);

            String url = getURL();

            if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#"))) {
                throw new NullPointerException("null or empty or invalid url");
            }
            url = UriUtils.fromString(url).asString();

            if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#"))) {
                throw new NullPointerException("null or empty or invalid url");
            }
            if (onLoad != null) {
                m_video.onloadedmetadata = e -> {
                    onLoad.onLoad(this, m_video);
                    return null;
                };
            }
            m_video.src = url;

            m_video.loop = isLoop();

            setVisible(m_video, false);

            m_video.playbackRate = getPlaybackRate();

            Js.asPropertyMap(m_video).set("preLoad",  "auto");

            if (getVolume() >= 0) {
                m_video.volume = getVolume();
            }
            setSizes();

            return new MovieAnimation(this, m_video);
        } else {
            return null;
        }
    }

    public static void setVisible(HTMLElement image, boolean visible) {
        image.style.display = visible ? "" : Style.Display.NONE.getCssName();

        if (visible) {
            image.removeAttribute("aria-hidden");
        } else {
            image.setAttribute("aria-hidden", "true");
        }
    }

    private final void setErrorHandler(Movie movie, HTMLVideoElement element) {
        element.onerror = e -> {
            movie.setErrorCode(((HTMLVideoElement) e.target).error.code);
            return null;
        };
    }

    private final String getTextBestFit(final Context2D context, final String text, final int wide) {
        double pt = LienzoCore.get().getDefaultFontSize();

        String st = LienzoCore.get().getDefaultFontStyle();

        String fm = LienzoCore.get().getDefaultFontFamily();

        String tf = textUtils.getFontString(pt, TextUnit.PT, st, fm);

        context.save();

        context.setToIdentityTransform();

        while (true) {
            context.setTextFont(tf);

            final TextMetrics tm = context.measureText(text);

            if (tm.width < wide) {
                break;
            }
            pt = pt - 2;

            if (pt < 6) {
                break;
            }
            tf = textUtils.getFontString(pt, TextUnit.PT, st, fm);
        }
        context.restore();

        return tf;
    }

    public Movie onEnded(final MovieEndedHandler onend) {
        m_onend = onend;

        return this;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, getWidth(), getHeight());
    }

    /**
     * Draws the frames of the video.  If looping has been set, frames are drawn
     * continuously in a loop.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        if (m_inits) {
            init();

            if ((null == m_error) && (isAutoPlay())) {
                play();
            }
        }
        int wide = getWidth();

        int high = getHeight();

        if (null != m_error) {
            if (!context.isSelection()) {
                if (wide < 1) {
                    wide = MOVIE_ERROR_WIDE;
                }
                if (high < 1) {
                    high = MOVIE_ERROR_HIGH;
                }
                context.save();

                context.setFillColor(ColorName.BLACK);

                context.rect(0, 0, wide, high);

                context.fill();

                context.setTextAlign(TextAlign.CENTER);

                context.setTextBaseline(TextBaseLine.MIDDLE);

                context.setTextFont(getTextBestFit(context, m_error, wide));

                context.setFillColor(ColorName.WHITE);

                context.rect(0, 0, wide, high);

                context.clip();

                context.fillText(m_error, wide / 2.0, high / 2.0);

                context.restore();
            }
        } else {
            if ((wide < 1) || (high < 1)) {
                return false;
            }
            if (context.isSelection()) {
                final String color = getColorKey();

                if (null != color) {
                    context.save();

                    context.setFillColor(color);

                    context.fillRect(0, 0, wide, high);

                    context.restore();
                }
                return false;
            }
            if (isEnded()) {
                if (null != m_postr) {
                    context.save();

                    context.setGlobalAlpha(alpha);

                    context.drawImage(m_postr, 0, 0, wide, high);

                    context.restore();
                } else {
                    String fill = getFillColor();

                    if (null != fill) {
                        context.save();

                        context.setGlobalAlpha(alpha);

                        context.setFillColor(fill);

                        context.fillRect(0, 0, wide, high);

                        context.restore();
                    }
                }
                return false;
            }
            context.save();

            context.setGlobalAlpha(alpha);

            if ((!m_xorig) && (m_filters.isActive())) {
                try {
                    m_canvas.getContext().drawImage(m_video, 0, 0, wide, high);

                    m_canvas.getContext().putImageData(m_filters.filter(m_canvas.getContext().getImageData(0, 0, wide, high), false), 0, 0);

                    context.drawImage(m_canvas.getElement(), 0, 0, wide, high);
                } catch (Exception e) {
                    // We should only get an exception here if the URL is cross-origin, and getImageData() is basically a security exception.
                    // ...or other unknown bad things, either way, turn off filtering. DSJ 7/18/2014

                    context.drawImage(m_video, 0, 0, wide, high);

                    m_xorig = true;

                    LienzoCore.get().error("ERROR: In Movie filtering " + m_video.src + " " + e.getMessage());
                }
            } else {
                context.drawImage(m_video, 0, 0, wide, high);
            }
            context.restore();
        }
        return false;
    }

    @Override
    public Movie setFilters(ImageDataFilter<?>... filters) {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public Movie addFilters(ImageDataFilter<?>... filters) {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public Movie removeFilters(ImageDataFilter<?>... filters) {
        m_filters.removeFilters(filters);

        return this;
    }

    @Override
    public Movie clearFilters() {
        m_filters.clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters() {
        return m_filters.getFilters();
    }

    @Override
    public Movie setFiltersActive(boolean active) {
        m_filters.setActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive() {
        return m_filters.areFiltersActive();
    }

    @Override
    public Movie setFilters(Iterable<ImageDataFilter<?>> filters) {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public Movie addFilters(Iterable<ImageDataFilter<?>> filters) {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public Movie removeFilters(Iterable<ImageDataFilter<?>> filters) {
        m_filters.removeFilters(filters);

        return this;
    }

    /**
     * Sets the movie's volume
     *
     * @param volume
     * @return this Movie
     */
    public Movie setVolume(double volume) {
        this.volume = volume;

        if (null != m_video) {
            m_video.volume = volume;
        }
        return this;
    }

    /**
     * Gets the value for the volume.
     *
     * @return double
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Gets the URL for this movie.
     *
     * @return String
     */
    public String getURL() {
        return this.url;
    }

    /**
     * Pauses this movie.
     *
     * @return this Movie
     */
    public Movie pause() {
        if ((null != m_video) && (!isPaused())) {
            m_pause = true;

            m_video.pause();
        }
        return this;
    }

    public Movie stop() {
        m_video.pause();
        m_animate.stop();

        return this;
    }

    public boolean isPaused() {
        return m_pause;
    }

    private final void setEnded(boolean ended) {
        if (m_ended = ended) {
            if (null != m_onend) {
                final Movie movie = this;

                Scheduler.get().scheduleDeferred(() -> {
                    if (null != m_onend) {
                        m_onend.onEnded(movie);
                    }
                });
            }
        }
    }

    public boolean isEnded() {
        return m_ended;
    }

    /**
     * Sets the movie to continuously loop or not.
     *
     * @param loop
     * @return this Movie
     */
    public Movie setLoop(boolean loop) {
        this.loop = loop;

        if (null != m_video) {
            m_video.loop = loop;
        }
        return this;
    }

    /**
     * Returns true if this movie is set to loop; false otherwise.
     *
     * @return boolean
     */
    public boolean isLoop() {
        return this.loop;
    }

    /**
     * Sets the width of this movie's display area
     *
     * @param wide
     * @return this Movie
     */
    public Movie setWidth(int wide) {
        this.width = wide;

        setSizes();

        return this;
    }

    /**
     * Gets the width of this movie's display area
     *
     * @return int
     */
    public int getWidth() {
        if (width >= 0) {
            int wide = (int) (width + 0.5);

            if (wide > 0) {
                return wide;
            }
        }
        if (null != m_video) {
            return m_video.videoWidth;
        }
        return 0;
    }

    /**
     * Sets the height of this movie's display area
     *
     * @param high
     * @return this Movie
     */
    public Movie setHeight(int high) {
        this.height = high;

        setSizes();

        return this;
    }

    /**
     * Gets the height of this movie's display area
     *
     * @return int
     */
    public int getHeight() {
        if (height >= 0) {
            int high = (int) (height + 0.5);

            if (high > 0) {
                return high;
            }
        }
        if (null != m_video) {
            return m_video.videoHeight;
        }
        return 0;
    }

    public final Movie setPlaybackRate(double rate) {
        this.playBackRate = rate;

        if (null != m_video) {
            m_video.playbackRate = rate;
        }
        return this;
    }

    public final double getPlaybackRate() {
        return this.playBackRate;
    }

    public final Movie setAutoPlay(boolean play) {
        this.autoPlay = play;

        return this;
    }

    public final boolean isAutoPlay() {
        return this.autoPlay;
    }

    public final Movie setShowPoster(boolean show) {
        this.showPoster = show;

        return this;
    }

    public final boolean isShowPoster() {
        return this.showPoster;
    }

    public final void play() {
        if ((null != m_video) && (null != m_animate) && (isPaused() || isEnded())) {
            m_pause = false;

            m_ended = false;

            m_animate.run();
        }
    }

    // This is temporary, while I wait for Elemental2 to be fixed
    private static class MediaErrorWithInt {

        public static final int MEDIA_ERR_ABORTED = 1;
        public static final int MEDIA_ERR_NETWORK = 2;
        public static final int MEDIA_ERR_DECODE = 3;
        public static final int MEDIA_ERR_SRC_NOT_SUPPORTED = 4;
    }

    private final void setErrorCode(int code) {
        switch (code) {
            case MediaErrorWithInt.MEDIA_ERR_ABORTED:
                m_error = MessageConstants.MESSAGES.moviePlaybackWasAborted();
                break;
            case MediaErrorWithInt.MEDIA_ERR_NETWORK:
                m_error = MessageConstants.MESSAGES.movieNetworkError();
                break;
            case MediaErrorWithInt.MEDIA_ERR_DECODE:
                m_error = MessageConstants.MESSAGES.movieErrorInDecoding();
                break;
            case MediaErrorWithInt.MEDIA_ERR_SRC_NOT_SUPPORTED:
                m_error = MessageConstants.MESSAGES.movieFormatNotSupported();
                break;
        }
    }

    public final String getError() {
        return m_error;
    }

    private final void init() {
        if (null != m_video) {
            MediaError status = m_video.error;

            if (status != null) {
                setErrorCode(status.code);
            } else {
                if (isShowPoster()) {
                    final String url = m_video.poster;

                    if (null != url) {
                        JsImageBitmap.loadImageBitmap(url, new JsImageBitmapCallback() {
                            @Override
                            public void onSuccess(JsImageBitmap image) {
                                m_postr = image;
                            }

                            @Override
                            public void onError(Object error) {
                                LienzoCore.get().error("ERROR: Getting video poster url[" + url + "] " + error.toString());
                            }
                        });
                    }
                }
            }
        } else {
            m_error = MessageConstants.MESSAGES.movieNotSupportedInThisBrowser();
        }
        m_inits = false;
    }

    private final void setSizes() {
        if (null != m_video) {
            final int wide = getWidth();

            final int high = getHeight();

            m_video.width = wide;

            m_video.height = high;

            m_canvas.setPixelSize(wide, high);
        }
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.WIDTH, Attribute.HEIGHT);
    }

    private static final class MovieAnimation extends IndefiniteAnimation {

        private HandlerRegistration m_watch = null;

        private final Movie m_movie;

        private final HTMLVideoElement m_video;

        private boolean m_start = true;

        public MovieAnimation(final Movie movie, final HTMLVideoElement video) {
            super(null);

            m_movie = movie;

            m_video = video;
        }

        @Override
        public IAnimationHandle run() {
            m_start = true;

            super.run();

            m_start = false;

            return this;
        }

        @Override
        public IAnimation doStart() {
            DomGlobal.document.body.appendChild(m_video);

            m_video.play();

            if (null == m_watch) {

                m_video.onended = e ->
                {

                    if (!m_movie.isLoop()) {
                        m_movie.setEnded(true);
                    }
                    return null;
                };
            }
            return draw();
        }

        @Override
        public IAnimation doFrame() {
            return draw();
        }

        @Override
        public IAnimation doClose() {
            m_video.remove();

            if (null != m_watch) {
                m_watch.removeHandler();

                m_watch = null;
            }
            return draw();
        }

        @Override
        public boolean isRunning() {
            if (m_start) {
                return false;
            }
            if (null != m_movie.getError()) {
                return false;
            }
            if (m_movie.isEnded()) {
                return false;
            }
            if (m_movie.isPaused()) {
                return false;
            }
            return true;
        }

        private final IAnimation draw() {
            final Layer layer = m_movie.getLayer();

            if (null != layer) {
                layer.draw();
            }
            return this;
        }
    }
}
