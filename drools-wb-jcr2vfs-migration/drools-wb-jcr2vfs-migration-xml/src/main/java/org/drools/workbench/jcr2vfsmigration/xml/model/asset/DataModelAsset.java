/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.xml.model.asset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DataModelAsset extends AbstractXmlAsset {

    private Collection<DataModelObject> dataObjects;

    public DataModelAsset( String name, String format ) {
        this.name = name;
        this.assetType = AssetType.getByType( format );
        this.dataObjects = new ArrayList<DataModelObject>();
    }

    public DataModelObject addDataModelObject( String name, String superType ) {
        DataModelObject obj = new DataObject( name, superType );
        dataObjects.add( obj );
        return obj;
    }

    public Iterator<DataModelObject> modelObjects() {
        return dataObjects.iterator();
    }


    public interface DataModelObject {
        String getName();
        String getSuperType();
        void addObjectProperty( String name, String type );
        void addObjectAnnotation( String name, String key, String value );
        Iterator<DataObjectProperty> properties();
        Iterator<DataObjectAnnotation> annotations();
    }

    public interface DataObjectProperty {
        String getName();
        String getType();
    }

    public interface DataObjectAnnotation {
        String getName();
        String getKey();
        String getValue();
    }

    private class DataObject implements DataModelObject {
        private String name;
        private String superType;
        private Collection<DataObjectAnnotation> objectAnnotations;
        private Collection<DataObjectProperty> objectProperties;

        private DataObject( String name,
                            String superType ) {
            this.name = name;
            this.superType = superType;
            this.objectAnnotations = new ArrayList<DataObjectAnnotation>();
            this.objectProperties = new ArrayList<DataObjectProperty>();
        }

        public void addObjectProperty( String name, String type ) {
            objectProperties.add( new ObjectProperty( name, type ) );
        }

        @Override
        public void addObjectAnnotation( String name, String key, String value ) {
            objectAnnotations.add( new ObjectAnnotation( name, key, value ) );
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuperType() {
            return superType;
        }

        @Override
        public Iterator<DataObjectProperty> properties() {
            return objectProperties.iterator();
        }

        @Override
        public Iterator<DataObjectAnnotation> annotations() {
            return objectAnnotations.iterator();
        }
    }

    private class ObjectProperty implements DataObjectProperty {
        private String name;
        private String type;

        private ObjectProperty( String name, String type ) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    private class ObjectAnnotation implements DataObjectAnnotation {
        private String name;
        private String key;
        private String value;

        private ObjectAnnotation( String name, String key, String value ) {
            this.name = name;
            this.key = key;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
