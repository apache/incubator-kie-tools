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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Categories;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;

@Dependent
public class DMNPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory {

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>() {{
        put(Categories.NODES,
            "Nodes");
        put(Categories.CONNECTORS,
            "Connectors");
    }};

    private static final Map<String, Class<?>> CAT_DEF_IDS = new HashMap<String, Class<?>>() {{
//        put(Categories.NODES,
//            StartNoneEvent.class);
//        put(Categories.CONNECTORS,
//            SequenceFlow.class);
    }};

    @Inject
    public DMNPaletteDefinitionFactory(final ShapeManager shapeManager,
                                       final DefinitionSetPaletteBuilder paletteBuilder) {
        super(shapeManager,
              paletteBuilder);
    }

    @Override
    protected void configureBuilder() {
        super.configureBuilder();

        excludeCategory(Categories.DIAGRAM);
        excludeCategory(Categories.CONNECTORS);
    }

    @Override
    protected String getCategoryTitle(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected Class<?> getCategoryTargetDefinitionId(final String id) {
        return CAT_DEF_IDS.get(id);
    }

    @Override
    protected String getCategoryDescription(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected String getMorphGroupTitle(final String morphBaseId,
                                        final Object definition) {
        return null;
    }

    @Override
    protected String getMorphGroupDescription(final String morphBaseId,
                                              final Object definition) {
        return null;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return DMNDefinitionSet.class;
    }
}
