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

package org.kie.workbench.common.dmn.client.components.palette;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Categories;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;

@Dependent
@DMNEditor
public class DMNPaletteDefinitionBuilder
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    private final AbstractPaletteDefinitionBuilder paletteDefinitionBuilder;

    // CDI proxy.
    protected DMNPaletteDefinitionBuilder() {
        this(null);
    }

    @Inject
    public DMNPaletteDefinitionBuilder(final CollapsedPaletteDefinitionBuilder paletteDefinitionBuilder) {
        this.paletteDefinitionBuilder = paletteDefinitionBuilder;
    }

    static final Set<String> EXCLUDED_CATEGORIES = new HashSet<String>(3) {{
        add(Categories.DIAGRAM);
        add(Categories.CONNECTORS);
        add(Categories.MISCELLANEOUS);
    }};

    @PostConstruct
    public void init() {
        paletteDefinitionBuilder
                .categoryFilter(category -> !EXCLUDED_CATEGORIES.contains(category));
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinitionConsumer) {
        paletteDefinitionBuilder
                .build(canvasHandler,
                       paletteDefinitionConsumer);
    }

    AbstractPaletteDefinitionBuilder getPaletteDefinitionBuilder() {
        return paletteDefinitionBuilder;
    }
}
