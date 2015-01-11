/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.java.util.function.Predicate;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Scene serves as a container for {@link Layer}<
 * 
 * <ul>
 * <li>A {@link Scene} can contain more than one {@link Layer}</li>
 * </ul> 
 */
public class Scene extends ContainerNode<Layer, Scene> implements IJSONSerializable<Scene>
{
    private int              m_wide    = 0;

    private int              m_high    = 0;

    private Viewport         m_owns    = null;

    private final DivElement m_element = Document.get().createDivElement();

    /**
     * Constructor. Creates an instance of a scene.
     */
    public Scene()
    {
        super(NodeType.SCENE);
    }

    protected Scene(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(NodeType.SCENE, node, ctx);
    }

    public final boolean adopt(final Viewport owns)
    {
        if ((null == m_owns) || (m_owns == owns))
        {
            m_owns = owns;

            return true;
        }
        return false;
    }

    /**
     * Returns the {@link DivElement}
     * 
     * @return {@link DivElement}
     */
    public DivElement getElement()
    {
        return m_element;
    }

    /**
     * Returns this scene's width, in pixels.
     * 
     * @return int
     */
    public int getWidth()
    {
        return m_wide;
    }

    /**
     * Returns this scene's height, in pixels
     * 
     * @return int
     */
    public int getHeight()
    {
        return m_high;
    }

    /**
     * Sets this scene's width, in pixels
     * 
     * @param wide
     * @return this Scene
     */
    public Scene setWidth(final int wide)
    {
        setPixelSize(wide, m_high);

        return this;
    }

    /**
     * Sets this scene's height, in pixels
     * 
     * @param high
     * @return this Scene
     */
    public Scene setHeight(final int high)
    {
        setPixelSize(m_wide, high);

        return this;
    }

