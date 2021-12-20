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
import com.ait.lienzo.client.core.animation.LayerRedrawManager;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.OnLayerAfterDraw;
import com.ait.lienzo.client.core.types.OnLayerBeforeDraw;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.LayerClearMode;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDivElement;

/**
 * Layer is an abstraction for the Canvas element.
 * <ul>
 *      <li>Layers are assigned z-indexes automatically.</li>
 *      <li>Every Layer containsBoundingBox a {@link SelectionLayer} to act as an off-set canvas.</li>
 *      <li>Layers may contain {@link IPrimitive} or {@link Group}.</li>
 * </ul>
 */
public class Layer extends ContainerNode<IPrimitive<?>, Layer> {

    private int m_wide = 0;

    private int m_high = 0;

    private boolean m_shower = false;

    private SelectionLayer m_select = null;

    private OnLayerBeforeDraw m_olbd = null;

    private OnLayerAfterDraw m_olad = null;

    private HTMLCanvasElement m_element = null;

    private Context2D m_context = null;

    private HTMLDivElement m_wrapper = null;

    private long m_batched = 0L;

    private boolean clearLayerBeforeDraw = true;

    private boolean transformable = true;

    private final ColorKeyRotor m_c_rotor = new ColorKeyRotor();

    private final NFastStringMap<Shape<?>> m_shape_color_map = new NFastStringMap<>();

    private static long idCounter;

    /**
     * Constructor. Creates an instance of a Layer.
     */
    public Layer() {
        super(NodeType.LAYER, new PrimitiveFastArrayStorageEngine());
    }

    public Layer(final IStorageEngine<IPrimitive<?>> storage) {
        super(NodeType.LAYER, storage);
    }

    public final HTMLDivElement getElement() {
        if (null == m_wrapper) {
            m_wrapper = (HTMLDivElement) DomGlobal.document.createElement("div"); //Document.get().createDivElement();

            m_wrapper.style.position = Style.Position.ABSOLUTE.getCssName();

            m_wrapper.style.display = Style.Display.INLINE_BLOCK.getCssName();

            m_wrapper.id = "layer_wrapper_div" + idCounter++;

            final HTMLCanvasElement element = getCanvasElement();

            if (null != element) {
                if (!isSelection()) {
                    m_wrapper.appendChild(element);
                }
            }
        }
        return m_wrapper;
    }

    public final boolean isBatchScheduled() {
        return (m_batched > 0L);
    }

    public final Layer doBatchScheduled() {
        m_batched++;

        return this;
    }

    public final Layer unBatchScheduled() {
        m_batched = 0L;

        return this;
    }

    @Override
    public final IStorageEngine<IPrimitive<?>> getDefaultStorageEngine() {
        return new PrimitiveFastArrayStorageEngine();
    }

    /**
     * Returns the Selection Layer.
     *
     * @return {@link SelectionLayer}
     */
    public final SelectionLayer getSelectionLayer() {
        if (isListening()) {
            if (null == m_select) {
                m_select = new SelectionLayer();

                m_select.setPixelSize(getWidth(), getHeight());
            }
            return m_select;
        }
        return null;
    }

