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

import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class StunnerWiresShapeControl extends DelegateWiresShapeControl {

    private final WiresShapeControlImpl delegate;

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
    public WiresShapeControlImpl getDelegate() {
        return delegate;
    }

    private ShapeView getShapeView() {
        return (ShapeView) getParentPickerControl().getShape();
    }
}