    /**
     * Sets this scene's size (width and height) in pixels.
     * 
     * @param wide
     * @param high
     * @return this Scene
     */
    public final Scene setPixelSize(final int wide, final int high)
    {
        m_wide = wide;

        m_high = high;

        m_element.getStyle().setWidth(wide, Unit.PX);

        m_element.getStyle().setHeight(high, Unit.PX);

        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers)
        {
            final int size = layers.size();

            for (int i = 0; i < size; i++)
            {
                final Layer layer = layers.get(i);

                if (null != layer)
                {
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
    public final Scene getScene()
    {
        return this;
    }

    /**
     * Convenience method to return an instance of itself.
     * 
     * @return Scene
     */
    @Override
    public final Scene asScene()
    {
        return this;
    }

    /**
     * Returns an instance of this scene cast to {@link IContainer}
     * 
     * @return Scene
     */
    @Override
    public final IContainer<Scene, Layer> asContainer()
    {
        return this;
    }

    /**
     * Returns the top layer (which is drawn last)
     * 
     * @return Layer
     */
    public final Layer getTopLayer()
    {
        final NFastArrayList<Layer> layers = getChildNodes();

        final int n = layers.size();

        return n == 0 ? null : layers.get(n - 1);
    }

    /**
     * Iterates over the list of {@link Layer} and draws them all.
     */
    public final void draw()
    {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers)
        {
            final int size = layers.size();

            for (int i = 0; i < size; i++)
            {
                final Layer layer = layers.get(i);

                if (null != layer)
                {
                    layer.draw();
                }
            }
        }
    }

    /**
     * Iterates over the list of {@link Layer} and batch draws them all.
     */
    public final void batch()
    {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers)
        {
            final int size = layers.size();

            for (int i = 0; i < size; i++)
            {
                final Layer layer = layers.get(i);

                if (null != layer)
                {
                    layer.batch();
                }
            }
        }
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
    public final Shape<?> findShapeAtPoint(final int x, final int y)
    {
        if (isVisible())
        {
            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
                int size = layers.size();

                for (int i = size - 1; i >= 0; i--)
                {
                    final Layer layer = layers.get(i);

                    if (null != layer)
                    {
                        Shape<?> shape = layer.findShapeAtPoint(x, y);

                        if (null != shape)
                        {
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
    public final void fireEvent(final GwtEvent<?> event)
    {
        final NFastArrayList<Layer> layers = getChildNodes();

        if (null != layers)
        {
            final int size = layers.size();

            for (int i = size - 1; i >= 0; i--)
            {
                final Layer layer = layers.get(i);

                if (null != layer)
                {
                    layer.fireEvent(event);
                }
            }
        }
    }

    /**
     * Returns a {@link JSONObject} representation containing the object type, attributes and its respective children.
     * 
     * @return JSONObject
     */
    @Override
    public final JSONObject toJSONObject()
    {
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getNodeType().getValue()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        final NFastArrayList<Layer> list = getChildNodes();

        final JSONArray children = new JSONArray();

        if (list != null)
        {
            final int size = list.size();

            for (int i = 0; i < size; i++)
            {
                final Layer layer = list.get(i);

                if (null != layer)
                {
                    JSONObject make = layer.toJSONObject();

                    if (null != make)
                    {
                        children.set(children.size(), make);
                    }
                }
            }
        }
        object.put("children", children);

        return object;
    }

    /**
     * Adds a {@link Layer} to the Scene.
     * A draw will be invoked after the layer is added.
     */
    @Override
    public final Scene add(final Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            CanvasElement element = layer.getCanvasElement();

            layer.setPixelSize(m_wide, m_high);

            element.getStyle().setPosition(Position.ABSOLUTE);

            element.getStyle().setDisplay(Display.INLINE_BLOCK);

            getElement().appendChild(element);

            super.add(layer);

            layer.batch();
        }
        return this;
    }

    @Override
    public final Scene add(final Layer layer, final Layer... children)
    {
        add(layer);

        for (Layer node : children)
        {
            add(node);
        }
        return this;
    }

    @Override
    public boolean removeFromParent()
    {
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final Viewport view = parent.asViewport();

            if (null != view)
            {
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
    public final Scene remove(final Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            CanvasElement element = layer.getCanvasElement();

            getElement().removeChild(element);

            super.remove(layer);
        }
        return this;
    }

    /**
     * Removes all {@link Layer}
     */
    @Override
    public final Scene removeAll()
    {
        if (LienzoCore.get().isCanvasSupported())
        {
            while (getElement().getChildCount() > 0)
            {
                CanvasElement element = getElement().getChild(0).cast();

                getElement().removeChild(element);
            }
            super.removeAll();
        }
        return this;
    }

    /**
     * Moves the layer one level down in this scene.
     * 
     * @param layer
     */
    @Override
    public final Scene moveDown(final Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            CanvasElement element = layer.getCanvasElement();

            final int size = getElement().getChildCount();

            if (size < 2)
            {
                return this;
            }
            for (int i = 0; i < size; i++)
            {
                CanvasElement look = getElement().getChild(i).cast();

                if (look == element)
                {
                    if (i == 0)
                    {
                        break; // already at bottom
                    }
                    look = getElement().getChild(i - 1).cast();

                    getElement().insertBefore(element, look);

                    break;
                }
            }
            NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
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
    public final Scene moveUp(Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            final int size = getElement().getChildCount();

            if (size < 2)
            {
                return this;
            }
            CanvasElement element = layer.getCanvasElement();

            for (int i = 0; i < size; i++)
            {
                CanvasElement look = getElement().getChild(i).cast();

                if (look == element)
                {
                    if ((i + 1) == size)
                    {
                        break; // already at top
                    }
                    look = getElement().getChild(i + 1).cast();

                    getElement().removeChild(element);

                    getElement().insertAfter(element, look);

                    break;
                }
            }
            NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
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
    public final Scene moveToTop(Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            final int size = getElement().getChildCount();

            if (size < 2)
            {
                return this;
            }
            CanvasElement element = layer.getCanvasElement();

            getElement().removeChild(element);

            getElement().appendChild(element);

            NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
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
    public final Scene moveToBottom(Layer layer)
    {
        if ((null != layer) && (LienzoCore.get().isCanvasSupported()))
        {
            final int size = getElement().getChildCount();

            if (size < 2)
            {
                return this;
            }
            CanvasElement element = layer.getCanvasElement();

            getElement().removeChild(element);

            getElement().insertFirst(element);

            NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
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
    public final Scene moveUp()
    {
        return this;
    }

    /**
     * No-op, but must implement.
     * 
     * @return this Scene
     */
    @Override
    public final Scene moveDown()
    {
        return this;
    }

    /**
     * No-op, but must implement.
     * 
     * @return this Scene
     */
    @Override
    public final Scene moveToTop()
    {
        return this;
    }

    /**
     * No-op, but must implement.
     * 
     * @return this Scene
     */
    @Override
    public final Scene moveToBottom()
    {
        return this;
    }

    public final String toDataURL()
    {
        if (LienzoCore.get().isCanvasSupported())
        {
            ScratchCanvas scratch = new ScratchCanvas(m_wide, m_high);

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
                final int size = layers.size();

                for (int i = size - 1; i >= 0; i--)
                {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible()))
                    {
                        layer.drawWithTransforms(scratch.getContext(), 1);
                    }
                }
            }
            return scratch.toDataURL();
        }
        else
        {
            return "data:,";
        }
    }

    // package protected

    final String toDataURL(final Layer background)
    {
        if (LienzoCore.get().isCanvasSupported())
        {
            ScratchCanvas scratch = new ScratchCanvas(m_wide, m_high);

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
                final int size = layers.size();

                if (null != background)
                {
                    background.drawWithTransforms(context, 1);
                }
                for (int i = size - 1; i >= 0; i--)
                {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible()))
                    {
                        layer.drawWithTransforms(context, 1);
                    }
                }
            }
            return scratch.toDataURL();
        }
        else
        {
            return "data:,";
        }
    }

    public final String toDataURL(final DataURLType mimetype)
    {
        if (LienzoCore.get().isCanvasSupported())
        {
            ScratchCanvas scratch = new ScratchCanvas(m_wide, m_high);

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
                final int size = layers.size();

                for (int i = size - 1; i >= 0; i--)
                {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible()))
                    {
                        layer.drawWithTransforms(context, 1);
                    }
                }
            }
            return scratch.toDataURL(mimetype);
        }
        else
        {
            return "data:,";
        }
    }

    // package protected

    final String toDataURL(final DataURLType mimetype, final Layer background)
    {
        if (LienzoCore.get().isCanvasSupported())
        {
            ScratchCanvas scratch = new ScratchCanvas(m_wide, m_high);

            final Context2D context = scratch.getContext();

            final NFastArrayList<Layer> layers = getChildNodes();

            if (null != layers)
            {
                final int size = layers.size();

                if (null != background)
                {
                    background.drawWithTransforms(context, 1);
                }
                for (int i = size - 1; i >= 0; i--)
                {
                    final Layer layer = layers.get(i);

                    if ((null != layer) && (layer.isVisible()))
                    {
                        layer.drawWithTransforms(context, 1);
                    }
                }
            }
            return scratch.toDataURL(mimetype);
        }
        else
        {
            return "data:,";
        }
    }

    @Override
    public void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff)
    {
        if (predicate.test(this))
        {
            buff.add(this);
        }
        final NFastArrayList<Layer> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            final Layer layer = list.get(i);

            if (null != layer)
            {
                if (predicate.test(layer))
                {
                    buff.add(layer);
                }
                layer.find(predicate, buff);
            }
        }
    }

    @Override
    public final IFactory<Scene> getFactory()
    {
        return new SceneFactory();
    }

    public static class SceneFactory extends ContainerNodeFactory<Scene>
    {
        public SceneFactory()
        {
            super(NodeType.SCENE);
        }

        @Override
        public final Scene container(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Scene(node, ctx);
        }

        @Override
        public final boolean addNodeForContainer(final IContainer<?, ?> container, final Node<?> node, final ValidationContext ctx)
        {
            if (node.getNodeType() == NodeType.LAYER)
            {
                container.asScene().add(node.asLayer());

                return true;
            }
            else
            {
                try
                {
                    ctx.addBadTypeError(node.getClass().getName() + " is not a Layer");
                }
                catch (ValidationException e)
                {
                    return false;
                }
            }
            return false;
        }
    }
}