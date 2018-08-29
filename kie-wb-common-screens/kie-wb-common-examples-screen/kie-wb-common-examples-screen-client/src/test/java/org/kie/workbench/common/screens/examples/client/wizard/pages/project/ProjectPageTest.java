/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.client.wizard.pages.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageSelectedEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectPageTest {

    private static final String EXAMPLE_REPOSITORY = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    @Mock
    private ProjectPageView projectsView;

    @Mock
    private NoRepositoryURLView noRepositoryURLView;

    @Mock
    private FetchRepositoryView fetchingRepositoryView;

    @Mock
    private Widget projectsViewWidget;

    @Mock
    private Widget noRepositoryURLViewWidget;

    @Mock
    private Widget fetchingRepositoryViewWidget;

    @Spy
    private Event<WizardPageSelectedEvent> pageSelectedEvent = new EventSourceMock<WizardPageSelectedEvent>() {
        @Override
        public void fire(final WizardPageSelectedEvent event) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Spy
    private Event<WizardPageStatusChangeEvent> pageStatusChangedEvent = new EventSourceMock<WizardPageStatusChangeEvent>() {
        @Override
        public void fire(final WizardPageStatusChangeEvent event) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Mock
    private TranslationService translator;

    private ExamplesService examplesService = mock(ExamplesService.class);
    private Caller<ExamplesService> examplesServiceCaller = new CallerMock<ExamplesService>(examplesService);

    @Captor
    private ArgumentCaptor<List<ImportProject>> projectsArgumentCaptor;

    private ProjectPage page;

    private ExamplesWizardModel model;

    private ImportProject project1 = new ImportProject(mock(Path.class),
                                                       "project1",
                                                       "",
                                                       EXAMPLE_REPOSITORY,
                                                       Arrays.asList("tag1",
                                                                       "tag2"));
    private ImportProject project2 = new ImportProject(mock(Path.class),
                                                       "project2",
                                                       "",
                                                       EXAMPLE_REPOSITORY,
                                                       Arrays.asList("tag2",
                                                                       "tag3"));

    @Before
    public void setup() {
        page = new ProjectPage(projectsView,
                               noRepositoryURLView,
                               fetchingRepositoryView,
                               pageSelectedEvent,
                               pageStatusChangedEvent,
                               translator,
                               examplesServiceCaller);

        model = new ExamplesWizardModel();
        page.setModel(model);

        when(projectsView.asWidget()).thenReturn(projectsViewWidget);
        when(noRepositoryURLView.asWidget()).thenReturn(noRepositoryURLViewWidget);
        when(fetchingRepositoryView.asWidget()).thenReturn(fetchingRepositoryViewWidget);
    }

    @Test
    public void testInit() {
        page.init();
        verify(projectsView,
               times(1)).init(eq(page));
    }

    @Test
    public void testInitialise() {
        page.initialise();
        verify(projectsView,
               times(1)).initialise();
    }

    @Test
    public void testDestroy() {
        page.destroy();
        verify(projectsView,
               times(1)).destroy();
    }

    @Test
    public void testPrepareView_NoRepositorySelected() {
        model.setSelectedRepository(null);
        page.prepareView();

        assertEquals(noRepositoryURLViewWidget,
                     page.asWidget());
    }

    @Test
    public void testPrepareView_InvalidRepositorySelected() {
        final ExampleRepository repository = new ExampleRepository("cheese");
        repository.setUrlValid(false);
        model.setSelectedRepository(repository);
        page.prepareView();

        assertEquals(noRepositoryURLViewWidget,
                     page.asWidget());
    }

    @Test
    public void testPrepareView_SameRepositorySelected() {
        final ExampleRepository repository = new ExampleRepository(EXAMPLE_REPOSITORY);
        model.setSourceRepository(repository);
        model.setSelectedRepository(repository);
        page.prepareView();

        assertEquals(projectsViewWidget,
                     page.asWidget());
    }

    @Test
    public void testPrepareView_NewRepositorySelected() {
        when(examplesService.getProjects(any(ExampleRepository.class))).thenReturn(new HashSet<ImportProject>() {{
            add(project1);
            add(project2);
        }});

        final ExampleRepository repository = new ExampleRepository(EXAMPLE_REPOSITORY);
        model.setSelectedRepository(repository);
        page.prepareView();

        assertEquals(projectsViewWidget,
                     page.asWidget());
        assertEquals(repository,
                     model.getSourceRepository());
        assertTrue(model.getProjects().isEmpty());

        verify(projectsView,
               times(1)).setProjectsInRepository(projectsArgumentCaptor.capture());
        final List<ImportProject> sortedProjects = projectsArgumentCaptor.getValue();
        assertNotNull(sortedProjects);
        assertEquals(2,
                     sortedProjects.size());
        assertEquals("project1",
                     sortedProjects.get(0).getName());
        assertEquals("project2",
                     sortedProjects.get(1).getName());

        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_NoSelectedProjects() {
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_SelectedProjects() {
        model.addProject(new ImportProject(mock(Path.class),
                                           "",
                                           "",
                                           EXAMPLE_REPOSITORY,
                                           Collections.EMPTY_LIST));
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(true));
    }

    @Test
    public void testAddProject() {
        page.addProject(mock(ImportProject.class));
        assertEquals(1,
                     model.getProjects().size());
        verify(pageStatusChangedEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testRemoveProject() {
        final ImportProject project = mock(ImportProject.class);
        model.addProject(project);
        page.removeProject(project);
        assertEquals(0,
                     model.getProjects().size());
        verify(pageStatusChangedEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testIsProjectSelected_Selected() {
        final ImportProject project = mock(ImportProject.class);
        model.addProject(project);

        assertTrue(page.isProjectSelected(project));
    }

    @Test
    public void testIsProjectSelected_NotSelected() {
        final ImportProject project = mock(ImportProject.class);

        assertFalse(page.isProjectSelected(project));
    }

    @Test
    public void testAddTag_SingleTag() {
        initExampleProjects();

        page.addTag("tag1");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    private void initExampleProjects() {
        when(examplesService.getProjects(any(ExampleRepository.class))).thenReturn(new HashSet<ImportProject>() {{
            add(project1);
            add(project2);
        }});

        final ExampleRepository repository = new ExampleRepository(EXAMPLE_REPOSITORY);
        model.setSelectedRepository(repository);
        page.prepareView();

        Mockito.reset(projectsView,
                      pageSelectedEvent);
    }

    @Test
    public void testAddTag_MultipleTags() {
        initExampleProjects();

        page.addTag("tag1");

        Mockito.reset(projectsView,
                      pageSelectedEvent);

        page.addTag("tag2");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testAddTag_NoProjectMatches() {
        initExampleProjects();

        page.addTag("nonMatchingTag");

        verify(projectsView,
               times(1)).setProjectsInRepository(Collections.emptyList());
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testAddTag_AllProjectsMatch() {
        initExampleProjects();

        page.addTag("tag2");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1,
                                                               project2));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testAddPartialTag_MultipleTags() {
        initExampleProjects();

        page.addTag("tag1");

        Mockito.reset(projectsView,
                      pageSelectedEvent);

        page.addPartialTag("tag2");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testAddPartialTag_NoProjectMatches() {
        initExampleProjects();

        page.addPartialTag("nonMatchingTag");

        verify(projectsView,
               times(1)).setProjectsInRepository(Collections.emptyList());
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testAddPartialTag_AllProjectsMatch() {
        initExampleProjects();

        page.addPartialTag("tag");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1,
                                                               project2));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testRemoveTag_NonMatchingTag() {
        initExampleProjects();

        page.addTag("nonMatchingTag");

        verify(projectsView,
               times(1)).setProjectsInRepository(Collections.emptyList());

        Mockito.reset(projectsView,
                      pageSelectedEvent);

        page.removeTag("nonMatchingTag");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1,
                                                               project2));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testRemoveTag_MatchingTag() {
        initExampleProjects();

        page.addTag("tag1");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1));

        Mockito.reset(projectsView,
                      pageSelectedEvent);

        page.removeTag("tag1");

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1,
                                                               project2));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }

    @Test
    public void testRemoveAllTags() {
        initExampleProjects();

        page.removeAllTags();

        verify(projectsView,
               times(1)).setProjectsInRepository(Arrays.asList(project1,
                                                               project2));
        verify(pageSelectedEvent,
               times(1)).fire(any(WizardPageSelectedEvent.class));
    }
}
