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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.sections;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.api.settings.SpaceScreenModel;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.annotation.SpaceSettings;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.sections.archetypes.ArchetypesSectionPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;

@Dependent
@SpaceSettings
public class SpaceSettingsSection implements SettingsSections {

    private final ArchetypesSectionPresenter archetypesSectionPresenter;

    @Inject
    public SpaceSettingsSection(final ArchetypesSectionPresenter archetypesSectionPresenter) {
        this.archetypesSectionPresenter = archetypesSectionPresenter;
    }

    @Override
    public List<Section<SpaceScreenModel>> getList() {
        return Collections.singletonList(archetypesSectionPresenter);
    }
}
