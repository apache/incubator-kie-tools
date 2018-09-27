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

import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.kie.workbench.common.services.verifier.reporting.client.reporting.ExplanationProvider;

public class AnalysisLineCell
        extends AbstractCell<Issue> {

    interface CellTemplate
            extends SafeHtmlTemplates {

        @Template("<div class=\"{0}\" style=\"margin-bottom: 1px; width:100%; height:100%; \">" +
                "<span class=\"{1}\"></span>" +
                "<span> - {2} - {3}</span>" +
                "</div>")
        SafeHtml text(final String cssStyleName,
                      final String icon,
                      final SafeHtml lineNumbers,
                      final String message);
    }

    private static final CellTemplate TEMPLATE = GWT.create(CellTemplate.class);

    @Override
    public void render(final Context context,
                       final Issue issue,
                       final SafeHtmlBuilder safeHtmlBuilder) {
        safeHtmlBuilder.append(TEMPLATE.text(getStyleName(issue.getSeverity()),
                                             getIcon(issue.getSeverity()),
                                             getLineNumbers(issue.getRowNumbers()),
                                             ExplanationProvider.toTitle(issue)));
    }

    private SafeHtml getLineNumbers(final Set<Integer> rowNumbers) {
        return (SafeHtml) () -> rowNumbers
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private String getStyleName(final Severity severity) {
        switch (severity) {
            case ERROR:
                return "alert-danger";
            case WARNING:
                return "alert-warning";
            default:
                return "alert-info";
        }
    }

    private String getIcon(final Severity severity) {
        switch (severity) {
            case ERROR:
                return "pficon pficon-error-circle-o btn-xs";
            case WARNING:
                return "pficon pficon-warning-triangle-o btn-xs";
            default:
                return "pficon pficon-info btn-xs";
        }
    }
}
