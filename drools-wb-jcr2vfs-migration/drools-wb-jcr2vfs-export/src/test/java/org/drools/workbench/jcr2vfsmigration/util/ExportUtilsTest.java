/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.util;

import org.junit.Assert;
import org.junit.Test;

// Moved from DRLMigrationUtilsTest
public class ExportUtilsTest {

    @Test
    public void shouldMigrateStartOfCommentInLineStartingWithWhiteSpaces() {
        migrateCommentsAndAssertResult("  \t  \t some text # some comment", "  \t  \t some text // some comment");
    }

    @Test
    public void shouldMigrateStartOfCommentInLineStartingWithComment() {
        migrateCommentsAndAssertResult("# some comment", "// some comment");
    }

    @Test
    public void shouldNotMigrateStartOfCommentInsideDoubleQuotes() {
        migrateCommentsAndAssertResult("hash tag \" # inside double quotes\"", "hash tag \" # inside double quotes\"");
    }

    @Test
    public void shouldNotMigrateStartOfCommentInsideSingleQuotes() {
        migrateCommentsAndAssertResult("hash tag ' # inside single quotes'", "hash tag ' # inside single quotes'");
    }

    @Test
    public void shouldNotMigrateDslDebugConstruct() {
        // '#/' is used for debugging in DSL and DSLR -> the hash tag in there must be ignored
        migrateCommentsAndAssertResult("some text #/", "some text #/");
        migrateCommentsAndAssertResult("#/ some text # some comment", "#/ some text // some comment");
    }

    @Test
    public void shouldMigrateStartOfCommentIgnoringEscapedQuotes() {
        migrateCommentsAndAssertResult(
                "\"smtg \\\" # - should not be migrated\\\"\"",
                "\"smtg \\\" # - should not be migrated\\\"\"");
        migrateCommentsAndAssertResult(
                "'smtg \\\' # - should not be migrated\\\''",
                "'smtg \\\' # - should not be migrated\\\''");
    }

    @Test
    public void shouldMigrateStartOfCommentInMultilineInput() {
        String input = "#/ some DSL debug value\n" +
                "rule \"test rule\"\n" +
                "when\n" +
                "    Object(\"'#'this has sign should not be migrated\") # some comment\n" +
                "then\n" +
                "    # some other comment\n" +
                "    System.out.println(\"#8 this one neither\");\n" +
                "end\n";

        String expectedResult = "#/ some DSL debug value\n" +
                "rule \"test rule\"\n" +
                "when\n" +
                "    Object(\"'#'this has sign should not be migrated\") // some comment\n" +
                "then\n" +
                "    // some other comment\n" +
                "    System.out.println(\"#8 this one neither\");\n" +
                "end\n";
        migrateCommentsAndAssertResult(input, expectedResult);
    }

    private void migrateCommentsAndAssertResult(String input, String expectedResult) {
        String actualResult = ExportUtils.migrateStartOfCommentChar( input );
        Assert.assertEquals( "Start of comment char not correctly migrated (# -> //)!", expectedResult, actualResult );
    }
}
