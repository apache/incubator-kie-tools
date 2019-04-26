/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement.BranchManagementPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.dependencies.DependenciesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects.ExternalDataObjectsPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.GeneralSettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.validation.ValidationPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.annotations.FallbackImplementation;

@Dependent
@FallbackImplementation
public class DefaultSettingsSections implements SettingsSections {

    private final DependenciesPresenter dependenciesSettingsSection;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSection;
    private final GeneralSettingsPresenter generalSettingsSection;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSection;
    private final PersistencePresenter persistenceSettingsSection;
    private final ValidationPresenter validationSettingsSection;
    private final BranchManagementPresenter branchManagementPresenter;

    @Inject
    public DefaultSettingsSections(final DependenciesPresenter dependenciesSettingsSection,
                                   final ExternalDataObjectsPresenter externalDataObjectsSettingsSection,
                                   final GeneralSettingsPresenter generalSettingsSection,
                                   final KnowledgeBasesPresenter knowledgeBasesSettingsSection,
                                   final PersistencePresenter persistenceSettingsSection,
                                   final ValidationPresenter validationSettingsSection,
                                   final BranchManagementPresenter branchManagementPresenter) {

        this.dependenciesSettingsSection = dependenciesSettingsSection;
        this.externalDataObjectsSettingsSection = externalDataObjectsSettingsSection;
        this.generalSettingsSection = generalSettingsSection;
        this.knowledgeBasesSettingsSection = knowledgeBasesSettingsSection;
        this.persistenceSettingsSection = persistenceSettingsSection;
        this.validationSettingsSection = validationSettingsSection;
        this.branchManagementPresenter = branchManagementPresenter;
    }

    @Override
    public List<Section<ProjectScreenModel>> getList() {
        return Arrays.asList(
                generalSettingsSection,
                dependenciesSettingsSection,
                knowledgeBasesSettingsSection,
                externalDataObjectsSettingsSection,
                validationSettingsSection,
                persistenceSettingsSection,
                branchManagementPresenter
        );
    }
}
