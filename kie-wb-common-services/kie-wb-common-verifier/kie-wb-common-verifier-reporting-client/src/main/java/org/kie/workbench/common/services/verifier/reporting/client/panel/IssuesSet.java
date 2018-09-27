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
package org.kie.workbench.common.services.verifier.reporting.client.panel;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.kie.workbench.common.services.verifier.reporting.client.reporting.ExplanationProvider;

public class IssuesSet
        extends TreeSet<Issue> {

    private static Set<CheckType> MERGEABLE_ISSUES = EnumSet.of(
            CheckType.EMPTY_RULE,
            CheckType.MISSING_RESTRICTION,
            CheckType.MISSING_ACTION
    );

    public IssuesSet(List<Issue> issues) {
        super(new Comparator<Issue>() {
            @Override
            public int compare(final Issue issue,
                               final Issue other) {
                int compareToSeverity = issue.getSeverity()
                        .compareTo(other.getSeverity());

                if (compareToSeverity == 0) {
                    final String thisTitle = ExplanationProvider.toTitle(issue);
                    final String otherTitle = ExplanationProvider.toTitle(other);
                    int compareToTitle = thisTitle.compareTo(otherTitle);
                    if (compareToTitle == 0) {
                        return compareRowNumbers(issue.getRowNumbers(),
                                                 other.getRowNumbers());
                    } else {
                        return compareToTitle;
                    }
                } else {
                    return compareToSeverity;
                }
            }

            private int compareRowNumbers(final Set<Integer> rowNumbers,
                                          final Set<Integer> other) {
                if (rowNumbers.equals(other)) {
                    return 0;
                } else {
                    for (Integer a : rowNumbers) {
                        for (Integer b : other) {
                            if (a < b) {
                                return -1;
                            }
                        }
                    }
                    return 1;
                }
            }
        });

        addAll(MERGEABLE_ISSUES.stream()
                       .map(typeToMerge -> mergeIssues(issues, typeToMerge))
                       .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()));

        issues.stream()
                .filter(issue -> !MERGEABLE_ISSUES.contains(issue.getCheckType()))
                .forEach(issue -> add(issue));
    }

    private Optional<Issue> mergeIssues(final List<Issue> issues,
                                        final CheckType typeToMerge) {
        Set<Issue> issuesToMerge = issues.stream()
                .filter(issue -> issue.getCheckType() == typeToMerge)
                .collect(Collectors.toSet());

        Set<Integer> affectedRows = issuesToMerge.stream()
                .flatMap(issue -> issue.getRowNumbers().stream())
                .sorted()
                .collect(Collectors.toSet());

        return issuesToMerge.stream()
                .findFirst() // Will be Optional.empty() if no issue of "typeToMerge" was present
                .map(issue -> new Issue(issue.getSeverity(), typeToMerge, affectedRows));
    }
}
