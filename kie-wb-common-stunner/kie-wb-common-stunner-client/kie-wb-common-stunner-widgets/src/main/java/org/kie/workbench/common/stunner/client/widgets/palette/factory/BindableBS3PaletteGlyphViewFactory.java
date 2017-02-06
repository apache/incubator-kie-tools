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

package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.PaletteIconSettings;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;

public abstract class BindableBS3PaletteGlyphViewFactory extends BindableBS3PaletteViewFactory {

    private final BS3PaletteGlyphViewFactory glyphViewFactory;

    public BindableBS3PaletteGlyphViewFactory(final ShapeManager shapeManager) {
        this.glyphViewFactory = new BS3PaletteGlyphViewFactory(shapeManager);
    }

    @Override
    public PaletteIconSettings getDefinitionIconSettings(String defSetId,
                                                         String itemId) {
        PaletteIconSettings settings = super.getDefinitionIconSettings(defSetId,
                                                                       itemId);

        if (settings != null) {
            return settings;
        }

        return glyphViewFactory.getDefinitionIconSettings(defSetId,
                                                          itemId);
    }
}
