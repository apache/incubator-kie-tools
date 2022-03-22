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

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.image.ImageLoader;
import com.ait.lienzo.client.core.image.SpriteLoadedHandler;
import com.ait.lienzo.client.core.image.SpriteOnRollHandler;
import com.ait.lienzo.client.core.image.SpriteOnTickHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.SpriteBehaviorMap;
import com.ait.lienzo.shared.core.types.ImageSerializationMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.Timer;
import com.google.gwt.resources.client.ImageResource;
import elemental2.dom.HTMLImageElement;
import jsinterop.annotations.JsProperty;

public class Sprite extends Shape<Sprite> {

    private int m_index = 0;

    private BoundingBox[] m_frames = null;

    private HTMLImageElement m_sprite = null;

    private SpriteLoadedHandler m_loaded = null;

    private SpriteOnTickHandler m_ontick = null;

    private SpriteOnRollHandler m_onroll = null;

    private boolean m_paused = true;

    private boolean m_inited = false;

    private Timer m_ticker = null;

    @JsProperty
    private String url;

    @JsProperty
    private boolean autoPlay;

    @JsProperty
    private double tickRate;

    @JsProperty
    private String spriteBehavior;

    private SpriteBehaviorMap spriteBehaviorMap;

    private ImageSerializationMode imageSerializationMode;

    public Sprite(final String url, double rate, SpriteBehaviorMap bmap, String behavior) {
        super(ShapeType.SPRITE);

        setURL(url).setTickRate(rate).setSpriteBehaviorMap(bmap).setSpriteBehavior(behavior);

        new ImageLoader(url) {
            @Override
            public void onImageElementLoad(final HTMLImageElement elem) {
                m_sprite = elem;

                if (null != m_loaded) {
                    m_loaded.onSpriteLoaded(Sprite.this);
                }
            }

            @Override
            public void onImageElementError(String message) {
                LienzoCore.get().error("Sprite could not load URL " + url + " " + message);
            }
        };
    }

    public Sprite(final ImageResource resource, double rate, SpriteBehaviorMap bmap, String behavior) {
        super(ShapeType.SPRITE);

        setURL(resource.getSafeUri().asString()).setTickRate(rate).setSpriteBehaviorMap(bmap).setSpriteBehavior(behavior);

        new ImageLoader(resource) {
            @Override
            public void onImageElementLoad(final HTMLImageElement elem) {
                m_sprite = elem;

                if (null != m_loaded) {
                    m_loaded.onSpriteLoaded(Sprite.this);
                }
            }

            @Override
            public void onImageElementError(String message) {
                LienzoCore.get().error("Sprite could not load resource " + resource.getName() + " " + message);
            }
        };
    }

    public Sprite(HTMLImageElement sprite, double rate, SpriteBehaviorMap bmap, String behavior) {
        super(ShapeType.SPRITE);

        setURL(sprite.src).setTickRate(rate).setSpriteBehaviorMap(bmap).setSpriteBehavior(behavior);

        m_sprite = sprite;

        if (null != m_loaded) {
            m_loaded.onSpriteLoaded(this);
        }
    }

    Sprite load() {
        if (isLoaded()) {
            if (null != m_loaded) {
                m_loaded.onSpriteLoaded(this);
            }
        } else {
            final String url = getURL();

            new ImageLoader(url) {
                @Override
                public void onImageElementLoad(final HTMLImageElement elem) {
                    m_sprite = elem;

                    if (null != m_loaded) {
                        m_loaded.onSpriteLoaded(Sprite.this);
                    }
                }

                @Override
                public void onImageElementError(String message) {
                    LienzoCore.get().error("Sprite could not load URL " + url + " " + message);
                }
            };
        }
        return this;
    }

    @Override
    public BoundingBox getBoundingBox() {
        double wide = 0;

        double high = 0;

        for (int i = 0; i < m_frames.length; i++) {
            BoundingBox bbox = m_frames[i];

            wide = Math.max(wide, bbox.getWidth());

            high = Math.max(high, bbox.getHeight());
        }
        return BoundingBox.fromDoubles(0, 0, wide, high);
    }

    public final String getURL() {
        return this.url;
    }

    public final Sprite setURL(String url) {
        if (null == url || (url = url.trim()).isEmpty()) {
            throw new NullPointerException("url is null or empty");
        }
        this.url = url;

        return this;
    }

    public final double getTickRate() {
        return this.tickRate;
    }

