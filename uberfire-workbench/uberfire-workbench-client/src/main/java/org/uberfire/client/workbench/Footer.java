package org.uberfire.client.workbench;


/**
 * CDI beans that implement Footer are automatically created and added to the top of the Workbench screen.
 */
public interface Footer extends OrderableIsWidget {

    /**
     * Returns the stacking order of this footer.
     * 
     * @return the order this header should be stacked in (higher numbers closer to the top of the screen).
     */
    @Override
    int getOrder();

}
