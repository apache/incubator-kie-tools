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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerCachedControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeLocationControlImpl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;

/**
 * TODO: This class can be removed when upgrading to lienzo 2.0.295+.
 * It just changes the getInitialParent method access to public.
 */
public class StunnerWiresParentPickerControl extends WiresParentPickerCachedControl {

    public StunnerWiresParentPickerControl(final WiresShape m_shape,
                                           final ColorMapBackedPicker.PickerOptions pickerOptions) {
        super(m_shape, pickerOptions);
    }

    public StunnerWiresParentPickerControl(final WiresShapeLocationControlImpl shapeLocationControl,
                                           final ColorMapBackedPicker.PickerOptions pickerOptions) {
        super(shapeLocationControl, pickerOptions);
    }

    public StunnerWiresParentPickerControl(final WiresShapeLocationControlImpl shapeLocationControl,
                                           final ColorMapBackedPickerProvider colorMapBackedPickerProvider) {
        super(shapeLocationControl, colorMapBackedPickerProvider);
    }

    @Override
    public WiresContainer getInitialParent() {
        return super.getInitialParent();
    }
}
