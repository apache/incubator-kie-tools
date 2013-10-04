package org.uberfire.workbench.model.menu;

public interface MenuCustom<T>
        extends MenuItem {

    T build();

}