    /**
     * Looks at the {@link SelectionLayer} and attempts to find a {@link Shape} whose alpha
     * channel is 255.
     *
     * @param x
     * @param y
     * @return {@link Shape}
     */
    public Shape<?> findShapeAtPoint(final int x, final int y) {
        if (isVisible()) {
            final SelectionLayer selection = getSelectionLayer();

            if (null != selection) {
                final ImageDataPixelColor rgba = selection.getContext().getImageDataPixelColor(x, y);// x,y is adjusted to canvas coordinates in event dispatch

                if (null != rgba) {
                    if (rgba.getA() != 255) {
                        return null;
                    }
                    final Shape<?> shape = m_shape_color_map.get(rgba.toBrowserRGB());

                    if ((null != shape) && (shape.isVisible())) {
                        return shape;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Attribute> getTransformingAttributes() {
        return LienzoCore.STANDARD_TRANSFORMING_ATTRIBUTES;
    }

    /**
     * Adds a primitive to the collection. Override to ensure primitive is putString in Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @Override
    public Layer add(final IPrimitive<?> child) {
        super.add(child);

        child.attachToLayerColorMap();

        return this;
    }

    @Override
    public Layer add(final IPrimitive<?> child, final IPrimitive<?>... children) {
        add(child);

        for (IPrimitive<?> node : children) {
            add(node);
        }
        return this;
    }

    /**
     * Removes a primitive from the container. Override to ensure primitive is removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @Override
    public Layer remove(final IPrimitive<?> child) {
        child.detachFromLayerColorMap();

        super.remove(child);

        return this;
    }

    @Override
    public boolean removeFromParent() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final Scene scene = parent.asScene();

            if (null != scene) {
                scene.remove(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Removes all primitives from the collection. Override to ensure all primitives are removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @Override
    public Layer removeAll() {
        final NFastArrayList<IPrimitive<?>> list = getChildNodes();

        if (null != list) {
            final int size = list.size();

            for (int i = 0; i < size; i++) {
                list.get(i).detachFromLayerColorMap();
            }
        }
        super.removeAll();

        return this;
    }

    /**
     * Internal method. Attach a Shape to the Layers Color Map
     */
    final void attachShapeToColorMap(final Shape<?> shape) {
        if (null != shape) {
            String color = shape.getColorKey();

            if (null != color) {
                m_shape_color_map.remove(color);

                shape.setColorKey(null);
            }
            int count = 0;

            do {
                count++;

                color = m_c_rotor.next();
            }
            while ((m_shape_color_map.get(color) != null) && (count <= ColorKeyRotor.COLOR_SPACE_MAXIMUM));

            if (count > ColorKeyRotor.COLOR_SPACE_MAXIMUM) {
                throw new IllegalArgumentException("Exhausted color space " + count);
            }
            m_shape_color_map.put(color, shape);

            shape.setColorKey(color);
        }
    }

    /**
     * Internal method. Detach a {@link Shape} from the Layers Color Map
     *
     * @param shape
     */
    final void detachShapeFromColorMap(final Shape<?> shape) {
        if (null != shape) {
            final String color = shape.getColorKey();

            if (null != color) {
                final Shape<?> look = m_shape_color_map.get(color);

                if (shape == look) {
                    shape.setColorKey(null);

                    m_shape_color_map.remove(color);
                }
            }
        }
    }

    /**
     * Sets this layer's pixel size.
     *
     * @param wide
     * @param high
     */
    public void setPixelSize(final int wide, final int high) {
        m_wide = wide;

        m_high = high;

        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            if (!isSelection()) {
                getElement().style.width = CSSProperties.WidthUnionType.of(wide + Style.Unit.PX.getType());

                getElement().style.height = CSSProperties.HeightUnionType.of(high + Style.Unit.PX.getType());
            }

            final HTMLCanvasElement element = getCanvasElement();

            element.width = wide;

            element.height = high;

            if (!isSelection()) {
                getContext().getNativeContext().initDeviceRatio();
            }

            if ((!isSelection()) && (null != m_select)) {
                m_select.setPixelSize(wide, high);
            }
        }
    }

    /**
     * Enables event handling on this object.
     *
     * @param listening
     * @param Layer
     */
    @Override
    public Layer setListening(final boolean listening) {
        super.setListening(listening);

        if (listening) {
            if (isShowSelectionLayer()) {
                if (null != getSelectionLayer()) {
                    doShowSelectionLayer(true);
                }
            }
        } else {
            if (isShowSelectionLayer()) {
                doShowSelectionLayer(false);
            }
            m_select = null;
        }
        return this;
    }

    public boolean isShowSelectionLayer() {
        return m_shower;
    }

    public Layer setShowSelectionLayer(final boolean shower) {
        m_shower = shower;

        return doShowSelectionLayer(shower);
    }

    private final Layer doShowSelectionLayer(final boolean shower) {
        if (!isSelection()) {
            if (null != m_select) {
                while (getElement().childElementCount > 0) {
                    getElement().removeChild(getElement().childNodes.getAt(0));
                }
                HTMLCanvasElement element = getCanvasElement();

                if (null != element) {
                    getElement().appendChild(element);
                }
                if (shower) {
                    element = m_select.getCanvasElement();

                    if (null != element) {
                        getElement().appendChild(element);
                    }
                }
            }
        }
        return this;
    }

    /**
     * Gets this layer's width.
     *
     * @return int
     */
    public int getWidth() {
        return m_wide;
    }

    /**
     * Sets this layer's width
     *
     * @param wide
     */
    void setWidth(final int wide) {
        m_wide = wide;
    }

    /**
     * Gets this layer's height
     *
     * @return int
     */
    public int getHeight() {
        return m_high;
    }

    /**
     * Sets this layer's height
     *
     * @param high
     * @return Layer
     */
    void setHeight(final int high) {
        m_high = high;
    }

    /**
     * Returns whether the Layer is zoomable.
     * If not, changes to the (parent) Viewport's transform (probably due to zoom or pan operations) won't affect this layer.
     * The default value is true.
     *
     * @return boolean
     */
    public boolean isTransformable() {
        return transformable;
    }

    /**
     * Sets whether the Layer is zoomable.
     * If not, changes to the (parent) Viewport's transform (probably due to zoom or pan operations) won't affect this layer.
     * The default value is true.
     *
     * @param zoomable boolean
     * @return
     */
    public Layer setTransformable(final boolean transformable) {
        this.transformable = transformable;

        return this;
    }

    /**
     * Returns whether this layer is cleared before being drawn.
     *
     * @return boolean
     */
    public boolean isClearLayerBeforeDraw() {
        return this.clearLayerBeforeDraw;
    }

    /**
     * Sets whether this layer should be cleared before being drawn.
     *
     * @param clear
     * @return Layer
     */
    public Layer setClearLayerBeforeDraw(final boolean clear) {
        this.clearLayerBeforeDraw = clear;
        return this;
    }

    /**
     * Return the {@link CanvasElement}.
     *
     * @return CanvasElement
     */
    public HTMLCanvasElement getCanvasElement() {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            if (null == m_element) {
                m_element = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");;

                m_element.style.position = Style.Position.ABSOLUTE.getCssName();

                m_element.style.display = Style.Display.INLINE_BLOCK.getCssName();

                m_element.id = "layer_canvas" + idCounter++;
            }
            if (null == m_context) {
                m_context = new Context2D(m_element);
            }
        }
        return m_element;
    }

    /**
     * Handler that can be used to hook into the pre-drawing process.
     * If the handler returns false, no drawing will take place.
     *
     * @param onLayerBeforeDrawHandler
     * @return Layer
     */
    public Layer setOnLayerBeforeDraw(final OnLayerBeforeDraw onLayerBeforeDrawHandler) {
        m_olbd = onLayerBeforeDrawHandler;

        return this;
    }

    /**
     * Handler that can be used to hook into the post-drawing process.
     * The handler will be invoked after the drawing process finishes.
     *
     * @param onLayerAfterDrawHandler
     * @return Layer
     */
    public Layer setOnLayerAfterDraw(final OnLayerAfterDraw onLayerAfterDrawHandler) {
        m_olad = onLayerAfterDrawHandler;

        return this;
    }

    /**
     * Draws the layer and invokes pre/post draw handlers.
     * Drawing only takes place if the layer is visible.
     */

    @Override
    public Layer draw() {
        return draw(getContext());
    }

    public Layer draw(Context2D context) {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            if (isClearLayerBeforeDraw()) {
                clear();
            }
            if (isVisible()) {
                boolean draw = true;

                if (null != m_olbd) {
                    draw = m_olbd.onLayerBeforeDraw(this);
                }
                if (draw) {
                    Transform transform = null;

                    final Viewport viewport = getViewport();

                    if ((isTransformable()) && (null != viewport)) {
                        transform = viewport.getTransform();
                    }
                    context.save();

                    if (null != transform) {
                        context.transform(transform);
                    }
                    final BoundingBox bbox = getStorageBounds();

                    IPathClipper vclp = null;

                    if (null != viewport) {
                        vclp = viewport.getPathClipper();

                        if ((null != vclp) && (vclp.isActive())) {
                            vclp.clip(context);
                        }
                    }
                    final IPathClipper lclp = getPathClipper();

                    if ((null != lclp) && (lclp.isActive())) {
                        lclp.clip(context);
                    }
                    drawWithTransforms(context, 1, bbox);

                    context.restore();

                    if (null != m_olad) {
                        m_olad.onLayerAfterDraw(this);
                    }
                    final SelectionLayer selection = getSelectionLayer();

                    if (null != selection) {
                        selection.clear();

                        context = selection.getContext();

                        context.save();

                        if (null != transform) {
                            context.transform(transform);
                        }
                        if ((null != vclp) && (vclp.isActive())) {
                            vclp.clip(context);
                        }
                        if ((null != lclp) && (lclp.isActive())) {
                            lclp.clip(context);
                        }
                        drawWithTransforms(context, 1, bbox);

                        context.restore();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Performs batch updates to the Layer, that is, drawing is deferred till the next AnimationFrame,
     * to cut down on redraws on rapid event dispatch.
     *
     * @return Layer
     */
    @Override
    public Layer batch() {
        return LayerRedrawManager.get().schedule(this);
    }

    /**
     * Sets whether this object is visible.
     *
     * @param visible
     * @return Layer
     */
    @Override
    public Layer setVisible(final boolean visible) {
        super.setVisible(visible);

        getElement().style.visibility = visible ? Style.Visibility.VISIBLE.getCssName() : Style.Visibility.HIDDEN.getCssName();

        return this;
    }

    /**
     * Returns this layer
     *
     * @return Layer
     */
    @Override
    public Layer getLayer() {
        return this;
    }

    @Override
    public Layer asLayer() {
        return this;
    }

    public boolean isSelection() {
        return false;
    }

    /**
     * Clears the layer.
     */
    public void clear() {
        if (LienzoCore.get().getLayerClearMode() == LayerClearMode.CLEAR) {
            final Context2D context = getContext();

            if (null != context) {
                context.clearRect(0, 0, getWidth(), getHeight());
            }
        } else {
            setPixelSize(getWidth(), getHeight());
        }
    }

    /**
     * Returns the {@link Context2D} this layer is operating on.
     *
     * @return Context2D
     */
    public Context2D getContext() {
        return m_context;
    }

    /**
     * Moves this layer one level up.
     *
     * @return Layer
     */
    @SuppressWarnings("unchecked")
    @Override
    public Layer moveUp() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container) {
                container.moveUp(this);
            }
        }
        return this;
    }

    /**
     * Moves this layer one level down.
     *
     * @return Layer
     */
    @SuppressWarnings("unchecked")
    @Override
    public Layer moveDown() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container) {
                container.moveDown(this);
            }
        }
        return this;
    }

    /**
     * Moves this layer to the top of the layer stack.
     *
     * @return Layer
     */
    @SuppressWarnings("unchecked")
    @Override
    public Layer moveToTop() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container) {
                container.moveToTop(this);
            }
        }
        return this;
    }

    /**
     * Moves this layer to the bottom of the layer stack.
     *
     * @return Layer
     */
    @SuppressWarnings("unchecked")
    @Override
    public Layer moveToBottom() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container) {
                container.moveToBottom(this);
            }
        }
        return this;
    }

    /**
     * Returns all the {@link Node} objects present in this layer that match the
     * given {@link com.ait.lienzo.client.core.types.INodeFilter}, this Layer
     * included.
     *
     * @param filter
     * @return ArrayList<Node>
     */
    @Override
    protected void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff) {
        if (predicate.test(this)) {
            buff.add(this);
        }
        final NFastArrayList<IPrimitive<?>> list = getChildNodes();

        if (null != list) {
            final int size = list.size();

            for (int i = 0; i < size; i++) {
                final IPrimitive<?> prim = list.get(i);

                if (null != prim) {
                    final Node<?> node = prim.asNode();

                    if (null != node) {
                        if (predicate.test(node)) {
                            buff.add(node);
                        }
                        final ContainerNode<?, ?> cont = node.asContainerNode();

                        if (null != cont) {
                            cont.find(predicate, buff);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the content of this Layer as a PNG image that can be used as a source for another canvas or an HTML element.
     *
     * @return String
     */
    public final String toDataURL() {
        if (null != m_element) {
            return toDataURL(m_element);
        } else {
            return "data:,";
        }
    }

    /**
     * Returns the content of this {@link Layer} as an image that can be used as a source for another canvas or an HTML element
     *
     * @return String
     */
    public final String toDataURL(DataURLType mimetype) {
        if (null != m_element) {
            if (null == mimetype) {
                mimetype = DataURLType.PNG;
            }
            return toDataURL(m_element, mimetype.getValue());
        } else {
            return "data:,";
        }
    }

    private static final String toDataURL(HTMLCanvasElement element) {
        return element.toDataURL(null);
    }

    private static final String toDataURL(HTMLCanvasElement element, String mimetype) {
        return element.toDataURL(mimetype);
    }

    public static class SelectionLayer extends Layer {

        private SelectionContext2D m_context;

        public SelectionLayer() {
            super();

            setListening(false);
        }

        /**
         * Empty implementation of draw. Not needed in this case.
         */
        @Override
        public Layer draw() {
            return this;
        }

        @Override
        public HTMLCanvasElement getCanvasElement() {
            final HTMLCanvasElement element = super.getCanvasElement();

            if (null != element) {
                if (null == m_context) {
                    m_context = new SelectionContext2D(element);
                }
            }
            return element;
        }

        @Override
        public boolean isSelection() {
            return true;
        }

        @Override
        public Context2D getContext() {
            return m_context;
        }

        private static class SelectionContext2D extends Context2D {

            public SelectionContext2D(final HTMLCanvasElement element) {
                super(element);

                super.setGlobalAlpha(1);
            }

            @Override
            public boolean isSelection() {
                return true;
            }

            @Override
            public void setGlobalAlpha(final double alpha) {
            }
        }
    }
}