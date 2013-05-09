package org.drools.workbench.screens.enums.client.editor;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class EnumParserTests {

    @Test
    public void testParsing1() {
        //Perfectly valid
        final String content = "'Fact.field' : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
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
    public void testParsing2() {
        //Fact is not prefixed with '
        final String content = "Fact.field' : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testParsing3() {
        //Field is not suffixed with '
        final String content = "'Fact.field : ['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testParsing4() {
        //Spaces omitted around colon
        final String content = "'Fact.field':['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
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
    public void testParsing5() {
        //Space before colon omitted
        final String content = "'Fact.field': ['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
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
    public void testParsing6() {
        //Space after colon omitted
        final String content = "'Fact.field' :['a', 'b']";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
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
    public void testMissingFact1() {
        final String content = "field' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testMissingFact2() {
        final String content = ".field' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testMissingField1() {
        final String content = "Fact' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testMissingField2() {
        final String content = "Fact.' : ['a', 'b']";
        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testMissingContext() {
        final String content = "Fact.field' :";
        final List<EnumRow> enums = EnumParser.parseEnums( content );
        assertTrue( enums.isEmpty() );
    }

    @Test
    public void testComments() {
        final String content = "'Fact.field' : ['a', 'b']\n"
                + "\n"
                + "#A comment\n"
                + "//Another comment\n";

        final List<EnumRow> enums = EnumParser.parseEnums( content );
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

}
