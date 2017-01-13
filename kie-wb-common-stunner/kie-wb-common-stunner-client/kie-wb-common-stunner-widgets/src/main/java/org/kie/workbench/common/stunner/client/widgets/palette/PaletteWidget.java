/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette;

import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinition;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

public interface PaletteWidget<D extends PaletteDefinition, V extends PaletteWidgetView>
        extends Palette<D> {

    interface ItemDropCallback {

        void onDropItem(final Object definition,
                        final ShapeFactory<?, ?, ? extends Shape> factory,
                        final double x,
                        final double y);
    }

    PaletteWidget<D, V> onItemDrop(final ItemDropCallback callback);

    PaletteWidget<D, V> setMaxWidth(final int maxWidth);

    PaletteWidget<D, V> setMaxHeight(final int maxHeight);

    void unbind();

    V getView();
}
