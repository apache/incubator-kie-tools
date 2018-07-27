package com.ait.lienzo.client.core.shape.wires;

public class DefaultSelectionListener implements SelectionListener
{

    @Override
    public void onChanged(SelectionManager.SelectedItems selectedItems)
    {
        SelectionManager.ChangedItems changed = selectedItems.getChanged();

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
            // it's one or the other, so attempt both, it'll short circuit if the first selects.
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
        else if (selectedItems.isSelectionGroup())
        {
            // we don't which have selectors shown, if any. Just iterate and unselect all
            // null check will do nothing, if it's already unselected.
            for (WiresShape shape : selectedItems.getShapes())
            {
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
        connector.getControl().showControlPoints();
    }

    private void unselect(WiresConnector connector)
    {
        connector.getControl().hideControlPoints();
    }
}
