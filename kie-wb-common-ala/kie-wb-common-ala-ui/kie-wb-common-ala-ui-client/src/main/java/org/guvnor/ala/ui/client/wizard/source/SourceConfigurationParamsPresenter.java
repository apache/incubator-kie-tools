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
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.spaces.Space;

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

        String getModule();

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

        void clearModules();

        void addModule(final String moduleName);
    }

    public static final String REPO_NAME = "repo-name";

    public static final String BRANCH = "branch";

    public static final String MODULE_DIR = "module-dir";

    private final View view;
    private final Caller<SourceService> sourceService;
    private final Map<String, Module> currentModules = new HashMap<>();

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
                   getSpace().getName() + "/" + getRepository());
        params.put(BRANCH,
                   getBranch());
        params.put(MODULE_DIR,
                   getModule().getModuleName());
        return params;
    }

    @Override
    public void clear() {
        view.clear();
        clearModules();
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

    private Module getModule() {
        return currentModules.get(view.getModule());
    }

    private boolean isValid() {
        return !getRuntime().isEmpty() &&
                !getOU().isEmpty() &&
                !getRepository().isEmpty() &&
                !getBranch().isEmpty() &&
                getModule() != null;
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
            clearModules();
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
            clearModules();
            loadBranches(getSpace(), getRepository());
        } else {
            view.setRepositoryStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onBranchChange() {
        if (!view.getBranch().isEmpty()) {
            view.setBranchStatus(FormStatus.VALID);
            clearModules();
            loadProjects(getSpace(),
                         getRepository(),
                         getBranch());
        } else {
            view.setBranchStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    private Space getSpace() {
        return new Space(view.getOU());
    }

    protected void onModuleChange() {
        if (!view.getModule().isEmpty()) {
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
                               clearModules();
                           }
        ).getOrganizationUnits();
    }

    private void loadRepositories(final String ou) {
        sourceService.call((Collection<String> repos) -> {
                               view.clearRepositories();
                               repos.forEach(view::addRepository);
                               view.clearBranches();
                               clearModules();
                           }
        ).getRepositories(ou);
    }

    private void loadBranches(final Space space, final String repository) {
        sourceService.call((Collection<String> branches) -> {
                               view.clearBranches();
                               branches.forEach(view::addBranch);
                               clearModules();
                           }
        ).getBranches(space, repository);
    }

    private void loadProjects(Space space,
                              String repository,
                              String branch) {
        sourceService.call((Collection<Module> modules) -> {
                               clearModules();
                               modules.forEach(module -> {
                                   view.addModule(module.getModuleName());
                                   currentModules.put(module.getModuleName(),
                                                      module);
                               });
                           },
                           new DefaultErrorCallback()
        ).getModules(space,
                     repository,
                     branch);
    }

    private void clearModules() {
        view.clearModules();
        currentModules.clear();
    }

    private void onContentChange() {
        fireChangeHandlers();
    }
}
