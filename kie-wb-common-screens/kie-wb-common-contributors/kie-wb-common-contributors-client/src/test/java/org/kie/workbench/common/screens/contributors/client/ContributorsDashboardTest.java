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
package org.kie.workbench.common.screens.contributors.client;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.renderer.client.selector.SelectorDisplayer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;
import org.kie.workbench.common.screens.contributors.client.screens.ContributorsKPIs;
import org.kie.workbench.common.screens.contributors.client.screens.ContributorsScreen;
import org.kie.workbench.common.screens.contributors.client.screens.ContributorsView;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.dashbuilder.dataset.Assertions.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsDashboardTest extends AbstractDisplayerTest {

    @Mock
    ContributorsView view;

    @Mock
    ContributorsI18n i18n;

    @Mock
    Event<NotificationEvent> workbenchNotification;

    @Mock
    DisplayerListener displayerListener;

    ContributorsScreen presenter;
    DataSet contributorsDataSet;
    DisplayerCoordinator displayerCoordinator;
    AbstractDisplayer commitsPerOrganization;
    AbstractDisplayer commitsEvolutionDisplayer;
    SelectorDisplayer organizationSelectorDisplayer;
    SelectorDisplayer repositorySelectorDisplayer;
    SelectorDisplayer authorSelectorDisplayer;
    SelectorDisplayer topAuthorSelectorDisplayer;
    AbstractDisplayer yearsSelectorDisplayer;
    AbstractDisplayer quarterSelectorDisplayer;
    AbstractDisplayer dayOfWeekSelectorDisplayer;
    AbstractDisplayer allCommitsDisplayer;

    public SelectorDisplayer createSelectorDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorDisplayer(mock(SelectorDisplayer.View.class)), settings);
    }

    @Before
    public void init() throws Exception {
        super.init();

        contributorsDataSet = ContributorsData.INSTANCE.toDataSet();
        contributorsDataSet.setUUID(ContributorsDataSets.GIT_CONTRIB);
        clientDataSetManager.registerDataSet(contributorsDataSet);

        commitsPerOrganization = createNewDisplayer(ContributorsKPIs.getCommitsPerOrganization(i18n));
        commitsEvolutionDisplayer = createNewDisplayer(ContributorsKPIs.getCommitsEvolution(i18n));
        organizationSelectorDisplayer = createSelectorDisplayer(ContributorsKPIs.getOrgUnitSelector(i18n));
        repositorySelectorDisplayer = createSelectorDisplayer(ContributorsKPIs.getRepoSelector(i18n));
        authorSelectorDisplayer = createSelectorDisplayer(ContributorsKPIs.getAuthorSelector(i18n));
        topAuthorSelectorDisplayer = createSelectorDisplayer(ContributorsKPIs.getTopAuthorSelector(i18n));
        yearsSelectorDisplayer = createNewDisplayer(ContributorsKPIs.getYears(i18n));
        quarterSelectorDisplayer = createNewDisplayer(ContributorsKPIs.getQuarters(i18n));
        dayOfWeekSelectorDisplayer = createNewDisplayer(ContributorsKPIs.getDaysOfWeek(i18n));
        allCommitsDisplayer = createNewDisplayer(ContributorsKPIs.getAllCommits(i18n));

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addListener(displayerListener);
        when(view.getI18nService()).thenReturn(i18n);

        presenter = new ContributorsScreen(view,
                commitsPerOrganization,
                commitsEvolutionDisplayer,
                organizationSelectorDisplayer,
                repositorySelectorDisplayer,
                authorSelectorDisplayer,
                topAuthorSelectorDisplayer,
                yearsSelectorDisplayer,
                quarterSelectorDisplayer,
                dayOfWeekSelectorDisplayer,
                allCommitsDisplayer,
                displayerCoordinator,
                workbenchNotification);
    }

    @Test
    public void testDrawAll() {
        verify(view).init(presenter,
                commitsPerOrganization,
                commitsEvolutionDisplayer,
                organizationSelectorDisplayer,
                repositorySelectorDisplayer,
                authorSelectorDisplayer,
                topAuthorSelectorDisplayer,
                yearsSelectorDisplayer,
                quarterSelectorDisplayer,
                dayOfWeekSelectorDisplayer,
                allCommitsDisplayer);


        verify(displayerListener).onDraw(commitsPerOrganization);
        verify(displayerListener).onDraw(commitsEvolutionDisplayer);
        verify(displayerListener).onDraw(organizationSelectorDisplayer);
        verify(displayerListener).onDraw(repositorySelectorDisplayer);
        verify(displayerListener).onDraw(authorSelectorDisplayer);
        verify(displayerListener).onDraw(topAuthorSelectorDisplayer);
        verify(displayerListener).onDraw(yearsSelectorDisplayer);
        verify(displayerListener).onDraw(quarterSelectorDisplayer);
        verify(displayerListener).onDraw(dayOfWeekSelectorDisplayer);
        verify(displayerListener).onDraw(allCommitsDisplayer);
    }

    @Test
    public void testCommitsPerOrganization() {
        Displayer displayer = presenter.getCommitsPerOrganization();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"org1", "2.00", "4.00", "org1", "4.00"},
                {"org2", "2.00", "4.00", "org2", "4.00"},
                {"emptyOrg", "1.00", "1.00", "emptyOrg", "1.00"}
        }, 0);
    }

    @Test
    public void testCommitsEvolution() {
        Displayer displayer = presenter.getCommitsEvolutionDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"2019-01", "1.00"},
                {"2019-02", "0.00"},
                {"2019-03", "1.00"},
                {"2019-04", "1.00"},
                {"2019-05", "0.00"},
                {"2019-06", "1.00"},
                {"2019-07", "1.00"},
                {"2019-08", "0.00"},
                {"2019-09", "1.00"},
                {"2019-10", "0.00"},
                {"2019-11", "1.00"},
                {"2019-12", "0.00"},
                {"2020-01", "0.00"},
                {"2020-02", "1.00"}
        }, 0);
    }

    @Test
    public void testOrganizationSelector() {
        SelectorDisplayer.View view = organizationSelectorDisplayer.getView();
        verify(view).addItem("0", "emptyOrg", false);
        verify(view).addItem("1", "org1", false);
        verify(view).addItem("2", "org2", false);
        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());
    }

    @Test
    public void testRepositorySelector() {
        SelectorDisplayer.View view = repositorySelectorDisplayer.getView();
        verify(view).addItem("1", "repo1", false);
        verify(view).addItem("2", "repo2", false);
        verify(view).addItem("3", "repo3", false);
        verify(view).addItem("4", "repo4", false);
        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());
    }

    @Test
    public void testAuthorSelector() {
        SelectorDisplayer.View view = authorSelectorDisplayer.getView();
        verify(view).addItem("1", "user1", false);
        verify(view).addItem("2", "user2", false);
        verify(view).addItem("3", "user3", false);
        verify(view).addItem("4", "user4", false);
        verify(view).addItem("5", "user5", false);
        verify(view).addItem("6", "user6", false);
        verify(view).addItem("7", "user7", false);
        verify(view).addItem("8", "user8", false);
        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());
    }

    @Test
    public void testTopAuthorSelector() {
        SelectorDisplayer.View view = topAuthorSelectorDisplayer.getView();
        verify(view).addItem("0", "user1", false);
        verify(view).addItem("1", "user2", false);
        verify(view).addItem("2", "user3", false);
        verify(view).addItem("3", "user4", false);
        verify(view).addItem("4", "user5", false);
        verify(view).addItem("5", "user6", false);
        verify(view).addItem("6", "user7", false);
        verify(view).addItem("7", "user8", false);
        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());
    }

    @Test
    public void testYearsSelector() {
        Displayer displayer = presenter.getYearsSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"2019", "7.00"},
                {"2020", "1.00"}
        }, 0);
    }

    @Test
    public void testQuarterSelector() {
        Displayer displayer = presenter.getQuarterSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"1", "3.00"},
                {"2", "2.00"},
                {"3", "2.00"},
                {"4", "1.00"}
        }, 0);
    }

    @Test
    public void testDayOfWeekSelector() {
        Displayer displayer = presenter.getDayOfWeekSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"1", "0.00"},
                {"2", "0.00"},
                {"3", "2.00"},
                {"4", "1.00"},
                {"5", "1.00"},
                {"6", "2.00"},
                {"7", "2.00"}
        }, 0);
    }

    @Test
    public void testAllCommits() {
        Displayer displayer = presenter.getAllCommitsDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"user1", "repo1", "01/01/19 12:00", "Commit 1"},
                {"user2", "repo1", "03/02/19 12:00", "Commit 2"},
                {"user3", "repo2", "04/03/19 12:00", "Commit 3"},
                {"user4", "repo2", "06/04/19 12:00", "Commit 4"},
                {"user5", "repo3", "07/05/19 12:00", "Commit 5"},
                {"user6", "repo3", "09/06/19 12:00", "Commit 6"},
                {"user7", "repo4", "11/07/19 12:00", "Commit 7"},
                {"user8", "repo4", "02/08/20 12:00", "Commit 8"},
                {"", "", "", ""}
        }, 0);
    }

    @Test
    public void test_BZ1255279_fix() {
        when(authorSelectorDisplayer.getView().getSelectedId()).thenReturn("1");
        authorSelectorDisplayer.onItemSelected();

        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet, new String[][]{
                {"user1", "repo1", "01/01/19 12:00", "Commit 1"},
        }, 0);
    }

    @Test
    public void testClickOnOrgUnit() throws Exception {
        commitsPerOrganization.filterUpdate(COLUMN_ORG, 0); // "org1" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 4);
    }

    @Test
    public void testSelectOrgUnit() throws Exception {
        organizationSelectorDisplayer.filterUpdate(COLUMN_ORG, 0); // "emptyOrg" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);
    }

    @Test
    public void testSelectRepo() throws Exception {
        repositorySelectorDisplayer.filterUpdate(COLUMN_REPO, 1); // "repo1" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 2);
    }

    @Test
    public void testSelectYear() throws Exception {
        yearsSelectorDisplayer.filterUpdate(COLUMN_DATE, 0); // "2019" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 7);
    }

    @Test
    public void testSelectWeekOfDay() throws Exception {
        dayOfWeekSelectorDisplayer.filterUpdate(COLUMN_DATE, 2); // "Tuesday" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 2);
    }

    @Test
    public void testAlwaysShow7Days() throws Exception {
        authorSelectorDisplayer.filterUpdate(COLUMN_AUTHOR, 1); // "user" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);

        // Bar chart must always show 7 bars, one per day of week
        dataSet = dayOfWeekSelectorDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 7);
    }

    @Test
    public void testSelectAuthorAndWeekOfDay() throws Exception {
        authorSelectorDisplayer.filterUpdate(COLUMN_AUTHOR, 1); // "user" selected
        DataSet dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);

        dayOfWeekSelectorDisplayer.filterUpdate(COLUMN_DATE, 2); // "Tuesday" selected
        dataSet = allCommitsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);
    }
}