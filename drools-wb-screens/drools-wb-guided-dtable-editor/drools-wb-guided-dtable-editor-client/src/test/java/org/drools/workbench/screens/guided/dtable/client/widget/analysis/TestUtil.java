/*
 * Copyright 2015 JBoss Inc
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

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;

import static org.junit.Assert.*;

public class TestUtil {

    public static void assertEmpty( List<CellValue<? extends Comparable<?>>> result ) {
        for ( CellValue cellValue : result ) {
            Analysis analysis = (Analysis) cellValue.getValue();
            assertEquals( "<span></span>", analysis.toHtmlString() );
        }
    }

    public static void assertContains( String expected,
                                       List<CellValue<? extends Comparable<?>>> result ) {
        boolean foundIt = false;

        for ( CellValue cellValue : result ) {
            Analysis analysis = (Analysis) cellValue.getValue();
            if ( analysis.toHtmlString().contains( expected ) ) {
                foundIt = true;
                break;
            }
        }

        assertTrue( "Could not find " + expected, foundIt );
    }

    public static void assertDoesNotContain( String notExpected,
                                             List<CellValue<? extends Comparable<?>>> result ) {
        boolean foundIt = false;

        for ( CellValue cellValue : result ) {
            Analysis analysis = (Analysis) cellValue.getValue();
            if ( analysis.toHtmlString().contains( notExpected ) ) {
                foundIt = true;
                break;
            }
        }

        assertFalse( "Found " + notExpected, foundIt );
    }

    public static void assertDoesNotContain( String notExpected,
                                             List<CellValue<? extends Comparable<?>>> result,
                                             int rowNumber ) {

        Analysis analysis = (Analysis) result.get( rowNumber ).getValue();

        assertFalse( "Found " + notExpected, analysis.toHtmlString().contains( notExpected ) );
    }

    public static void assertContains( String expected,
                                       List<CellValue<? extends Comparable<?>>> result,
                                       int rowNumber ) {

        Analysis analysis = (Analysis) result.get( rowNumber ).getValue();

        assertTrue( "Could not find " + expected, analysis.toHtmlString().contains( expected ) );
    }
}
