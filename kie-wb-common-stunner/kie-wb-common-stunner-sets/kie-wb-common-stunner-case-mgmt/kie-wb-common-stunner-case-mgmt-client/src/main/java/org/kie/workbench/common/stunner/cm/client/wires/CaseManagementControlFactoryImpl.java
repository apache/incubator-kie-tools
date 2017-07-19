/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlFactoryImpl;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;

@ApplicationScoped
@CaseManagementEditor
public class CaseManagementControlFactoryImpl extends WiresControlFactoryImpl {

    private final CaseManagementContainmentStateHolder state;

    @Inject
    public CaseManagementControlFactoryImpl(final CaseManagementContainmentStateHolder state) {
        this.state = state;
    }

    @Override
    public WiresDockingAndContainmentControl newDockingAndContainmentControl(final WiresShape shape,
                                                                             final WiresManager wiresManager) {
        return new CaseManagementDockingAndContainmentControlImpl(shape,
                                                                  wiresManager,
                                                                  state);
    }
}
