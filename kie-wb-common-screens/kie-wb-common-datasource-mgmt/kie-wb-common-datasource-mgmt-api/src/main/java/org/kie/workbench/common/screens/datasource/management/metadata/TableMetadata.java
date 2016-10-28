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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TableMetadata {

    private String catalogName;

    private String schemaName;

    private String tableName;

    private String tableType;

    public TableMetadata( @MapsTo( "catalogName" ) String catalogName,
                          @MapsTo( "schemaName" ) String schemaName,
                          @MapsTo( "tableName" ) String tableName,
                          @MapsTo( "tableType" ) String tableType ) {
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.tableType = tableType;
    }

    public String getCatalogName( ) {
        return catalogName;
    }

    public void setCatalogName( String catalogName ) {
        this.catalogName = catalogName;
    }

    public String getSchemaName( ) {
        return schemaName;
    }

    public void setSchemaName( String schemaName ) {
        this.schemaName = schemaName;
    }

    public String getTableName( ) {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public String getTableType( ) {
        return tableType;
    }

    public void setTableType( String tableType ) {
        this.tableType = tableType;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;

        TableMetadata that = ( TableMetadata ) o;

        if ( catalogName != null ? !catalogName.equals( that.catalogName ) : that.catalogName != null ) return false;
        if ( schemaName != null ? !schemaName.equals( that.schemaName ) : that.schemaName != null ) return false;
        if ( tableName != null ? !tableName.equals( that.tableName ) : that.tableName != null ) return false;
        return tableType != null ? tableType.equals( that.tableType ) : that.tableType == null;

    }

    @Override
    public int hashCode( ) {
        int result = catalogName != null ? catalogName.hashCode( ) : 0;
        result  = ~~result;
        result = 31 * result + ( schemaName != null ? schemaName.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( tableName != null ? tableName.hashCode( ) : 0 );
        result  = ~~result;
        result = 31 * result + ( tableType != null ? tableType.hashCode( ) : 0 );
        result  = ~~result;
        return result;
    }

    @Override
    public String toString( ) {
        return "TableMetadata{" +
                "catalogName='" + catalogName + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                '}';
    }
}