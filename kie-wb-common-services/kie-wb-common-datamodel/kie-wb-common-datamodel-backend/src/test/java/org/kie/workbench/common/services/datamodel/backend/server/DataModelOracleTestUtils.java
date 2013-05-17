package org.kie.workbench.common.services.datamodel.backend.server;

import static org.junit.Assert.fail;

/**
 * Utility methods for DataModelOracle tests
 */
public class DataModelOracleTestUtils {

    public static void assertContains( final String string,
                                       final String[] c ) {
        for ( int i = 0; i < c.length; i++ ) {
            if ( string.equals( c[ i ] ) ) {
                return;
            }
        }
        fail( "String array did not contain: " + string );
    }

}
