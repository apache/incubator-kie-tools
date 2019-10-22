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
package org.kie.workbench.common.stunner.kogito.client.editor.event;

import java.lang.annotation.Annotation;

public class OnDiagramFocusEvent {

    private final Annotation[] qualifiers;

    public OnDiagramFocusEvent(final Annotation qualifier) {
        this.qualifiers = new Annotation[]{qualifier};
    }

    public OnDiagramFocusEvent(final Annotation[] qualifiers) {
        this.qualifiers = qualifiers;
    }

    public Annotation[] getQualifiers() {
        return qualifiers;
    }
}
