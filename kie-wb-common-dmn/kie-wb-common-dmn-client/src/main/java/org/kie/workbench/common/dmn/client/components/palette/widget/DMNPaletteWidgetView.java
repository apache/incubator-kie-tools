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

import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.uberfire.client.mvp.UberElement;

public interface DMNPaletteWidgetView extends UberElement<BS3PaletteWidget<DefinitionsPalette>> {

    void showDragProxy(final String itemId,
                       final double x,
                       final double y,
                       final double width,
                       final double height);

    void setBackgroundColor(final String backgroundColor);

    void showEmptyView(final boolean showEmptyView);

    void add(final DMNPaletteItemWidget widget);

    void clear();

    void destroy();
}
