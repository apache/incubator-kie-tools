/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette;

public class PaletteItemMouseEvent extends PaletteItemEvent {

    private final double mouseX;
    private final double mouseY;
    private final double itemX;
    private final double itemY;

    public PaletteItemMouseEvent(final String id,
                                 final double mouseX,
                                 final double mouseY,
                                 final double itemX,
                                 final double itemY) {
        super(id);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.itemX = itemX;
        this.itemY = itemY;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getItemX() {
        return itemX;
    }

    public double getItemY() {
        return itemY;
    }
}
