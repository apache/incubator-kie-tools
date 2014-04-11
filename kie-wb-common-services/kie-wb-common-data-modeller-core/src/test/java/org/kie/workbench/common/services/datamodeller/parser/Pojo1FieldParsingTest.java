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

import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactory;
import org.kie.workbench.common.services.datamodeller.parser.descr.DescriptorFactoryImpl;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Pojo1FieldParsingTest extends JavaParserBaseTest {

    public Pojo1FieldParsingTest( ) {
        super( "Pojo1.java" );
        init();
    }

    List<String> fieldSentences = new ArrayList<String>(  );
    List<TypeTestResult> typeTestResults = new ArrayList<TypeTestResult>(  );


    private void init() {

        fieldSentences.add( "int field1;" );
        typeTestResults.add( new TypeTestResult(true, false, false, false) );

        fieldSentences.add( "java.lang.Integer field2;" );
        typeTestResults.add( new TypeTestResult(false, true, false, false) );

        fieldSentences.add( "Integer field3;" );
        typeTestResults.add( new TypeTestResult(false, true, false, false) );

        fieldSentences.add( "List<Integer> field4;" );
        typeTestResults.add( new TypeTestResult(false, false, false, true) );

        fieldSentences.add( "java.util.List<Integer> field5;" );
        typeTestResults.add( new TypeTestResult(false, false, false, true) );

        fieldSentences.add( "List<java.lang.Integer> field6;" );
        typeTestResults.add( new TypeTestResult(false, false, false, true) );

        fieldSentences.add( "java.util.List<java.lang.Integer> field7;" );
        typeTestResults.add( new TypeTestResult(false, false, false, true) );

        fieldSentences.add( "private /*comment2*/ java.lang.String name  ;" );
        typeTestResults.add( new TypeTestResult(false, true, false, false) );

        fieldSentences.add( "public  static  int a  = 3 ,   b =   4         ;" );
        typeTestResults.add( new TypeTestResult(true, false, false, false) );

        fieldSentences.add( "java.util.List<List<String>> list;" );
        typeTestResults.add( new TypeTestResult(false, false, false, false) );

    }

    @Test
    public void testFieldsReading1( ) {
        try {

            assertClass( );
            List<FieldDescr> fields = parser.getFileDescr( ).getClassDescr( ).getFields( );
            assertEquals( fieldSentences.size(), fields.size() );

            for ( int i = 0; i < fieldSentences.size(); i++ ) {
                assertEquals( typeTestResults.get( i ).primitive, DriverUtils.getInstance().isPrimitiveType( fields.get( i ).getType() ));
                assertEquals( typeTestResults.get( i ).simpleClass, DriverUtils.getInstance().isSimpleClass(  fields.get( i ).getType() ));
                assertEquals( typeTestResults.get( i ).array, DriverUtils.getInstance().isArray( fields.get( i ).getType() ));

                Object[] genericsCheck = DriverUtils.getInstance().isSimpleGeneric( fields.get( i ).getType() );

                assertEquals( typeTestResults.get( i ).simpleGeneric, genericsCheck[0] );


            }

        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage());
        }
    }

    public class TypeTestResult {

        public TypeTestResult( boolean primitive, boolean simpleClass, boolean array, boolean simpleGeneric) {
            this.primitive = primitive;
            this.simpleClass = simpleClass;
            this.array = array;
            this.simpleGeneric = simpleGeneric;
        }

        public boolean primitive;
        public boolean simpleClass;
        public boolean array;
        public boolean simpleGeneric;

    }

}
