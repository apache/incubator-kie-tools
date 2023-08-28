package org.dashbuilder.client.place;

import elemental2.dom.HTMLElement;

public interface Place {

    String getId();

    HTMLElement getElement();
    
    default boolean isDefault() {
        return false;
    }

    default void onOpen() {
        // no op
    }

    default void onClose() {
        // no op
    }
}
