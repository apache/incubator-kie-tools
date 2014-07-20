/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.ArrayList;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.LienzoGlobals;
import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.animation.LayerRedrawManager;
import com.ait.lienzo.client.core.image.BlobImageReadyCallback;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.NFastStringMap;
import com.ait.lienzo.client.core.types.INodeFilter;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.NativeInternalType;
import com.ait.lienzo.client.core.types.OnLayerAfterDraw;
import com.ait.lienzo.client.core.types.OnLayerBeforeDraw;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.LayerClearMode;
import com.ait.lienzo.shared.core.types.NodeType;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Layer is an abstraction for the Canvas element.
 * <ul>
 *      <li>Layers are assigned z-indexes automatically.</li>
 *      <li>Every Layer contains a {@link SelectionLayer} to act as an off-set canvas.</li>
 *      <li>Layers may contain {@link IPrimitive} or {@link Group}.</li>
 * </ul> 
 */
public class Layer extends ContainerNode<IPrimitive<?>, Layer>
{
    private int                            m_wide            = 0;

    private int                            m_high            = 0;

    private boolean                        m_virgin          = true;

    private SelectionLayer                 m_select          = null;

    private OnLayerBeforeDraw              m_olbd            = null;

    private OnLayerAfterDraw               m_olad            = null;

    private CanvasElement                  m_element         = null;

    private Context2D                      m_context         = null;

    private final NFastStringMap<Shape<?>> m_shape_color_map = new NFastStringMap<Shape<?>>();

    /**
     * Constructor. Creates an instance of a Layer.
     */
    public Layer()
    {
        super(NodeType.LAYER);

        setClearLayerBeforeDraw(true);

        setTransformable(true);
    }

    /**
     * Constructor. Creates an instance of a Layer.
     * 
     * @param node 
     */
    protected Layer(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(NodeType.LAYER, node, ctx);

        if (NativeInternalType.BOOLEAN != getAttributes().typeOf(Attribute.CLEAR_LAYER_BEFORE_DRAW))
        {
            setClearLayerBeforeDraw(true);
        }
        if (NativeInternalType.BOOLEAN != getAttributes().typeOf(Attribute.TRANSFORMABLE))
        {
            setTransformable(true);
        }
    }

    /**
     * Returns this Layer as a {@link Node}
     * 
     * @return 
     */
    @Override
    public Node<?> asNode()
    {
        return this;
    }

    /**
     * Returns the Selection Layer.
     * 
     * @return {@link SelectionLayer}
     */
    public final SelectionLayer getSelectionLayer()
    {
        if (isListening())
        {
            if (m_select == null)
            {
                m_select = new SelectionLayer();

                m_select.setPixelSize(m_wide, m_high);
            }
            return m_select;
        }
        return null;
    }

