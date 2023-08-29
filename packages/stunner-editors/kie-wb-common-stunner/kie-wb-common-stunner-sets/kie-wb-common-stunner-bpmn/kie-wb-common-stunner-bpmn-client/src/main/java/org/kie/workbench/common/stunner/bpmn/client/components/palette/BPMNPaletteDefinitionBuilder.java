/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.components.palette;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.CategoryBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.GroupBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.ItemBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.CategoryDefinitionProvider;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.isType;

@Dependent
@BPMN
public class BPMNPaletteDefinitionBuilder
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    //palette categories order customization.
    private static final List<String> CATEGORIES_ORDER = Stream.of(
            BPMNCategories.START_EVENTS,
            BPMNCategories.INTERMEDIATE_EVENTS,
            BPMNCategories.END_EVENTS,
            BPMNCategories.ACTIVITIES,
            BPMNCategories.SUB_PROCESSES,
            BPMNCategories.GATEWAYS,
            BPMNCategories.CONTAINERS,
            BPMNCategories.CUSTOM_TASKS,
            BPMNCategories.ARTIFACTS)
            .collect(Collectors.toList());

    private static final Map<String, String> CUSTOM_GROUPS = Stream.of(
                    new AbstractMap.SimpleEntry<>(Lane.class.getName(), "org.kie.workbench.common.stunner.bpmn.definition.customGroup.Containers"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private final DefinitionManager definitionManager;
    private final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder;
    private final StunnerTranslationService translationService;
    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;
    private final Function<WorkItemDefinition, CustomTask> customTaskBuilder;
    private final DefinitionUtils definitionUtils;
    private final CategoryDefinitionProvider categoryDefinitionProvider;

    // CDI proxy.
    protected BPMNPaletteDefinitionBuilder() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public BPMNPaletteDefinitionBuilder(final DefinitionManager definitionManager,
                                        final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder,
                                        final StunnerTranslationService translationService,
                                        final ManagedInstance<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                                        final DefinitionUtils definitionUtils,
                                        final BPMNCategoryDefinitionProvider categoryDefinitionProvider) {
        this(definitionManager,
             paletteDefinitionBuilder,
             translationService,
             workItemDefinitionRegistry::get,
             wid -> new CustomTaskFactory.CustomTaskBuilder(wid).build(),
             definitionUtils,
             categoryDefinitionProvider);
    }

    BPMNPaletteDefinitionBuilder(final DefinitionManager definitionManager,
                                 final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder,
                                 final StunnerTranslationService translationService,
                                 final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                                 final Function<WorkItemDefinition, CustomTask> customTaskBuilder,
                                 final DefinitionUtils definitionUtils,
                                 final BPMNCategoryDefinitionProvider categoryDefinitionProvider) {
        this.definitionManager = definitionManager;
        this.paletteDefinitionBuilder = paletteDefinitionBuilder;
        this.translationService = translationService;
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.customTaskBuilder = customTaskBuilder;
        this.definitionUtils = definitionUtils;
        this.categoryDefinitionProvider = categoryDefinitionProvider;
    }

    @PostConstruct
    public void init() {
        paletteDefinitionBuilder
                .itemFilter(isDefinitionAllowed())
                .categoryFilter(category -> !BPMNCategories.CONNECTING_OBJECTS.equals(category))
                .categoryDefinitionIdProvider(categoryDefinitionProvider.definitionIdProvider())
                .categoryGlyphProvider(categoryDefinitionProvider.glyphProvider())
                .categoryMessages(categoryDefinitionProvider.categoryMessageProvider(translationService))
                .customGroupIdProvider(CUSTOM_GROUPS::get)
                .customGroupMessages(new DefaultPaletteDefinitionProviders.DefaultCustomGroupMessageProvider(translationService))
                .morphDefinitionProvider(this::getMorphDefinition);
    }

    private <T> MorphDefinition getMorphDefinition(final T definition) {
        final MorphAdapter<Object> adapter = definitionManager.adapters().registry().getMorphAdapter(definition.getClass());
        if (Objects.equals(BPMNCategories.SUB_PROCESSES, definitionManager.adapters().forDefinition().getCategory(definition))) {
            //aggregate all sub-processes on the same morph definition, in this way they have the same palette group
            final String subProcessId = BindableAdapterUtils.getDefinitionId(BaseSubprocess.class);
            return Optional.ofNullable(adapter.getMorphDefinitions(subProcessId, subProcessId)).orElse(Collections.emptyList()).iterator().next();
        } else {
            return definitionUtils.getMorphDefinition(definition);
        }
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinitionConsumer) {
        paletteDefinitionBuilder
                .build(canvasHandler,
                       paletteDefinition -> {
                           paletteDefinition
                                   .getItems()
                                   .sort(Comparator.comparingInt(item -> getCategoryOrder(item.getId())));
                           createPaletteCustomTasksCategory(paletteDefinition,
                                                            paletteDefinitionConsumer);
                       });
    }

    private int getCategoryOrder(final String categoryId) {
        return CATEGORIES_ORDER.indexOf(categoryId);
    }

    ExpandedPaletteDefinitionBuilder getPaletteDefinitionBuilder() {
        return paletteDefinitionBuilder;
    }

    private Predicate<String> isDefinitionAllowed() {
        return isType(BPMNDiagramImpl.class)
                .or(isType(NoneTask.class))
                .or(isType(SequenceFlow.class))
                .or(isType(Association.class))
                .negate();
    }

    private void createPaletteCustomTasksCategory(final DefaultPaletteDefinition paletteDefinition,
                                                  final Consumer<DefaultPaletteDefinition> callback) {
        final ExpandedPaletteDefinitionBuilder.ItemMessageProvider categoryMessageProvider =
                paletteDefinitionBuilder.getCategoryMessageProvider();
        final Function<String, Glyph> categoryGlyphProvider = paletteDefinitionBuilder.getCategoryGlyphProvider();
        final Collection<WorkItemDefinition> workItemDefinitions = workItemDefinitionRegistry.get().items().stream().sorted(Comparator.comparing(WorkItemDefinition::getName)).collect(Collectors.toCollection(ArrayList::new));

        if (!workItemDefinitions.isEmpty()) {
            final String customTasksTitle = categoryMessageProvider.getTitle(BPMNCategories.CUSTOM_TASKS);
            final String customTasksDesc = categoryMessageProvider.getDescription(BPMNCategories.CUSTOM_TASKS);
            final DefaultPaletteCategory workItemsCategory = new CategoryBuilder()
                    .setItemId(BPMNCategories.CUSTOM_TASKS)
                    .setTitle(customTasksTitle)
                    .setDescription(customTasksDesc)
                    .setTooltip(customTasksTitle)
                    .setGlyph(categoryGlyphProvider
                                      .apply(BPMNCategories.CUSTOM_TASKS))
                    .build();

            int i = 0;
            for (final WorkItemDefinition workItemDefinition : workItemDefinitions) {
                final CustomTask customTask = customTaskBuilder.apply(workItemDefinition);
                final DefinitionAdapter<Object> adapter =
                        definitionManager.adapters().registry().getDefinitionAdapter(customTask.getClass());
                final String category = adapter.getCategory(customTask);
                DefaultPaletteGroup subcategoryGroup = null;
                final Optional<DefaultPaletteItem> subcategoryGroupOp = workItemsCategory.getItems().stream()
                        .filter(item -> category.equals(item.getId()))
                        .findFirst();
                if (!subcategoryGroupOp.isPresent()) {
                    subcategoryGroup = new GroupBuilder()
                            .setItemId(category)
                            .setTitle(category)
                            .setDescription(category)
                            .build();
                    workItemsCategory.getItems().add(subcategoryGroup);
                } else {
                    subcategoryGroup = (DefaultPaletteGroup) subcategoryGroupOp.get();
                }
                final String defId = adapter.getId(customTask).value();
                final String title = adapter.getTitle(customTask);
                final String description = adapter.getDescription(customTask);
                final DefaultPaletteItem item =
                        new ItemBuilder()
                                .setItemId(defId)
                                .setDefinitionId(defId)
                                .setTitle(title)
                                .setDescription(description)
                                .build();
                subcategoryGroup.getItems().add(item);

                // Link the first work item definition found to the work item's palette category.
                if (0 == i) {
                    workItemsCategory.setDefinitionId(defId);
                }
                i++;
            }
            paletteDefinition.getItems().add(workItemsCategory);
        }
        callback.accept(paletteDefinition);
    }
}
