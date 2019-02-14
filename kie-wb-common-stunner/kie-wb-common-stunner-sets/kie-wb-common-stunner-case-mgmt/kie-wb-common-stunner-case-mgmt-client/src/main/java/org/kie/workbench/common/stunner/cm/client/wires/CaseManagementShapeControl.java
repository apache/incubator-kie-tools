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

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresMagnetsControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.kie.workbench.common.stunner.client.lienzo.wires.DelegateWiresShapeControl;

public class CaseManagementShapeControl
        extends DelegateWiresShapeControl {

    private final WiresShapeControlImpl shapeControl;

    public CaseManagementShapeControl(final WiresShape shape,
                                      final CaseManagementContainmentStateHolder state) {
        final WiresParentPickerControl parentPicker =
                new WiresParentPickerControlImpl(new CaseManagementShapeLocationControl(shape),
                                                 new Supplier<WiresLayerIndex>() {
                                                     @Override
                                                     public WiresLayerIndex get() {
                                                         return shapeControl.getIndex().get();
                                                     }
                                                 });
        shapeControl = new WiresShapeControlImpl(parentPicker,
                                                 new WiresMagnetsControlImpl(shape),
                                                 null,
                                                 new CaseManagementContainmentControl(parentPicker, state));
    }

    @Override
    public WiresShapeControlImpl getDelegate() {
        return shapeControl;
    }
}
