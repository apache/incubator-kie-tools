package org.kie.workbench.common.screens.library.client.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    @Customizable
    private SettingsSections settingsSections;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private SavePopUpPresenter savePopUpPresenter;

    @Mock
    private SettingsPresenter.View view;

    @Mock
    private ProjectScreenService projectScreenService;

    @Mock
    private SectionManager<ProjectScreenModel> sectionManager;

    @Mock
    private LibraryPermissions libraryPermissions;

    private static final SyncPromises promises = new SyncPromises();

    private SettingsPresenter presenter;

    @Before
    public void before() {

        DomGlobal.console = new Console() {
            @Override
            public void info(final Object... objects) {
            }
        };

        final ObservablePath observablePath = mock(ObservablePath.class);
        doReturn(observablePath).when(observablePath).wrap(any());
        doReturn(observablePath).when(observablePaths).get();

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(projectContext).getActiveWorkspaceProject();
        doReturn(Optional.of(mock(Module.class))).when(projectContext).getActiveModule();
        doNothing().when(projectContext).updateProjectModule(any());
        presenter = spy(new SettingsPresenter(
                view,
                promises,
                notificationEvent,
                settingsSections,
                savePopUpPresenter,
                new CallerMock<>(projectScreenService),
                projectContext,
                observablePaths,
                conflictingRepositoriesPopup,
                sectionManager,
                libraryPermissions));
    }

    @Test
    public void testSetup() {
        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();
        final List<Section<ProjectScreenModel>> sections = Arrays.asList(section1, section2);

        doReturn(sections).when(settingsSections).getList();
        doReturn(promises.resolve()).when(presenter).setupUsingCurrentSection();

        presenter.setup();

        verify(presenter, times(1)).setupUsingCurrentSection();
        verify(sectionManager).init(eq(sections), any(), any());
    }

    @Test
    public void testSetupNoMainModule() {
        doReturn(Optional.empty()).when(projectContext).getActiveModule();

        presenter.setup();

        verify(view, never()).showBusyIndicator();
    }

    @Test
    public void setupUsingCurrentSection() {

        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(new ArrayList<>(singletonList(section))).when(sectionManager).getSections();
        doReturn(true).when(sectionManager).manages(any());
        doReturn(promises.resolve()).when(sectionManager).goToCurrentSection();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(true).when(libraryPermissions).userCanUpdateProject(any());

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(view).showBusyIndicator();
        verify(view, never()).disableActions();
        verify(projectScreenService).load(any());
        verify(presenter).setupSections(any());
        verify(sectionManager).goToCurrentSection();
        verify(view).hideBusyIndicator();

        verify(sectionManager, never()).goToFirstAvailable();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupUsingCurrentSectionWithoutPermission() {

        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(new ArrayList<>(singletonList(section))).when(sectionManager).getSections();
        doReturn(true).when(sectionManager).manages(any());
        doReturn(promises.resolve()).when(sectionManager).goToCurrentSection();
        doReturn(promises.resolve()).when(presenter).setupSections(any());
        doReturn(false).when(libraryPermissions).userCanUpdateProject(any());

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(view).showBusyIndicator();
        verify(view).disableActions();
        verify(projectScreenService).load(any());
        verify(presenter).setupSections(any());
        verify(sectionManager).goToCurrentSection();
        verify(view).hideBusyIndicator();

        verify(sectionManager, never()).goToFirstAvailable();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupUsingCurrentSectionWithRemovalDueToErrors() {

        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(new ArrayList<>(singletonList(section))).when(sectionManager).getSections();
        doReturn(false).when(sectionManager).manages(eq(section));
        doReturn(promises.resolve()).when(sectionManager).goToFirstAvailable();
        doReturn(promises.resolve()).when(presenter).setupSections(any());

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(view).showBusyIndicator();
        verify(projectScreenService).load(any());
        verify(presenter).setupSections(any());
        verify(sectionManager).goToFirstAvailable();
        verify(view).hideBusyIndicator();

        verify(sectionManager, never()).goToCurrentSection();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testSetupWithOneSectionSetupRejection() {

        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();

        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();
        doReturn(promises.reject("Test")).when(presenter).setupSections(any());

        presenter.setupUsingCurrentSection();

        verify(view).init(eq(presenter));
        verify(view).showBusyIndicator();
        verify(projectScreenService).load(any());
        verify(presenter).setupSections(any()); // All sections are setup regardless of exceptions/rejections
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSetupSections() {
        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();

        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();
        doReturn(promises.resolve()).when(presenter).setupSection(any(), any());

        presenter.setupSections(mock(ProjectScreenModel.class)).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(presenter, times(2)).setupSection(any(), any());
    }

    @Test
    public void testSetupSectionsWithEmptyListAfterSetup() {
        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();

        doReturn(true).when(sectionManager).isEmpty();
        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();

        presenter.setupSections(mock(ProjectScreenModel.class)).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        verify(presenter, times(2)).setupSection(any(), any());
    }

    @Test
    public void testSetupSection() {
        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(promises.resolve()).when(section).setup(any());

        presenter.setupSection(mock(ProjectScreenModel.class), section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(section).setup(any());
        verify(sectionManager).resetDirtyIndicator(section);
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testSetupSectionRejected() {
        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(promises.reject(section)).when(section).setup(any());
        doReturn("Message").when(presenter).getSectionSetupErrorMessage(eq(section));

        presenter.setupSection(mock(ProjectScreenModel.class), section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(section).setup(any());
        verify(sectionManager, never()).resetDirtyIndicator(section);
        verify(notificationEvent).fire(any());
        verify(sectionManager).remove(section);
    }

    @Test
    public void testShowSaveModal() {
        doReturn(true).when(libraryPermissions).userCanUpdateProject(any());
        doReturn(promises.resolve()).when(sectionManager).validateAll();

        presenter.showSaveModal();

        verify(savePopUpPresenter).show(any());
        verify(sectionManager, never()).goTo(any());
    }

    @Test
    public void testShowSaveModalWithoutPermission() {
        doReturn(false).when(libraryPermissions).userCanUpdateProject(any());
        doReturn(promises.resolve()).when(sectionManager).validateAll();

        presenter.showSaveModal();

        verify(savePopUpPresenter, never()).show(any());
        verify(sectionManager, never()).goTo(any());
    }

    @Test
    public void testShowSaveModalWithValidationError() {
        doReturn(true).when(libraryPermissions).userCanUpdateProject(any());
        Section<ProjectScreenModel> section = newMockedSection();
        doReturn(promises.reject(section)).when(sectionManager).validateAll();

        presenter.showSaveModal();

        verify(savePopUpPresenter, never()).show(any());
        verify(view).hideBusyIndicator();
        verify(sectionManager).goTo(section);
    }

    @Test
    public void testSave() {
        WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(project).when(projectScreenService).save(any(),
                                                          any(),
                                                          any(),
                                                          any());

        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();

        doReturn(promises.resolve()).when(section1).save(any(), any());
        doReturn(promises.resolve()).when(section2).save(any(), any());

        doReturn(promises.resolve()).when(sectionManager).resetAllDirtyIndicators();
        doReturn(promises.resolve()).when(presenter).saveProjectScreenModel(any(), any(), any());
        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();

        presenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2).save(eq("Test comment"), any());
        verify(presenter).saveProjectScreenModel(eq("Test comment"), eq(DeploymentMode.VALIDATED), any());
        verify(presenter).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionRejection() {
        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();

        doReturn(promises.reject(section1)).when(section1).save(any(), any());
        doReturn(promises.resolve()).when(section2).save(any(), any());
        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();

        presenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2, never()).save(any(), any());
        verify(sectionManager).goTo(eq(section1));
        verify(presenter, never()).saveProjectScreenModel(any(), any(), any());

        verify(sectionManager, never()).resetDirtyIndicator(any());
        verify(presenter, never()).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionException() {
        final Section<ProjectScreenModel> section1 = newMockedSection();
        final Section<ProjectScreenModel> section2 = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        doThrow(testException).when(section1).save(any(), any());
        doReturn(promises.resolve()).when(section2).save(any(), any());
        doReturn(new ArrayList<>(Arrays.asList(section1, section2))).when(sectionManager).getSections();

        presenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2, never()).save(any(), any());
        verify(presenter, never()).saveProjectScreenModel(any(), any(), any());
        verify(sectionManager, never()).resetDirtyIndicator(any());
        verify(presenter, never()).displaySuccessMessage();
    }

    @Test
    public void testDisplaySuccessMessage() {
        presenter.displaySuccessMessage().catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSaveProjectScreenModel() {

        WorkspaceProject project = mock(WorkspaceProject.class);
        Module module = mock(Module.class);
        doReturn(module).when(project).getMainModule();
        doReturn(project).when(projectScreenService).save(any(),
                                                          any(),
                                                          any(),
                                                          any());

        presenter.saveProjectScreenModel("Test comment",
                                         DeploymentMode.VALIDATED,
                                         null).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(projectScreenService).save(any(),
                                          any(),
                                          eq("Test comment"),
                                          eq(DeploymentMode.VALIDATED));
        verify(projectContext).updateProjectModule(module);
        verify(presenter,
               never()).handlePomConcurrentUpdate(any(),
                                                  any());
    }

    @Test
    public void testSaveProjectScreenModelWithLocallyDetectedConcurrentUpdate() {

        presenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doNothing().when(presenter).handlePomConcurrentUpdate(eq("Test comment"),
                                                              any());

        presenter.saveProjectScreenModel("Test comment",
                                         DeploymentMode.VALIDATED,
                                         null).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        verify(projectScreenService,
               never()).save(any(),
                             any(),
                             any(),
                             any());
        verify(presenter).handlePomConcurrentUpdate(eq("Test comment"),
                                                    any());
    }

    @Test
    public void testSaveProjectScreenModelThrowingException() {

        final RuntimeException testException = mock(RuntimeException.class);
        doThrow(testException).when(projectScreenService).save(any(),
                                                               any(),
                                                               any(),
                                                               any());

        presenter.saveProjectScreenModel("Test comment",
                                         DeploymentMode.VALIDATED,
                                         null).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(projectScreenService).save(any(),
                                          any(),
                                          eq("Test comment"),
                                          eq(DeploymentMode.VALIDATED));
        verify(presenter,
               never()).handlePomConcurrentUpdate(any(),
                                                  any());
    }

    @Test
    public void testHandlePomConcurrentUpdate() {

        presenter.model = mock(ProjectScreenModel.class);
        doReturn(mock(POM.class)).when(presenter.model).getPOM();

        presenter.handlePomConcurrentUpdate("Test comment", null, new GAVAlreadyExistsException()).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        verify(view).hideBusyIndicator();
        verify(conflictingRepositoriesPopup).setContent(any(), any(), any());
        verify(conflictingRepositoriesPopup).show();
    }

    @Test
    public void testForceSave() {
        WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(project).when(projectScreenService).save(any(), any(), any(), any());
        presenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);

        presenter.forceSave("Test comment", null);

        assertEquals(null, presenter.concurrentPomUpdateInfo);
        verify(conflictingRepositoriesPopup).hide();
        verify(presenter).saveProjectScreenModel(eq("Test comment"),
                                                 eq(DeploymentMode.FORCED),
                                                 any());
    }

    @Test
    public void testOnSettingsSectionChanged() {
        final Section<ProjectScreenModel> section = newMockedSection();
        doReturn(true).when(sectionManager).manages(eq(section));

        presenter.onSettingsSectionChanged(new SettingsSectionChange<>(section));

        verify(sectionManager).updateDirtyIndicator(eq(section));
    }

    @Test
    public void testResetWithPermission() {
        doReturn(true).when(libraryPermissions).userCanUpdateProject(any());
        doReturn(promises.resolve()).when(presenter).setupUsingCurrentSection();

        presenter.reset();

        verify(presenter).setupUsingCurrentSection();
    }

    @Test
    public void testResetWithoutPermission() {
        doReturn(false).when(libraryPermissions).userCanUpdateProject(any());
        doReturn(promises.resolve()).when(presenter).setupUsingCurrentSection();

        presenter.reset();

        verify(presenter, never()).setupUsingCurrentSection();
    }

    public static Section<ProjectScreenModel> newMockedSection() {
        final Section<ProjectScreenModel> section = mock(Section.class);
        doReturn(mock(MenuItem.class)).when(section).getMenuItem();
        doReturn(promises.resolve()).when(section).setup(any());
        doReturn(promises.resolve()).when(section).save(any(),
                                                        any());
        return section;
    }
}