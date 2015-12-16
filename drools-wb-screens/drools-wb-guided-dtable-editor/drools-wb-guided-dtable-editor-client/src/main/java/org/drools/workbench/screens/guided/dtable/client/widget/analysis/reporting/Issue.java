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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Issue
        implements Comparable<Issue> {

    private final Severity severity;
    private final TreeSet<Integer> rowNumbers = new TreeSet<Integer>();

    private final String title;
    private final Explanation explanation = new Explanation();

    public Issue( final Severity severity,
                  final String title,
                  final Integer... rowNumbers ) {
        this.severity = severity;
        this.title = title;
        this.rowNumbers.addAll( Arrays.asList( rowNumbers ) );
    }

    public Severity getSeverity() {
        return severity;
    }

    public Set<Integer> getRowNumbers() {
        return rowNumbers;
    }

    public String getTitle() {
        return title;
    }

    public Explanation getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo( final Issue issue ) {
        int compareToSeverity = severity.compareTo( issue.getSeverity() );

        if ( compareToSeverity == 0 ) {
            int compareToTitle = title.compareTo( issue.getTitle() );
            if ( compareToTitle == 0 ) {
                return compareRowNumbers( issue.getRowNumbers() );

            } else {
                return compareToTitle;
            }
        } else {
            return compareToSeverity;
        }

    }

    private int compareRowNumbers( final Set<Integer> rowNumbers ) {
        if ( this.rowNumbers.equals( rowNumbers ) ) {
            return 0;
        } else {
            for ( Integer a : this.rowNumbers ) {
                for ( Integer b : rowNumbers ) {
                    if ( a < b ) {
                        return -1;
                    }
                }
            }
            return 1;
        }
    }

}
