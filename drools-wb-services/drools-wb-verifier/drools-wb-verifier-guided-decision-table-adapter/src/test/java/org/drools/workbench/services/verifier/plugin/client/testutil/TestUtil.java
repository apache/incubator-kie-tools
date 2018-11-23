/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client.testutil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.services.verifier.plugin.client.fromfile.DecisionTableAnalyzerFromFileTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestUtil {

    public static String loadResource(final String name) throws
            Exception {
        final InputStream in = DecisionTableAnalyzerFromFileTest.class.getResourceAsStream(name);
        final Reader reader = new InputStreamReader(in);
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[1024];
        int len = 0;
        while ((len = reader.read(buf)) >= 0) {
            text.append(buf,
                        0,
                        len);
        }
        return text.toString();
    }

    public static void assertOnlyContains(final Set<Issue> result,
                                          final CheckType... expected) {
        if (result.isEmpty()) {
            fail("Data is empty.");
        }

        Set<CheckType> resultTitles = result.stream()
                .map(issue -> issue.getCheckType())
                .collect(Collectors.toSet());

        assertThat(resultTitles).containsOnly(expected);
    }

    public static void assertResultIsEmpty(final Set<Issue> analysisReport) {

        final StringBuilder foundIssues = new StringBuilder();

        if (!analysisReport.isEmpty()) {
            analysisReport.stream().forEach(i -> foundIssues.append(i.getCheckType()).append(", "));
        }

        assertTrue(foundIssues.toString(),
                   analysisReport.isEmpty());
    }

    public static void assertContains(final Set<Issue> result,
                                      final CheckType expected,
                                      final Severity severity,
                                      final Integer... rowNumbers) {

        assertTrue("Could not find: " + expected + " for rows: " + rowNumbers + " and severity: " + severity,
                   result.stream()
                           .filter(issue -> issue.getSeverity() == severity)
                           .filter(issue -> titleEquals(issue,
                                                        expected))
                           .filter(issue -> rowNumberContains(issue,
                                                              Arrays.asList(rowNumbers)))
                           .count() > 0);
    }

    public static void assertDoesNotContain(final CheckType notExpected,
                                            final Set<Issue> result) {
        assertFalse("Found " + notExpected,
                    result.stream().filter(issue -> titleEquals(issue,
                                                                notExpected)).count() > 0);
    }

    public static void assertDoesNotContain(final CheckType notExpected,
                                            final Set<Issue> result,
                                            final int rowNumber) {

        assertFalse("Found " + notExpected,
                    result.stream()
                            .filter(issue -> titleEquals(issue,
                                                         notExpected))
                            .filter(issue -> rowNumberContains(issue,
                                                               Arrays.asList(rowNumber)))
                            .count() > 0);
    }

    private static boolean titleEquals(Issue issue,
                                       CheckType expected) {
        return issue.getCheckType().equals(expected);
    }

    private static boolean rowNumberContains(Issue issue,
                                             List<Integer> rowNumbers) {
        return issue.getRowNumbers().containsAll(rowNumbers);
    }
}
