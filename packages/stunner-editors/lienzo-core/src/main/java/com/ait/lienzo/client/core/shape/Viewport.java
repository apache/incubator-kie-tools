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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.event.EventReceiver;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.event.OrientationChangeEvent;
import com.ait.lienzo.client.core.event.OrientationChangeHandler;
import com.ait.lienzo.client.core.event.ResizeChangeEvent;
import com.ait.lienzo.client.core.event.ResizeChangeHandler;
import com.ait.lienzo.client.core.event.ResizeEndEvent;
import com.ait.lienzo.client.core.event.ResizeEndHandler;
import com.ait.lienzo.client.core.event.ResizeStartEvent;
import com.ait.lienzo.client.core.event.ResizeStartHandler;
import com.ait.lienzo.client.core.event.ViewportTransformChangedEvent;
import com.ait.lienzo.client.core.event.ViewportTransformChangedHandler;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.ViewportFastArrayStorageEngine;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.DragMouseControl;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.INodeEvent;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDivElement;

/**
 * Serves as a container for {@link Scene}
 *
 * <ul>
 * <li>A {@link Viewport} containsBoundingBox three {@link Scene} (Main, Drag and Back Scene)</li>
 * <li>The main {@link Scene} can contain multiple {@link Layer}.</li>
 * </ul>
 */
public class Viewport extends ContainerNode<Scene, Viewport> implements EventReceiver {

    private int m_wide = 0;

    private int m_high = 0;

    private final HTMLDivElement m_element = (HTMLDivElement) DomGlobal.document.createElement("div");
    ;

    private Scene m_drag = new Scene();

    private Scene m_main = null;

    private Scene m_back = new Scene();

    private ScratchPad m_spad = new ScratchPad(0, 0);

    private Mediators m_mediators;

    private DragMouseControl m_drag_mouse_control;

    private static long idCounter;

    private final OnEventHandlers m_onEventHandlers = new OnEventHandlers();

    private ViewportTransformChangedEvent viewportTransformChangedEvent;

    public Viewport() {
        this(0, 0);
    }

    public Viewport(final Scene main, final int wide, final int high) {
        super(NodeType.VIEWPORT, new ViewportFastArrayStorageEngine());

        m_wide = wide;

        m_high = high;

        setSceneAndState(main);
    }

    /**
     * Constructor. Creates an instance of a viewport.
     *
     * @param wide
     * @param high
     */
    public Viewport(final int wide, final int high) {
        super(NodeType.VIEWPORT, new ViewportFastArrayStorageEngine());

        m_wide = wide;

        m_high = high;

        setSceneAndState(new Scene());
    }

    @Override
    public final IStorageEngine<Scene> getDefaultStorageEngine() {
        return new ViewportFastArrayStorageEngine();
    }

    /**
     * Sets the Transform for this Viewport and fires a ZoomEvent
     * to any ZoomHandlers registered with this Viewport.
     *
     * @param transform Transform
     * @return this Viewport
     */

    @Override
    public final Viewport setTransform(final Transform transform) {
        super.setTransform(transform);
        if (viewportTransformChangedEvent != null) {
            viewportTransformChangedEvent.revive();
            viewportTransformChangedEvent.override(this);
            super.fireEvent(viewportTransformChangedEvent);
            viewportTransformChangedEvent.kill();
        }
        return this;
    }

    @Override
    protected Transform getPossibleNodeTransform() {
        return getTransform();
    }

    @Override
    public List<Attribute> getTransformingAttributes() {
        return LienzoCore.VIEWPORT_TRANSFORMING_ATTRIBUTES;
    }

    public void setDragMouseButtons(DragMouseControl controls) {
        m_drag_mouse_control = controls;
    }

    public DragMouseControl getDragMouseButtons() {
        return m_drag_mouse_control;
    }

    private final void setSceneAndState(final Scene main) {
        add(m_back, m_main = main, m_drag);

        m_drag.add(new DragLayer());

        m_drag.add(new Layer());

        m_mediators = new Mediators(this);

        viewportTransformChangedEvent = new ViewportTransformChangedEvent(getElement());

        final Transform transform = getTransform();

        if (null == transform) {
            // Zoom mediators rely on the Transform not being null.

            setTransform(new Transform());
        }
        m_element.id = "viewPort_div" + idCounter++;
    }

    @Override
    public final Viewport asViewport() {
        return this;
    }

    /**
     * Returns the viewport width in pixels.
     *
     * @return int
     */
    public final int getWidth() {
        return m_wide;
    }

    /**
     * Returns the viewport height in pixels.
     *
     * @return int
     */
    public final int getHeight() {
        return m_high;
    }

