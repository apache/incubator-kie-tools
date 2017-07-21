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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * Provides a palette builder for a DefinitionsPalette.
 */
@Dependent
public class DefinitionsPaletteBuilderImpl
        extends AbstractPaletteDefinitionBuilder<PaletteDefinitionBuilder.Configuration, DefinitionsPalette, ClientRuntimeError>
        implements DefinitionsPaletteBuilder {

    private final DefinitionUtils definitionUtils;
    private final ClientFactoryService clientFactoryServices;

    protected DefinitionsPaletteBuilderImpl() {
        this(null,
             null);
    }

    @Inject
    public DefinitionsPaletteBuilderImpl(final DefinitionUtils definitionUtils,
                                         final ClientFactoryService clientFactoryServices) {
        this.definitionUtils = definitionUtils;
        this.clientFactoryServices = clientFactoryServices;
    }

    @Override
    public void build(final PaletteDefinitionBuilder.Configuration configuration,
                      final Callback<DefinitionsPalette, ClientRuntimeError> callback) {
        final String defSetId = configuration.getDefinitionSetId();
        final Collection<String> definitions = configuration.getDefinitionIds();

        if (null != definitions) {
            final List<DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder> builders = new LinkedList<DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder>();
            for (final String definitionId : definitions) {
                if (!isDefinitionExcluded(definitionId)) {
                    clientFactoryServices.newDefinition(definitionId,
                                                        new ServiceCallback<Object>() {
                                                            @Override
                                                            public void onSuccess(final Object definition) {
                                                                final String category = getDefinitionManager().adapters().forDefinition().getCategory(definition);
                                                                final String categoryId = toValidId(category);

                                                                if (!isCategoryExcluded(categoryId)) {
                                                                    final String id = toValidId(definitionId);
                                                                    final String title = getDefinitionManager().adapters().forDefinition().getTitle(definition);
                                                                    final String description = getDefinitionManager().adapters().forDefinition().getDescription(definition);
                                                                    final DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder itemBuilder = new DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder(id)
                                                                            .definitionId(definitionId)
                                                                            .title(title)
                                                                            .description(description)
                                                                            .tooltip(description);
                                                                    builders.add(itemBuilder);
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(final ClientRuntimeError error) {
                                                                callback.onError(error);
                                                            }
                                                        });
                }
            }
            if (!builders.isEmpty()) {
                final List<DefinitionPaletteItem> paletteItems = new LinkedList<DefinitionPaletteItem>();
                for (final DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder builder : builders) {
                    paletteItems.add(builder.build());
                }
                final DefinitionsPaletteImpl definitionsPalette = new DefinitionsPaletteImpl(paletteItems,
                                                                                             defSetId);
                callback.onSuccess(definitionsPalette);
            } else {
                callback.onError(new ClientRuntimeError("No categories found."));
            }
        } else {
            callback.onError(new ClientRuntimeError("Missing definitions argument."));
        }
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }
}
