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

        boolean onItemHover( String id, double mouseX, double mouseY, double itemX, double itemY );

    }

    interface ItemOutCallback {

        boolean onItemOut( String id );

    }

    interface ItemMouseDownCallback {

        boolean onItemMouseDown( String id, double mouseX, double mouseY, double itemX, double itemY );

    }

    interface ItemClickCallback {

        boolean onItemClick( String id, double mouseX, double mouseY, double itemX, double itemY );

    }

    Palette<I> onItemHover( ItemHoverCallback callback );

    Palette<I> onItemOut( ItemOutCallback callback );

    Palette<I> onItemMouseDown( ItemMouseDownCallback callback );

    Palette<I> onItemClick( ItemClickCallback callback );

    Palette<I> onClose( CloseCallback callback );

    Palette<I> bind( I paletteDefinition );

    I getDefinition();

    void destroy();

}
