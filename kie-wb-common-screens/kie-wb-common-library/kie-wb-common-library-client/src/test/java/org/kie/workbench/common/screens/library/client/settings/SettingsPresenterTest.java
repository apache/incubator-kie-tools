package org.kie.workbench.common.screens.library.client.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.event.Event;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter.Section;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.file.Customizable;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private Event<NotificationEvent> notificationEvent;

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
    private SettingsPresenter.MenuItemsListPresenter menuItemsListPresenter;

    private final SyncPromises promises = new SyncPromises();

    private SettingsPresenter settingsPresenter;

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
        settingsPresenter = spy(new SettingsPresenter(
                view,
                promises,
                notificationEvent,
                settingsSections,
                savePopUpPresenter,
                new CallerMock<>(projectScreenService),
                projectContext,
                menuItemsListPresenter,
                observablePaths,
                conflictingRepositoriesPopup));
    }

    @Test
    public void testOnOpen() {
        doReturn(promises.resolve()).when(settingsPresenter).setup(any());

        settingsPresenter.onOpen();

        verify(view).hide();
        verify(view).show();
    }

    @Test
    public void testSetup() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();
        final List<Section> sections = Arrays.asList(section1,
                                                     section2);

        doReturn(sections).when(settingsSections).getList();
        doReturn(promises.resolve()).when(settingsPresenter).setup(section1);
        doNothing().when(settingsPresenter).setActiveMenuItem();
        settingsPresenter.setup();

        verify(settingsPresenter,
               times(1)).setActiveMenuItem();

        assertEquals(2,
                     settingsPresenter.sections.size());
        assertTrue(settingsPresenter.sections.containsAll(sections));
    }

    @Test
    public void testSetupWithSection() {

        final Section section = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(singletonList(section));
        doNothing().when(settingsPresenter).setupMenuItems();
        doReturn(promises.resolve()).when(settingsPresenter).setupSections(any());

        settingsPresenter.setup(section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        assertEquals(section,
                     settingsPresenter.currentSection);
        verify(view).init(eq(settingsPresenter));
        verify(projectScreenService).load(any());
        verify(settingsPresenter).setupSections(any());
        verify(settingsPresenter).setupMenuItems();
        verify(settingsPresenter).goTo(eq(section));
        verify(notificationEvent,
               never()).fire(any());
    }

    @Test
    public void testSetupWithOneSectionSetupRejection() {

        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));
        doReturn(promises.reject("Test")).when(settingsPresenter).setupSections(any());

        settingsPresenter.setup(section1).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        // All sections are setup regardless of exceptions/rejections
        verify(projectScreenService).load(any());
        verify(settingsPresenter).setupSections(any());
        verify(settingsPresenter,
               never()).setupMenuItems();
        verify(settingsPresenter,
               never()).goTo(any());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSetupSections() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        final List<Section> sections = new ArrayList<>(Arrays.asList(section1,
                                                                     section2));
        settingsPresenter.sections = sections;
        doReturn(promises.resolve()).when(settingsPresenter).setupSection(any(),
                                                                          any());

        settingsPresenter.setupSections(mock(ProjectScreenModel.class)).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        assertEquals(sections,
                     settingsPresenter.sections);
        verify(settingsPresenter,
               times(2)).setupSection(any(),
                                      any());
    }

    @Test
    public void testSetupSectionsWithOneError() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));

        doThrow(new RuntimeException("Test exception")).when(section1).setup(any());
        doReturn(promises.resolve()).when(section2).setup(any());

        settingsPresenter.setupSections(mock(ProjectScreenModel.class)).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        assertEquals(singletonList(section2),
                     settingsPresenter.sections);
        verify(settingsPresenter,
               times(2)).setupSection(any(),
                                      any());
    }

    @Test
    public void testSetupSectionsWithAllErrors() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));

        doThrow(new RuntimeException("Test exception")).when(section1).setup(any());
        doThrow(new RuntimeException("Test exception")).when(section2).setup(any());

        settingsPresenter.setupSections(mock(ProjectScreenModel.class)).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        assertTrue(settingsPresenter.sections.isEmpty());
        verify(settingsPresenter,
               times(2)).setupSection(any(),
                                      any());
    }

    @Test
    public void testSetupSection() {
        final Section section = newMockedSection();

        final List<Section> sections = new ArrayList<>(singletonList(section));
        settingsPresenter.sections = sections;
        doReturn(promises.resolve()).when(section).setup(any());

        settingsPresenter.setupSection(mock(ProjectScreenModel.class),
                                       section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        assertEquals(sections,
                     settingsPresenter.sections);
        verify(section).setup(any());
        verify(section.getMenuItem()).setup(any(),
                                            any());
        verify(settingsPresenter).resetDirtyIndicator(section);
        verify(notificationEvent,
               never()).fire(any());
    }

    @Test
    public void testSetupSectionRejected() {
        final Section section = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(singletonList(section));
        doReturn(promises.reject(section)).when(section).setup(any());
        doReturn("Message").when(settingsPresenter).getSectionSetupErrorMessage(eq(section));

        settingsPresenter.setupSection(mock(ProjectScreenModel.class),
                                       section).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        assertFalse(settingsPresenter.sections.contains(section));
        assertTrue(settingsPresenter.sections.isEmpty());
        verify(section).setup(any());
        verify(notificationEvent).fire(any());
        verify(section.getMenuItem(),
               never()).setup(any(),
                              any());
        verify(settingsPresenter,
               never()).resetDirtyIndicator(section);
    }

    @Test
    public void testShowSaveModal() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));
        doReturn(promises.resolve()).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2).validate();
        verify(savePopUpPresenter).show(any());
        verify(settingsPresenter,
               never()).goTo(any());
    }

    @Test
    public void testShowSaveModalWithValidationError() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));
        doReturn(promises.reject(section1)).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2,
               never()).validate();
        verify(view).hideBusyIndicator();
        verify(savePopUpPresenter,
               never()).show(any());
        verify(settingsPresenter).goTo(section1);
    }

    @Test
    public void testShowSaveModalWithValidationException() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));
        doThrow(testException).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2,
               never()).validate();
        verify(view,
               never()).hideBusyIndicator();
        verify(savePopUpPresenter,
               never()).show(any());
        verify(settingsPresenter,
               never()).goTo(section1);
        verify(settingsPresenter).defaultErrorResolution(testException);
    }

    @Test
    public void testSave() {
        WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(project).when(projectScreenService).save(any(),
                                                          any(),
                                                          any(),
                                                          any());

        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        doReturn(promises.resolve()).when(settingsPresenter).resetDirtyIndicator(eq(section1));
        doReturn(promises.resolve()).when(settingsPresenter).resetDirtyIndicator(eq(section2));

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"),
                              any());
        verify(section2).save(eq("Test comment"),
                              any());
        verify(settingsPresenter).saveProjectScreenModel(eq("Test comment"),
                                                         eq(DeploymentMode.VALIDATED),
                                                         any());
        verify(settingsPresenter).resetDirtyIndicator(eq(section1));
        verify(settingsPresenter).resetDirtyIndicator(eq(section2));
        verify(settingsPresenter).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionRejection() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        doReturn(promises.reject(section1)).when(section1).save(any(),
                                                                any());
        doReturn(promises.resolve()).when(section2).save(any(),
                                                         any());

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"),
                              any());
        verify(section2,
               never()).save(any(),
                             any());
        verify(settingsPresenter).goTo(eq(section1));
        verify(settingsPresenter,
               never()).saveProjectScreenModel(any(),
                                               any(),
                                               any());
        verify(settingsPresenter,
               never()).resetDirtyIndicator(any());
        verify(settingsPresenter,
               never()).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionException() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        doThrow(testException).when(section1).save(any(),
                                                   any());
        doReturn(promises.resolve()).when(section2).save(any(),
                                                         any());
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(testException);

        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section1,
                                                                   section2));

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"),
                              any());
        verify(section2,
               never()).save(any(),
                             any());
        verify(settingsPresenter).defaultErrorResolution(eq(testException));
        verify(settingsPresenter,
               never()).saveProjectScreenModel(any(),
                                               any(),
                                               any());
        verify(settingsPresenter,
               never()).resetDirtyIndicator(any());
        verify(settingsPresenter,
               never()).displaySuccessMessage();
    }

    @Test
    public void testDisplaySuccessMessage() {
        settingsPresenter.displaySuccessMessage().catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testResetDirtyIndicator() {
        final Map<Section, Integer> hashes = new HashMap<>();
        final Section section = newMockedSection();

        doReturn(42).when(section).currentHashCode();

        settingsPresenter.originalHashCodes = hashes;
        settingsPresenter.resetDirtyIndicator(section);

        assertEquals((Integer) 42,
                     hashes.get(section));
        verify(settingsPresenter).updateDirtyIndicator(eq(section));
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

        settingsPresenter.saveProjectScreenModel("Test comment",
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
        verify(settingsPresenter,
               never()).handlePomConcurrentUpdate(any(),
                                                  any());
        verify(settingsPresenter,
               never()).defaultErrorResolution(any());
        verify(settingsPresenter,
               never()).handleSaveProjectScreenModelError(any(),
                                                          any(),
                                                          any());
    }

    @Test
    public void testSaveProjectScreenModelWithLocallyDetectedConcurrentUpdate() {

        settingsPresenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doNothing().when(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"),
                                                                      any());

        settingsPresenter.saveProjectScreenModel("Test comment",
                                                 DeploymentMode.VALIDATED,
                                                 null).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });
        ;

        verify(projectScreenService,
               never()).save(any(),
                             any(),
                             any(),
                             any());
        verify(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"),
                                                            any());
        verify(settingsPresenter,
               never()).defaultErrorResolution(any());
        verify(settingsPresenter,
               never()).handleSaveProjectScreenModelError(any(),
                                                          any(),
                                                          any());
    }

    @Test
    public void testSaveProjectScreenModelThrowingException() {

        final RuntimeException testException = mock(RuntimeException.class);
        doThrow(testException).when(projectScreenService).save(any(),
                                                               any(),
                                                               any(),
                                                               any());
        doReturn(promises.resolve()).when(settingsPresenter).handleSaveProjectScreenModelError(any(),
                                                                                               any(),
                                                                                               any());

        settingsPresenter.saveProjectScreenModel("Test comment",
                                                 DeploymentMode.VALIDATED,
                                                 null).catch_(i -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(projectScreenService).save(any(),
                                          any(),
                                          eq("Test comment"),
                                          eq(DeploymentMode.VALIDATED));
        verify(settingsPresenter,
               never()).handlePomConcurrentUpdate(any(),
                                                  any());
        verify(settingsPresenter,
               never()).defaultErrorResolution(any());
        verify(settingsPresenter).handleSaveProjectScreenModelError(any(),
                                                                    any(),
                                                                    any());
    }

    @Test
    public void testHandleSaveProjectScreenModelAnyException() {
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());
        doReturn(promises.resolve()).when(settingsPresenter).handlePomConcurrentUpdate(any(),
                                                                                       any(),
                                                                                       any());

        final RuntimeException testException = new RuntimeException();
        settingsPresenter.handleSaveProjectScreenModelError("Test comment",
                                                            null,
                                                            testException);

        verify(settingsPresenter).defaultErrorResolution(eq(testException));
        verify(settingsPresenter,
               never()).handlePomConcurrentUpdate(any(),
                                                  any(),
                                                  any());
    }

    @Test
    public void testHandleSaveProjectScreenModelGavAlreadyExistsException() {
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());
        doReturn(promises.resolve()).when(settingsPresenter).handlePomConcurrentUpdate(any(),
                                                                                       any(),
                                                                                       any());

        final GAVAlreadyExistsException testException = new GAVAlreadyExistsException();
        settingsPresenter.handleSaveProjectScreenModelError("Test comment",
                                                            null,
                                                            testException);

        verify(settingsPresenter,
               never()).defaultErrorResolution(any());
        verify(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"),
                                                            any(),
                                                            eq(testException));
    }

    @Test
    public void testHandlePomConcurrentUpdate() {

        settingsPresenter.currentSection = newMockedSection();
        settingsPresenter.model = mock(ProjectScreenModel.class);
        doReturn(mock(POM.class)).when(settingsPresenter.model).getPOM();

        settingsPresenter.handlePomConcurrentUpdate("Test comment",
                                                    null,
                                                    new GAVAlreadyExistsException()).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });

        verify(view).hideBusyIndicator();
        verify(conflictingRepositoriesPopup).setContent(any(),
                                                        any(),
                                                        any());
        verify(conflictingRepositoriesPopup).show();
    }

    @Test
    public void testForceSave() {
        WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(project).when(projectScreenService).save(any(),
                                                          any(),
                                                          any(),
                                                          any());
        settingsPresenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);

        settingsPresenter.forceSave("Test comment",
                                    null);

        assertEquals(null,
                     settingsPresenter.concurrentPomUpdateInfo);
        verify(conflictingRepositoriesPopup).hide();
        verify(settingsPresenter).saveProjectScreenModel(eq("Test comment"),
                                                         eq(DeploymentMode.FORCED),
                                                         any());
    }

    @Test
    public void testOnSettingsSectionChanged() {
        final Section section = newMockedSection();
        settingsPresenter.originalHashCodes = new HashMap<>();

        settingsPresenter.onSettingsSectionChanged(new SettingsSectionChange(section));

        verify(settingsPresenter).updateDirtyIndicator(eq(section));
    }

    @Test
    public void testUpdateDirtyIndicatorNonexistentSection() {
        final Section section = newMockedSection();
        settingsPresenter.originalHashCodes = new HashMap<>();

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentDirtySection() {
        final Section section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section));

        settingsPresenter.originalHashCodes = new HashMap<>();
        settingsPresenter.originalHashCodes.put(section,
                                                32);

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(true);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentNotDirtySection() {
        final Section section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        settingsPresenter.sections = new ArrayList<>(Arrays.asList(section));

        settingsPresenter.originalHashCodes = new HashMap<>();
        settingsPresenter.originalHashCodes.put(section,
                                                42);

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }

    @Test
    public void testGoTo() {
        final Section section = newMockedSection();

        settingsPresenter.goTo(section);

        assertEquals(section,
                     settingsPresenter.currentSection);
        verify(view).setSection(eq(section.getView()));
    }

    private Section newMockedSection() {
        final Section section = mock(Section.class);
        doReturn(mock(SettingsPresenter.MenuItem.class)).when(section).getMenuItem();
        doReturn(promises.resolve()).when(section).setup(any());
        doReturn(promises.resolve()).when(section).save(any(),
                                                        any());
        return section;
    }
}