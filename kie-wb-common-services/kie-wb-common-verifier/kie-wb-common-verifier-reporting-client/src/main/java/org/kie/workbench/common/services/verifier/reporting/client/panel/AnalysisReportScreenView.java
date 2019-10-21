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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.verifier.api.reporting.Issue;

public interface AnalysisReportScreenView
        extends IsWidget {

    void setUpDataProvider(final ListDataProvider<Issue> dataProvider);

    void setPresenter(final AnalysisReportScreen presenter);

    void showIssue(final Issue issue);

    void clearIssue();

    void showStatusComplete();

    void showStatusTitle(final int start,
                         final int end,
                         final int totalCheckCount);

    void hideProgressStatus();
}
