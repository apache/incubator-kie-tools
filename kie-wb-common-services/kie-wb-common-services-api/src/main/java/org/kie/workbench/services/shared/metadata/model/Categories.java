package org.kie.workbench.services.shared.metadata.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.Iterator;

/**
 *
 */
@Portable
public class Categories
        extends CategoryItem
        implements Iterable<CategoryItem> {

    public Categories() {
    }

    public int size() {
        return getChildren().size();
    }

    public Iterator<CategoryItem> iterator() {
        return getChildren().iterator();
    }
}