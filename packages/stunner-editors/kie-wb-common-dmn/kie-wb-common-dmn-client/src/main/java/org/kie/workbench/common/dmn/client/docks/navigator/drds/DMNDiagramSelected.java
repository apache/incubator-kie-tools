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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.uberfire.workbench.events.UberFireEvent;

@NonPortable
public class DMNDiagramSelected implements UberFireEvent {

    private final DMNDiagramElement diagramElement;

    public DMNDiagramElement getDiagramElement() {
        return diagramElement;
    }

    public DMNDiagramSelected(final DMNDiagramElement diagramElement) {
        this.diagramElement = diagramElement;
    }
}
