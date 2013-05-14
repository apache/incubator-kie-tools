package org.kie.workbench.screens.explorer.model;

import org.uberfire.backend.vfs.Path;

/**
 * Items shown in Explorer
 */
public interface Item {

    ItemType getType();

    Path getPath();

    String getCaption();

}
