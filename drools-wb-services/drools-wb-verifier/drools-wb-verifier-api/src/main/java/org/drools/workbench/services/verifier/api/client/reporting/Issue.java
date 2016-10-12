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

package org.drools.workbench.services.verifier.api.client.reporting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.workbench.services.verifier.api.client.cache.inspectors.RuleInspector;

public class Issue {

    public static final Issue EMPTY = new Issue();

    private final Severity severity;
    private final HashSet<RuleInspector> ruleInspectors = new HashSet<>();

    private final String              title;
    private final ExplanationProvider explanationProvider;

    public Issue( final Severity severity,
                  final String title,
                  final ExplanationProvider explanationProvider,
                  final RuleInspector... ruleInspectors ) {
        this.severity = severity;
        this.title = title;
        this.explanationProvider = explanationProvider;
        this.ruleInspectors.addAll( Arrays.asList( ruleInspectors ) );
    }

    private Issue() {
        severity = null;
        explanationProvider = null;
        title = null;
    }

    public boolean hasIssue() {
        return title != null;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Set<Integer> getRowNumbers() {
        Set<Integer> rowNumbers = new HashSet<>();

        for ( RuleInspector ruleInspector : ruleInspectors ) {
            rowNumbers.add( ruleInspector.getRowIndex() + 1 );
        }

        return rowNumbers;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }


    public SafeHtml getExplanationHTML() {
        return explanationProvider.toHTML();
    }
}
