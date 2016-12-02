/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.verifier.webworker.client.testutil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.verifier.webworker.client.DecisionTableAnalyzerFromFileTest;

import static org.junit.Assert.*;

public class TestUtil {

    public static String loadResource( final String name ) throws
                                                           Exception {
        final InputStream in = DecisionTableAnalyzerFromFileTest.class.getResourceAsStream( name );
        final Reader reader = new InputStreamReader( in );
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[1024];
        int len = 0;
        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

    public static void assertOnlyContains( final Set<Issue> result,
                                           final String... expected ) {

        if ( result.isEmpty() ) {
            fail( "Data is empty." );
        }

        for ( final Issue issue : result ) {
            String title = ExplanationProvider.toTitle( issue );
            if ( !contains( expected,
                            title ) ) {
                fail( "Should not find: " + title );
            }
        }
    }

    private static boolean contains( final String[] expected,
                                     final String title ) {
        for ( final String item : expected ) {
            if ( title.contains( item ) ) {
                return true;
            }
        }

        return false;
    }

    public static void assertContains( final String expected,
                                       final Set<Issue> result ) {
        boolean foundIt = false;

        for ( final Issue issue : result ) {
            if ( ExplanationProvider.toTitle( issue )
                    .contains( expected ) ) {
                foundIt = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected,
                    foundIt );
    }

    public static void assertDoesNotContain( final String notExpected,
                                             final Set<Issue> result ) {
        boolean foundIt = false;

        for ( final Issue issue : result ) {
            if ( ExplanationProvider.toTitle( issue )
                    .contains( notExpected ) ) {
                foundIt = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected,
                     foundIt );
    }

    public static void assertDoesNotContain( final String notExpected,
                                             final Set<Issue> result,
                                             final int rowNumber ) {

        boolean foundOne = false;

        for ( final Issue issue : result ) {
            if ( containsRowNumber( rowNumber,
                                    issue ) && ExplanationProvider.toTitle( issue )
                    .contains( notExpected ) ) {
                foundOne = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected,
                     foundOne );
    }

    public static void assertContains( final String expected,
                                       final Set<Issue> result,
                                       final int rowNumber ) {

        boolean foundOne = false;

        for ( Issue issue : result ) {
            final String title = ExplanationProvider.toTitle( issue );
            if ( containsRowNumber( rowNumber,
                                    issue ) && title
                    .contains( expected ) ) {
                foundOne = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected + " from: " + result.toString(),
                    foundOne );
    }

    private static boolean containsRowNumber( final int rowNumber,
                                              final Issue issue ) {
        for ( Integer number : issue.getRowNumbers() ) {
            if ( rowNumber == number ) {
                return true;
            }
        }
        return false;
    }
}
