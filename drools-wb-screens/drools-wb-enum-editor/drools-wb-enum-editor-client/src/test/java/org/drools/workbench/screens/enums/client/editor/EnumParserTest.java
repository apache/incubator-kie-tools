/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.enums.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnumParserTest {

    @Test
    public void testFromStringParsing1() {
        //Perfectly valid
        final String content = "'Fact.field' : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( 1,
                      enums.size() );
        assertEquals( "Fact",
                      enums.get( 0 ).getFactName() );
        assertEquals( "field",
                      enums.get( 0 ).getFieldName() );
        assertEquals( "['a', 'b']",
                      enums.get( 0 ).getContext() );
    }

    @Test
    public void testFromStringParsing2() {
        //Fact is not prefixed with '
        final String content = "Fact.field' : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "Fact.field' : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringParsing3() {
        //Field is not suffixed with '
        final String content = "'Fact.field : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "'Fact.field : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringParsing4() {
        //Spaces omitted around colon
        final String content = "'Fact.field':['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( 1,
                      enums.size() );
        assertEquals( "Fact",
                      enums.get( 0 ).getFactName() );
        assertEquals( "field",
                      enums.get( 0 ).getFieldName() );
        assertEquals( "['a', 'b']",
                      enums.get( 0 ).getContext() );
    }

    @Test
    public void testFromStringParsing5() {
        //Space before colon omitted
        final String content = "'Fact.field': ['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( 1,
                      enums.size() );
        assertEquals( "Fact",
                      enums.get( 0 ).getFactName() );
        assertEquals( "field",
                      enums.get( 0 ).getFieldName() );
        assertEquals( "['a', 'b']",
                      enums.get( 0 ).getContext() );
    }

    @Test
    public void testFromStringParsing6() {
        //Space after colon omitted
        final String content = "'Fact.field' :['a', 'b']";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( 1,
                      enums.size() );
        assertEquals( "Fact",
                      enums.get( 0 ).getFactName() );
        assertEquals( "field",
                      enums.get( 0 ).getFieldName() );
        assertEquals( "['a', 'b']",
                      enums.get( 0 ).getContext() );
    }

    @Test
    public void testFromStringMissingFact1() {
        final String content = "field' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "field' : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringMissingFact2() {
        final String content = ".field' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( ".field' : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringMissingField1() {
        final String content = "Fact' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "Fact' : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringMissingField2() {
        final String content = "Fact.' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "Fact.' : ['a', 'b']",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringMissingContext() {
        final String content = "Fact.field' :";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "Fact.field' :",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringInvalidSyntax() {
        final String content = "This isn't even close to a correct definition";
        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( "This isn't even close to a correct definition",
                      enums.get( 0 ).getRaw() );
    }

    @Test
    public void testFromStringComments() {
        final String content = "'Fact.field' : ['a', 'b']\n"
                + "\n"
                + "#A comment\n"
                + "//Another comment\n";

        final List<EnumRow> enums = EnumParser.fromString( content );
        assertFalse( enums.isEmpty() );
        assertEquals( 1,
                      enums.size() );
        assertEquals( "Fact",
                      enums.get( 0 ).getFactName() );
        assertEquals( "field",
                      enums.get( 0 ).getFieldName() );
        assertEquals( "['a', 'b']",
                      enums.get( 0 ).getContext() );
    }

    @Test
    public void testToString1() {
        final List<EnumRow> content = new ArrayList<EnumRow>() {{
            add( new EnumRow( "Fact",
                              "field",
                              "['a', 'b']" ) );
            add( new EnumRow( "A raw value" ) );
        }};

        final String enums = EnumParser.toString( content );
        assertEquals( enums,
                      "'Fact.field' : ['a', 'b']\n" +
                              "A raw value\n" );
    }

}
