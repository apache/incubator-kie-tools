/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
