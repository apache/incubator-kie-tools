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

package org.kie.workbench.common.stunner.core.client.components.palette.view;

public class PaletteGridLayoutBuilder extends AbstractPaletteGridBuilder<PaletteGridLayoutBuilder> {

    public static final PaletteGridLayoutBuilder HORIZONTAL = new PaletteGridLayoutBuilder().layout( GridLayout.HORIZONTAL );
    public static final PaletteGridLayoutBuilder VERTICAL = new PaletteGridLayoutBuilder().layout( GridLayout.VERTICAL );

    public enum GridLayout {
        HORIZONTAL, VERTICAL;
    }

    protected GridLayout layout = GridLayout.HORIZONTAL;

    public PaletteGridLayoutBuilder layout( final GridLayout layout ) {
        this.layout = layout;
        return this;
    }

    @Override
    public PaletteGrid build() {
        final int _r = ( layout.equals( GridLayout.HORIZONTAL ) ) ? 1 : -1;
        final int _c = ( layout.equals( GridLayout.VERTICAL ) ) ? 1 : -1;
        return new PaletteGridImpl( _r, _c, iconSize, padding );

    }

}
