package org.drools.guvnor.client.mvp;

public interface Startable {
    //REVISIT: No need to have onBeforeStart(). As there is no way we call onBeforeStart() if the Presenter has not been instantiated yet.
    /**
    * True - Start the screen
    * False - Do not start the screen
    */
/*    public boolean onBeforeStart(); */

    /* onStart() is called right after the Presenter is constructed. */
    public void onStart();
}
