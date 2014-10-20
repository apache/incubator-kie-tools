package org.kie.uberfire.apps.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Directory {

    private String name;
    private Map<String, List<String>> tagMap;

    private String fullPath;
    private Directory parent;
    private String URI;

    private List<Directory> childsDirectories = new ArrayList<Directory>();
    private List<String> childComponents = new ArrayList<String>();

    public Directory() {
    }

    public Directory( String name,
                      String fullPath,
                      String URI,
                      Directory parent ) {
        this.name = name;
        this.fullPath = fullPath;
        this.parent = parent;
        this.URI = URI;
        this.tagMap = parent.getTagMap();
        setupChildComponents();
    }

    public Directory( String name,
                      String fullPath,
                      String URI,
                      Map<String, List<String>> tagMap ) {
        this.fullPath = fullPath;
        this.name = name;
        this.URI = URI;
        this.tagMap = tagMap;
        setupChildComponents();
    }

    private void setupChildComponents() {
        final List<String> components = tagMap.get( name.toUpperCase() );
        if ( components != null ) {
            childComponents.addAll( components );
        }
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

    public Map<String, List<String>> getTagMap() {
        return tagMap;
    }

    public List<String> getChildComponents() {
        return childComponents;
    }

    public void removeChildDirectoryByURI( String uri ) {
        Directory candidate = null;
        for ( Directory child : childsDirectories ) {
            if ( child.getURI().equalsIgnoreCase( uri ) ) {
                candidate = child;
                break;
            }
        }
        if ( candidate != null ) {
            childsDirectories.remove( candidate );
        }
    }

    public String getFullPath() {
        return fullPath;
    }
}
