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

package org.kie.workbench.common.dmn.client.editors.included;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;

import static java.util.Collections.emptyList;

/**
 * Represents the state (the data) in the {@link IncludedModelsPage}.
 */
@ApplicationScoped
public class IncludedModelsPageState {

    private IncludedModelsPageStateProvider pageProvider;

    public void init(final IncludedModelsPageStateProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    public String getCurrentDiagramNamespace() {
        return getPageProvider()
                .map(IncludedModelsPageStateProvider::getCurrentDiagramNamespace)
                .orElse("");
    }

    public List<BaseIncludedModelActiveRecord> generateIncludedModels() {
        return getPageProvider()
                .map(IncludedModelsPageStateProvider::generateIncludedModels)
                .orElse(emptyList());
    }

    private Optional<IncludedModelsPageStateProvider> getPageProvider() {
        return Optional.ofNullable(pageProvider);
    }
}
