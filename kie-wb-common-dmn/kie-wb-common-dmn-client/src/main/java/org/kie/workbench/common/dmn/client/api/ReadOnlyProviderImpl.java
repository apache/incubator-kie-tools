/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.api;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.appformer.client.context.EditorContextProvider;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;

@Alternative
@ApplicationScoped
@DMNEditor
public class ReadOnlyProviderImpl implements ReadOnlyProvider {

    private final EditorContextProvider contextProvider;

    @Inject
    public ReadOnlyProviderImpl(final EditorContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public boolean isReadOnlyDiagram() {
        return contextProvider.isReadOnly();
    }
}