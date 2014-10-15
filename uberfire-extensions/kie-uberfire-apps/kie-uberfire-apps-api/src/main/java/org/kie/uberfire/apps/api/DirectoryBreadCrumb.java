package org.kie.uberfire.apps.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DirectoryBreadCrumb {

    private String name;
    private String uri;

    public DirectoryBreadCrumb() {
    }


    public static List<DirectoryBreadCrumb> getBreadCrumbs(Directory directory) {
        List<DirectoryBreadCrumb> breadcrumbs = new ArrayList<DirectoryBreadCrumb>();
        breadcrumbs.add( new DirectoryBreadCrumb( directory.getName(), directory.getURI() ) );
        Directory tempParent = directory.getParent();
        while ( tempParent != null ) {
            breadcrumbs.add( new DirectoryBreadCrumb( tempParent.getName(), tempParent.getURI() ) );
            tempParent = tempParent.getParent();
        }
        Collections.reverse( breadcrumbs );
        return breadcrumbs;
    }

    private DirectoryBreadCrumb( String name,
                                String uri ) {

        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
