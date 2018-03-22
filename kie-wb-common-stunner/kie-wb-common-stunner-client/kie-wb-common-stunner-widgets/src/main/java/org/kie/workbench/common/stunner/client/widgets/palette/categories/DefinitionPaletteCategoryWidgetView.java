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

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetPresenter;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.client.mvp.UberElement;

public interface DefinitionPaletteCategoryWidgetView extends UberElement<DefinitionPaletteCategoryWidgetView.Presenter> {

    void render(Glyph glyph,
                double width,
                double height);

    void addItem(DefinitionPaletteItemWidget item);

    void addGroup(DefinitionPaletteGroupWidget groupWidget);

    interface Presenter extends BS3PaletteWidgetPresenter<DefaultPaletteCategory> {

        DefaultPaletteCategory getCategory();

        void onMouseDown(int clientX,
                         int clientY,
                         int x,
                         int y);
    }
}