    public final Layer getLayerSelectionLayer()
    {
        if (isListening())
        {
            if (m_select == null)
            {
                m_select = new SelectionLayer();

                m_select.setPixelSize(m_wide, m_high);
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
    public Shape<?> findShapeAtPoint(int x, int y)
    {
        if (isVisible())
        {
            SelectionLayer selection = getSelectionLayer();

            if (selection != null)
            {
                ImageDataPixelColor rgba = selection.getContext().getImageDataPixelColor(x, y); // x,y is adjusted to canvas coordinates in event dispatch

                if (rgba != null)
                {
                    if (rgba.getA() != 255)
                    {
                        return null;
                    }
                    String ckey = rgba.toBrowserRGB();

                    Shape<?> shape = m_shape_color_map.get(ckey);

                    if ((shape != null) && (ckey.equals(shape.getColorKey())) && (shape.isVisible()))
                    {
                        return shape;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds a primitive to the collection. Override to ensure primitive is put in Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public Layer add(IPrimitive<?> child)
    {
        super.add(child);

        child.attachToLayerColorMap();

        return this;
    }

    /**
    * Removes a primitive from the container. Override to ensure primitive is removed from Layers Color Map
    * <p>
    * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
    * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
    * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
    */
    @Override
    public Layer remove(IPrimitive<?> child)
    {
        child.detachFromLayerColorMap();

        super.remove(child);

        return this;
    }

    /**
     * Removes all primitives from the collection. Override to ensure all primitives are removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public Layer removeAll()
    {
        NFastArrayList<IPrimitive<?>> list = getChildNodes();

        if (null != list)
        {
            int size = list.length();

            for (int i = 0; i < size; i++)
            {
                list.get(i).detachFromLayerColorMap();
            }
        }
        super.removeAll();

        return this;
    }

    /**
     * Internal method. Attach a Shape to the Layers Color Map
     */
    final void attachShapeToColorMap(Shape<?> shape)
    {
        if (null != shape)
        {
            Shape<?> look = m_shape_color_map.get(shape.getColorKey());

            if (null == look)
            {
                m_shape_color_map.put(shape.getColorKey(), shape);
            }
        }
    }

    /**
     * Internal method. Detach a {@link Shape} from the Layers Color Map
     * 
     * @param shape
     */
    final void detachShapeFromColorMap(Shape<?> shape)
    {
        if (null != shape)
        {
            Shape<?> look = m_shape_color_map.get(shape.getColorKey());

            if (shape == look)
            {
                m_shape_color_map.remove(shape.getColorKey());
            }
        }
    }

    /**
     * Serializes this Layer as a {@link com.google.gwt.json.client.JSONObject}
     * 
     * @return JSONObject
     */
    @Override
    public JSONObject toJSONObject()
    {
        JSONObject object = new JSONObject();

        object.put("type", new JSONString(getNodeType().getValue()));

        object.put("attributes", new JSONObject(getAttributes()));

        NFastArrayList<IPrimitive<?>> list = getChildNodes();

        JSONArray children = new JSONArray();

        if (list != null)
        {
            int size = list.length();

            for (int i = 0; i < size; i++)
            {
                IPrimitive<?> prim = list.get(i);

                if (null != prim)
                {
                    Node<?> node = prim.asNode();

                    if (null != node)
                    {
                        JSONObject make = node.toJSONObject();

                        if (null != make)
                        {
                            children.set(children.size(), make);
                        }
                    }
                }
            }
        }
        object.put("children", children);

        return object;
    }

    /**
     * Sets this layer's pixel size.
     * 
     * @param wide
     * @param high
     */
    void setPixelSize(int wide, int high)
    {
        m_wide = wide;

        m_high = high;

        if (LienzoGlobals.get().isCanvasSupported())
        {
            m_element.setWidth(wide);

            m_element.setHeight(high);

            if (null != m_select)
            {
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
    public Layer setListening(boolean listening)
    {
        super.setListening(listening);

        if (false == listening)
        {
            m_select = null;
        }
        return this;
    }

    /**
     * Gets this layer's width.
     * 
     * @return int
     */
    public int getWidth()
    {
        return m_wide;
    }

    /**
     * Sets this layer's width
     * 
     * @param wide
     */
    void setWidth(int wide)
    {
        m_wide = wide;
    }

    /**
     * Gets this layer's height
     * 
     * @return int
     */
    public int getHeight()
    {
        return m_high;
    }

    /**
     * Sets this layer's height
     * 
     * @param high
     * @return Layer
     */
    void setHeight(int high)
    {
        m_high = high;
    }

    /**
     * Returns whether the Layer is zoomable.
     * If not, changes to the (parent) Viewport's transform (probably due to zoom or pan operations) won't affect this layer.
     * The default value is true.
     * 
     * @return boolean
     */
    public boolean isTransformable()
    {
        return getAttributes().isTransformable();
    }

    /**
     * Sets whether the Layer is zoomable.
     * If not, changes to the (parent) Viewport's transform (probably due to zoom or pan operations) won't affect this layer.
     * The default value is true.
     * 
     * @param zoomable boolean
     * @return
     */
    public Layer setTransformable(boolean transformable)
    {
        getAttributes().setTransformable(transformable);

        return this;
    }

    /**
     * Returns whether this layer is cleared before being drawn.
     * 
     * @return boolean
     */
    public boolean isClearLayerBeforeDraw()
    {
        return getAttributes().isClearLayerBeforeDraw();
    }

    /**
     * Sets whether this layer should be cleared before being drawn.
     * 
     * @param clear
     * @return Layer
     */
    public Layer setClearLayerBeforeDraw(boolean clear)
    {
        getAttributes().setClearLayerBeforeDraw(clear);

        return this;
    }

    /**
     * Returns this layer as a {@link IContainer}
     * 
     * @return IContainer
     */
    @Override
    public IContainer<Layer, IPrimitive<?>> asContainer()
    {
        return this;
    }

    /**
     * Return the {@link CanvasElement}.
     * 
     * @return CanvasElement
     */
    public CanvasElement getCanvasElement()
    {
        if (LienzoGlobals.get().isCanvasSupported())
        {
            if (null == m_element)
            {
                m_element = Document.get().createCanvasElement();
            }
            if (null == m_context)
            {
                m_context = new Context2D(getNativeContext2D(m_element));
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
    public Layer setOnLayerBeforeDraw(OnLayerBeforeDraw onLayerBeforeDrawHandler)
    {
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
    public Layer setOnLayerAfterDraw(OnLayerAfterDraw onLayerAfterDrawHandler)
    {
        m_olad = onLayerAfterDrawHandler;

        return this;
    }

    /**
     * Draws the layer and invokes pre/post draw handlers.
     * Drawing only takes place if the layer is visible.
     */
    public void draw()
    {
        if (LienzoGlobals.get().isCanvasSupported())
        {
            boolean clear = isClearLayerBeforeDraw();

            if (clear)
            {
                clear();
            }
            if (isVisible())
            {
                boolean draw = true;

                if (m_olbd != null)
                {
                    draw = m_olbd.onLayerBeforeDraw(this);
                }
                if (draw)
                {
                    Context2D context = getContext();

                    Transform transform = null;

                    if (isTransformable())
                    {
                        Viewport viewport = getViewport();

                        if (null != viewport)
                        {
                            transform = viewport.getTransform();
                        }
                    }
                    if (transform != null)
                    {
                        context.save();

                        context.transform(transform);
                    }
                    drawWithTransforms(context);

                    if (transform != null)
                    {
                        context.restore();
                    }
                    if (m_olad != null)
                    {
                        m_olad.onLayerAfterDraw(this);
                    }
                    if (isListening())
                    {
                        SelectionLayer selection = getSelectionLayer();

                        if (null != selection)
                        {
                            selection.clear();

                            context = selection.getContext();

                            if (transform != null)
                            {
                                context.save();

                                context.transform(transform);
                            }
                            drawWithTransforms(context);

                            if (transform != null)
                            {
                                context.restore();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Performs batch updates to the Layer, that is, drawing is deferred till the next AnimationFrame,
     * to cut down on redraws on rapid event dispatch.
     * 
     * @return Layer
     */
    public Layer batch()
    {
        LayerRedrawManager.get().schedule(this);

        return this;
    }

    /**
     * Sets whether this object is visible.
     * 
     * @param visible
     * @return Layer
     */
    @Override
    public Layer setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (null != m_element)
        {
            if (false == visible)
            {
                m_element.getStyle().setVisibility(Visibility.HIDDEN);
            }
            else
            {
                m_element.getStyle().setVisibility(Visibility.VISIBLE);
            }
        }
        return this;
    }

    /**
     * Returns this layer
     * 
     * @return Layer
     */
    @Override
    public Layer getLayer()
    {
        return this;
    }

    @Override
    public Layer asLayer()
    {
        return this;
    }

    /**
     * Clears the layer.
     */
    public void clear()
    {
        if (false == m_virgin)
        {
            if (LienzoGlobals.get().getLayerClearMode() == LayerClearMode.CLEAR)
            {
                Context2D context = getContext();

                if (null != context)
                {
                    context.clearRect(0, 0, m_wide, m_high);
                }
            }
            else
            {
                setPixelSize(m_wide, m_high);
            }
        }
        else
        {
            m_virgin = false;
        }
    }

    /**
     * Returns the {@link Context2D} this layer is operating on.
     * 
     * @return Context2D
     */
    public Context2D getContext()
    {
        return m_context;
    }

    protected static final native NativeContext2D getNativeContext2D(CanvasElement element)
    /*-{
		return element.getContext("2d");
    }-*/;

    /**
     * Moves this layer one level up.
     * 
     * @return Layer
     */
    @SuppressWarnings("unchecked")
    @Override
    public Layer moveUp()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container)
            {
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
    public Layer moveDown()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container)
            {
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
    public Layer moveToTop()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container)
            {
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
    public Layer moveToBottom()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, Layer> container = (IContainer<?, Layer>) parent.asContainer();

            if (null != container)
            {
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
    public ArrayList<Node<?>> search(INodeFilter filter)
    {
        ArrayList<Node<?>> find = new ArrayList<Node<?>>();

        if (filter.matches(this))
        {
            find.add(this);
        }
        int size = length();

        for (int i = 0; i < size; i++)
        {
            IPrimitive<?> prim = getChildNodes().get(i);

            if (null != prim)
            {
                Node<?> node = prim.asNode();

                if (null != node)
                {
                    if (filter.matches(node))
                    {
                        if (false == find.contains(node))
                        {
                            find.add(node);
                        }
                    }
                    IContainer<?, ?> cont = node.asContainer();

                    if (null != cont)
                    {
                        for (Node<?> look : cont.search(filter))
                        {
                            if (false == find.contains(look))
                            {
                                find.add(look);
                            }
                        }
                    }
                }
            }
        }
        return find;
    }

    public final boolean isBlobAPISupported()
    {
        return LienzoGlobals.get().isBlobAPIEnabled() && (null != m_element) && isBlobAPISupported0(m_element);
    }

    public final void getImageFromBlob(BlobImageReadyCallback callback)
    {
        if (false == isBlobAPISupported())
        {
            throw new IllegalArgumentException("Blob Images are not supported");
        }
        getImageFromBlob0(m_element, callback);
    }

    private static native final void getImageFromBlob0(CanvasElement element, BlobImageReadyCallback callback)
    /*-{
		var blobber = function(blob) {
			url = $wnd.URL.createObjectURL(blob);

			var image = new $wnd.Image();

			image.onload = function() {

				$wnd.URL.revokeObjectURL(url);

				callback.@com.ait.lienzo.client.core.image.BlobImageReadyCallback::onBlobImageReady(Lcom/ait/lienzo/client/core/image/JSImage;)(image);
			}
			image.src = url;
		};
		element.toBlob(blobber);
    }-*/;

    private static native final boolean isBlobAPISupported0(CanvasElement element)
    /*-{
		return !!element.toBlob;
    }-*/;

    /**
     * Returns the content of this Layer as a PNG image that can be used as a source for another canvas or an HTML element.
     * 
     * @return String
     */
    public final String toDataURL()
    {
        if (null != m_element)
        {
            return toDataURL(m_element);
        }
        else
        {
            return "data:,";
        }
    }

    /**
     * Returns the content of this {@link Layer} as an image that can be used as a source for another canvas or an HTML element
     * 
     * @return String
     */
    public final String toDataURL(DataURLType mimetype)
    {
        if (null != m_element)
        {
            if (null == mimetype)
            {
                mimetype = DataURLType.PNG;
            }
            return toDataURL(m_element, mimetype.getValue());
        }
        else
        {
            return "data:,";
        }
    }

    @Override
    public IFactory<Layer> getFactory()
    {
        return new LayerFactory();
    }

    private static native final String toDataURL(CanvasElement element)
    /*-{
		return element.toDataURL();
    }-*/;

    private static native final String toDataURL(CanvasElement element, String mimetype)
    /*-{
		return element.toDataURL(mimetype);
    }-*/;

    private static class SelectionLayer extends Layer
    {
        private SelectionContext2D m_context;

        public SelectionLayer()
        {
            super();

            setVisible(false).setListening(false);
        }

        /**
         * Empty implementation of draw. Not needed in this case.
         */
        @Override
        public void draw()
        {
        }

        @Override
        public CanvasElement getCanvasElement()
        {
            CanvasElement element = null;

            if (LienzoGlobals.get().isCanvasSupported())
            {
                element = super.getCanvasElement();

                if (null != element)
                {
                    if (null == m_context)
                    {
                        m_context = new SelectionContext2D(getNativeContext2D(element));
                    }
                }
            }
            return element;
        }

        @Override
        public void setPixelSize(int wide, int high)
        {
            if (LienzoGlobals.get().isCanvasSupported())
            {
                CanvasElement element = getCanvasElement();

                element.getStyle().setPosition(Position.ABSOLUTE);

                element.getStyle().setDisplay(Display.INLINE_BLOCK);

                element.setWidth(wide);

                element.setHeight(high);

                super.setWidth(wide);

                super.setHeight(high);
            }
        }

        @Override
        public Context2D getContext()
        {
            return m_context;
        }

        private static class SelectionContext2D extends Context2D
        {
            public SelectionContext2D(NativeContext2D jso)
            {
                super(jso);
            }

            @Override
            public boolean isSelection()
            {
                return true;
            }
        }
    }

    public static class LayerFactory extends ContainerNodeFactory<Layer>
    {
        public LayerFactory()
        {
            super(NodeType.LAYER);

            addAttribute(Attribute.CLEAR_LAYER_BEFORE_DRAW);

            addAttribute(Attribute.TRANSFORMABLE);
        }

        @Override
        public Layer create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            Layer container = new Layer(node, ctx);

            JSONDeserializer.getInstance().deserializeChildren(container, node, this, ctx);

            return container;
        }

        @Override
        public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx)
        {
            if (node.getNodeType().isPrimitive())
            {
                container.asLayer().add(node.asPrimitive());

                return true;
            }
            else
            {
                try
                {
                    ctx.addBadTypeError(node.getClass().getName() + " is not a Primitive");
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