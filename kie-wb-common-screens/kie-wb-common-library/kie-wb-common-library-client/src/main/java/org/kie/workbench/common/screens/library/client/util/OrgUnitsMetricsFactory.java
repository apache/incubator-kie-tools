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

import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import static org.dashbuilder.dataset.date.DayOfWeek.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.displayer.Position.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.*;

@ApplicationScoped
public class OrgUnitsMetricsFactory {

    private TranslationService translationService;
    private TranslationUtils translationUtils;
    private DisplayerLocator displayerLocator;

    @Inject
    public OrgUnitsMetricsFactory(TranslationService translationService,
                                  TranslationUtils translationUtils,
                                  DisplayerLocator displayerLocator) {
        this.translationService = translationService;
        this.translationUtils = translationUtils;
        this.displayerLocator = displayerLocator;
    }

    public Displayer lookupCommitsOverTimeDisplayer() {
        DisplayerSettings settings = buildCommitsOverTimeSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsOverTimeDisplayer_small() {
        DisplayerSettings settings = buildCommitsOverTimeSettings();
        settings.setChartWidth(300);
        settings.setChartHeight(80);
        settings.setChartMarginTop(5);
        settings.setChartMarginBottom(5);
        settings.setTitleVisible(false);
        settings.setYAxisTitle(null);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerAuthorDisplayer() {
        DisplayerSettings settings = buildCommitsPerAuthorSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerOrgUnitDisplayer() {
        DisplayerSettings settings = buildCommitsPerOrgUnitSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerProjectDisplayer() {
        DisplayerSettings settings = buildCommitsPerProjectSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByYearDisplayer() {
        DisplayerSettings settings = buildCommitsByYearSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByQuarterDisplayer() {
        DisplayerSettings settings = buildCommitsByQuarterSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByDayOfWeekDisplayer() {
        DisplayerSettings settings = buildCommitsByDayOfWeekSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupOrgUnitSelectorDisplayer() {
        DisplayerSettings settings = buildOrgUnitSelectorSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupProjectSelectorDisplayer() {
        DisplayerSettings settings = buildProjectSelectorSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupTopContributorSelectorDisplayer() {
        DisplayerSettings settings = buildTopContributorSelectorSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupDateSelectorDisplayer() {
        DisplayerSettings settings = buildDateSelectorSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupAllCommitsDisplayer() {
        DisplayerSettings settings = buildAllCommitsSettings();
        return displayerLocator.lookupDisplayer(settings);
    }

    public DisplayerSettings buildCommitsPerAuthorSettings() {
        return DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_PROJECT, DISTINCT).format(translationService.getTranslation(LibraryConstants.Projects), "#,##0")
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_ORG, DISTINCT).format(translationUtils.getOrganizationalUnitAliasInPlural(), "#,##0")
                .titleVisible(false)
                .width(450).height(200)
                .margins(10, 40, 40, 0)
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfProjects))
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsOverTimeSettings() {
        return DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(GIT_CONTRIB)
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

    public DisplayerSettings buildCommitsPerOrgUnitSettings() {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_ORG, FilterFactory.notNull())
                .group(COLUMN_ORG)
                .column(COLUMN_ORG)
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 0, 10, 5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsPerProjectSettings() {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_PROJECT, FilterFactory.notNull())
                .group(COLUMN_PROJECT)
                .column(COLUMN_PROJECT)
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 0, 10, 5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByYearSettings() {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
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

    public DisplayerSettings buildCommitsByQuarterSettings() {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
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

    public DisplayerSettings buildCommitsByDayOfWeekSettings() {
        return DisplayerSettingsFactory.newBarChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
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

    public DisplayerSettings buildOrgUnitSelectorSettings() {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_ORG)
                .column(COLUMN_ORG, translationUtils.getOrganizationalUnitAliasInSingular())
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .sort("#commits", DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0, 0, 10, 0)
                .width(200)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildProjectSelectorSettings() {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_PROJECT)
                .column(COLUMN_PROJECT, translationService.getTranslation(LibraryConstants.Project))
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .sort("#commits", DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0, 0, 10, 0)
                .width(200)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildTopContributorSelectorSettings() {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
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

    public DisplayerSettings buildDateSelectorSettings() {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .column(COLUMN_DATE).format(translationService.getTranslation(LibraryConstants.Date), "dd MMM, yyyy HH:mm")
                .subtype(DisplayerSubType.SELECTOR_SLIDER)
                .titleVisible(false)
                .width(420)
                .margins(0, 0, 20, 10)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildAllCommitsSettings() {
        return DisplayerSettingsFactory.newTableSettings()
                .dataset(GIT_CONTRIB)
                .column(COLUMN_ORG, translationUtils.getOrganizationalUnitAliasInSingular())
                .column(COLUMN_PROJECT, translationService.getTranslation(LibraryConstants.Project))
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
