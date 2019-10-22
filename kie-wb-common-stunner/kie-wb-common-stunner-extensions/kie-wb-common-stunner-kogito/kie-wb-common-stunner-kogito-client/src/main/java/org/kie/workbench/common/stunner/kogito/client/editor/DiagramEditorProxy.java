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
package org.kie.workbench.common.stunner.kogito.client.editor;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.kogito.api.editor.KogitoDiagramResource;

public class DiagramEditorProxy<RESOURCE extends KogitoDiagramResource> {

    private Optional<Supplier<Integer>> hashCodeSupplier;
    private Supplier<RESOURCE> contentSupplier = () -> null;

    public DiagramEditorProxy() {
        hashCodeSupplier = Optional.empty();
    }

    public void setHashCodeSupplier(final Supplier<Integer> hashCodeSupplier) {
        this.hashCodeSupplier = Optional.ofNullable(hashCodeSupplier);
    }

    public int getEditorHashCode() {
        return hashCodeSupplier.orElse(() -> 0).get();
    }

    public Supplier<RESOURCE> getContentSupplier() {
        return contentSupplier;
    }

    public void setContentSupplier(final Supplier<RESOURCE> contentSupplier) {
        this.contentSupplier = contentSupplier;
    }
}
