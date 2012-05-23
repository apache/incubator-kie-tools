package org.drools.guvnor.client.mvp;

public interface ScreenService {
    /* onBind() is called right after the Presenter is constructed. */
    public void onBind(AcceptItem acceptPanel);

    /* onClose() is called after the Presenter is destroyed. */
    public void onClose();

    /**
    * True - Close the screen
    * False - Do not close the screen
    */
    public boolean mayClose();

    /*onReveal() is called whenever the Presenter was not visible on screen and becomes visible.*/
    public void onReveal();

    /*onHide() is called when the Presenter was visible on screen and is being hidden.*/
    public void onHide();

    /**
    * True - Hide the screen
    * False - Do not hide the screen. For example, need to save data.
    */
    public void mayOnHide();
}
