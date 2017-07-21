/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.components.palette.factory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Categories;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindablePaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;

@Dependent
public class DMNPaletteDefinitionFactory extends BindablePaletteDefinitionFactory<DefinitionsPaletteBuilder, DefinitionsPalette, BS3PaletteWidget<DefinitionsPalette>> {

    @Inject
    public DMNPaletteDefinitionFactory(final ShapeManager shapeManager,
                                       final DefinitionsPaletteBuilder paletteBuilder,
                                       final BS3PaletteWidget<DefinitionsPalette> palette) {
        super(shapeManager,
              paletteBuilder,
              palette);
        this.paletteBuilder.excludeCategory(Categories.DIAGRAM);
        this.paletteBuilder.excludeCategory(Categories.CONNECTORS);
        this.paletteBuilder.excludeCategory(Categories.MISCELLANEOUS);
    }

    @Override
    protected DefinitionsPaletteBuilder newBuilder() {
        return paletteBuilder;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return DMNDefinitionSet.class;
    }
}
