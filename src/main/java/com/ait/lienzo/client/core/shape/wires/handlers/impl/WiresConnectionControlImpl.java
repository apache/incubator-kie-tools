package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.BackingColorMapUtils;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class WiresConnectionControlImpl implements WiresConnectionControl
{
    private WiresConnector                    m_connector;

    private WiresManager                      m_manager;

    private boolean                           m_head;

    private ImageData                         m_shapesBacking;

    private ImageData                         m_magnetsBacking;

    private MagnetManager.Magnets             m_magnets;

    private double                            m_startX;

    private double                            m_startY;

    private String                            m_colorKey;

    private final NFastStringMap<WiresShape> m_shape_color_map  = new NFastStringMap<WiresShape>();

    private final NFastStringMap<WiresMagnet> m_magnet_color_map = new NFastStringMap<WiresMagnet>();

    public WiresConnectionControlImpl(final WiresConnector connector, final WiresManager wiresManager)
    {
        m_connector = connector;
        m_manager = wiresManager;
    }

    @Override
    public void dragStart(final Context context)
    {
        final Node<?> node = (Node<?>) context.getSource();
        m_head = node == m_connector.getHeadConnection().getControl();
        Point2D points = WiresUtils.getLocation(node);
        m_startX = points.getX();
        m_startY = points.getY();

        ScratchPad scratch = m_manager.getLayer().getLayer().getScratchPad();
        m_shapesBacking = BackingColorMapUtils.drawShapesToBacking(m_manager.getLayer().getChildShapes(), scratch, null, m_shape_color_map);
        m_connector.getLine().getOverLayer().getContext().createImageData(m_shapesBacking);

        WiresConnection connection = getConnection();
        if (connection.getMagnet() != null)
        {
            m_magnets = connection.getMagnet().getMagnets();
            m_magnetsBacking = m_manager.getMagnetManager().drawMagnetsToBack(m_magnets, m_shape_color_map, m_magnet_color_map, scratch);
        }

        String colorKey = BackingColorMapUtils.findColorAtPoint(m_shapesBacking, (int) m_startX, (int) m_startY);
        showMagnets(colorKey);
    }

    @Override
    public void dragMove(final Context context)
    {
    }

    @Override
    public boolean dragEnd(final Context context)
    {
        if (m_magnets != null)
        {
            m_magnets.hide();
        }

        m_shapesBacking = null;// uses lots of memory, so let it GC
        m_magnetsBacking = null;// uses lots of memory, so let it GC
        m_magnets = null;// if this is not nulled, the Mangets reference could stop Magnets being GC, when not used anywhere else
        m_colorKey = null;
        m_shape_color_map.clear();
        m_magnet_color_map.clear();
        return true;
    }

    @Override
    public boolean dragAdjust(final Point2D dxy)
    {
        Shape<?> control = null;
        WiresMagnet magnet = null;
        int x = (int) (m_startX + dxy.getX());
        int y = (int) (m_startY + dxy.getY());

        String colorKey = BackingColorMapUtils.findColorAtPoint(m_shapesBacking, x, y);
        if (m_colorKey != null && colorKey != null && !colorKey.equals(m_colorKey))
        {
            // this can happen when the mouse moves from an outer shape to an inner shape, or vice-versa
            // hide and null, and it'll show for the new.
            if (null != m_magnets)
            {
                m_magnets.hide();
            }
            m_magnets = null;
            m_colorKey = null;
        }

        if (m_magnets == null)
        {
            showMagnets(colorKey);
        }

        if (m_magnets != null)
        {
            String magnetColorKey = BackingColorMapUtils.findColorAtPoint(m_magnetsBacking, x, y);
            if (magnetColorKey == null)
            {
                if (null != m_magnets)
                {
                    m_magnets.hide();
                }
                m_magnets = null;
                m_colorKey = null;
                magnet = null;
            }
            else
            {

                magnet = m_magnet_color_map.get(magnetColorKey);
                if (magnet != null) // it can be null, when over the main shape, instead of a magnet
                {
                    control = magnet.getControl().asShape();
                }
            }
        }

        WiresConnection connection = getConnection();

        boolean accept = true;
        if (m_head)
        {
            accept = m_connector.getConnectionAcceptor().acceptHead(connection, magnet);
        }
        else
        {
            accept = m_connector.getConnectionAcceptor().acceptTail(connection, magnet);
        }

        if (accept)
        {
            connection.setMagnet(magnet);
        }
        else
        {
            control = null;
        }

        if (control != null)
        {
            // If there is a control, snap to it
            Point2D absControl = WiresUtils.getLocation(control);
            double targetX = absControl.getX();
            double targetY = absControl.getY();

            double dx = targetX - m_startX - dxy.getX();
            double dy = targetY - m_startY - dxy.getY();
            if (dx != 0 || dy != 0)
            {
                dxy.setX(dxy.getX() + dx).setY(dxy.getY() + dy);
            }

        }

        return true;
    }


    private void showMagnets(String colorKey)
    {
        final WiresShape prim = null != colorKey ? m_shape_color_map.get(colorKey) : null;
        boolean accept = true;
        if (m_head)
        {
            accept = m_connector.getConnectionAcceptor().headConnectionAllowed(m_connector.getHeadConnection(), prim);
        }
        else
        {
            accept = m_connector.getConnectionAcceptor().tailConnectionAllowed(m_connector.getTailConnection(), prim);
        }

        if (accept)
        {
            m_colorKey = colorKey;

            m_magnets = null != prim ? prim.getMagnets() : null;
            if (m_magnets != null)
            {
                m_magnets.show();
                final ScratchPad scratch = m_manager.getLayer().getLayer().getScratchPad();
                m_magnetsBacking = m_manager.getMagnetManager().drawMagnetsToBack(m_magnets, m_shape_color_map, m_magnet_color_map, scratch);
            }
        }
    }

    private WiresConnection getConnection()
    {
        if (m_head)
        {
            return m_connector.getHeadConnection();
        }
        else
        {
            return m_connector.getTailConnection();
        }
    }
}