    /**
     * Returns the {@link HTMLDivElement}
     *
     * @return {@link HTMLDivElement}
     */
    public final HTMLDivElement getElement() {
        return m_element;
    }

    /**
     * Sets size of the {@link Viewport} in pixels
     *
     * @param wide
     * @param high
     * @return Viewpor this viewport
     */
    public final Viewport setPixelSize(final int wide, final int high) {
        m_wide = wide;

        m_high = high;

        getElement().style.width = CSSProperties.WidthUnionType.of(wide + Style.Unit.PX.getType());

        getElement().style.height = CSSProperties.HeightUnionType.of(high + Style.Unit.PX.getType());

        final NFastArrayList<Scene> scenes = getChildNodes();

        if (null != scenes) {
            final int size = scenes.size();

            for (int i = 0; i < size; i++) {
                final Scene scene = scenes.get(i);

                if (null != scene) {
                    scene.setPixelSize(wide, high);
                }
            }
        }
        m_spad.setPixelSize(wide, high);

        return this;
    }

    public OnEventHandlers getOnEventHandlers() {
        return m_onEventHandlers;
    }

    /**
     * Adds a {@link Scene} to this viewport.
     *
     * @param scene
     */
    @Override
    public final Viewport add(final Scene scene) {
        if ((null != scene) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            if (!scene.adopt(this)) {
                throw new IllegalArgumentException("Scene is already adopted.");
            }
            if (length() > 2) {
                throw new IllegalArgumentException("Too many Scene objects is Viewport.");
            }
            HTMLDivElement element = scene.getElement();

            setScenePixelSize(scene, m_wide, m_high);

            element.style.position = Style.Position.ABSOLUTE.getCssName();

            element.style.display = Style.Display.INLINE_BLOCK.getCssName();

            getElement().appendChild(element);

            super.add(scene);
        }
        return this;
    }

    public Scene setScenePixelSize(Scene scene, int h, int w) {
        scene.setPixelSize(h, w);
        return scene;
    }

    @Override
    public final Viewport add(final Scene scene, final Scene... children) {
        add(scene);

        for (Scene node : children) {
            add(node);
        }
        return this;
    }

    @Override
    public boolean removeFromParent() {
        return false;
    }

    public HandlerRegistration addViewportTransformChangedHandler(final ViewportTransformChangedHandler handler) {
        return addEnsureHandler(ViewportTransformChangedEvent.getType(), handler);
    }

    public HandlerRegistration addOrientationChangeHandler(final OrientationChangeHandler handler) {
        return addEnsureHandler(OrientationChangeEvent.getType(), handler);
    }

    public HandlerRegistration addResizeStartHandler(final ResizeStartHandler handler) {
        return addEnsureHandler(ResizeStartEvent.getType(), handler);
    }

    public HandlerRegistration addResizeChangeHandler(final ResizeChangeHandler handler) {
        return addEnsureHandler(ResizeChangeEvent.getType(), handler);
    }

    public HandlerRegistration addResizeEndHandler(final ResizeEndHandler handler) {
        return addEnsureHandler(ResizeEndEvent.getType(), handler);
    }

    @Override
    public final Viewport draw() {
        final NFastArrayList<Scene> scenes = getChildNodes();

        if (null != scenes) {
            final int size = scenes.size();

            for (int i = 0; i < size; i++) {
                final Scene scene = scenes.get(i);

                if (null != scene) {
                    scene.draw();
                }
            }
        }
        return this;
    }

    @Override
    public final Viewport batch() {
        final NFastArrayList<Scene> scenes = getChildNodes();

        if (null != scenes) {
            final int size = scenes.size();

            for (int i = 0; i < size; i++) {
                final Scene scene = scenes.get(i);

                if (null != scene) {
                    scene.batch();
                }
            }
        }
        return this;
    }

    /**
     * Returns the main Scene for the {@link Viewport}
     *
     * @return {@link Scene}
     */
    @Override
    public final Scene getScene() {
        return m_main;
    }

    /**
     * Sets the background layer
     *
     * @param layer
     * @return this Viewport
     */
    public final Viewport setBackgroundLayer(final Layer layer) {
        m_back.removeAll();

        m_back.add(layer);

        return this;
    }

    /**
     * Returns the Drag Layer.
     *
     * @return {@link Layer}
     */
    public final Layer getDragLayer() {
        return m_drag.getChildNodes().get(0);
    }

    @Override
    public final Layer getOverLayer() {
        return m_drag.getChildNodes().get(1);
    }

