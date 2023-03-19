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
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.SceneFastArrayStorageEngine;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.INodeEvent;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;

/**
 * Scene serves as a container for {@link Layer}<
 *
 * <ul>
 * <li>A {@link Scene} can contain more than one {@link Layer}</li>
 * </ul>
 */
public class Scene extends ContainerNode<Layer, Scene> {

    private int m_wide = 0;

    private int m_high = 0;

    private Viewport m_owns = null;

    private static long idCounter;

    private final HTMLDivElement m_element = (HTMLDivElement) DomGlobal.document.createElement("div");

    /**
     * Constructor. Creates an instance of a scene.
     */
    public Scene() {
        super(NodeType.SCENE, new SceneFastArrayStorageEngine());
        m_element.id = "scene_div" + idCounter++;
    }

    @Override
    public final IStorageEngine<Layer> getDefaultStorageEngine() {
        return new SceneFastArrayStorageEngine();
    }

    @Override
    public List<Attribute> getTransformingAttributes() {
        return LienzoCore.STANDARD_TRANSFORMING_ATTRIBUTES;
    }

    public final boolean adopt(final Viewport owns) {
        if ((null == m_owns) || (m_owns == owns)) {
            m_owns = owns;

            return true;
        }
        return false;
    }

    /**
     * Returns the {@link HTMLDivElement}
     *
     * @return {@link HTMLDivElement}
     */
    public HTMLDivElement getElement() {
        return m_element;
    }

    /**
     * Returns this scene's width, in pixels.
     *
     * @return int
     */
    public int getWidth() {
        return m_wide;
    }

    /**
     * Returns this scene's height, in pixels
     *
     * @return int
     */
    public int getHeight() {
        return m_high;
    }

    /**
     * Sets this scene's width, in pixels
     *
     * @param wide
     * @return this Scene
     */
    public Scene setWidth(final int wide) {
        setPixelSize(wide, getHeight());

        return this;
    }

    /**
     * Sets this scene's height, in pixels
     *
     * @param high
     * @return this Scene
     */
    public Scene setHeight(final int high) {
        setPixelSize(getWidth(), high);

        return this;
    }

