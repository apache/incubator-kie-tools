/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.components.palette.widget;

import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.client.mvp.UberElement;

public interface DMNPaletteItemWidgetView extends UberElement<DMNPaletteItemWidgetView.Presenter> {

    void render(Glyph glyph,
                double width,
                double height);

    interface Presenter {

        DefinitionPaletteItem getItem();

        void onMouseDown(int clientX,
                         int clientY,
                         int x,
                         int y);
    }
}
