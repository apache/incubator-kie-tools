package org.drools.guvnor.client.mvp;

public interface BaseService {

    //Called before a screen is closed. This gives the screen an opportunity to object to closure
    public boolean onMayClose();

    //Called when the screen is closed.
    public void onClose();

    //Called when the screen is displayed.
    public void onReveal();

}
