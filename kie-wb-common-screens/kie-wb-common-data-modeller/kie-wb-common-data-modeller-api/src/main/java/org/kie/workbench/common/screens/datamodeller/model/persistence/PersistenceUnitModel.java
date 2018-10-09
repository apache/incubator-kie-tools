/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.model.persistence;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PersistenceUnitModel {

    private String name;

    private String description;

    private String provider;

    private String jtaDataSource;

    private String nonJtaDataSource;

    private List<String> mappingFile = new ArrayList<String>();

    private List<String> jarFile = new ArrayList<String>();

    private List<PersistableDataObject> classes = new ArrayList<PersistableDataObject>();

    private Boolean excludeUnlistedClasses;

    private CachingType sharedCacheMode;

    private ValidationMode validationMode;

    protected List<Property> properties = new ArrayList<Property>();

    protected TransactionType transactionType;

    public PersistenceUnitModel() {
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider( String provider ) {
        this.provider = provider;
    }

    public String getJtaDataSource() {
        return jtaDataSource;
    }

    public void setJtaDataSource( String jtaDataSource ) {
        this.jtaDataSource = jtaDataSource;
    }

    public String getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    public void setNonJtaDataSource( String nonJtaDataSource ) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    public List<String> getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile( List<String> mappingFile ) {
        this.mappingFile = mappingFile;
    }

    public List<String> getJarFile() {
        return jarFile;
    }

    public void setJarFile( List<String> jarFile ) {
        this.jarFile = jarFile;
    }

    public List<PersistableDataObject> getClasses() {
        return classes;
    }

    public void setClasses( List<PersistableDataObject> classes ) {
        this.classes = classes;
    }

    public Boolean getExcludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    public void setExcludeUnlistedClasses( Boolean excludeUnlistedClasses ) {
        this.excludeUnlistedClasses = excludeUnlistedClasses;
    }

    public CachingType getSharedCacheMode() {
        return sharedCacheMode;
    }

    public void setSharedCacheMode( CachingType sharedCacheMode ) {
        this.sharedCacheMode = sharedCacheMode;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode( ValidationMode validationMode ) {
        this.validationMode = validationMode;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties( List<Property> properties ) {
        this.properties = properties;
    }

    public void addProperty( Property property ) {
        if ( properties == null ) {
            properties = new ArrayList<Property>(  );
        }
        properties.add( property );
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType( TransactionType transactionType ) {
        this.transactionType = transactionType;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PersistenceUnitModel that = ( PersistenceUnitModel ) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( description != null ? !description.equals( that.description ) : that.description != null ) {
            return false;
        }
        if ( provider != null ? !provider.equals( that.provider ) : that.provider != null ) {
            return false;
        }
        if ( jtaDataSource != null ? !jtaDataSource.equals( that.jtaDataSource ) : that.jtaDataSource != null ) {
            return false;
        }
        if ( nonJtaDataSource != null ? !nonJtaDataSource.equals( that.nonJtaDataSource ) : that.nonJtaDataSource != null ) {
            return false;
        }
        if ( mappingFile != null ? !mappingFile.equals( that.mappingFile ) : that.mappingFile != null ) {
            return false;
        }
        if ( jarFile != null ? !jarFile.equals( that.jarFile ) : that.jarFile != null ) {
            return false;
        }
        if ( classes != null ? !classes.equals( that.classes ) : that.classes != null ) {
            return false;
        }
        if ( excludeUnlistedClasses != null ? !excludeUnlistedClasses.equals( that.excludeUnlistedClasses ) : that.excludeUnlistedClasses != null ) {
            return false;
        }
        if ( sharedCacheMode != that.sharedCacheMode ) {
            return false;
        }
        if ( validationMode != that.validationMode ) {
            return false;
        }
        if ( properties != null ? !properties.equals( that.properties ) : that.properties != null ) {
            return false;
        }
        return transactionType == that.transactionType;

    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( description != null ? description.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( provider != null ? provider.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( jtaDataSource != null ? jtaDataSource.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( nonJtaDataSource != null ? nonJtaDataSource.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( mappingFile != null ? mappingFile.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( jarFile != null ? jarFile.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( classes != null ? classes.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( excludeUnlistedClasses != null ? excludeUnlistedClasses.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( sharedCacheMode != null ? sharedCacheMode.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( validationMode != null ? validationMode.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( properties != null ? properties.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( transactionType != null ? transactionType.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
