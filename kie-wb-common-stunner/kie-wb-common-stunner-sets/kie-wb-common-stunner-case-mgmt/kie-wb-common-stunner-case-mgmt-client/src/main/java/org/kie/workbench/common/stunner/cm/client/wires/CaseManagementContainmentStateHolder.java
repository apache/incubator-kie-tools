/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;

@ApplicationScoped
public class CaseManagementContainmentStateHolder {

    private Optional<Integer> originalIndex = Optional.empty();
    private Optional<WiresContainer> originalParent = Optional.empty();
    private Optional<CaseManagementShapeView> ghost = Optional.empty();

    public Optional<Integer> getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(final Optional<Integer> originalIndex) {
        this.originalIndex = originalIndex;
    }

    public Optional<WiresContainer> getOriginalParent() {
        return originalParent;
    }

    public void setOriginalParent(final Optional<WiresContainer> originalParent) {
        this.originalParent = originalParent;
    }

    public Optional<CaseManagementShapeView> getGhost() {
        return ghost;
    }

    public void setGhost(final Optional<CaseManagementShapeView> ghost) {
        this.ghost = ghost;
    }
}
