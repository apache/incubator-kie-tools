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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.drools.verifier.api.reporting.Issue;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.services.verifier.reporting.client.resources.i18n.AnalysisConstants;

@Dependent
@Templated
public class AnalysisReportScreenViewImpl
        extends Composite
        implements AnalysisReportScreenView,
                   RequiresResize {

    private AnalysisReportScreen presenter;

    @DataField("progressTooltip")
    Element progressTooltip = DOM.createDiv();

    @DataField("progressPanel")
    Element progressPanel = DOM.createDiv();

    @DataField("issuesList")
    CellList<Issue> issuesList = new CellList<>(new AnalysisLineCell());

    @DataField("issueDetailsView")
    Widget issueDetailsView;

    private IssuePresenter issueDetails;

    public AnalysisReportScreenViewImpl() {
    }

    @Inject
    public AnalysisReportScreenViewImpl(final IssuePresenter issuePresenter) {
        this.issueDetails = issuePresenter;
        issueDetailsView = issuePresenter.asWidget();
    }

    @PostConstruct
    private void init() {
        issuesList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        issuesList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        issuesList.setSelectionModel(getSelectionModel());
    }

    private SingleSelectionModel<Issue> getSelectionModel() {
        final SingleSelectionModel<Issue> selectionModel = new SingleSelectionModel<Issue>();

        selectionModel.addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
                    public void onSelectionChange(SelectionChangeEvent event) {
                        presenter.onSelect(selectionModel.getSelectedObject());
                    }
                });

        return selectionModel;
    }

    @Override
    public void setUpDataProvider(ListDataProvider<Issue> dataProvider) {
        dataProvider.addDataDisplay(issuesList);
    }

    @Override
    public void setPresenter(final AnalysisReportScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showIssue(Issue issue) {
        issueDetails.show(issue);
    }

    @Override
    public void clearIssue() {
        issueDetails.clear();
    }

    @Override
    public void showStatusComplete() {
        progressTooltip.getStyle().setVisibility(Style.Visibility.VISIBLE);
        progressPanel.getStyle().setColor("WHITE");
        progressPanel.getStyle().setBackgroundColor("GREEN");
        progressPanel.setInnerHTML(AnalysisConstants.INSTANCE.AnalysisComplete());
    }

    @Override
    public void showStatusTitle(final int start,
                                final int end,
                                final int totalCheckCount) {
        progressTooltip.getStyle().setVisibility(Style.Visibility.VISIBLE);
        progressPanel.getStyle().setColor("BLACK");
        progressPanel.getStyle().setBackgroundColor("#ffc");
        progressPanel.setInnerHTML(AnalysisConstants.INSTANCE.AnalysingChecks0To1Of2(start,
                                                                                     end,
                                                                                     totalCheckCount));
    }

    @Override
    public void hideProgressStatus() {
        progressTooltip.getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    @Override
    public void onResize() {
        setHeight(getParent().getOffsetHeight() + "px");
        setWidth(getWidth() + "px");
    }

    private int getWidth() {
        final int width = getParent().getOffsetWidth() - 15;
        if (width < 0) {
            return 0;
        } else {
            return width;
        }
    }
}
