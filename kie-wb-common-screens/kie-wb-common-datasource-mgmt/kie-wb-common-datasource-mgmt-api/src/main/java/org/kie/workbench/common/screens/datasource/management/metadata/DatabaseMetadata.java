/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.metadata;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DatabaseMetadata {

    public enum DatabaseType { H2, MYSQL, MARIADB, POSTGRESQL, ORACLE, SQLSERVER, DB2 }

    public enum TableType { ALL, TABLE, SYSTEM_TABLE,  VIEW, SYSTEM_VIEW, SEQUENCE }

    private DatabaseType databaseType;

    private String databaseProductName;

    private String databaseProductVersion;

    private String driverName;

    private String driverVersion;

    private int driverMajorVersion;

    private int driverMinorVersion;

    private List<SchemaMetadata> schemas = new ArrayList<>( );

    private List<CatalogMetadata> catalogs = new ArrayList<>( );

    public DatabaseType getDatabaseType( ) {
        return databaseType;
    }

    public void setDatabaseType( DatabaseType databaseType ) {
        this.databaseType = databaseType;
    }

    public String getDatabaseProductName( ) {
        return databaseProductName;
    }

    public void setDatabaseProductName( String databaseProductName ) {
        this.databaseProductName = databaseProductName;
    }

    public String getDatabaseProductVersion( ) {
        return databaseProductVersion;
    }

    public void setDatabaseProductVersion( String databaseProductVersion ) {
        this.databaseProductVersion = databaseProductVersion;
    }

    public String getDriverName( ) {
        return driverName;
    }

    public void setDriverName( String driverName ) {
        this.driverName = driverName;
    }

    public String getDriverVersion( ) {
        return driverVersion;
    }

    public void setDriverVersion( String driverVersion ) {
        this.driverVersion = driverVersion;
    }

    public int getDriverMajorVersion( ) {
        return driverMajorVersion;
    }

    public void setDriverMajorVersion( int driverMajorVersion ) {
        this.driverMajorVersion = driverMajorVersion;
    }

    public int getDriverMinorVersion( ) {
        return driverMinorVersion;
    }

    public void setDriverMinorVersion( int driverMinorVersion ) {
        this.driverMinorVersion = driverMinorVersion;
    }

    public List< SchemaMetadata > getSchemas( ) {
        return schemas;
    }

    public void setSchemas( List< SchemaMetadata > schemas ) {
        this.schemas = schemas;
    }

    public List< CatalogMetadata > getCatalogs( ) {
        return catalogs;
    }

    public void setCatalogs( List< CatalogMetadata > catalogs ) {
        this.catalogs = catalogs;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;

        DatabaseMetadata that = ( DatabaseMetadata ) o;

        if ( driverMajorVersion != that.driverMajorVersion ) return false;
        if ( driverMinorVersion != that.driverMinorVersion ) return false;
        if ( databaseType != that.databaseType ) return false;
        if ( databaseProductName != null ? !databaseProductName.equals( that.databaseProductName ) : that.databaseProductName != null )
            return false;
        if ( databaseProductVersion != null ? !databaseProductVersion.equals( that.databaseProductVersion ) : that.databaseProductVersion != null )
            return false;
        if ( driverName != null ? !driverName.equals( that.driverName ) : that.driverName != null ) return false;
        if ( driverVersion != null ? !driverVersion.equals( that.driverVersion ) : that.driverVersion != null )
            return false;
        if ( schemas != null ? !schemas.equals( that.schemas ) : that.schemas != null ) return false;
        return catalogs != null ? catalogs.equals( that.catalogs ) : that.catalogs == null;

    }

    @Override
    public int hashCode( ) {
        int result = databaseType != null ? databaseType.hashCode( ) : 0;
        result  = ~~result;
        result = 31 * result + ( databaseProductName != null ? databaseProductName.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( databaseProductVersion != null ? databaseProductVersion.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( driverName != null ? driverName.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( driverVersion != null ? driverVersion.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + driverMajorVersion;
        result  = ~~result;
        result = 31 * result + driverMinorVersion;
        result  = ~~result;
        result = 31 * result + ( schemas != null ? schemas.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( catalogs != null ? catalogs.hashCode( ) : 0 );
        result  = ~~result;
        return result;
    }
}