    public final Sprite setTickRate(double rate) {
        this.tickRate = rate;

        if (isPlaying()) {
            pause();

            play();
        }
        return this;
    }

    public final SpriteBehaviorMap getSpriteBehaviorMap() {
        return this.spriteBehaviorMap;
    }

    public final Sprite setSpriteBehaviorMap(SpriteBehaviorMap bmap) {
        if (bmap == null) {
            throw new NullPointerException("SpriteBehaviorMap is null");
        }
        this.spriteBehaviorMap = bmap;

        String behavior = getSpriteBehavior();

        if ((null != behavior) && (!behavior.trim().isEmpty())) {
            m_index = 0;

            m_frames = bmap.getFramesForBehavior(behavior);
        }
        return this;
    }

    public final String getSpriteBehavior() {
        return this.spriteBehavior;
    }

    public final Sprite setSpriteBehavior(String behavior) {
        if ((null == behavior) || ((behavior = behavior.trim()).isEmpty())) {
            throw new NullPointerException("behavior is null or empty");
        }
        this.spriteBehavior = behavior;

        SpriteBehaviorMap bmap = getSpriteBehaviorMap();
        if (null != bmap) {
            m_index = 0;

            m_frames = bmap.getFramesForBehavior(behavior);
        }
        return this;
    }

    public final Sprite setSerializationMode(ImageSerializationMode mode) {
        this.imageSerializationMode = mode;

        return this;
    }

    public final ImageSerializationMode getSerializationMode() {
        return imageSerializationMode;
    }

    public final Sprite setAutoPlay(boolean play) {
        this.autoPlay = play;

        return this;
    }

    public final boolean isAutoPlay() {
        return this.autoPlay;
    }

    public final Sprite play() {
        if (!isPlaying()) {
            if ((null != m_frames) && (null != m_sprite) && (m_index < m_frames.length)) {
                final Layer layer = getLayer();

                if (null != layer) {
                    final Sprite sprite = this;

                    final int repeat = (int) (1000.0 / Math.min(Math.max(getTickRate(), 0.001), 60.0));

                    m_paused = false;

                    m_ticker = new Timer() {
                        @Override
                        public void run() {
                            boolean draw = true;

                            if ((++m_index) >= m_frames.length) {
                                m_index = 0;

                                if (null != m_onroll) {
                                    draw = m_onroll.onSpriteRoll(sprite);
                                }
                            }
                            if (draw) {
                                if (null != m_ontick) {
                                    draw = m_ontick.onSpriteTick(sprite);
                                }
                                if (draw) {
                                    layer.batch();
                                }
                            }
                        }
                    };
                    m_ticker.scheduleRepeating(repeat);
                }
            }
        }
        return this;
    }

    public final Sprite onTick(SpriteOnTickHandler handler) {
        m_ontick = handler;

        return this;
    }

    public final Sprite onRoll(SpriteOnRollHandler handler) {
        m_onroll = handler;

        return this;
    }

    public final Sprite pause() {
        m_paused = true;

        if (null != m_ticker) {
            m_ticker.cancel();
        }
        return this;
    }

    public final boolean isPlaying() {
        return (!m_paused);
    }

    @Override
    protected boolean prepare(Context2D context, double alpha) {
        if ((null != m_frames) && (null != m_sprite) && (m_index < m_frames.length)) {
            final BoundingBox bbox = m_frames[m_index];

            if (null != bbox) {
                if (!m_inited) {
                    m_inited = true;

                    if (isAutoPlay()) {
                        play();
                    }
                }
                if (context.isSelection()) {
                    final String color = getColorKey();

                    if (null != color) {
                        context.save();

                        context.setFillColor(color);

                        context.fillRect(0, 0, bbox.getWidth(), bbox.getHeight());

                        context.restore();
                    }
                } else {
                    context.save();

                    context.setGlobalAlpha(alpha);

                    context.drawImage(m_sprite, bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight(), 0, 0, bbox.getWidth(), bbox.getHeight());

                    context.restore();
                }
            }
        }
        return false;
    }

    public final Sprite onLoaded(SpriteLoadedHandler handler) {
        m_loaded = handler;

        if (null != m_sprite) {
            m_loaded.onSpriteLoaded(this);
        }
        return this;
    }

    public final int getTick() {
        return m_index;
    }

    public final boolean isLoaded() {
        return (m_sprite != null);
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.URL, Attribute.SPRITE_BEHAVIOR_MAP, Attribute.SPRITE_BEHAVIOR);
    }
}
