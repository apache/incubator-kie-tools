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

package org.kie.workbench.common.services.backend.rulename;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

public class RuleNameResolverTest {

    @Test
    public void testSimple() throws Exception {
        Set<String> ruleNames = new RuleNameResolver(
                "rule test\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertTrue(ruleNames.contains("test"));
    }

    @Test
    public void testSimpleWithQuotes() throws Exception {
        Set<String> ruleNames = new RuleNameResolver(
                "rule \"test rule\"\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertTrue(ruleNames.contains("test rule"));
    }

    @Test
    public void testSimpleWithSingleQuotes() throws Exception {
        Set<String> ruleNames = new RuleNameResolver(
                "rule 'test rule'\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertTrue(ruleNames.contains("test rule"));
    }

    @Test
    public void testExtends() throws Exception {
        Set<String> ruleNames = new RuleNameResolver(
                "rule test extends parentRule\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertTrue(ruleNames.contains("test"));
    }

    @Test
    public void testDTableSource() throws Exception {
        Set<String> ruleList = new RuleNameResolver(
                getRuleDRL(1)
                        + getRuleDRL(2)
                        + getRuleDRL(3)
                        + getRuleDRL(4)).getRuleNames();

        assertEquals(4, ruleList.size());
        assertTrue(ruleList.contains("test row 1"));
        assertTrue(ruleList.contains("test row 2"));
        assertTrue(ruleList.contains("test row 3"));
        assertTrue(ruleList.contains("test row 4"));
    }

    @Test
    public void testIgnoreMultiLineCommentedRules() throws Exception {
        Set<String> ruleList = new RuleNameResolver(
                getRuleDRL(1)
                        + "/*\n"
                        + getRuleDRL(2)
                        + "/* there might be several /* */ pairs inside the comment.\n"
                        + getRuleDRL(100)
                        + "*/\n"
                        + getRuleDRL(3)
                        + "/*"
                        + getRuleDRL(4)
                        + "*/"
                        + getRuleDRL(5)).getRuleNames();

        assertEquals(3, ruleList.size());
        assertTrue(ruleList.contains("test row 1"));
        assertTrue(ruleList.contains("test row 3"));
        assertTrue(ruleList.contains("test row 5"));
    }

    @Test
    public void testIgnoreSingleLineCommentedRules() throws Exception {
        RuleNameResolver resolver = new RuleNameResolver(
                getRuleDRL(1)
                        + "package org.test;\n"
                        + "// rule \"I'm not here\"\n"
                        + "// when\n" +
                        "// then\n"
                        + "// end\n"
                        + getRuleDRL(3)
                        + "// Just for fun, I'll end the line with //\n"
                        + getRuleDRL(4)
                        + getRuleDRL(5)
                        + "// end of file");
        Set<String> ruleList = resolver.getRuleNames();

        assertEquals("org.test", resolver.getPackageName());
        assertEquals(4, ruleList.size());
        assertTrue(ruleList.contains("test row 1"));
        assertTrue(ruleList.contains("test row 3"));
        assertTrue(ruleList.contains("test row 4"));
        assertTrue(ruleList.contains("test row 5"));
    }

    @Test
    public void testSimpleWithSomeSpaces() throws Exception {
        RuleNameResolver resolver = new RuleNameResolver(
                "package org.test2\n" +
                        "rule            test\n" +
                        "when\n" +
                        "then\n" +
                        "end");
        Set<String> ruleNames = resolver.getRuleNames();

        assertEquals("org.test2", resolver.getPackageName());
        assertEquals(1, ruleNames.size());
        assertTrue(ruleNames.contains("test"));
    }

    private String getRuleDRL(int line) {
        return "rule \"test row " + line + "\"\n" +
                "when\n" +
                "then\n" +
                "end\n";
    }
}