    /**
     * Sets this scene's size (width and height) in pixels.
     *
     * @param wide
     * @param high
     * @return this Scene
     */
    public final Scene setPixelSize(final int wide, final int high) {
        m_wide = wide;

        m_high = high;

        m_element.style.width = CSSProperties.WidthUnionType.of(wide + Style.Unit.PX.getType());

        m_element.style.height = CSSProperties.HeightUnionType.of(high + Style.Unit.PX.getType());

        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers) {
            final int size = layers.size();

            for (int i = 0; i < size; i++) {
                final Layer layer = layers.get(i);

                if (null != layer) {
                    layer.setPixelSize(wide, high);
                }
            }
        }
        return this;
    }

    /**
     * Returns this scene.
     *
     * @return Scene
     */
    @Override
    public final Scene getScene() {
        return this;
    }

    /**
     * Convenience method to return an instance of itself.
     *
     * @return Scene
     */
    @Override
    public final Scene asScene() {
        return this;
    }

    /**
     * Returns an instance of this scene cast to {@link IContainer}
     *
     * @return Scene
     */
    @Override
    public final IContainer<Scene, Layer> asContainer() {
        return this;
    }

    /**
     * Returns the top layer (which is drawn last)
     *
     * @return Layer
     */
    public final Layer getTopLayer() {
        final NFastArrayList<Layer> layers = getChildNodes();

        final int n = layers.size();

        return n == 0 ? null : layers.get(n - 1);
    }

    /**
     * Iterates over the list of {@link Layer} and draws them all.
     */
    @Override
    public final Scene draw() {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers) {
            final int size = layers.size();

            for (int i = 0; i < size; i++) {
                final Layer layer = layers.get(i);

                if (null != layer) {
                    layer.draw();
                }
            }
        }
        return this;
    }

    /**
     * Iterates over the list of {@link Layer} and batch draws them all.
     */
    @Override
    public final Scene batch() {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers) {
            final int size = layers.size();

            for (int i = 0; i < size; i++) {
                final Layer layer = layers.get(i);

                if (null != layer) {
                    layer.batch();
                }
            }
        }
        return this;
    }

    /**
     * Given a set of (x,y) coordinates, returns the {@link Shape} that is matched.
     * The {@link Shape} returned will be the one found in the upper {@link Layer}
     * Return null if no {@link Shape} is detected or found.
     *
     * @param x
     * @param y
     * @return Shape
     */
    public final Shape<?> findShapeAtPoint(final int x, final int y) {
        if (isVisible()) {
            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers) {
                int size = layers.size();

                for (int i = size - 1; i >= 0; i--) {
                    final Layer layer = layers.get(i);

                    if (null != layer) {
                        final Shape<?> shape = layer.findShapeAtPoint(x, y);

                        if (null != shape) {
                            return shape;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Fires the given GWT event.
     */
    public final <H extends EventHandler, S> void fireEvent(final INodeEvent<H, S> event) {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers) {
            final int size = layers.size();

            for (int i = size - 1; i >= 0; i--) {
                final Layer layer = layers.get(i);

                if (null != layer) {
                    layer.fireEvent(event);
                }
            }
        }
    }

    /**
     * Adds a {@link Layer} to the Scene.
     * A draw will be invoked after the layer is added.
     */
    @Override
    public final Scene add(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            layer.removeFromParent();

            layer.setPixelSize(getWidth(), getHeight());

            getElement().appendChild(layer.getElement());

            super.add(layer);
        }
        return this;
    }

    @Override
    public final Scene add(final Layer layer, final Layer... children) {
        add(layer);

        for (Layer node : children) {
            add(node);
        }
        return this;
    }

    @Override
    public boolean removeFromParent() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final Viewport view = parent.asViewport();

            if (null != view) {
                view.remove(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Removes a {@link Layer}
     */
    @Override
    public final Scene remove(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            if (getChildNodes().contains(layer)) {
                getElement().removeChild(layer.getElement());
            }
            super.remove(layer);
        }
        return this;
    }

    /**
     * Removes all {@link Layer}
     */
    @Override
    public final Scene removeAll() {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            while (getElement().childElementCount > 0) {
                getElement().removeChild(getElement().firstChild);
            }
        }
        super.removeAll();

        return this;
    }

    /**
     * Moves the layer one level down in this scene.
     *
     * @param layer
     */
    @Override
    public final Scene moveDown(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            final int size = (int) getElement().childElementCount;

            if (size < 2) {
                return this;
            }
            final HTMLDivElement element = layer.getElement();

            for (int i = 0; i < size; i++) {
                final HTMLDivElement look = Js.uncheckedCast(getElement().childNodes.getAt(i));

                if (look == element) {
                    if (i == 0) {
                        // already at bottom

                        break;
                    }
                    getElement().insertBefore(element, getElement().childNodes.getAt(i - 1));

                    break;
                }
            }
            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers) {
                layers.moveDown(layer);
            }
        }
        return this;
    }

    /**
     * Moves the layer one level up in this scene.
     *
     * @param layer
     */
    @Override
    public final Scene moveUp(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            final int size = (int) getElement().childElementCount;

            if (size < 2) {
                return this;
            }
            final HTMLDivElement element = layer.getElement();

            for (int i = 0; i < size; i++) {
                final HTMLDivElement look = Js.uncheckedCast(getElement().childNodes.getAt(i));

                if (look == element) {
                    if ((i + 1) == size) {
                        break;// already at top
                    }
                    getElement().removeChild(element);
                    getElement().insertBefore(element, getElement().childNodes.getAt(i + 1).nextSibling);
                    break;
                }
            }
            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers) {
                layers.moveUp(layer);
            }
        }
        return this;
    }

    /**
     * Moves the layer to the top of the layers stack in this scene.
     *
     * @param layer
     */
    @Override
    public final Scene moveToTop(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            final double size = getElement().childElementCount;

            if (size < 2) {
                return this;
            }
            final HTMLDivElement element = layer.getElement();

            getElement().removeChild(element);

            getElement().appendChild(element);

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers) {
                layers.moveToTop(layer);
            }
        }
        return this;
    }

    /**
     * Moves the layer to the bottom of the layers stack in this scene.
     *
     * @param layer
     */
    @Override
    public final Scene moveToBottom(final Layer layer) {
        if ((null != layer) && (LienzoCore.IS_CANVAS_SUPPORTED)) {
            final int size = (int) getElement().childElementCount;

            if (size < 2) {
                return this;
            }
            final HTMLDivElement element = layer.getElement();

            getElement().removeChild(element);

            getElement().insertBefore(element, getElement().firstChild);

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers) {
                layers.moveToBottom(layer);
            }
        }
        return this;
    }

    /**
     * No-op, but must implement.
     *
     * @return this Scene
     */
    @Override
    public final Scene moveUp() {
        return this;
    }

    /**
     * No-op, but must implement.
     *
     * @return this Scene
     */
    @Override
    public final Scene moveDown() {
        return this;
    }

    /**
     * No-op, but must implement.
     *
     * @return this Scene
     */
    @Override
    public final Scene moveToTop() {
        return this;
    }

    /**
     * No-op, but must implement.
     *
     * @return this Scene
     */
    @Override
    public final Scene moveToBottom() {
        return this;
    }

    public final String toDataURL() {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            final ScratchPad scratch = new ScratchPad(getWidth(), getHeight());

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            BoundingBox bbox = getStorageBounds();

            if (null == bbox) {
                Viewport viewport = getViewport();

                if (null != viewport) {
                    bbox = viewport.getStorageBounds();
                }
            }
            if (null != layers) {
                final int size = layers.size();

                final IPathClipper clip = getPathClipper();

                if ((null != clip) && (clip.isActive())) {
                    context.save();

                    clip.clip(context);
                }
                for (int i = size - 1; i >= 0; i--) {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible())) {
                        layer.drawWithTransforms(context, 1, bbox);
                    }
                }
                if ((null != clip) && (clip.isActive())) {
                    context.restore();
                }
            }
            return scratch.toDataURL();
        } else {
            return "data:,";
        }
    }

    // package protected

    final String toDataURL(final Layer background) {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            final ScratchPad scratch = new ScratchPad(getWidth(), getHeight());

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            BoundingBox bbox = getStorageBounds();

            if (null == bbox) {
                Viewport viewport = getViewport();

                if (null != viewport) {
                    bbox = viewport.getStorageBounds();
                }
            }
            if (null != layers) {
                final int size = layers.size();

                if (null != background) {
                    background.drawWithTransforms(context, 1, bbox);
                }
                final IPathClipper clip = getPathClipper();

                if ((null != clip) && (clip.isActive())) {
                    context.save();

                    clip.clip(context);
                }
                for (int i = size - 1; i >= 0; i--) {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible())) {
                        layer.drawWithTransforms(context, 1, bbox);
                    }
                }
                if ((null != clip) && (clip.isActive())) {
                    context.restore();
                }
            }
            return scratch.toDataURL();
        } else {
            return "data:,";
        }
    }

    public final String toDataURL(final DataURLType mimetype) {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            final ScratchPad scratch = new ScratchPad(getWidth(), getHeight());

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            BoundingBox bbox = getStorageBounds();

            if (null == bbox) {
                Viewport viewport = getViewport();

                if (null != viewport) {
                    bbox = viewport.getStorageBounds();
                }
            }
            if (null != layers) {
                final int size = layers.size();

                final IPathClipper clip = getPathClipper();

                if ((null != clip) && (clip.isActive())) {
                    context.save();

                    clip.clip(context);
                }
                for (int i = size - 1; i >= 0; i--) {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible())) {
                        layer.drawWithTransforms(context, 1, bbox);
                    }
                }
                if ((null != clip) && (clip.isActive())) {
                    context.restore();
                }
            }
            return scratch.toDataURL(mimetype, 1.0);
        } else {
            return "data:,";
        }
    }

    // package protected

    final String toDataURL(final DataURLType mimetype, final Layer background) {
        if (LienzoCore.IS_CANVAS_SUPPORTED) {
            final ScratchPad scratch = new ScratchPad(getWidth(), getHeight());

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            BoundingBox bbox = getStorageBounds();

            if (null == bbox) {
                Viewport viewport = getViewport();

                if (null != viewport) {
                    bbox = viewport.getStorageBounds();
                }
            }
            if (null != layers) {
                final int size = layers.size();

                if (null != background) {
                    background.drawWithTransforms(context, 1, bbox);
                }
                final IPathClipper clip = getPathClipper();

                if ((null != clip) && (clip.isActive())) {
                    context.save();

                    clip.clip(context);
                }
                for (int i = size - 1; i >= 0; i--) {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible())) {
                        layer.drawWithTransforms(context, 1, bbox);
                    }
                }
                if ((null != clip) && (clip.isActive())) {
                    context.restore();
                }
            }
            return scratch.toDataURL(mimetype, 1.0);
        } else {
            return "data:,";
        }
    }

    @Override
    protected void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff) {
        if (predicate.test(this)) {
            buff.add(this);
        }
        final NFastArrayList<Layer> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++) {
            final Layer layer = list.get(i);

            if (null != layer) {
                if (predicate.test(layer)) {
                    buff.add(layer);
                }
                layer.find(predicate, buff);
            }
        }
    }
}