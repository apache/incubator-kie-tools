package org.drools.guvnor.client.mvp;

public interface Closable {
    /**
    * True - Close the screen
    * False - Do not close the screen
    */
    public boolean mayClose(); 
    
    public void onClose();
}
