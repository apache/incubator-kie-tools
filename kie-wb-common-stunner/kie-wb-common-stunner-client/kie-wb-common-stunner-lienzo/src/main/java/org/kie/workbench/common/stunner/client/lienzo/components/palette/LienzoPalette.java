/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.palette;

import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoPaletteView;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;

public interface LienzoPalette<D extends HasPaletteItems, V extends LienzoPaletteView> extends Palette<D> {

    enum Layout {
        HORIZONTAL, VERTICAL;
    }

    void setLayout( Layout layout );

    LienzoPalette<D, V> setIconSize( int iconSize );

    LienzoPalette<D, V> setPadding( int padding );

    LienzoPalette<D, V> setExpandable( boolean canExpand );

    LienzoPalette<D, V> expand();

    LienzoPalette<D, V> collapse();

    V getView();

}
