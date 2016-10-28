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

package org.kie.workbench.common.screens.datasource.management.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.kie.workbench.common.screens.datasource.management.metadata.CatalogMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.SchemaMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;

import static org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata.DatabaseType.*;

/**
 * Utility class for retrieving metadata from a database.
 */
public class DatabaseMetadataUtil {

    /**
     * Gets the metadata for a given database.
     * @param conn A valid connection to the target database.
     * @param includeCatalogs If true the database catalogs metadata will be included in the results.
     * @param includeSchemas If true the database schemas metadata will be included in the results.
     * @return The metadata for the given database.
     * @throws Exception if a database error is produced.
     */
    public static DatabaseMetadata getMetadata( Connection conn,
                                                boolean includeCatalogs,
                                                boolean includeSchemas ) throws Exception {
        try {
            DatabaseMetadata result = new DatabaseMetadata( );
            ResultSet rs;
            DatabaseMetaData sqlMetadata = conn.getMetaData( );

            result.setDatabaseType( DatabaseMetadataUtil.getDatabaseType( sqlMetadata.getDatabaseProductName() ) );
            result.setDatabaseProductName( sqlMetadata.getDatabaseProductName( ) );
            result.setDatabaseProductVersion( sqlMetadata.getDatabaseProductVersion( ) );
            result.setDriverName( sqlMetadata.getDriverName( ) );
            result.setDriverVersion( sqlMetadata.getDriverVersion( ) );
            result.setDriverMajorVersion( sqlMetadata.getDriverMajorVersion() );
            result.setDriverMinorVersion( sqlMetadata.getDriverMinorVersion( ) );

            if ( includeCatalogs ) {
                List< CatalogMetadata > catalogs = new ArrayList<>( );
                rs = sqlMetadata.getCatalogs( );
                while ( rs.next( ) ) {
                    catalogs.add( new CatalogMetadata( rs.getString( "TABLE_CAT" ) ) );
                }
                rs.close( );
                result.setCatalogs( catalogs );
            }

            if ( includeSchemas ) {
                List< SchemaMetadata > schemas = new ArrayList<>( );
                rs = sqlMetadata.getSchemas( );
                while ( rs.next( ) ) {
                    schemas.add( new SchemaMetadata( rs.getString( "TABLE_CATALOG" ), rs.getString( "TABLE_SCHEM" ) ) );
                }
                rs.close( );
                result.setSchemas( schemas );
            }
            return result;
        } catch ( Exception e ) {
            throw new Exception( "It was not possible to read database metadata due to the following error: "
                    + e.getMessage( ) );
        } finally {
            try {
                conn.close( );
            } catch ( Exception e ) {
                //we are not interested in raising this error case.
            }
        }
    }

    /**
     * Gets a list of database objects metadata for a given database.
     * @param conn A valid connection to the target database.
     * @param schema A schema name for filtering. A null value will query all the available schemas.
     * @param tableNamePattern A table name pattern for filtering the database objects by name, e.g. %INVOICE_%. A null
     * value will include all tables.
     * @param types A list of database object types for filtering. A null value will include all types.
     * @return A list of database objects fulfilling the filtering criteria.
     */
    public static List< TableMetadata > findTables( Connection conn,
                                                    String schema,
                                                    String tableNamePattern,
                                                    DatabaseMetadata.TableType... types ) throws Exception {
        try {
            List< TableMetadata > result = new ArrayList<>( );
            DatabaseMetaData sqlMetadata = conn.getMetaData( );
            ResultSet rs = sqlMetadata.getTables( null, schema, tableNamePattern, toSqlTypes( types ) );
            TableMetadata tableMetadata;
            while ( rs.next( ) ) {
                tableMetadata = new TableMetadata( rs.getString( "TABLE_CAT" ),
                        rs.getString( "TABLE_SCHEM" ), rs.getString( "TABLE_NAME" ), rs.getString( "TABLE_TYPE" ) );
                result.add( tableMetadata );
            }
            rs.close( );
            return result;
        } catch ( Exception e ) {
            throw new Exception( "It was not possible to read schema tables due to the following error: " + e.getMessage( ) );
        } finally {
            try {
                conn.close( );
            } catch ( Exception e ) {
                //we are not interested in raising this error case.
            }
        }
    }

    public static DatabaseMetadata.DatabaseType getDatabaseType( String dbProductName ) {
        String lowerCasedName = dbProductName.toLowerCase();

        if ( lowerCasedName.contains( "h2" ) ) {
            return H2;
        } else if ( lowerCasedName.contains( "mysql" ) ) {
            return MYSQL;
        } else if ( lowerCasedName.contains( "mariadb" ) ) {
            return MARIADB;
        } else if ( lowerCasedName.contains( "postgresql" ) ) {
            return POSTGRESQL;
        } else if ( lowerCasedName.contains( "oracle" ) ) {
            return ORACLE;
        } else if ( lowerCasedName.contains( "microsoft" ) && lowerCasedName.contains( "sql" ) && lowerCasedName.contains( "server" ) ) {
            return SQLSERVER;
        } else if ( lowerCasedName.contains( "db2" ) ) {
            return DB2;
        }
        return null;
    }

    private static String[] toSqlTypes( DatabaseMetadata.TableType types[] ) {
        HashSet< DatabaseMetadata.TableType > typesSet = new HashSet<>( );
        String result[] = null;
        if ( types != null ) {
            for ( int i = 0; i < types.length; i++ ) {
                typesSet.add( types[ i ] );
            }
        }
        if ( !typesSet.isEmpty( ) && !typesSet.contains( DatabaseMetadata.TableType.ALL ) ) {
            result = new String[ typesSet.size( ) ];
            int i = 0;
            for ( DatabaseMetadata.TableType type : typesSet ) {
                result[ i++ ] = toSqlType( type );
            }
        }
        return result;
    }

    private static String toSqlType( DatabaseMetadata.TableType type ) {
        switch ( type ) {
            case TABLE:
                return "TABLE";
            case SYSTEM_TABLE:
                return "SYSTEM TABLE";
            case VIEW:
                return "VIEW";
            case SYSTEM_VIEW:
                return "SYSTEM VIEW";
            case SEQUENCE:
                return "SEQUENCE";
        }
        return null;
    }
}