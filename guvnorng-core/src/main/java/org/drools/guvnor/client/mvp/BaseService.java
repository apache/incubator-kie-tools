package org.drools.guvnor.client.mvp;

public interface BaseService {

    //Called before a screen is closed. This gives the screen an opportunity to object to closure
    public boolean mayClose();

    //Called when the screen is closed.
    public void onClose();

    //Called when the screen is displayed.
    public void onReveal();

    //Called when the screen looses the focus
    public void onLostFocus();

    //Called when the screen gains the focus
    public void onFocus();

}
