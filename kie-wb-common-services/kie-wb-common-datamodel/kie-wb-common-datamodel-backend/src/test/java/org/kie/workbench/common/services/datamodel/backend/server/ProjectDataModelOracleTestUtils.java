package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.ModelField;

import static org.junit.Assert.*;

/**
 * Utility methods for DataModelOracle tests
 */
public class ProjectDataModelOracleTestUtils {

    public static void assertContains( final String string,
                                       final String[] strings ) {
        for ( int i = 0; i < strings.length; i++ ) {
            if ( string.equals( strings[ i ] ) ) {
                return;
            }
        }
        fail( "String[] did not contain: " + string );
    }

    public static void assertContains( final String string,
                                       final Set<String> strings ) {
        if ( !strings.contains( string ) ) {
            fail( "Set<String> did not contain: " + string );
        }
    }

    public static void assertContains( final String fieldName,
                                       final ModelField[] fieldDefinitions ) {
        for ( int i = 0; i < fieldDefinitions.length; i++ ) {
            if ( fieldName.equals( fieldDefinitions[ i ].getName() ) ) {
                return;
            }
        }
        fail( "ModelField[] did not contain field: " + fieldName );
    }

}
