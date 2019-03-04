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
import com.ait.lienzo.client.core.image.ImageLoader;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.MovieEndedHandler;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.media.client.Video;
import com.google.gwt.media.dom.client.MediaError;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Movie provides a mechanism for viewing and controlling videos in a Canvas.
 * Due to discrepancies in the adoption of the Canvas specification by different vendors,
 * you should provide multiple formats of the video to ensure portability.
 */
public class Movie extends Shape<Movie>implements ImageDataFilterable<Movie>
{
    private static final int           MOVIE_ERROR_HIGH = 360;

    private static final int           MOVIE_ERROR_WIDE = 640;

    private boolean                    m_inits          = true;

    private boolean                    m_ended          = true;

    private boolean                    m_pause          = true;

    private boolean                    m_xorig          = false;

    private String                     m_error          = null;

    private ImageElement               m_postr          = null;

    private MovieEndedHandler          m_onend          = null;

    private final Video                m_video          = Video.createIfSupported();

    private final MovieAnimation       m_animate;

    private final ImageDataFilterChain m_filters        = new ImageDataFilterChain();

    private final ScratchPad           m_canvas         = new ScratchPad(0, 0);

    /**
     * Constructor. Creates an instance of a movie.
     * 
     * @param url
     */
    public Movie(final String url)
    {
        super(ShapeType.MOVIE);

        getAttributes().setURL(url);

        m_animate = doInitialize();
    }

    public Movie(final String url, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.MOVIE);

        getAttributes().setURL(url);

        m_animate = doInitialize();

