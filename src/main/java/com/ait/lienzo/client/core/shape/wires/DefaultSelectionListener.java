package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;

public class DefaultSelectionListener implements SelectionListener
{
    private final Layer                          m_layer;

    private final SelectionManager.SelectedItems m_selectedItems;

    public DefaultSelectionListener(Layer layer, SelectionManager.SelectedItems selectedItems)
    {
        m_layer = layer;
        m_selectedItems = selectedItems;
    }

    @Override
    public void onChanged(SelectionManager.SelectedItems selectedItems)
    {
        SelectionManager.ChangedItems changed = selectedItems.getChanged();

//
//            for (WiresShape shape : selectedItems.getShapes())
//            {
//                Console.get().info(shape.getContainer().getUserData().toString());
//            }
//
//            for (WiresConnector connector : selectedItems.getConnectors())
//            {
//                Console.get().info("connector + "  + connector.getGroup().uuid() );
//            }
//
//            for (WiresShape shape : changed.getRemovedShapes())
//            {
//                Console.get().info("removed" + shape.getContainer().getUserData().toString());
//            }
//
//            for (WiresConnector connector : changed.getRemovedConnectors())
//            {
//                Console.get().info("removed connector + "  + connector.getGroup().uuid() );
//            }
//
//            for (WiresShape shape : changed.getAddedShapes())
//            {
//                Console.get().info("added" + shape.getContainer().getUserData().toString());
//            }
//
//            for (WiresConnector connector : changed.getAddedConnectors())
//            {
//                Console.get().info("added connector + "  + connector.getGroup().uuid() );
//            }

        for (WiresShape shape : changed.getRemovedShapes())
        {
            unselect(shape);
        }

        for (WiresConnector connector : changed.getRemovedConnectors())
        {
            unselect(connector);
        }

        if (!selectedItems.isSelectionGroup() && selectedItems.size() == 1)
        {
            if (selectedItems.getShapes().size() == 1)
            {
                for (WiresShape shape : selectedItems.getShapes())
                {
                    select(shape);
                    break;
                }
            }
            else
            {
                for (WiresConnector connector : selectedItems.getConnectors())
                {
                    select(connector);
                    break;
                }
            }
        }
    }

    private void select(WiresShape shape)
    {
        if (shape.getControls() != null)
        {
            shape.getControls().show();
        }
    }

    private void unselect(WiresShape shape)
    {
        if (shape.getControls() != null)
        {
            shape.getControls().hide();
        }
    }

    private void select(WiresConnector connector)
    {
        ((WiresConnectorControlImpl) connector.getWiresConnectorHandler().getControl()).showControlPoints();
    }

    private void unselect(WiresConnector connector)
    {
        ((WiresConnectorControlImpl) connector.getWiresConnectorHandler().getControl()).hideControlPoints();
    }
}
