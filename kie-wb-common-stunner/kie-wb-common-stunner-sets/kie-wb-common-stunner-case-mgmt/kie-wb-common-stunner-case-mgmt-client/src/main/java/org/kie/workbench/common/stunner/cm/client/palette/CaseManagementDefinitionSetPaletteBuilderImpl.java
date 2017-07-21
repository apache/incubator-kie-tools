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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl.DefinitionPaletteCategoryImpl;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl.DefinitionPaletteItemImpl;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl.DefinitionSetPaletteImpl;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
@CaseManagementEditor
public class CaseManagementDefinitionSetPaletteBuilderImpl
        extends AbstractPaletteDefinitionBuilder<PaletteDefinitionBuilder.Configuration, DefinitionSetPalette, ClientRuntimeError>
        implements DefinitionSetPaletteBuilder {

    private final DefinitionUtils definitionUtils;
    private final ClientFactoryService clientFactoryServices;

    private PaletteCategoryProvider paletteCategoryProvider;

    private static final Map<String, String> DEFINITION_CATEGORY_ID_MAPPINGS = new HashMap<String, String>(5) {{
        put(AdHocSubprocess.class.getName(),
            CaseManagementPaletteDefinitionFactory.STAGES);
        put(UserTask.class.getName(),
            CaseManagementPaletteDefinitionFactory.ACTIVITIES);
        put(ScriptTask.class.getName(),
            CaseManagementPaletteDefinitionFactory.ACTIVITIES);
        put(BusinessRuleTask.class.getName(),
            CaseManagementPaletteDefinitionFactory.ACTIVITIES);
        put(ReusableSubprocess.class.getName(),
            CaseManagementPaletteDefinitionFactory.ACTIVITIES);
    }};

    protected CaseManagementDefinitionSetPaletteBuilderImpl() {
        this(null,
             null);
    }

    @Inject
    public CaseManagementDefinitionSetPaletteBuilderImpl(final DefinitionUtils definitionUtils,
                                                         final ClientFactoryService clientFactoryServices) {
        this.definitionUtils = definitionUtils;
        this.clientFactoryServices = clientFactoryServices;
        this.paletteCategoryProvider = CATEGORY_PROVIDER;
    }

    public void build(final PaletteDefinitionBuilder.Configuration configuration,
                      final Callback<DefinitionSetPalette, ClientRuntimeError> callback) {
        final String defSetId = configuration.getDefinitionSetId();
        final Collection<String> definitions = configuration.getDefinitionIds();

        if (null != definitions) {
            final List<DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder> categoryBuilders = new LinkedList<>();
            for (final String defId : definitions) {

                if (!isDefinitionExcluded(defId)) {
                    clientFactoryServices.newDefinition(defId,
                                                        new ServiceCallback<Object>() {
                                                            @Override
                                                            public void onSuccess(final Object definition) {
                                                                final String id = getDefinitionManager().adapters().forDefinition().getId(definition);
                                                                final String category = getDefinitionManager().adapters().forDefinition().getCategory(definition);
                                                                final String categoryId = toValidId(category);

                                                                if (isCategoryExcluded(categoryId)) {
                                                                    return;
                                                                }

                                                                final String paletteCategoryId = DEFINITION_CATEGORY_ID_MAPPINGS.get(id);

                                                                DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder categoryGroupBuilder = getItemBuilder(categoryBuilders,
                                                                                                                                                                     paletteCategoryId);
                                                                if (categoryGroupBuilder == null) {
                                                                    categoryGroupBuilder = new DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder(paletteCategoryId)
                                                                            .definitionId(paletteCategoryProvider.getDefinitionId(categoryId))
                                                                            .title(paletteCategoryProvider.getTitle(paletteCategoryId))
                                                                            .tooltip(paletteCategoryProvider.getTitle(paletteCategoryId))
                                                                            .description(paletteCategoryProvider.getDescription(paletteCategoryId));
                                                                    categoryBuilders.add(categoryGroupBuilder);
                                                                }

                                                                final String title = getDefinitionManager().adapters().forDefinition().getTitle(definition);
                                                                final String description = getDefinitionManager().adapters().forDefinition().getDescription(definition);
                                                                final DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder itemBuilder = new DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder(id)
                                                                        .definitionId(id)
                                                                        .title(title)
                                                                        .description(description)
                                                                        .tooltip(description);
                                                                categoryGroupBuilder.addItem(itemBuilder);
                                                            }

                                                            @Override
                                                            public void onError(final ClientRuntimeError error) {
                                                                callback.onError(error);
                                                            }
                                                        });
                }
            }

            if (!categoryBuilders.isEmpty()) {
                final List<DefinitionPaletteCategory> categories = categoryBuilders.stream().map(DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder::build).collect(Collectors.toList());
                final DefinitionSetPaletteImpl definitionPalette = new DefinitionSetPaletteImpl(categories,
                                                                                                defSetId);
                callback.onSuccess(definitionPalette);
            } else {
                callback.onError(new ClientRuntimeError("No categories found."));
            }
        } else {
            callback.onError(new ClientRuntimeError("Missing definition argument."));
        }
    }

    static final PaletteCategoryProvider CATEGORY_PROVIDER = new PaletteCategoryProvider() {

        @Override
        public String getTitle(final String id) {
            return id;
        }

        @Override
        public String getDescription(final String id) {
            return id;
        }

        @Override
        public String getDefinitionId(final String id) {
            return id;
        }
    };

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    @Override
    public DefinitionSetPaletteBuilder setCategoryProvider(final PaletteCategoryProvider categoryProvider) {
        this.paletteCategoryProvider = categoryProvider;
        return this;
    }

    @Override
    public DefinitionSetPaletteBuilder setMorphGroupProvider(final PaletteMorphGroupProvider groupProvider) {
        //Case Management doesn't consider Morph Group for the Palette
        return this;
    }
}
