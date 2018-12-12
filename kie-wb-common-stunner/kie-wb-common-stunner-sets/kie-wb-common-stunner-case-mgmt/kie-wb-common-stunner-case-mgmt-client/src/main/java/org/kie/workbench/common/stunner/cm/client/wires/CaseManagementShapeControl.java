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

import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresMagnetsControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerCachedControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import org.kie.workbench.common.stunner.client.lienzo.wires.DelegateWiresShapeControl;

public class CaseManagementShapeControl
        extends DelegateWiresShapeControl {

    private final WiresShapeControlImpl shapeControl;

    public CaseManagementShapeControl(final WiresShape shape,
                                      final CaseManagementContainmentStateHolder state) {
        final ColorMapBackedPicker.PickerOptions pickerOptions =
                new ColorMapBackedPicker.PickerOptions(false,
                                                       0);
        final WiresParentPickerCachedControl parentPicker =
                new WiresParentPickerCachedControl(new CaseManagementShapeLocationControl(shape),
                                                   new CaseManagementColorMapBackedPickerProvider(pickerOptions));
        shapeControl = new WiresShapeControlImpl(parentPicker,
                                                 new WiresMagnetsControlImpl(shape),
                                                 null,
                                                 new CaseManagementContainmentControl(parentPicker, state));
    }

    @Override
    protected WiresShapeControl getDelegate() {
        return shapeControl;
    }

    public static class CaseManagementColorMapBackedPickerProvider extends WiresParentPickerControlImpl.ColorMapBackedPickerProviderImpl {

        public CaseManagementColorMapBackedPickerProvider(final ColorMapBackedPicker.PickerOptions pickerOptions) {
            super(pickerOptions);
        }

        @Override
        public ColorMapBackedPicker get(WiresLayer layer) {
            return new CaseManagementColorMapBackedPicker(layer,
                                                          getOptions());
        }
    }
}
