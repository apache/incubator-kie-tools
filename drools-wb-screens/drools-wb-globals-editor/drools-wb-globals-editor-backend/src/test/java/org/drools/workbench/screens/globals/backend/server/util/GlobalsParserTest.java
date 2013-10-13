package org.drools.workbench.screens.globals.backend.server.util;

import org.junit.Test;
import org.uberfire.commons.data.Pair;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.GlobalsParser;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for GlobalsParser.
 */
public class GlobalsParserTest {

    @Test
    public void testSimpleEntry() {
        final String content = "global java.util.List myList;";
        final List<Pair<String, String>> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 1,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getK1() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getK2() );
    }

    @Test
    public void testMultipleEntries() {
        final String content = "global java.util.List myList;\n"
                + "global java.lang.String myString;";
        final List<Pair<String, String>> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 2,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getK1() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getK2() );
        assertEquals( "myString",
                      globals.get( 1 ).getK1() );
        assertEquals( "java.lang.String",
                      globals.get( 1 ).getK2() );
    }

    @Test
    public void testCommentedEntry() {
        final String content = "global java.util.List myList;\n"
                + "#global java.lang.String myString;";
        final List<Pair<String, String>> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 1,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getK1() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getK2() );
    }

    @Test
    public void testWhiteSpace() {
        final String content = "  global    java.util.List myList;\n"
                + "  global   java.lang.String   myString;   ";
        final List<Pair<String, String>> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 2,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getK1() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getK2() );
        assertEquals( "myString",
                      globals.get( 1 ).getK1() );
        assertEquals( "java.lang.String",
                      globals.get( 1 ).getK2() );
    }

}
