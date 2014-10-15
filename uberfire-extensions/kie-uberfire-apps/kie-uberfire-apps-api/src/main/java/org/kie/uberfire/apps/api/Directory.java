package org.kie.uberfire.apps.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Directory {

    private String name;
    private Directory parent;
    private String URI;

    private List<Directory> childsDirectories = new ArrayList<Directory>();

    public Directory() {
    }

    public Directory( String name,
                      String URI ) {
        this.name = name;
        this.URI = URI;
        this.parent = null;
    }

    public Directory( String name,
                      String URI,
                      Directory parent ) {
        this.name = name;
        this.parent = parent;
        this.URI = URI;
    }

    public String getName() {
        return name;
    }

    public List<Directory> getChildsDirectories() {
        return childsDirectories;
    }

    public void addChildDirectory( Directory directory ) {
        childsDirectories.add( directory );
    }

    public void addChildDirectories( List<Directory> directories ) {
        childsDirectories.addAll( directories );
    }

    public Directory getParent() {
        return parent;
    }

    public String getURI() {
        return URI;
    }


}
