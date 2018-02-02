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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.widget.FormStatus;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.SourceConfigurationPageView_SelectOption_placeholder;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.SourceConfigurationPageView_Title;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.widget.StyleHelper.setFormStatus;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class SourceConfigurationParamsView
        implements IsElement,
                   SourceConfigurationParamsPresenter.View {

    @Inject
    @DataField("runtime-form")
    private Div runtimeForm;

    @Inject
    @DataField("runtime-name")
    private TextInput runtimeName;

    @Inject
    @DataField("ou-form")
    private Div ouForm;

    @Inject
    @DataField
    private Select ous;

    @Inject
    @DataField("repo-form")
    private Div repoForm;

    @Inject
    @DataField
    private Select repos;

    @Inject
    @DataField("branch-form")
    private Div branchForm;

    @Inject
    @DataField
    private Select branches;

    @Inject
    @DataField("module-form")
    private Div moduleForm;

    @Inject
    @DataField
    private Select modules;

    @Inject
    private TranslationService translationService;

    private SourceConfigurationParamsPresenter presenter;

    @Override
    public void init(final SourceConfigurationParamsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(SourceConfigurationPageView_Title);
    }

    @Override
    public String getRuntimeName() {
        return runtimeName.getValue();
    }

    @Override
    public String getOU() {
        return ous.getValue();
    }

    @Override
    public String getRepository() {
        return repos.getValue();
    }

    @Override
    public String getBranch() {
        return branches.getValue();
    }

    @Override
    public String getModule() {
        return modules.getValue();
    }

    @Override
    public void disable() {
        resetFormState();
        enable(false);
    }

    @Override
    public void enable() {
        resetFormState();
        enable(true);
    }

    @Override
    public void setRuntimeStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(runtimeForm,
                      status);
    }

    @Override
    public void setOUStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(ouForm,
                      status);
    }

    @Override
    public void setRepositoryStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(repoForm,
                      status);
    }

    @Override
    public void setBranchStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(branchForm,
                      status);
    }

    @Override
    public void setProjectStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(moduleForm,
                      status);
    }

    @Override
    public void clear() {
        resetFormState();
        clearOrganizationUnits();
        clearRepositories();
        clearBranches();
        clearModules();
        this.runtimeName.setValue(EMPTY_STRING);
        this.ous.setValue(EMPTY_STRING);
        this.repos.setValue(EMPTY_STRING);
        this.branches.setValue(EMPTY_STRING);
        this.modules.setValue(EMPTY_STRING);
    }

    @Override
    public void addOrganizationUnit(final String ou) {
        ous.add(newOption(ou,
                          ou));
    }

    @Override
    public void addRepository(final String repo) {
        repos.add(newOption(repo,
                            repo));
    }

    @Override
    public void addBranch(final String branch) {
        branches.add(newOption(branch,
                               branch));
    }

    @Override
    public void addModule(String moduleName) {
        modules.add(newOption(moduleName,
                              moduleName));
    }

    @Override
    public void clearOrganizationUnits() {
        clear(ous);
    }

    @Override
    public void clearRepositories() {
        clear(repos);
    }

    @Override
    public void clearBranches() {
        clear(branches);
    }

    @Override
    public void clearModules() {
        clear(modules);
    }

    @EventHandler("runtime-name")
    private void onRuntimeChange(@ForEvent("change") final Event event) {
        presenter.onRuntimeNameChange();
    }

    @EventHandler("ous")
    private void onOrganizationalUnitChange(@ForEvent("change") final Event event) {
        presenter.onOrganizationalUnitChange();
    }

    @EventHandler("repos")
    private void onRepositoryChange(@ForEvent("change") final Event event) {
        presenter.onRepositoryChange();
    }

    @EventHandler("branches")
    private void onBranchChange(@ForEvent("change") final Event event) {
        presenter.onBranchChange();
    }

    @EventHandler("modules")
    private void onProjectChange(@ForEvent("change") final Event event) {
        presenter.onModuleChange();
    }

    private void enable(boolean enabled) {
        this.runtimeName.setDisabled(!enabled);
        this.ous.setDisabled(!enabled);
        this.repos.setDisabled(!enabled);
        this.branches.setDisabled(!enabled);
        this.modules.setDisabled(!enabled);
    }

    private void resetFormState() {
        setFormStatus(runtimeForm,
                      FormStatus.VALID);
        setFormStatus(ouForm,
                      FormStatus.VALID);
        setFormStatus(repoForm,
                      FormStatus.VALID);
        setFormStatus(branchForm,
                      FormStatus.VALID);
        setFormStatus(moduleForm,
                      FormStatus.VALID);
    }

    private Option newOption(final String text,
                             final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }

    private HTMLElement defaultOption() {
        final HTMLElement option = Window.getDocument().createElement("option");
        option.setAttribute("value",
                            "");
        option.setAttribute("disabled",
                            "");
        option.setAttribute("selected",
                            "");
        option.setTextContent(translationService.getTranslation(SourceConfigurationPageView_SelectOption_placeholder));
        return option;
    }

    private void clear(final Select select) {
        for (int i = 0; i < select.getOptions().getLength(); i++) {
            select.remove(i);
        }
        select.setInnerHTML(EMPTY_STRING);
        select.add(defaultOption(),
                   null);
    }
}