/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.jcr2vfsmigration.xml;

public class XMLStringBuilderTest {

    public void testXmlStringBuilder() {
        String xml =
            new XMLStringBuilder()
                .startTag( "modules" ).newLine().indent()
                .startTag( "module").newLine().indent()
                .startTag( "name").printTagContent( "module1" ).endTag( "name" ).newLine()
                .startTag( "uuid").printTagContent( "uuid1" ).endTag( "uuid" ).newLine().unIndent()
                .endTag( "module" ).newLine()
                .startTag( "module" ).newLine().indent()
                .startTag( "name").printTagContent( "module1" ).endTag( "name" ).newLine()
                .startTag( "uuid" ).printTagContent( "uuid1" ).endTag( "uuid" ).newLine()
                .endTag( "module" ).newLine().unIndent()
                .endTag( "modules" )
                .toString();
        System.out.println( "XML: \n" + xml );
    }

    public static void main( String[] args ) {
        XMLStringBuilderTest test = new XMLStringBuilderTest();
        test.testXmlStringBuilder();
    }
}
