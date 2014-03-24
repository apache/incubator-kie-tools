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
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactoryImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import static org.junit.Assert.assertEquals;

public class JavaFileHandler2Test extends JavaFileHandlerBaseTest {

    String fileContents[] = new String[ 6 ];

    public JavaFileHandler2Test( ) throws Exception {
        super( "JavaFileHandler2.java" );
    }

    private void assertStrings( String a, String b ) {
        for ( int i = 0; i < a.length( ) && i < b.length( ); i++ ) {
            assertEquals( "character i: " + i + " expected: " + a.charAt( i ) + " current: " + b.length( ), a.charAt( i ), b.charAt( i ) );
        }
    }

    @Test
    public void test( ) {
        try {

            String result = fileHandler.buildResult( );
            System.out.println( result );

            ClassDescr classDescr = fileHandler.getFileDescr( ).getClassDescr( );

            FieldDescr field = DescriptorFactoryImpl.getInstance( ).createFieldDescr( "\n\n\tpublic /*eso*/static   int value  = (2+4), otro=1  /**/ ; /**/" );

            ParserUtil.setSourceBufferTMP( field, field.getSourceBuffer( ) );
            ParserUtil.populateUnManagedElements( 0, field );
            ParserUtil.setSourceBufferTMP( field, field.getSourceBuffer( ) );

            classDescr.addField( field );
            classDescr.addField( field );

            result = fileHandler.buildResult( );
            System.out.println( result );

            classDescr.getElements( ).remove( field );
            classDescr.getElements( ).remove( field );

            result = fileHandler.buildResult( );
            System.out.println( result );

            assertEquals( originalFileContent, result );
            int i = 0;

        } catch ( Exception e ) {
            e.printStackTrace( );
        }

    }

}
