/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.settings.SpaceScreenModel;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChangeType;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsScreenPresenterTest {

    private SettingsScreenPresenter presenter;

    @Mock
    private SettingsScreenPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private SectionManager<SpaceScreenModel> sectionManager;

    @Mock
    private SettingsSections settingsSections;

    private Promises promises;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private SettingsSectionChange<SpaceScreenModel> settingsSectionChangeEvent;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new SettingsScreenPresenter(view,
                                                    ts,
                                                    organizationalUnitController,
                                                    projectContext,
                                                    busyIndicatorView,
                                                    sectionManager,
                                                    settingsSections,
                                                    promises,
                                                    notificationEvent));

        doReturn(Optional.of(organizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
    }

    @Test
    public void postConstructTest() {
        final List<Section<SpaceScreenModel>> sections = Arrays.asList(createSectionMock(),
                                                                       createSectionMock());

        doReturn(sections).when(settingsSections).getList();
        doReturn(promises.resolve()).when(presenter).setupUsingCurrentSection();

        presenter.postConstruct();

        verify(sectionManager).init(eq(sections),
                                    any(),
                                    any());
    }

    @Test
    public void setupUsingCurrentSectionTest() {
        doReturn(new ArrayList<>(singletonList(createSectionMock()))).when(sectionManager).getSections();
        doReturn(true).when(sectionManager).manages(any());
        doReturn(promises.resolve()).when(sectionManager).goToCurrentSection();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(view).enableActions(eq(true));
        verify(presenter).setupSections(any());
        verify(sectionManager).goToCurrentSection();
        verify(busyIndicatorView).hideBusyIndicator();

        verify(sectionManager, never()).goToFirstAvailable();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupUsingCurrentSectionWithoutPermissionTest() {
        doReturn(new ArrayList<>(singletonList(createSectionMock()))).when(sectionManager).getSections();
        doReturn(true).when(sectionManager).manages(any());
        doReturn(promises.resolve()).when(sectionManager).goToCurrentSection();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(view).enableActions(eq(false));
        verify(presenter).setupSections(any());
        verify(sectionManager).goToCurrentSection();
        verify(busyIndicatorView).hideBusyIndicator();

        verify(sectionManager, never()).goToFirstAvailable();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupUsingCurrentSectionWithRemovalDueToErrorsTest() {
        final Section<SpaceScreenModel> section = createSectionMock();

        doReturn(new ArrayList<>(singletonList(section))).when(sectionManager).getSections();
        doReturn(false).when(sectionManager).manages(eq(section));
        doReturn(promises.resolve()).when(sectionManager).goToFirstAvailable();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(presenter).setupSections(any());
        verify(sectionManager).goToFirstAvailable();
        verify(busyIndicatorView).hideBusyIndicator();

        verify(sectionManager, never()).goToCurrentSection();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testSetupWithOneSectionSetupRejectionTest() {
        doReturn(new ArrayList<>(Arrays.asList(createSectionMock(),
                                               createSectionMock()))).when(sectionManager).getSections();
        doReturn(promises.reject("test")).when(presenter).setupSections(any());
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(presenter).setupSections(any());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void setupSectionsTest() {
        doReturn(new ArrayList<>(Arrays.asList(createSectionMock(),
                                               createSectionMock()))).when(sectionManager).getSections();
        doReturn(promises.resolve()).when(presenter).setupSection(any(), any());

        presenter.setupSections(mock(SpaceScreenModel.class)).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(presenter, times(2)).setupSection(any(), any());
    }

    @Test
    public void setupSectionsWithEmptyListAfterSetupTest() {
        doReturn(true).when(sectionManager).isEmpty();
        doReturn(new ArrayList<>(Arrays.asList(createSectionMock(),
                                               createSectionMock()))).when(sectionManager).getSections();

        presenter.setupSections(mock(SpaceScreenModel.class)).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        verify(presenter, times(2)).setupSection(any(), any());
    }

    @Test
    public void setupSectionTest() {
        final Section<SpaceScreenModel> section = createSectionMock();

        doReturn(promises.resolve()).when(section).setup(any());

        presenter.setupSection(mock(SpaceScreenModel.class), section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(section).setup(any());
        verify(sectionManager).resetDirtyIndicator(section);
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupSectionRejectedTest() {
        final Section<SpaceScreenModel> section = createSectionMock();

        doReturn(promises.reject(section)).when(section).setup(any());

        presenter.setupSection(mock(SpaceScreenModel.class), section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(section).setup(any());
        verify(sectionManager, never()).resetDirtyIndicator(section);
        verify(notificationEvent).fire(any());
        verify(sectionManager).remove(section);
    }

    @Test
    public void onSettingsSectionChangedWhenChangeTypeTest() {
        final Section<SpaceScreenModel> section = createSectionMock();

        doReturn(true).when(sectionManager).manages(eq(section));
        doReturn(SettingsSectionChangeType.CHANGE).when(settingsSectionChangeEvent).getType();
        doReturn(section).when(settingsSectionChangeEvent).getSection();

        presenter.onSettingsSectionChanged(settingsSectionChangeEvent);

        verify(sectionManager).updateDirtyIndicator(section);
    }

    @Test
    public void onSettingsSectionChangedWhenResetTypeTest() {
        final Section<SpaceScreenModel> section = createSectionMock();

        doReturn(true).when(sectionManager).manages(eq(section));
        doReturn(SettingsSectionChangeType.RESET).when(settingsSectionChangeEvent).getType();
        doReturn(section).when(settingsSectionChangeEvent).getSection();

        presenter.onSettingsSectionChanged(settingsSectionChangeEvent);

        verify(presenter).setupSection(any(),
                                       eq(section));
    }

    @Test
    public void saveWhenAllowedTest() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);
        doReturn(promises.resolve()).when(sectionManager).validateAll();

        final Section<SpaceScreenModel> section1 = createSectionMock();
        final Section<SpaceScreenModel> section2 = createSectionMock();

        doReturn(promises.resolve()).when(section1).save(any(),
                                                         any());
        doReturn(promises.resolve()).when(section2).save(any(),
                                                         any());

        doReturn(promises.resolve()).when(sectionManager).resetAllDirtyIndicators();
        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();

        presenter.save();

        verify(sectionManager, never()).goTo(any());
    }

    @Test
    public void saveWhenNotAllowedTest() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);
        doReturn(promises.resolve()).when(sectionManager).validateAll();

        presenter.save();

        verify(sectionManager, never()).goTo(any());
    }

    @Test
    public void saveWithValidationErrorTest() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);
        Section<SpaceScreenModel> section = createSectionMock();
        doReturn(promises.reject(section)).when(sectionManager).validateAll();

        presenter.save();

        verify(busyIndicatorView).hideBusyIndicator();
        verify(sectionManager).goTo(section);
    }

    @Test
    public void resetTest() {
        doReturn(new ArrayList<>(singletonList(createSectionMock()))).when(sectionManager).getSections();
        doReturn(true).when(sectionManager).manages(any());
        doReturn(promises.resolve()).when(sectionManager).goToCurrentSection();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.reset();

        verify(presenter).setupUsingCurrentSection();
    }

    private Section<SpaceScreenModel> createSectionMock() {
        final Section<SpaceScreenModel> section = mock(Section.class);
        doReturn(mock(MenuItem.class)).when(section).getMenuItem();
        doReturn(promises.resolve()).when(section).setup(any());
        doReturn(promises.resolve()).when(section).save(any(),
                                                        any());

        final SectionView view = mock(SectionView.class);
        doReturn("title").when(view).getTitle();
        doReturn(view).when(section).getView();

        return section;
    }
}