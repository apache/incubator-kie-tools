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

package org.kie.workbench.common.stunner.core.client.components.palette;

import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;

public interface Palette<I extends HasPaletteItems> {

    interface CloseCallback {

        boolean onClose();
    }

    interface ItemHoverCallback {

        boolean onItemHover( final String id,
                             final double mouseX,
                             final double mouseY,
                             final double itemX,
                             final double itemY );
    }

    interface ItemOutCallback {

        boolean onItemOut( final String id );
    }

    interface ItemMouseDownCallback {

        boolean onItemMouseDown( final String id,
                                 final double mouseX,
                                 final double mouseY,
                                 final double itemX,
                                 final double itemY );
    }

    interface ItemClickCallback {

        boolean onItemClick( final String id,
                             final double mouseX,
                             final double mouseY,
                             final double itemX,
                             final double itemY );
    }

    Palette<I> onItemHover( final ItemHoverCallback callback );

    Palette<I> onItemOut( final ItemOutCallback callback );

    Palette<I> onItemMouseDown( final ItemMouseDownCallback callback );

    Palette<I> onItemClick( final ItemClickCallback callback );

    Palette<I> onClose( final CloseCallback callback );

    Palette<I> bind( final I paletteDefinition );

    I getDefinition();

    void destroy();
}
