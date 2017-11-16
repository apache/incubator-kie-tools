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
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import static org.dashbuilder.dataset.date.DayOfWeek.SUNDAY;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.DISTINCT;
import static org.dashbuilder.dataset.group.DateIntervalType.DAY_OF_WEEK;
import static org.dashbuilder.dataset.group.DateIntervalType.MONTH;
import static org.dashbuilder.dataset.group.DateIntervalType.QUARTER;
import static org.dashbuilder.dataset.group.DateIntervalType.YEAR;
import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.dashbuilder.displayer.Position.RIGHT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_AUTHOR;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_DATE;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_MSG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_ORG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_PROJECT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.GIT_CONTRIB;

@ApplicationScoped
public class OrgUnitsMetricsFactory {

    private TranslationService translationService;
    private DisplayerLocator displayerLocator;

    @Inject
    public OrgUnitsMetricsFactory(final TranslationService translationService,
                                  final DisplayerLocator displayerLocator) {
        this.translationService = translationService;
        this.displayerLocator = displayerLocator;
    }

    public Displayer lookupCommitsOverTimeDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsOverTimeSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerAuthorDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsPerAuthorSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerProjectDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsPerProjectSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByYearDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsByYearSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByQuarterDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsByQuarterSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByDayOfWeekDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildCommitsByDayOfWeekSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupProjectSelectorDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildProjectSelectorSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupTopContributorSelectorDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildTopContributorSelectorSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupDateSelectorDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildDateSelectorSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupAllCommitsDisplayer(final OrganizationalUnit organizationalUnit) {
        DisplayerSettings settings = buildAllCommitsSettings(organizationalUnit);
        return displayerLocator.lookupDisplayer(settings);
    }

    protected ColumnFilter createOrgUnitFilter(final OrganizationalUnit organizationalUnit) {
        return equalsTo(COLUMN_ORG,
                        organizationalUnit.getName());
    }

    public DisplayerSettings buildCommitsPerAuthorSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_PROJECT,
                        DISTINCT).format(translationService.getTranslation(LibraryConstants.Projects),
                                         "#,##0")
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_AUTHOR,
                        DISTINCT).format(translationService.getTranslation(LibraryConstants.Author),
                                         "#,##0")
                .titleVisible(false)
                .width(450).height(200)
                .margins(10,
                         40,
                         40,
                         0)
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfProjects))
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsOverTimeSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .group(COLUMN_DATE).dynamic(80,
                                            MONTH,
                                            true)
                .column(COLUMN_DATE,
                        translationService.getTranslation(LibraryConstants.Date))
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .titleVisible(false)
                .width(450).height(145)
                .margins(10,
                         5,
                         40,
                         0)
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsPerProjectSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .filter(COLUMN_PROJECT,
                        FilterFactory.notNull())
                .group(COLUMN_PROJECT)
                .column(COLUMN_PROJECT)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         0,
                         10,
                         5)
                .legendOn(RIGHT)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByYearSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .group(COLUMN_DATE).dynamic(YEAR,
                                            false)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .sort(COLUMN_DATE,
                      ASCENDING)
                .title(translationService.getTranslation(LibraryConstants.Years))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         0,
                         10,
                         5)
                .legendOn(RIGHT)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByQuarterSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .group(COLUMN_DATE).fixed(QUARTER,
                                          false)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .title(translationService.getTranslation(LibraryConstants.Quarters))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         0,
                         5,
                         5)
                .legendOn(RIGHT)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByDayOfWeekSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newBarChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK,
                                          true).firstDay(SUNDAY)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .title(translationService.getTranslation(LibraryConstants.DayOfWeek))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         40,
                         70,
                         0)
                .subType_Bar()
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildProjectSelectorSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .group(COLUMN_PROJECT)
                .column(COLUMN_PROJECT,
                        translationService.getTranslation(LibraryConstants.Project))
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .sort("#commits",
                      DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0,
                         0,
                         10,
                         0)
                .width(200)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildTopContributorSelectorSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.TopContributor))
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .sort("#commits",
                      DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0,
                         0,
                         10,
                         0)
                .width(200)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildDateSelectorSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .column(COLUMN_DATE)
                .format(translationService.getTranslation(LibraryConstants.Date),
                        "dd MMM, yyyy HH:mm")
                .subtype(DisplayerSubType.SELECTOR_SLIDER)
                .titleVisible(false)
                .width(420)
                .margins(0,
                         0,
                         20,
                         10)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildAllCommitsSettings(final OrganizationalUnit organizationalUnit) {
        return DisplayerSettingsFactory.newTableSettings()
                .dataset(GIT_CONTRIB)
                .filter(createOrgUnitFilter(organizationalUnit))
                .column(COLUMN_PROJECT,
                        translationService.getTranslation(LibraryConstants.Project))
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_DATE,
                        translationService.getTranslation(LibraryConstants.Date))
                .column(COLUMN_MSG,
                        translationService.getTranslation(LibraryConstants.Message))
                .sort(COLUMN_DATE,
                      DESCENDING)
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
