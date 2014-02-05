package org.uberfire.client.workbench.widgets.dnd;

import org.uberfire.client.workbench.BeanFactory;

public class WorkbenchDragAndDropManagerUnitTestWrapper extends WorkbenchDragAndDropManager {

    public void setupMocks( WorkbenchPickupDragController dragController,
                            BeanFactory factory ) {
        this.dragController = dragController;
        this.factory = factory;
    }
}
