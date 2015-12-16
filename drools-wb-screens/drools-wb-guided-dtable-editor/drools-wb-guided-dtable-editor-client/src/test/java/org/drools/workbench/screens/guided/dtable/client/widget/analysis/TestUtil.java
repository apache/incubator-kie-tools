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

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.NumericIntegerConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.StringConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;

import static org.junit.Assert.*;

public class TestUtil {

    public static void assertContains( String expected,
                                       AnalysisReport result ) {
        boolean foundIt = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( issue.getTitle().contains( expected ) ) {
                foundIt = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected, foundIt );
    }

    public static void assertDoesNotContain( String notExpected,
                                             AnalysisReport result ) {
        boolean foundIt = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( issue.getTitle().contains( notExpected ) ) {
                foundIt = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected, foundIt );
    }

    public static void assertDoesNotContain( String notExpected,
                                             AnalysisReport result,
                                             int rowNumber ) {

        boolean foundOne = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( containsRowNumber( rowNumber, issue ) && issue.getTitle().contains( notExpected ) ) {
                foundOne = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected, foundOne );
    }

    public static void assertContains( String expected,
                                       AnalysisReport result,
                                       int rowNumber ) {

        boolean foundOne = false;

        for ( Issue issue : result.getAnalysisData() ) {
            if ( containsRowNumber( rowNumber, issue ) && issue.getTitle().contains( expected ) ) {
                foundOne = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected, foundOne );
    }

    private static boolean containsRowNumber( int rowNumber,
                                              Issue issue ) {
        for ( Integer number : issue.getRowNumbers() ) {
            if ( rowNumber == number ) {
                return true;
            }
        }
        return false;
    }

    public static NumericIntegerConditionInspector getNumericIntegerCondition( Pattern52 pattern,
                                                                               String factField,
                                                                               String operator,
                                                                               int value ) {
        return new NumericIntegerConditionInspector( pattern,
                                                     factField,
                                                     value,
                                                     operator );
    }

    public static StringConditionInspector getStringCondition( Pattern52 pattern,
                                                               String factField,
                                                               String operator,
                                                               String value ) {
        return new StringConditionInspector( pattern,
                                             factField,
                                             value,
                                             operator );
    }
}
