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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssert;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupView;
import org.uberfire.experimental.client.editor.group.TestExperimentalFeaturesGroup;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.editor.FeaturesEditorService;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeaturesRegistryImpl;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry.FEATURE_1;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentalFeaturesEditorScreenTest {

    private static final String USER_NAME = "user";

    @Mock
    private ManagedInstance<ExperimentalFeatureEditor> editorInstance;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClientExperimentalFeaturesRegistryService registryService;

    private ExperimentalFeatureDefRegistry defRegistry;

    private ExperimentalFeaturesRegistryImpl registry;

    private List<ExperimentalFeatureImpl> features = new ArrayList<>();

    @Mock
    private ExperimentalFeaturesEditorScreenView view;

    @Mock
    private ManagedInstance<ExperimentalFeaturesGroup> instance;

    @Mock
    private FeaturesEditorService featuresEditorService;

    @Mock
    private AuthorizationManager authorizationManager;

    private SessionInfoMock sessionInfo;

    private CallerMock<FeaturesEditorService> editorServiceCaller;

    private ExperimentalFeaturesEditorScreen presenter;

    private List<TestExperimentalFeaturesGroup> groups = new ArrayList<>();

    @Before
    public void init() {

        when(editorInstance.get()).thenReturn(mock(ExperimentalFeatureEditor.class));

        sessionInfo = new SessionInfoMock(USER_NAME);

        defRegistry = new TestExperimentalFeatureDefRegistry();

        features = defRegistry.getAllFeatures().stream()
                .map(def -> new ExperimentalFeatureImpl(def.getId(), false))
                .collect(Collectors.toList());

        registry = new ExperimentalFeaturesRegistryImpl(features);

        when(registryService.getFeaturesRegistry()).thenReturn(registry);

        when(instance.get()).thenAnswer((Answer<ExperimentalFeaturesGroup>) invocationOnMock -> createGroup());

        editorServiceCaller = new CallerMock<>(featuresEditorService);

        presenter = new ExperimentalFeaturesEditorScreen(translationService, registryService, defRegistry, view, instance, editorServiceCaller, sessionInfo, authorizationManager);
    }

    @Test
    public void testBasicFunctions() {
        presenter.init();

        verify(view).init(presenter);

        presenter.getTitle();

        verify(translationService).getTranslation(UberfireExperimentalConstants.experimentalFeaturesTitle);

        presenter.clear();

        verifyClear();

        assertSame(view, presenter.getView());
    }

    @Test
    public void testShowGroupsWithPermissions() {

        testShowGroups(true);
    }

    @Test
    public void testShowGroupsWithoutPermissions() {

        testShowGroups(false);
    }

    private void testShowGroups(final boolean withPermissions) {

        when(authorizationManager.authorize(any(ResourceType.class), any(ResourceAction.class), any(User.class))).thenReturn(withPermissions);

        presenter.show();

        verifyClear();

        int expectedGroups = 2;

        final List<String> expectedGroupNames = new ArrayList<>();
        expectedGroupNames.add(UberfireExperimentalConstants.experimentalFeaturesGeneralGroupKey);
        expectedGroupNames.add(TestExperimentalFeatureDefRegistry.GROUP);

        if (withPermissions) {
            expectedGroups++;
            expectedGroupNames.add(UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey);
        }

        verify(instance, times(expectedGroups)).get();

        Map<String, TestExperimentalFeaturesGroup> groupMap = groups.stream()
                .collect(Collectors.toMap(TestExperimentalFeaturesGroup::getLabelKey, group -> group));

        MapAssert<String, TestExperimentalFeaturesGroup> mapAssert = Assertions.assertThat(groupMap);

        mapAssert.hasSize(expectedGroups)
                .containsKeys(expectedGroupNames.toArray(new String[expectedGroups]));
    }

    @Test
    public void testModificationCallback() {
        EditableExperimentalFeature feature = new EditableExperimentalFeature(FEATURE_1, true);

        presenter.doSave(feature);

        verify(featuresEditorService).save(feature);
        verify(registryService).updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled());
    }

    private ExperimentalFeaturesGroup createGroup() {
        TestExperimentalFeaturesGroup group = new TestExperimentalFeaturesGroup(mock(ExperimentalFeaturesGroupView.class), mock(TranslationService.class), editorInstance);

        groups.add(group);

        return group;
    }

    private void verifyClear() {
        verify(view).clear();
        verify(instance).destroyAll();
    }
}
