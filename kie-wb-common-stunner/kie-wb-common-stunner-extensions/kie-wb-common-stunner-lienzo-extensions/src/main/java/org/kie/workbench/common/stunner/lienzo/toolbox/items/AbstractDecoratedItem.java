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

package org.kie.workbench.common.stunner.lienzo.toolbox.items;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;

public abstract class AbstractDecoratedItem<T extends DecoratedItem>
        extends AbstractPrimitiveItem<T>
        implements DecoratedItem<T> {

    @Override
    public final T show() {
        return show(() -> {
                    },
                    () -> {
                    });
    }

    @Override
    public final T hide() {
        return hide(() -> {
                    },
                    () -> {
                    });
    }

    public abstract T show(Runnable before,
                           Runnable after);

    public abstract T hide(Runnable before,
                           Runnable after);

    public abstract IPrimitive<?> getPrimitive();

    public abstract Supplier<BoundingBox> getBoundingBox();
}
