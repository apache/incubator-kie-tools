package com.ait.lienzo.client.core.shape.wires;

import java.awt.*;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.tooling.nativetools.client.util.Console;

public class DragAndDropManager
{
    private static final DragAndDropManager m_instance = new DragAndDropManager();

    private  static final MagnetManager magnetManager = MagnetManager.getInstance();

    public static final DragAndDropManager getInstance()
    {
        return m_instance;
    }

    private       NFastStringMap<WiresShape>    m_shape_color_map;

    public        NFastStringMap<IContainer<?, ?>> m_containers      = new NFastStringMap();

    private ImageData m_shapesBacking;

    public DragAndDropManager()
    {

    }

    public boolean isContainer(IPrimitive prim)
    {
        return m_containers.isDefined(prim.uuid());
    }

    public static class WiresShapeDragHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private DragAndDropManager m_dndManager;
        private WiresShape m_shape;
        private WiresContainer m_parent;
        private WiresLayer m_layer;
        private WiresManager m_wiresManager;
        private String m_priorFill;
        private double m_priorAlpha;

        public WiresShapeDragHandler(DragAndDropManager dndManager, WiresShape shape, WiresManager wiresManager)
        {
            m_dndManager = dndManager;
            m_shape = shape;
            m_wiresManager = wiresManager;
            m_layer = m_wiresManager.getLayer();
        }

        @Override public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_dndManager.m_shape_color_map = new NFastStringMap<WiresShape>();
            m_dndManager.m_shapesBacking = MagnetManager.drawShapesToBacking(m_layer.getChildShapes(), m_layer.getLayer().getScratchPad(), m_shape, m_dndManager.m_shape_color_map);
        }

        @Override public void onNodeDragMove(NodeDragMoveEvent event)
        {
            String color = MagnetManager.findColorAtPoint(m_dndManager.m_shapesBacking, event.getX(), event.getY());
            WiresContainer parent = null;
            if ( color != null )
            {
                parent =  m_dndManager.m_shape_color_map.get(color);
            }
            if ( parent != m_parent)
            {
                if ( m_parent != null && m_parent instanceof WiresShape )
                {

                    ((WiresShape)m_parent).getPath().setFillColor(m_priorFill);
                    ((WiresShape)m_parent).getPath().setAlpha(m_priorAlpha);
                }
                if ( parent != null && parent instanceof WiresShape )
                {
                    m_priorFill = ((WiresShape)parent).getPath().getFillColor();
                    m_priorAlpha = ((WiresShape)parent).getPath().getAlpha();
                    ((WiresShape)parent).getPath().setFillColor("#CCCCCC");
                    ((WiresShape)parent).getPath().setAlpha(0.5);
                }
            }
            m_parent = parent;
            m_layer.getLayer().batch();
        }

        @Override public void onNodeDragEnd(NodeDragEndEvent event)
        {
            addShapeToParent();
        }

        @Override public void onNodeMouseDown(NodeMouseDownEvent event)
        {
            Group prim = m_shape.getGroup();

            Point2D absLoc = prim.getAbsoluteLocation();
            m_shape.removeFromParent();

            prim.setLocation(absLoc);
            m_layer.add(m_shape);
            m_layer.getLayer().draw();
        }

        @Override public void onNodeMouseUp(NodeMouseUpEvent event)
        {
            addShapeToParent();
        }

        private void addShapeToParent()
        {
            m_shape.removeFromParent();

            if ( m_parent != null && m_parent instanceof WiresShape )
            {
                ((WiresShape)m_parent).getPath().setFillColor(m_priorFill);
                ((WiresShape)m_parent).getPath().setAlpha(m_priorAlpha);
            }

            if (m_parent == null)
            {
                m_parent = m_layer;
            }


            if ( m_parent != m_layer )
            {
                // re-adjust offsets, if adding to nested group
                IContainer prim = m_parent.getContainer();
                Point2D absLoc = prim.getAbsoluteLocation();
                Group group = m_shape.getGroup();
                group.setX( group.getX() - absLoc.getX());
                group.setY( group.getY() - absLoc.getY() );
            }

            m_parent.add(m_shape);
            m_layer.getLayer().batch();

            m_parent = null;
            m_dndManager.m_shapesBacking = null;
            m_dndManager.m_shape_color_map = null;
        }
    }


}
