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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.decorator;

public class DecoratorsFactory {

    public static DecoratorsFactory INSTANCE = new DecoratorsFactory();

    private DecoratorsFactory() {
    }

    /**
     * A box decorator is rendered by applying some stroke but no fill.
     * Can be used as decorator for any item type.
     */
    public BoxDecorator box() {
        return new BoxDecorator();
    }

    /**
     * A button decorator is rendered by applying some stroke and some fill.
     * It never is being completely hidden, so it's typically applied
     * on items like shapes, groups or buttons, but no item-grids
     * neither toolboxes.
     */
    public BoxDecorator button() {
        return new BoxDecorator()
                .configure(path -> path.setFillAlpha(0.7))
                .useShowExecutor(path -> path.setAlpha(1))
                .useHideExecutor(path -> {
                });
    }
}
