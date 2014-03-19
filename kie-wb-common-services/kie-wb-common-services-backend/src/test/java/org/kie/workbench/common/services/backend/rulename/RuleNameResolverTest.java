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

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class RuleNameResolverTest {

    @Test
    public void testSimple() throws Exception {
        List<String> ruleNames = new RuleNameResolver(
                "rule test\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertEquals("test", ruleNames.get(0));
    }

    @Test
    public void testSimpleWithQuotes() throws Exception {
        List<String> ruleNames = new RuleNameResolver(
                "rule \"test rule\"\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertEquals("test rule", ruleNames.get(0));
    }

    @Test
    public void testExtends() throws Exception {
        List<String> ruleNames = new RuleNameResolver(
                "rule test extends parentRule\n" +
                        "when\n" +
                        "then\n" +
                        "end").getRuleNames();

        assertEquals(1, ruleNames.size());
        assertEquals("test", ruleNames.get(0));
    }

    @Test
    public void testDTableSource() throws Exception {
        List<String> ruleList = new RuleNameResolver(
                getRuleDRL(1)
                        + getRuleDRL(2)
                        + getRuleDRL(3)
                        + getRuleDRL(4)).getRuleNames();

        assertEquals(4, ruleList.size());
        assertEquals("test row 1", ruleList.get(0));
        assertEquals("test row 2", ruleList.get(1));
        assertEquals("test row 3", ruleList.get(2));
        assertEquals("test row 4", ruleList.get(3));
    }

    @Test
    public void testIgnoreMultiLineCommentedRules() throws Exception {
        List<String> ruleList = new RuleNameResolver(
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
        assertEquals("test row 1", ruleList.get(0));
        assertEquals("test row 3", ruleList.get(1));
        assertEquals("test row 5", ruleList.get(2));
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
        List<String> ruleList = resolver.getRuleNames();

        assertEquals("org.test", resolver.getPackageName());
        assertEquals(4, ruleList.size());
        assertEquals("test row 1", ruleList.get(0));
        assertEquals("test row 3", ruleList.get(1));
        assertEquals("test row 4", ruleList.get(2));
        assertEquals("test row 5", ruleList.get(3));
    }

    @Test
    public void testSimpleWithSomeSpaces() throws Exception {
        RuleNameResolver resolver = new RuleNameResolver(
                "package org.test2\n" +
                        "rule            test\n" +
                        "when\n" +
                        "then\n" +
                        "end");
        List<String> ruleNames = resolver.getRuleNames();

        assertEquals("org.test2", resolver.getPackageName());
        assertEquals(1, ruleNames.size());
        assertEquals("test", ruleNames.get(0));
    }

    private String getRuleDRL(int line) {
        return "rule \"test row " + line + "\"\n" +
                "when\n" +
                "then\n" +
                "end\n";
    }
}
