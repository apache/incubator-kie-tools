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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.verifier.api.reporting.Issue;
import org.kie.workbench.common.services.verifier.reporting.client.reporting.ExplanationProvider;
import org.uberfire.mvp.PlaceRequest;

public class AnalysisReport {

    private final ArrayList<Issue> issues = new ArrayList<>();
    private final PlaceRequest place;

    public AnalysisReport(final PlaceRequest place) {
        this.place = place;
    }

    public AnalysisReport(final PlaceRequest place,
                          final Set<Issue> issues) {
        this(place);
        setIssues(issues);
    }

    public void setIssues(final Set<Issue> issues) {
        this.issues.addAll(issues);
    }

    public PlaceRequest getPlace() {
        return place;
    }

    public List<Issue> getAnalysisData() {
        return issues;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("AnalysisReport: ");
        builder.append("\n");

        if (issues.isEmpty()) {
            builder.append("No issues.");
            builder.append("\n");
        } else {
            for (Issue issue : issues) {
                builder.append(ExplanationProvider.toHTML(issue));
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
