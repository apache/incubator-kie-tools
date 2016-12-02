/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;

public class IssuesSet
        extends TreeSet<Issue> {

    public IssuesSet() {
        super( new Comparator<Issue>() {
            @Override
            public int compare( final Issue issue,
                                final Issue other ) {
                int compareToSeverity = issue.getSeverity()
                        .compareTo( other.getSeverity() );

                if ( compareToSeverity == 0 ) {
                    final String thisTitle = ExplanationProvider.toTitle( issue );
                    final String otherTitle = ExplanationProvider.toTitle( other );
                    int compareToTitle = thisTitle.compareTo( otherTitle );
                    if ( compareToTitle == 0 ) {
                        return compareRowNumbers( issue.getRowNumbers(),
                                                  other.getRowNumbers() );
                    } else {
                        return compareToTitle;
                    }
                } else {
                    return compareToSeverity;
                }

            }

            private int compareRowNumbers( final Set<Integer> rowNumbers,
                                           final Set<Integer> other ) {
                if ( rowNumbers.equals( other ) ) {
                    return 0;
                } else {
                    for ( Integer a : rowNumbers ) {
                        for ( Integer b : other ) {
                            if ( a < b ) {
                                return -1;
                            }
                        }
                    }
                    return 1;
                }
            }
        } );
    }
}
