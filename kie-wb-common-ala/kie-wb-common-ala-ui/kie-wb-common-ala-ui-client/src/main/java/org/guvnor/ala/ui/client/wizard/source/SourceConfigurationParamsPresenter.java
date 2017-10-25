/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.wizard.source;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.guvnor.ala.ui.service.SourceService;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

import static org.guvnor.ala.ui.client.util.UIUtil.trimOrGetEmpty;
import static org.guvnor.ala.ui.client.wizard.NewDeployWizard.RUNTIME_NAME;

@ApplicationScoped
public class SourceConfigurationParamsPresenter
        extends AbstractHasContentChangeHandlers
        implements PipelineParamsForm {

    public interface View
            extends UberElement<SourceConfigurationParamsPresenter> {

        String getRuntimeName();

        String getOU();

        String getRepository();

        String getBranch();

        String getProject();

        void setRuntimeStatus(final FormStatus status);

        void setOUStatus(final FormStatus formStatus);

        void setRepositoryStatus(final FormStatus formStatus);

        void setBranchStatus(final FormStatus formStatus);

        void setProjectStatus(final FormStatus formStatus);

        void clear();

        void disable();

        void enable();

        String getTitle();

        void clearRepositories();

        void addRepository(String repo);

        void clearBranches();

        void addBranch(String branch);

        void clearOrganizationUnits();

        void addOrganizationUnit(String ou);

        void clearProjects();

        void addProject(String projectName);
    }

    public static final String REPO_NAME = "repo-name";

    public static final String BRANCH = "branch";

    public static final String PROJECT_DIR = "project-dir";

    private final View view;
    private final Caller<SourceService> sourceService;
    private final Map<String, Project> currentProjects = new HashMap<>();

    @Inject
    public SourceConfigurationParamsPresenter(final View view,
                                              final Caller<SourceService> sourceService) {
        this.view = view;
        this.sourceService = sourceService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public IsElement getView() {
        return view;
    }

    @Override
    public String getWizardTitle() {
        return view.getTitle();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(isValid());
    }

    @Override
    public void initialise() {
        setup();
    }

    @Override
    public void prepareView() {
    }

    @Override
    public Map<String, String> buildParams() {
        Map<String, String> params = new HashMap<>();
        params.put(RUNTIME_NAME,
                   getRuntime());
        params.put(REPO_NAME,
                   getRepository());
        params.put(BRANCH,
                   getBranch());
        params.put(PROJECT_DIR,
                   getProject().getProjectName());
        return params;
    }

    public void clear() {
        view.clear();
        clearProjects();
    }

    private void setup() {
        loadOUs();
    }

    private String getRuntime() {
        return trimOrGetEmpty(view.getRuntimeName());
    }

    private String getBranch() {
        return view.getBranch();
    }

    private String getRepository() {
        return view.getRepository();
    }

    private String getOU() {
        return view.getOU();
    }

    private Project getProject() {
        return currentProjects.get(view.getProject());
    }

    private boolean isValid() {
        return !getRuntime().isEmpty() &&
                !getOU().isEmpty() &&
                !getRepository().isEmpty() &&
                !getBranch().isEmpty() &&
                getProject() != null;
    }

    public void disable() {
        view.disable();
    }

    protected void onRuntimeNameChange() {
        if (!getRuntime().isEmpty()) {
            view.setRuntimeStatus(FormStatus.VALID);
        } else {
            view.setRuntimeStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onOrganizationalUnitChange() {
        if (!view.getOU().isEmpty()) {
            view.setOUStatus(FormStatus.VALID);
            view.clearRepositories();
            view.clearBranches();
            clearProjects();
            loadRepositories(getOU());
        } else {
            view.setOUStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onRepositoryChange() {
        if (!view.getRepository().isEmpty()) {
            view.setRepositoryStatus(FormStatus.VALID);
            view.clearBranches();
            clearProjects();
            loadBranches(getRepository());
        } else {
            view.setRepositoryStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onBranchChange() {
        if (!view.getBranch().isEmpty()) {
            view.setBranchStatus(FormStatus.VALID);
            clearProjects();
            loadProjects(getRepository(),
                         getBranch());
        } else {
            view.setBranchStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onProjectChange() {
        if (!view.getProject().isEmpty()) {
            view.setProjectStatus(FormStatus.VALID);
        } else {
            view.setProjectStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    private void loadOUs() {
        sourceService.call((Collection<String> ous) -> {
                               view.clearOrganizationUnits();
                               ous.forEach(view::addOrganizationUnit);
                               view.clearRepositories();
                               view.clearBranches();
                               clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getOrganizationUnits();
    }

    private void loadRepositories(final String ou) {
        sourceService.call((Collection<String> repos) -> {
                               view.clearRepositories();
                               repos.forEach(view::addRepository);
                               view.clearBranches();
                               clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getRepositories(ou);
    }

    private void loadBranches(final String repository) {
        sourceService.call((Collection<String> branches) -> {
                               view.clearBranches();
                               branches.forEach(view::addBranch);
                               clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getBranches(repository);
    }

    private void loadProjects(String repository,
                              String branch) {
        sourceService.call((Collection<Project> projects) -> {
                               clearProjects();
                               projects.forEach(project -> {
                                   view.addProject(project.getProjectName());
                                   currentProjects.put(project.getProjectName(),
                                                       project);
                               });
                           },
                           new DefaultErrorCallback()
        ).getProjects(repository,
                      branch);
    }

    private void clearProjects() {
        view.clearProjects();
        currentProjects.clear();
    }

    private void onContentChange() {
        fireChangeHandlers();
    }
}