    @Override
    public final Viewport getViewport() {
        return this;
    }

    @Override
    public final ScratchPad getScratchPad() {
        return m_spad;
    }

    /**
     * No-op; this method has no effect. Simply overriden but in reality Scenes will not be removed from this {@link Viewport}
     */
    @Override
    public final Viewport remove(final Scene scene) {
        return this;
    }

    /**
     * No-op; this method has no effect. Simply overriden but in reality Scenes will not be removed from this {@link Viewport}
     */
    @Override
    public final Viewport removeAll() {
        getScene().removeAll();

        return this;
    }

    /**
     * No-op.
     *
     * @return this Viewport
     */
    @Override
    public final Viewport moveUp() {
        return this;
    }

    /**
     * No-op.
     *
     * @return this Viewport
     */
    @Override
    public final Viewport moveDown() {
        return this;
    }

    /**
     * No-op.
     *
     * @return this Viewport
     */
    @Override
    public final Viewport moveToTop() {
        return this;
    }

    /**
     * No-op.
     *
     * @return this Viewport
     */
    @Override
    public final Viewport moveToBottom() {
        return this;
    }

    /**
     * Change the viewport's transform so that the specified area (in global or canvas coordinates)
     * is visible.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void viewGlobalArea(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        final Transform t = getTransform();

        if (null != t) {
            Point2D a = new Point2D(x, y);

            Point2D b = new Point2D(x + width, y + height);

            final Transform inv = t.getInverse();

            inv.transform(a, a);

            inv.transform(b, b);

            x = a.getX();

            y = a.getY();

            width = b.getX() - x;

            height = b.getY() - y;
        }
        viewLocalArea(x, y, width, height);
    }

    /**
     * Change the viewport's transform so that the specified area (in local or world coordinates)
     * is visible.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void viewLocalArea(final double x, final double y, final double width, final double height) {
        final Transform t = Transform.createViewportTransform(x, y, width, height, m_wide, m_high);

        if (t != null) {
            setTransform(t);
        }
    }

    private final Layer getBackgroundLayer() {
        final NFastArrayList<Layer> list = m_back.getChildNodes();

        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public final Shape<?> findShapeAtPoint(final int x, final int y) {
        if (isVisible()) {
            return getScene().findShapeAtPoint(x, y);
        }
        return null;
    }

    /**
     * Fires the given GWT event.
     */
    public final <H extends EventHandler, S> void fireEvent(final INodeEvent<H, S> event) {
        getScene().fireEvent(event);
    }

    public final String toDataURL() {
        return getScene().toDataURL();
    }

    public final String toDataURL(final boolean includeBackgroundLayer) {
        if (includeBackgroundLayer) {
            return getScene().toDataURL(getBackgroundLayer());
        } else {
            return getScene().toDataURL();
        }
    }

    public final String toDataURL(final DataURLType mimetype) {
        return getScene().toDataURL(mimetype);
    }

    public final String toDataURL(final DataURLType mimetype, final boolean includeBackgroundLayer) {
        if (includeBackgroundLayer) {
            return getScene().toDataURL(mimetype, getBackgroundLayer());
        } else {
            return getScene().toDataURL(mimetype);
        }
    }

    @Override
    protected void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff) {
        if (predicate.test(this)) {
            buff.add(this);
        }
        m_main.find(predicate, buff);
    }

    /**
     * Returns the {@link Mediators} for this viewport.
     * Mediators can be used to e.g. to addBoundingBox zoom operations.
     *
     * @return Mediators
     */
    public final Mediators getMediators() {
        return m_mediators;
    }

    /**
     * Add a mediator to the stack of {@link Mediators} for this viewport.
     * The one that is added last, will be called first.
     * <p>
     * Mediators can be used to e.g. to addBoundingBox zoom operations.
     *
     * @param mediator IMediator
     */
    public final void pushMediator(final IMediator mediator) {
        m_mediators.push(mediator);
    }

    private static class DragLayer extends Layer {

        private DragContext2D m_context;

        public DragLayer() {
            setVisible(true);

            setListening(false);
        }

        @Override
        public HTMLCanvasElement getCanvasElement() {
            final HTMLCanvasElement element = super.getCanvasElement();

            if (null != element) {
                if (null == m_context) {
                    m_context = new DragContext2D(element);
                }
            }
            return element;
        }

        @Override
        public final Context2D getContext() {
            return m_context;
        }

        private static class DragContext2D extends Context2D {

            public DragContext2D(HTMLCanvasElement element) {
                super(element);
            }

            @Override
            public boolean isDrag() {
                return true;
            }
        }
    }
}
