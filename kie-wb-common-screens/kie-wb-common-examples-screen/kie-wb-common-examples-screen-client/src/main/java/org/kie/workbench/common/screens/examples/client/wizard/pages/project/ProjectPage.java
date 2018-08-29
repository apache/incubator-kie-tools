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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.resources.i18n.ExamplesScreenConstants;
import org.kie.workbench.common.screens.examples.client.wizard.pages.BaseExamplesWizardPage;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageSelectedEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class ProjectPage extends BaseExamplesWizardPage implements ProjectPageView.Presenter {

    private IsWidget activeView;
    private ProjectPageView projectsView;
    private NoRepositoryURLView noRepositoryURLView;
    private FetchRepositoryView fetchingRepositoryView;
    private Event<WizardPageSelectedEvent> pageSelectedEvent;

    private List<ImportProject> importProjects;
    private Set<String> tags = new HashSet<>();

    public ProjectPage() {
        //Zero-argument constructor for CDI proxies
    }

    @Inject
    public ProjectPage(final ProjectPageView projectsView,
                       final NoRepositoryURLView noRepositoryURLView,
                       final FetchRepositoryView fetchingRepositoryView,
                       final Event<WizardPageSelectedEvent> pageSelectedEvent,
                       final Event<WizardPageStatusChangeEvent> pageStatusChangedEvent,
                       final TranslationService translator,
                       final Caller<ExamplesService> examplesService) {
        super(translator,
              examplesService,
              pageStatusChangedEvent);
        this.projectsView = projectsView;
        this.noRepositoryURLView = noRepositoryURLView;
        this.fetchingRepositoryView = fetchingRepositoryView;
        this.pageSelectedEvent = pageSelectedEvent;
    }

    @PostConstruct
    public void init() {
        projectsView.init(this);
    }

    @Override
    public void initialise() {
        projectsView.initialise();
        activeView = noRepositoryURLView;
    }

    @Override
    public void destroy() {
        projectsView.destroy();

        importProjects = null;
        tags.clear();
    }

    @Override
    public String getTitle() {
        return translator.format(ExamplesScreenConstants.ProjectPage_WizardSelectProjectPageTitle);
    }

    @Override
    public void prepareView() {
        final ExampleRepository sourceRepository = model.getSourceRepository();
        final ExampleRepository selectedRepository = model.getSelectedRepository();
        if (!isRepositorySelected(selectedRepository)) {
            activeView = noRepositoryURLView;
        } else if (!selectedRepository.isUrlValid()) {
            activeView = noRepositoryURLView;
        } else if (!selectedRepository.equals(sourceRepository)) {
            activeView = fetchingRepositoryView;
            fetchRepository(selectedRepository);
        } else {
            activeView = projectsView;
        }
    }

    private boolean isRepositorySelected(final ExampleRepository repository) {
        if (repository == null) {
            return false;
        }
        return (!(repository.getUrl() == null || repository.getUrl().isEmpty()));
    }

    private void fetchRepository(final ExampleRepository selectedRepository) {
        examplesService.call(new RemoteCallback<Set<ImportProject>>() {
                                 @Override
                                 public void callback(final Set<ImportProject> projects) {
                                     activeView = projectsView;
                                     model.getProjects().clear();
                                     model.setSourceRepository(selectedRepository);

                                     final List<ImportProject> sortedProjects = sort(projects);

                                     projectsView.setProjectsInRepository(sortedProjects);
                                     importProjects = sortedProjects;

                                     pageSelectedEvent.fire(new WizardPageSelectedEvent(ProjectPage.this));
                                 }
                             },
                             new DefaultErrorCallback() {
                                 @Override
                                 public boolean error(final Message message,
                                                      final Throwable throwable) {
                                     model.setSourceRepository(null);
                                     model.getSelectedRepository().setUrlValid(false);
                                     return super.error(message,
                                                        throwable);
                                 }
                             }).getProjects(selectedRepository);
    }

    private List<ImportProject> sort(final Set<ImportProject> projects) {
        final List<ImportProject> sortedProjects = new ArrayList<ImportProject>(projects);
        Collections.sort(sortedProjects,
                         new Comparator<ImportProject>() {
                             @Override
                             public int compare(final ImportProject o1,
                                                final ImportProject o2) {
                                 return o1.getName().compareTo(o2.getName());
                             }
                         });
        return sortedProjects;
    }

    @Override
    public Widget asWidget() {
        return activeView.asWidget();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(model.getProjects().size() > 0);
    }

    @Override
    public void addProject(final ImportProject project) {
        model.addProject(project);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    @Override
    public void removeProject(final ImportProject project) {
        model.removeProject(project);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    @Override
    public boolean isProjectSelected(ImportProject project) {
        return model.getProjects().contains(project);
    }

    @Override
    public void addTag(String tag) {
        tags.add(tag);

        updateProjectsInRepository(tags);
    }

    @Override
    public void addPartialTag(final String tag) {
        List<String> partialTags = new ArrayList<>(tags);
        partialTags.add(tag);

        updateProjectsInRepository(partialTags);
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag);

        updateProjectsInRepository(tags);
    }

    private void updateProjectsInRepository(final Collection<String> tags) {
        List<ImportProject> resultList = importProjects.stream()
                .filter(p -> tags.stream().allMatch(userTag -> p.getTags().stream().anyMatch(projectTag -> projectTag.toLowerCase().contains(userTag.toLowerCase()))))
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());

        projectsView.setProjectsInRepository(resultList);
        pageSelectedEvent.fire(new WizardPageSelectedEvent(ProjectPage.this));
    }

    @Override
    public void removeAllTags() {
        tags.clear();
        projectsView.setProjectsInRepository(importProjects);
        pageSelectedEvent.fire(new WizardPageSelectedEvent(ProjectPage.this));
    }
}
