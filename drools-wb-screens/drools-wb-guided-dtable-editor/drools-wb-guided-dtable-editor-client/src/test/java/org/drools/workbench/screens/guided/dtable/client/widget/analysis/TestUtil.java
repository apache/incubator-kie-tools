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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;

import static org.junit.Assert.*;

public class TestUtil {

    public static void assertOnlyContains( final AnalysisReport result,
                                           final String... expected ) {

        for ( final Issue issue : result.getAnalysisData() ) {
            if ( !contains( expected,
                            issue.getTitle() ) ) {
                fail( "Should not find: " + issue.getTitle() );
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
                                       final AnalysisReport result ) {
        boolean foundIt = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( issue.getTitle().contains( expected ) ) {
                foundIt = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected, foundIt );
    }

    public static void assertDoesNotContain( final String notExpected,
                                             final AnalysisReport result ) {
        boolean foundIt = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( issue.getTitle().contains( notExpected ) ) {
                foundIt = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected, foundIt );
    }

    public static void assertDoesNotContain( final String notExpected,
                                             final AnalysisReport result,
                                             final int rowNumber ) {

        boolean foundOne = false;

        for ( final Issue issue : result.getAnalysisData() ) {
            if ( containsRowNumber( rowNumber, issue ) && issue.getTitle().contains( notExpected ) ) {
                foundOne = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected, foundOne );
    }

    public static void assertContains( final String expected,
                                       final AnalysisReport result,
                                       final int rowNumber ) {

        boolean foundOne = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( containsRowNumber( rowNumber, issue ) && issue.getTitle().contains( expected ) ) {
                foundOne = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected + " from: " + result.toString(), foundOne );
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
