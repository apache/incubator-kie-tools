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
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class JavaFileHandler3Test extends JavaFileHandlerBaseTest {

    String fileContents[] = new String[ 6 ];

    public JavaFileHandler3Test( ) throws Exception {
        super( "JavaFileHandler1.java" );

        InputStream inputStream;
        for ( int i = 0; i < fileContents.length; i++ ) {
            inputStream = this.getClass( ).getResourceAsStream( "JavaFileHandler1.java.result" + i + ".txt" );
            fileContents[ i ] = ParserUtil.readString( inputStream );

        }
    }

    private void assertStrings( String a, String b ) {
        for ( int i = 0; i < a.length( ) && i < b.length( ); i++ ) {
            assertEquals( "character i: " + i + " expected: " + a.charAt( i ) + " current: " + b.length( ), a.charAt( i ), b.charAt( i ) );
        }
    }

    @Test
    public void testMethodRemoval( ) {
        try {

            /*
            fileHandler.deleteMethod("getField2", null);
            assertStrings(fileContents[0], fileHandler.buildResult());

            fileHandler.deleteMethod("setField1", null);
            assertStrings(fileContents[1], fileHandler.buildResult());


            fileHandler.deleteMethod("getField1", null);
            assertStrings(fileContents[2], fileHandler.buildResult());

            fileHandler.deleteField("field12");
            assertStrings(fileContents[3], fileHandler.buildResult());

            fileHandler.createField("\n\n    public int field100 = 12;");
            assertStrings(fileContents[4], fileHandler.buildResult());

            fileHandler.createMethod("\n\n    public java.lang.String getAddress() { return null; }");
            assertStrings(fileContents[5], fileHandler.buildResult());

            //System.out.println(fileHandler.buildResult());

            String classText = originalFileContent.substring(fileHandler.getFileDescr().getClassDescr().getStart(), fileHandler.getFileDescr().getClassDescr().getStop() +1);
            System.out.println(classText);


            ((JavaFileHandlerImplOLD)fileHandler).populateUnManagedElements(fileHandler.getFileDescr());

            String tree = ((JavaFileHandlerImplOLD)fileHandler).printTree(fileHandler.getFileDescr());


*/

            int i = 0;

            /*

            TODO add more cases

            assertEquals(fileContents[0], fileHandler.build());


            fileHandler.deleteMethod("getField1");
            System.out.println(fileHandler.build());

            fileHandler.deleteField("setField2");
            System.out.println(fileHandler.build());

            fileHandler.deleteMethod("setField2");
            fileHandler.addField("\n\tprotected String surname = null;\n");
            fileHandler.deleteMethod("setField1");
            fileHandler.deleteField("field7");
            fileHandler.addMethod("\n\tpublic static final java.lang.String echo(String msg) {\n\t\treturn msg;\n\t}\n");
            fileHandler.deleteField("field8");
            fileHandler.addField("\n\tprotected int i = 0;\n");
            fileHandler.deleteField("field6");
            fileHandler.deleteField("field9");
            fileHandler.deleteField("field11");
            fileHandler.deleteField("field14");

            fileHandler.addMember("\n\tpublic String getUserName() {\n\t\treturn surname;\n\t}\n");
            */

            String result = fileHandler.buildResult( );
            //System.out.println(result);

        } catch ( Exception e ) {
            e.printStackTrace( );
        }

    }

}
