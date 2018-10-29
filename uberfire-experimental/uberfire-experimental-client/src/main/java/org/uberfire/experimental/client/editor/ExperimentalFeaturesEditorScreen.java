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

package org.uberfire.experimental.client.editor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.editor.FeaturesEditorService;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.experimental.service.security.GlobalExperimentalFeatureAction;
import org.uberfire.experimental.service.security.GlobalExperimentalFeatureResourceType;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
@WorkbenchScreen(identifier = ExperimentalFeaturesEditorScreen.SCREEN_ID)
public class ExperimentalFeaturesEditorScreen implements ExperimentalFeaturesEditorScreenView.Presenter {

    public static final String SCREEN_ID = "ExperimentalFeaturesEditor";

    private final TranslationService translationService;
    private final ClientExperimentalFeaturesRegistryService registryService;
    private final ExperimentalFeatureDefRegistry defRegistry;
    private final ExperimentalFeaturesEditorScreenView view;
    private final ManagedInstance<ExperimentalFeaturesGroup> groupsInstance;
    private final Caller<FeaturesEditorService> editorService;
    private final SessionInfo sessionInfo;
    private final AuthorizationManager authorizationManager;

    @Inject
    public ExperimentalFeaturesEditorScreen(final TranslationService translationService,
                                            final ClientExperimentalFeaturesRegistryService registryService,
                                            final ExperimentalFeatureDefRegistry defRegistry,
                                            final ExperimentalFeaturesEditorScreenView view,
                                            final ManagedInstance<ExperimentalFeaturesGroup> groupsInstance,
                                            final Caller<FeaturesEditorService> editorService,
                                            final SessionInfo sessionInfo,
                                            final AuthorizationManager authorizationManager) {
        this.translationService = translationService;
        this.registryService = registryService;
        this.defRegistry = defRegistry;
        this.view = view;
        this.groupsInstance = groupsInstance;
        this.editorService = editorService;
        this.sessionInfo = sessionInfo;
        this.authorizationManager = authorizationManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @OnOpen
    public void show() {
        clear();

        ExperimentalFeaturesRegistry registry = registryService.getFeaturesRegistry();

        if (registry != null) {
            Map<String, Set<ExperimentalFeature>> groupedFeatures = registry.getAllFeatures().stream()
                    .collect(Collectors.groupingBy(this::getFeatureGroupName, Collectors.toSet()));

            TreeSet<ExperimentalFeaturesGroup> groups = groupedFeatures.entrySet()
                    .stream()
                    .map(this::getFeaturesGroup)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(TreeSet::new));

            if (!groups.isEmpty()) {
                groups.first().expand();
                groups.forEach(view::add);
            }
        }
    }

    private String getFeatureGroupName(ExperimentalFeature feature) {
        ExperimentalFeatureDefinition definition = defRegistry.getFeatureById(feature.getFeatureId());

        if (definition.isGlobal()) {
            return UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey;
        }

        if (definition.getGroup().isEmpty()) {
            return UberfireExperimentalConstants.experimentalFeaturesGeneralGroupKey;
        }

        return definition.getGroup();
    }

    private ExperimentalFeaturesGroup getFeaturesGroup(Map.Entry<String, Set<ExperimentalFeature>> entry) {
        String groupName = entry.getKey();

        if (groupName.equals(UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey)) {
            if (!authorizationManager.authorize(new GlobalExperimentalFeatureResourceType(), GlobalExperimentalFeatureAction.EDIT, sessionInfo.getIdentity())) {
                return null;
            }
        }

        ExperimentalFeaturesGroup group = groupsInstance.get();

        group.init(groupName, entry.getValue(), this::doSave);

        return group;
    }

    protected void doSave(final EditableExperimentalFeature feature) {
        editorService.call((RemoteCallback<Void>) aVoid -> registryService.updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled())).save(feature);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation(UberfireExperimentalConstants.experimentalFeaturesTitle);
    }

    @WorkbenchPartView
    public ExperimentalFeaturesEditorScreenView getView() {
        return view;
    }

    @PreDestroy
    public void clear() {
        view.clear();
        groupsInstance.destroyAll();
    }
}
