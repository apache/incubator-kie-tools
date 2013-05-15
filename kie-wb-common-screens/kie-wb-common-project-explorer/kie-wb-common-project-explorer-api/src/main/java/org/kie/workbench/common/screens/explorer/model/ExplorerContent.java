package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for information required for Explorer widget
 */
@Portable
public class ExplorerContent {

    private List<Item> items;
    private List<BreadCrumb> breadCrumbs;

    public ExplorerContent() {
        this( new ArrayList<Item>() );
    }

    public ExplorerContent( final List<Item> items ) {
        this( items,
              new ArrayList<BreadCrumb>() );
    }

    public ExplorerContent( final List<Item> items,
                            final List<BreadCrumb> breadCrumbs ) {
        PortablePreconditions.checkNotNull( "items",
                                            items );
        PortablePreconditions.checkNotNull( "breadCrumbs",
                                            breadCrumbs );
        this.items = items;
        this.breadCrumbs = breadCrumbs;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList( items );
    }

    public List<BreadCrumb> getBreadCrumbs() {
        return Collections.unmodifiableList( breadCrumbs );
    }

}
