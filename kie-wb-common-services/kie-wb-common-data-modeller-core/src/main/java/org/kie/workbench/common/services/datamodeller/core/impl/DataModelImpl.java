/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaEnum;
import org.kie.workbench.common.services.datamodeller.core.ObjectSource;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

public class DataModelImpl implements DataModel {

    Map<String, DataObject> dataObjects = new HashMap<String, DataObject>();

    Map<String, DataObject> dependencyDataObjects = new HashMap<String, DataObject>();

    Map<String, JavaEnum> javaEnums = new HashMap<String, JavaEnum>();

    Map<String, JavaEnum> dependencyJavaEnums = new HashMap<String, JavaEnum>();

    public DataModelImpl() {
        //errai marshalling
    }

    @Override
    public Set<DataObject> getDataObjects() {
        return getDataObjects( ObjectSource.INTERNAL );
    }

    @Override
    public Set<DataObject> getDataObjects( ObjectSource source ) {
        switch ( source ) {
            case INTERNAL:
                return getDataObjects( dataObjects );
            case DEPENDENCY:
                return getDataObjects( dependencyDataObjects );
        }
        return null;
    }

    @Override
    public DataObject getDataObject( String className ) {
        return getDataObject( className, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject getDataObject( String className, ObjectSource source ) {
        switch ( source ) {
            case INTERNAL:
                return dataObjects.get( className );
            case DEPENDENCY:
                return dependencyDataObjects.get( className );
        }
        return null;
    }

    @Override
    public DataObject removeDataObject( String className ) {
        return removeDataObject( className, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject removeDataObject( String className, ObjectSource source ) {
        switch ( source ) {
            case INTERNAL:
                return dataObjects.remove( className );
            case DEPENDENCY:
                return dependencyDataObjects.remove( className );
        }
        return null;
    }

    @Override
    public DataObject addDataObject( String packageName, String name ) {
        return addDataObject( packageName, name, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject addDataObject( String className ) {
        return addDataObject( className, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject addDataObject( String className, ObjectSource source ) {
        return addDataObject( className, Visibility.PUBLIC, false, false, source );
    }

    @Override
    public DataObject addDataObject( String packageName, String name, ObjectSource source ) {
        return addDataObject( packageName, name, Visibility.PUBLIC, false, false, source );
    }

    @Override
    public DataObject addDataObject( String packageName, String name, Visibility visibility ) {
        return addDataObject( packageName, name, visibility, false, false, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject addDataObject( String packageName, String name, Visibility visibility, boolean isAbstract, boolean isFinal ) {
        return addDataObject( packageName, name, visibility, isAbstract, isFinal, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject addDataObject( String className, Visibility visibility, boolean isAbstract, boolean isFinal ) {
        return addDataObject( className, visibility, isAbstract, isFinal, ObjectSource.INTERNAL );
    }

    @Override
    public DataObject addDataObject( String className, Visibility visibility, boolean isAbstract, boolean isFinal, ObjectSource source ) {
        String name = NamingUtils.extractClassName( className );
        String packageName = NamingUtils.extractPackageName( className );
        return addDataObject( packageName, name, visibility, isAbstract, isFinal, source );
    }

    @Override
    public DataObject addDataObject( String packageName, String name, Visibility visibility, boolean isAbstract, boolean isFinal, ObjectSource source ) {
        switch ( source ) {
            case INTERNAL:
                return addDataObject( packageName, name, visibility, isAbstract, isFinal, dataObjects );
            case DEPENDENCY:
                return addDataObject( packageName, name, visibility, isAbstract, isFinal, dependencyDataObjects );
        }
        return null;
    }

    private Set<DataObject> getDataObjects( Map<String, DataObject> objectsMap ) {
        HashSet<DataObject> set = new HashSet<DataObject>();
        set.addAll( objectsMap.values() );
        return set;
    }

    private DataObject addDataObject( String packageName, String name, Visibility visibility, boolean isAbstract, boolean isFinal, Map<String, DataObject> objectsMap ) {
        DataObject dataObject = new DataObjectImpl( packageName, name, visibility, isAbstract, isFinal );
        objectsMap.put( dataObject.getClassName(), dataObject );
        return dataObject;
    }

    @Override
    public DataObject addDataObject( DataObject dataObject ) {
        dataObjects.put( dataObject.getClassName(), dataObject );
        return dataObject;
    }

    @Override
    public List<DataObject> getExternalClasses() {
        List<DataObject> result = new ArrayList<DataObject>();
        result.addAll( dependencyDataObjects.values() );
        return result;
    }

    @Override
    public JavaEnum addJavaEnum( JavaEnum javaEnum ) {
        return addJavaEnum( javaEnum, ObjectSource.INTERNAL );
    }

    @Override
    public JavaEnum addJavaEnum( JavaEnum javaEnum, ObjectSource source ) {
        switch ( source ) {
            case INTERNAL:
                javaEnums.put( javaEnum.getClassName(), javaEnum );
                break;
            case DEPENDENCY:
                dependencyJavaEnums.put( javaEnum.getClassName(), javaEnum );
        }
        return javaEnum;
    }

    @Override
    public JavaEnum removeJavaEnum( String className ) {
        return javaEnums.remove( className );
    }

    @Override
    public JavaEnum getJavaEnum( String className ) {
        return javaEnums.get( className );
    }

    @Override
    public List<JavaEnum> getJavaEnums() {
        List<JavaEnum> result = new ArrayList<JavaEnum>();
        result.addAll( javaEnums.values() );
        return result;
    }

    @Override
    public List<JavaEnum> getDependencyJavaEnums() {
        List<JavaEnum> result = new ArrayList<JavaEnum>();
        result.addAll( dependencyJavaEnums.values() );
        return result;
    }

    @Override
    public boolean isExternal( String className ) {
        return dependencyDataObjects.containsKey( className );
    }

    @Override
    public boolean isEnum( String className ) {
        return javaEnums.containsKey( className ) || dependencyJavaEnums.containsKey( className );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass( ) != o.getClass( ) ) {
            return false;
        }

        DataModelImpl dataModel = ( DataModelImpl ) o;

        if ( dataObjects != null ? !dataObjects.equals( dataModel.dataObjects ) : dataModel.dataObjects != null ) {
            return false;
        }
        if ( dependencyDataObjects != null ? !dependencyDataObjects.equals( dataModel.dependencyDataObjects ) : dataModel.dependencyDataObjects != null ) {
            return false;
        }

        if ( javaEnums != null ? !javaEnums.equals( dataModel.javaEnums ) : dataModel.javaEnums != null ) {
            return false;
        }

        return dependencyJavaEnums != null ? dependencyJavaEnums.equals( dataModel.dependencyJavaEnums ) : dataModel.dependencyJavaEnums == null;
    }

    @Override
    public int hashCode( ) {
        int result = dataObjects != null ? dataObjects.hashCode( ) : 0;
        result = ~~result;
        result = 31 * result + ( dependencyDataObjects != null ? dependencyDataObjects.hashCode( ) : 0 );
        result = ~~result;
        result = 31 * result + ( javaEnums != null ? javaEnums.hashCode( ) : 0 );
        result = ~~result;
        result = 31 * result + ( dependencyJavaEnums != null ? dependencyJavaEnums.hashCode( ) : 0 );
        result = ~~result;
        return result;
    }
}
