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
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;
import org.kie.workbench.common.screens.contributors.client.screens.ContributorsScreen;
import org.kie.workbench.common.screens.contributors.client.screens.ContributorsView;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;
import static org.dashbuilder.dataset.Assertions.*;

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

    public void registerContributorsDataset() throws Exception {
        contributorsDataSet = ContributorsData.INSTANCE.toDataSet();
        contributorsDataSet.setUUID(ContributorsDataSets.GIT_CONTRIB);
        clientDataSetManager.registerDataSet(contributorsDataSet);
    }

    @Before
    public void init() throws Exception {
        super.init();
        registerContributorsDataset();
        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addListener(displayerListener);
        when(view.getI18nService()).thenReturn(i18n);
        presenter = new ContributorsScreen(view, displayerLocator, displayerCoordinator, workbenchNotification);
    }

    @Test
    public void testDrawAll() {
        verify(view).init(presenter,
                presenter.getCommitsPerOrganization(),
                presenter.getCommitsEvolutionDisplayer(),
                presenter.getOrganizationSelectorDisplayer(),
                presenter.getRepositorySelectorDisplayer(),
                presenter.getAuthorSelectorDisplayer(),
                presenter.getTopAuthorSelectorDisplayer(),
                presenter.getYearsSelectorDisplayer(),
                presenter.getQuarterSelectorDisplayer(),
                presenter.getDayOfWeekSelectorDisplayer(),
                presenter.getAllCommitsDisplayer());


        verify(displayerListener).onDraw(presenter.getCommitsPerOrganization());
        verify(displayerListener).onDraw(presenter.getCommitsEvolutionDisplayer());
        verify(displayerListener).onDraw(presenter.getOrganizationSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getRepositorySelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getAuthorSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getTopAuthorSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getYearsSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getQuarterSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getDayOfWeekSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getAllCommitsDisplayer());
    }

    @Test
    public void testCommitsPerOrganization() {
        Displayer displayer = presenter.getCommitsPerOrganization();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"org1", "2.00", "4.00", "org1", "4.00"},
                {"org2", "2.00", "4.00", "org2", "4.00"}
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
        Displayer displayer = presenter.getOrganizationSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"org1", "4.00"},
                {"org2", "4.00"}
        }, 0);
    }

    @Test
    public void testRepositorySelector() {
        Displayer displayer = presenter.getRepositorySelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"repo1", "2.00"},
                {"repo2", "2.00"},
                {"repo3", "2.00"},
                {"repo4", "2.00"}
        }, 0);
    }

    @Test
    public void testAuthorSelector() {
        Displayer displayer = presenter.getAuthorSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"user1", "1.00"},
                {"user2", "1.00"},
                {"user3", "1.00"},
                {"user4", "1.00"},
                {"user5", "1.00"},
                {"user6", "1.00"},
                {"user7", "1.00"},
                {"user8", "1.00"}
        }, 0);
    }

    @Test
    public void testTopAuthorSelector() {
        Displayer displayer = presenter.getTopAuthorSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"user1", "1.00"},
                {"user2", "1.00"},
                {"user3", "1.00"},
                {"user4", "1.00"},
                {"user5", "1.00"},
                {"user6", "1.00"},
                {"user7", "1.00"},
                {"user8", "1.00"}
        }, 0);
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
                {"user8", "repo4", "02/08/20 12:00", "Commit 8"}
        }, 0);
    }
}