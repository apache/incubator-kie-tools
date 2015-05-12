package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public class UpdateInfo {

    Map<String, String> renamedClasses = new HashMap<String, String>( );

    List<String> deletedClasses = new ArrayList<String>( );

    private Map<ObjectProperty, String> renamedProperties = new HashMap<ObjectProperty, String>( );

    private Map<ObjectProperty, ObjectProperty> newProperties = new HashMap<ObjectProperty, ObjectProperty>( );

    public void addClassRename( String oldClassName, String newClassName ) {
        renamedClasses.put( oldClassName, newClassName );
    }

    public boolean isRenamed( String oldClassName ) {
        return renamedClasses.containsKey( oldClassName );
    }

    public String getClassNewName( String oldClassName ) {
        return renamedClasses.get( oldClassName );
    }

    public Map<String, String> getRenamedClasses( ) {
        return renamedClasses;
    }

    public void addDeletedClass( String className ) {
        deletedClasses.add( className );
    }

    public void addDeletedClasses( List deletedClasses ) {
        if ( deletedClasses != null ) {
            this.deletedClasses.addAll( deletedClasses );
        }
    }

    public List<String> getDeletedClasses( ) {
        return deletedClasses;
    }

    public boolean isDeleted( String className ) {
        return deletedClasses.contains( className );
    }

    public void addNewProperty( ObjectProperty property ) {
        newProperties.put( property, property );
    }

    public boolean isNew( ObjectProperty property ) {
        return newProperties.containsKey( property );
    }

    public void addPropertyRename( ObjectProperty property, String currentName, String oldName ) {
        renamedProperties.put( property, oldName );
    }

    public boolean isRenamed( ObjectProperty property ) {
        return renamedProperties.containsKey( property );
    }

    public String getPropertyOldName( ObjectProperty property ) {
        return renamedProperties.get( property );
    }
}