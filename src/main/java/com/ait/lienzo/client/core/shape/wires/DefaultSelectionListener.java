package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;

public class DefaultSelectionListener implements SelectionListener
{
    private final SelectionManager.SelectedItems m_selectedItems;

    public DefaultSelectionListener(SelectionManager.SelectedItems selectedItems)
    {
        m_selectedItems = selectedItems;
    }

    @Override
    public void onChanged(SelectionManager.SelectedItems selectedItems)
    {
        SelectionManager.ChangedItems changed = selectedItems.getChanged();


// leaving in comments for now, as I re-enable those during debug, if there are problems.
//            for (WiresShape shape : selectedItems.getShapes())
//            {
//                Console.get().info(shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
//            }
//
//            for (WiresConnector connector : selectedItems.getConnectors())
//            {
//                Console.get().info("connector + " + connector.getGroup().uuid());
//            }
//
//            for (WiresShape shape : changed.getRemovedShapes())
//            {
//                Console.get().info("removed" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
//            }
//
//            for (WiresConnector connector : changed.getRemovedConnectors())
//            {
//                Console.get().info("removed connector + "  + connector.getGroup().uuid() );
//            }
//
//            for (WiresShape shape : changed.getAddedShapes())
//            {
//                Console.get().info("added" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
//            }
//
//            for (WiresConnector connector : changed.getAddedConnectors())
//            {
//                Console.get().info("added connector + "  + connector.getGroup().uuid() );
//            }
//
        for (WiresShape shape : changed.getRemovedShapes())
        {
            //Console.get().info("unselected" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
            unselect(shape);
        }

        for (WiresConnector connector : changed.getRemovedConnectors())
        {
            unselect(connector);
        }

        if (!selectedItems.isSelectionGroup() && selectedItems.size() == 1)
        {
            // it's one or the other, so attempt both, it'll short circuit if the first selects.
            if (selectedItems.getShapes().size() == 1)
            {
                for (WiresShape shape : selectedItems.getShapes())
                {
//                    Console.get().info("select" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
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
        else if (selectedItems.isSelectionGroup())
        {
            // we don't which have selectors shown, if any. Just iterate and unselect all
            // null check will do nothing, if it's already unselected.
            for (WiresShape shape : selectedItems.getShapes())
            {
//                Console.get().info("unselected" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
                unselect(shape);
            }


            for (WiresConnector connector : selectedItems.getConnectors())
            {
                unselect(connector);
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
