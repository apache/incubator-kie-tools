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

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresContainmentControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresDockingControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresMagnetsControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class StunnerWiresShapeControl extends DelegateWiresShapeControl {

    private final WiresShapeControlImpl delegate;

    public StunnerWiresShapeControl(final WiresShape shape,
                                    final WiresManager wiresManager) {
        final ColorMapBackedPicker.PickerOptions pickerOptions =
                new ColorMapBackedPicker.PickerOptions(true,
                                                       wiresManager.getDockingAcceptor().getHotspotSize());
        // TODO: There is no need to use StunnerWiresParentPickerControl once moving to lienzo 2.0.295
        // TODO: so no need to build the WiresShapeControlImpl instance here neither, it can be provided just as a constructor argument.
        final StunnerWiresParentPickerControl parentPickerControl =
                new StunnerWiresParentPickerControl(shape,
                                                    pickerOptions);
        this.delegate = new WiresShapeControlImpl(parentPickerControl,
                                                  new WiresMagnetsControlImpl(shape),
                                                  new WiresDockingControlImpl(parentPickerControl),
                                                  new WiresContainmentControlImpl(parentPickerControl));
    }

    StunnerWiresShapeControl(final WiresShapeControlImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onMoveStart(final double x,
                            final double y) {
        super.onMoveStart(x, y);
        getShapeView().moveToTop();
    }

    @Override
    protected WiresShapeControl getDelegate() {
        return delegate;
    }

    private ShapeView getShapeView() {
        return (ShapeView) getParentPickerControl().getShape();
    }
}
