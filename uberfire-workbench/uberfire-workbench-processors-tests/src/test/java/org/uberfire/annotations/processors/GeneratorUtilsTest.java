package org.uberfire.annotations.processors;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class GeneratorUtilsTest {

    @Test
    public void testFormatProblems1Item() {
        String msg = GeneratorUtils.formatProblemsList( "com.foo.MyAnnotation", singletonList( "be fun" ) );
        assertEquals( "Methods annotated with @MyAnnotation must be fun", msg );
    }

    @Test
    public void testFormatProblems2Items() {
        String msg = GeneratorUtils.formatProblemsList(
                "com.foo.MyAnnotation",
                asList( "be wise", "be fair" ) );
        assertEquals( "Methods annotated with @MyAnnotation must be wise and be fair", msg );
    }

    @Test
    public void testFormatProblems3Items() {
        String msg = GeneratorUtils.formatProblemsList(
                "com.foo.MyAnnotation",
                asList( "be wise", "be fair", "be kind" ) );
        assertEquals( "Methods annotated with @MyAnnotation must be wise, be fair, and be kind", msg );
    }

}
