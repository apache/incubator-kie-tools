/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.parser.descr.ImportDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Imports1Test extends JavaParserBaseTest {

    public Imports1Test( ) {
        super( "Imports1.java" );
        init( );
    }

    private List<String> importSentences = new ArrayList<String>( );
    private List<String> importNames = new ArrayList<String>( );

    @Test
    public void testImportSentencesReading( ) {
        try {
            assertClass( );
            List<ImportDescr> imports = parser.getFileDescr( ).getImports( );
            assertEquals( importSentences.size( ), imports.size( ) );

            //test import definition sentences
            int i = 0;
            for ( String importSentence : importSentences ) {
                assertEquals( importSentences.get( i ), ParserUtil.readElement( buffer, imports.get( i ) ) );
                i++;
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }
    }

    @Test
    public void testImportNames( ) {
        try {
            assertClass( );
            List<ImportDescr> imports = parser.getFileDescr( ).getImports( );
            assertEquals( importSentences.size( ), imports.size( ) );

            //test import names
            int i = 0;
            for ( String importName : importNames ) {
                assertEquals( importNames.get( i ), imports.get( i ).getName( true ) );
                i++;
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }
    }

    /*
    @Test
    public void testClassFields() {
        try {
            assertClass();
            List<FieldDescr> fields = parser.getFileDescr().getClassDescr().getFields();
            for (int i = 0; i < expectedFields.size(); i++) {
                assertEqualsFieldDeclaration(buffer, expectedFields.get(i), fields.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }
    */

    private void init( ) {

        importSentences.add( "import org.kie.workbench.common.services.datamodeller.parser.*;" );
        importNames.add( "org.kie.workbench.common.services.datamodeller.parser.*" );

        importSentences.add( "import org.kie.workbench.common.services.datamodeller.parser.JavaParser;" );
        importNames.add( "org.kie.workbench.common.services.datamodeller.parser.JavaParser" );

        importSentences.add( "import java.util.*;" );
        importNames.add( "java.util.*" );

        importSentences.add( "import java.util.AbstractList;" );
        importNames.add( "java.util.AbstractList" );

        importSentences.add( "import static org.junit.Assert.assertArrayEquals;" );
        importNames.add( "org.junit.Assert.assertArrayEquals" );

        importSentences.add( "import static org.junit.Assert.*;" );
        importNames.add( "org.junit.Assert.*" );
    }

}
