/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import static org.dashbuilder.dataset.date.DayOfWeek.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.displayer.Position.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.*;
import static org.dashbuilder.dataset.filter.FilterFactory.*;

@ApplicationScoped
public class ProjectMetricsFactory {

    private TranslationService translationService;
    private DisplayerLocator displayerLocator;

    @Inject
    public ProjectMetricsFactory(TranslationService translationService, DisplayerLocator displayerLocator) {
        this.translationService = translationService;
        this.displayerLocator = displayerLocator;
    }

    public Displayer lookupCommitsOverTimeDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsOverTimeSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsOverTimeDisplayer_small(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsOverTimeSettings(projectInfo);
        settings.setChartWidth(300);
        settings.setChartHeight(80);
        settings.setChartMarginTop(5);
        settings.setChartMarginBottom(5);
        settings.setTitleVisible(false);
        settings.setYAxisTitle(null);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerAuthorDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsPerAuthorSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByYearDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsByYearSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByQuarterDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsByQuarterSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByDayOfWeekDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildCommitsByDayOfWeekSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupTopContributorSelectorDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildTopContributorSelectorSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupDateSelectorDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildDateSelectorSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupAllCommitsDisplayer(ProjectInfo projectInfo) {
        DisplayerSettings settings = buildAllCommitsSettings(projectInfo);
        return displayerLocator.lookupDisplayer(settings);
    }

    protected ColumnFilter createProjectFilter(ProjectInfo projectInfo) {
        String repoAlias = projectInfo.getRepository().getAlias();
        String projectName = projectInfo.getProject().getProjectName();
        return AND(equalsTo(COLUMN_REPO, repoAlias), equalsTo(COLUMN_PROJECT, projectName));
    }

    public DisplayerSettings buildCommitsPerAuthorSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_AUTHOR, DISTINCT).format(translationService.getTranslation(LibraryConstants.Author), "#,##0")
                .titleVisible(false)
                .width(450).height(200)
                .margins(10, 40, 40, 0)
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsOverTimeSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_DATE).dynamic(80, MONTH, true)
                .column(COLUMN_DATE, translationService.getTranslation(LibraryConstants.Date))
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .titleVisible(false)
                .width(450).height(145)
                .margins(10, 5, 40, 0)
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByYearSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_DATE).dynamic(YEAR, false)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .sort(COLUMN_DATE, ASCENDING)
                .title(translationService.getTranslation(LibraryConstants.Years))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 0, 10, 5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByQuarterSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_DATE).fixed(QUARTER, false)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .title(translationService.getTranslation(LibraryConstants.Quarters))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 0, 5, 5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByDayOfWeekSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newBarChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK, true).firstDay(SUNDAY)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .title(translationService.getTranslation(LibraryConstants.DayOfWeek))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 40, 70, 0)
                .subType_Bar()
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildTopContributorSelectorSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(projectInfo))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.TopContributor))
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .sort("#commits", DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0, 0, 10, 0)
                .width(200)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildDateSelectorSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(projectInfo))
                .column(COLUMN_DATE).format(translationService.getTranslation(LibraryConstants.Date), "dd MMM, yyyy HH:mm")
                .subtype(DisplayerSubType.SELECTOR_SLIDER)
                .titleVisible(false)
                .width(420)
                .margins(0, 0, 20, 10)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildAllCommitsSettings(ProjectInfo projectInfo) {
        return DisplayerSettingsFactory.newTableSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(projectInfo))
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_DATE, translationService.getTranslation(LibraryConstants.Date))
                .column(COLUMN_MSG, translationService.getTranslation(LibraryConstants.Message))
                .sort(COLUMN_DATE, DESCENDING)
                .tablePageSize(5)
                .tableWidth(950)
                .tableOrderEnabled(true)
                .tableColumnPickerEnabled(true)
                .allowExcelExport(true)
                .allowCsvExport(true)
                .filterOff(true)
                .buildSettings();
    }
}
