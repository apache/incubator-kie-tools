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

import static org.junit.Assert.assertEquals;

import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.util.List;

public class Pojo1FieldParsingTest extends JavaParserBaseTest {

    public Pojo1FieldParsingTest( ) {
        super( "Pojo1.java" );
    }

    @Test
    public void testFieldsReading1( ) {
        try {

            assertClass( );
            List<FieldDescr> fields = parser.getFileDescr( ).getClassDescr( ).getFields( );

            assertEquals( 3, fields.size( ) );
            String[] fieldSentences = new String[ 3 ];

            fieldSentences[ 0 ] = "private /*comment2*/ java.lang.String name  ;";
            fieldSentences[ 1 ] = "public  static  int a  = 3 ,   b =   4         ;";
            fieldSentences[ 2 ] = "java.util.List<List<String>> list;";

            for ( int i = 0; i < fields.size( ) && i < fieldSentences.length; i++ ) {
                assertEquals( fieldSentences[ i ], ParserUtil.readElement( buffer, fields.get( i ) ) );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

}
