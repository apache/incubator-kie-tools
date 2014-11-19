package org.uberfire.workbench.model.menu;

/**
 * A menu item that provides its own widget.
 *
 * @param <T> The type of widget the custom menu item provides.
 */
public interface MenuCustom<T>
        extends MenuItem {

    T build();

}