        setFilters(filter, filters);
    }

    protected Movie(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.MOVIE, node, ctx);

        m_animate = doInitialize();
    }

    private final MovieAnimation doInitialize()
    {
        if (null != m_video)
        {
            setErrorHandler(this, m_video.getVideoElement());

            String url = getURL();

            if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
            {
                throw new NullPointerException("null or empty or invalid url");
            }
            url = UriUtils.fromString(url).asString();

            if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
            {
                throw new NullPointerException("null or empty or invalid url");
            }
            m_video.setSrc(url);

            m_video.setLoop(isLoop());

            m_video.setVisible(false);

            m_video.setPlaybackRate(getPlaybackRate());

            m_video.setPreload(MediaElement.PRELOAD_AUTO);

            if (getAttributes().isDefined(Attribute.VOLUME))
            {
                m_video.setVolume(getVolume());
            }
            setSizes();

            return new MovieAnimation(this, m_video);
        }
        else
        {
            return null;
        }
    }

    private final native void setErrorHandler(Movie movie, VideoElement element)
    /*-{
		element.onerror = function(e) {
			movie.@com.ait.lienzo.client.core.shape.Movie::setErrorCode(I)(e.target.error.code);
		};
    }-*/;

    private final String getTextBestFit(final Context2D context, final String text, final int wide)
    {
        double pt = LienzoCore.get().getDefaultFontSize();

        String st = LienzoCore.get().getDefaultFontStyle();

        String fm = LienzoCore.get().getDefaultFontFamily();

        String tf = Text.getFontString(pt, TextUnit.PT, st, fm);

        context.save();

        context.setToIdentityTransform();

        while (true)
        {
            context.setTextFont(tf);

            final TextMetrics tm = context.measureText(text);

            if (tm.getWidth() < wide)
            {
                break;
            }
            pt = pt - 2;

            if (pt < 6)
            {
                break;
            }
            tf = Text.getFontString(pt, TextUnit.PT, st, fm);
        }
        context.restore();

        return tf;
    }

    public Movie onEnded(final MovieEndedHandler onend)
    {
        m_onend = onend;

        return this;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, getWidth(), getHeight());
    }

    /**
     * Draws the frames of the video.  If looping has been set, frames are drawn
     * continuously in a loop.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_inits)
        {
            init();

            if ((null == m_error) && (isAutoPlay()))
            {
                play();
            }
        }
        int wide = getWidth();

        int high = getHeight();

        if (null != m_error)
        {
            if (false == context.isSelection())
            {
                if (wide < 1)
                {
                    wide = MOVIE_ERROR_WIDE;
                }
                if (high < 1)
                {
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
        }
        else
        {
            if ((wide < 1) || (high < 1))
            {
                return false;
            }
            if (context.isSelection())
            {
                final String color = getColorKey();

                if (null != color)
                {
                    context.save();

                    context.setFillColor(color);

                    context.fillRect(0, 0, wide, high);

                    context.restore();
                }
                return false;
            }
            if (isEnded())
            {
                if (null != m_postr)
                {
                    context.save();

                    context.setGlobalAlpha(alpha);

                    context.drawImage(m_postr, 0, 0, wide, high);

                    context.restore();
                }
                else
                {
                    String fill = getFillColor();

                    if (null != fill)
                    {
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

            if ((false == m_xorig) && (m_filters.isActive()))
            {
                try
                {
                    m_canvas.getContext().drawImage(m_video.getElement(), 0, 0, wide, high);

                    m_canvas.getContext().putImageData(m_filters.filter(m_canvas.getContext().getImageData(0, 0, wide, high), false), 0, 0);

                    context.drawImage(m_canvas.getElement(), 0, 0, wide, high);
                }
                catch (Exception e)
                {
                    // We should only get an exception here if the URL is cross-origin, and getImageData() is basically a security exception.
                    // ...or other unknown bad things, either way, turn off filtering. DSJ 7/18/2014

                    context.drawImage(m_video.getElement(), 0, 0, wide, high);

                    m_xorig = true;

                    LienzoCore.get().error("ERROR: In Movie filtering " + m_video.getSrc() + " " + e.getMessage());
                }
            }
            else
            {
                context.drawImage(m_video.getElement(), 0, 0, wide, high);
            }
            context.restore();
        }
        return false;
    }

    @Override
    public Movie setFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters)
    {
        m_filters.setFilters(filter, filters);

        return this;
    }

    @Override
    public Movie addFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters)
    {
        m_filters.addFilters(filter, filters);

        return this;
    }

    @Override
    public Movie removeFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters)
    {
        m_filters.removeFilters(filter, filters);

        return this;
    }

    @Override
    public Movie clearFilters()
    {
        m_filters.clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters()
    {
        return m_filters.getFilters();
    }

    @Override
    public Movie setFiltersActive(boolean active)
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
    public Movie setFilters(Iterable<ImageDataFilter<?>> filters)
    {
        m_filters.setFilters(filters);

        return this;
    }

    @Override
    public Movie addFilters(Iterable<ImageDataFilter<?>> filters)
    {
        m_filters.addFilters(filters);

        return this;
    }

    @Override
    public Movie removeFilters(Iterable<ImageDataFilter<?>> filters)
    {
        m_filters.removeFilters(filters);

        return this;
    }

    /**
     * Sets the movie's volume
     * 
     * @param volume
     * @return this Movie
     */
    public Movie setVolume(double volume)
    {
        getAttributes().setVolume(volume);

        if (null != m_video)
        {
            m_video.setVolume(getVolume());
        }
        return this;
    }

    /**
     * Gets the value for the volume.
     * 
     * @return double
     */
    public double getVolume()
    {
        return getAttributes().getVolume();
    }

    /**
     * Gets the URL for this movie.
     * 
     * @return String
     */
    public String getURL()
    {
        return getAttributes().getURL();
    }

    /**
     * Pauses this movie.
     * 
     * @return this Movie
     */
    public Movie pause()
    {
        if ((null != m_video) && (false == isPaused()))
        {
            m_pause = true;

            m_video.pause();
        }
        return this;
    }

    public boolean isPaused()
    {
        return m_pause;
    }

    private final void setEnded(boolean ended)
    {
        if (m_ended = ended)
        {
            if (null != m_onend)
            {
                final Movie movie = this;

                Scheduler.get().scheduleDeferred(new ScheduledCommand()
                {
                    @Override
                    public void execute()
                    {
                        if (null != m_onend)
                        {
                            m_onend.onEnded(movie);
                        }
                    }
                });
            }
        }
    }

    public boolean isEnded()
    {
        return m_ended;
    }

    /**
     * Sets the movie to continuously loop or not.
     * 
     * @param loop
     * @return this Movie
     */
    public Movie setLoop(boolean loop)
    {
        getAttributes().setLoop(loop);

        if (null != m_video)
        {
            m_video.setLoop(loop);
        }
        return this;
    }

    /**
     * Returns true if this movie is set to loop; false otherwise.
     * 
     * @return boolean
     */
    public boolean isLoop()
    {
        return getAttributes().isLoop();
    }

    /**
     * Sets the width of this movie's display area
     * 
     * @param wide
     * @return this Movie
     */
    public Movie setWidth(int wide)
    {
        getAttributes().setWidth(wide);

        setSizes();

        return this;
    }

    /**
     * Gets the width of this movie's display area
     * 
     * @return int
     */
    public int getWidth()
    {
        if (getAttributes().isDefined(Attribute.WIDTH))
        {
            int wide = (int) (getAttributes().getWidth() + 0.5);

            if (wide > 0)
            {
                return wide;
            }
        }
        if (null != m_video)
        {
            return m_video.getVideoWidth();
        }
        return 0;
    }

    /**
     * Sets the height of this movie's display area
     * 
     * @param high
     * @return this Movie
     */
    public Movie setHeight(int high)
    {
        getAttributes().setHeight(high);

        setSizes();

        return this;
    }

    /**
     * Gets the height of this movie's display area
     * 
     * @return int
     */
    public int getHeight()
    {
        if (getAttributes().isDefined(Attribute.HEIGHT))
        {
            int high = (int) (getAttributes().getHeight() + 0.5);

            if (high > 0)
            {
                return high;
            }
        }
        if (null != m_video)
        {
            return m_video.getVideoHeight();
        }
        return 0;
    }

    public final Movie setPlaybackRate(double rate)
    {
        getAttributes().setPlaybackRate(rate);

        if (null != m_video)
        {
            m_video.setPlaybackRate(rate);
        }
        return this;
    }

    public final double getPlaybackRate()
    {
        return getAttributes().getPlaybackRate();
    }

    public final Movie setAutoPlay(boolean play)
    {
        getAttributes().setAutoPlay(play);

        return this;
    }

    public final boolean isAutoPlay()
    {
        return getAttributes().isAutoPlay();
    }

    public final Movie setShowPoster(boolean show)
    {
        getAttributes().setShowPoster(show);

        return this;
    }

    public final boolean isShowPoster()
    {
        return getAttributes().isShowPoster();
    }

    public final void play()
    {
        if ((null != m_video) && (null != m_animate) && (isPaused() || isEnded()))
        {
            m_pause = false;

            m_ended = false;

            m_animate.run();
        }
    }

    private final void setErrorCode(int code)
    {
        switch (code)
        {
            case MediaError.MEDIA_ERR_ABORTED:
                m_error = MessageConstants.MESSAGES.moviePlaybackWasAborted();
                break;
            case MediaError.MEDIA_ERR_NETWORK:
                m_error = MessageConstants.MESSAGES.movieNetworkError();
                break;
            case MediaError.MEDIA_ERR_DECODE:
                m_error = MessageConstants.MESSAGES.movieErrorInDecoding();
                break;
            case MediaError.MEDIA_ERR_SRC_NOT_SUPPORTED:
                m_error = MessageConstants.MESSAGES.movieFormatNotSupported();
                break;
        }
    }

    public final String getError()
    {
        return m_error;
    }

    private final void init()
    {
        if (null != m_video)
        {
            MediaError status = m_video.getError();

            if (status != null)
            {
                setErrorCode(status.getCode());
            }
            else
            {
                if (isShowPoster())
                {
                    final String url = m_video.getPoster();

                    if (null != url)
                    {
                        new ImageLoader(url)
                        {
                            @Override
                            public void onImageElementLoad(final ImageElement elem)
                            {
                                m_postr = elem;
                            }

                            @Override
                            public void onImageElementError(String message)
                            {
                                LienzoCore.get().error("ERROR: Getting video poster url[" + url + "] " + message);
                            }
                        };
                    }
                }
            }
        }
        else
        {
            m_error = MessageConstants.MESSAGES.movieNotSupportedInThisBrowser();
        }
        m_inits = false;
    }

    private final void setSizes()
    {
        if (null != m_video)
        {
            final int wide = getWidth();

            final int high = getHeight();

            m_video.setWidth(wide + "px");

            m_video.setHeight(high + "px");

            m_canvas.setPixelSize(wide, high);
        }
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject object = super.toJSONObject();

        ImageDataFilterChain chain = m_filters;

        if ((null != chain) && (chain.size() > 0))
        {
            JSONArray filters = new JSONArray();

            JSONObject filter = new JSONObject();

            filter.put("active", JSONBoolean.getInstance(chain.isActive()));

            for (ImageDataFilter<?> ifilter : chain.getFilters())
            {
                if (null != ifilter)
                {
                    JSONObject make = ifilter.toJSONObject();

                    if (null != make)
                    {
                        filters.set(filters.size(), make);
                    }
                }
            }
            filter.put("filters", filters);

            object.put("filter", filter);
        }
        return object;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.WIDTH, Attribute.HEIGHT);
    }

    private static final class MovieAnimation extends IndefiniteAnimation
    {
        private HandlerRegistration m_watch = null;

        private final Movie         m_movie;

        private final Video         m_video;

        private boolean             m_start = true;

        public MovieAnimation(final Movie movie, final Video video)
        {
            super(null);

            m_movie = movie;

            m_video = video;
        }

        @Override
        public IAnimationHandle run()
        {
            m_start = true;

            super.run();

            m_start = false;

            return this;
        }

        @Override
        public IAnimation doStart()
        {
            RootPanel.get().add(m_video);

            m_video.play();

            if (null == m_watch)
            {
                m_watch = m_video.addEndedHandler(new EndedHandler()
                {
                    @Override
                    public void onEnded(EndedEvent event)
                    {
                        if (false == m_movie.isLoop())
                        {
                            m_movie.setEnded(true);
                        }
                    }
                });
            }
            return draw();
        }

        @Override
        public IAnimation doFrame()
        {
            return draw();
        }

        @Override
        public IAnimation doClose()
        {
            RootPanel.get().remove(m_video);

            if (null != m_watch)
            {
                m_watch.removeHandler();

                m_watch = null;
            }
            return draw();
        }

        @Override
        public boolean isRunning()
        {
            if (m_start)
            {
                return false;
            }
            if (null != m_movie.getError())
            {
                return false;
            }
            if (m_movie.isEnded())
            {
                return false;
            }
            if (m_movie.isPaused())
            {
                return false;
            }
            return true;
        }

        private final IAnimation draw()
        {
            final Layer layer = m_movie.getLayer();

            if (null != layer)
            {
                layer.batch();
            }
            return this;
        }
    }

    public static class MovieFactory extends ShapeFactory<Movie>
    {
        public MovieFactory()
        {
            super(ShapeType.MOVIE);

            addAttribute(Attribute.URL, true);

            addAttribute(Attribute.LOOP);

            addAttribute(Attribute.WIDTH);

            addAttribute(Attribute.HEIGHT);

            addAttribute(Attribute.VOLUME);

            addAttribute(Attribute.AUTO_PLAY);

            addAttribute(Attribute.SHOW_POSTER);

            addAttribute(Attribute.PLAYBACK_RATE);
        }

        @Override
        public Movie create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            Movie movie = new Movie(node, ctx);

            JSONValue jval = node.get("filter");

            if (null != jval)
            {
                JSONObject object = jval.isObject();

                if (null != object)
                {
                    JSONDeserializer.get().deserializeFilters(movie, object, ctx);

                    jval = object.get("active");

                    JSONBoolean active = jval.isBoolean();

                    if (null != active)
                    {
                        movie.setFiltersActive(active.booleanValue());
                    }
                }
            }
            return movie;
        }
    }
}
