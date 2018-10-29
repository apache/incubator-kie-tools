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

package org.uberfire.experimental.client.editor.group;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorView;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentalFeaturesGroupTest {

    private static final String GROUP_KEY = "group";

    private static final String ANOTHER_GROUP_KEY = "group2";

    @Mock
    private ExperimentalFeaturesGroupView view;

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<ExperimentalFeatureEditor> editorInstance;

    @Mock
    private ParameterizedCommand<EditableExperimentalFeature> callback;

    private TestExperimentalFeatureDefRegistry defRegistry;

    private List<ExperimentalFeatureImpl> features;
    private List<ExperimentalFeatureEditor> editors;

    private ExperimentalFeaturesGroup group;

    @Before
    public void init() {

        defRegistry = new TestExperimentalFeatureDefRegistry();

        features = defRegistry.getUserFeatures().stream()
                .map(definition -> new ExperimentalFeatureImpl(definition.getId(), false))
                .collect(Collectors.toList());

        editors = new ArrayList<>();

        when(editorInstance.get()).then(invocationOnMock -> {
            ExperimentalFeatureEditorView editorView = mock(ExperimentalFeatureEditorView.class);

            ExperimentalFeatureEditor editor = spy(new ExperimentalFeatureEditor(defRegistry, translationService, editorView));

            doAnswer((Answer<Void>) invocationOnMock1 -> {
                Boolean enabled = (Boolean) invocationOnMock1.getArguments()[0];
                editor.notifyChange(enabled);
                return null;
            }).when(editorView).setEnabled(anyBoolean());

            editors.add(editor);

            return editor;
        });

        group = new ExperimentalFeaturesGroup(view, translationService, editorInstance);

        verify(view).init(group);

        group.getElement();

        verify(view).getElement();
    }

    @Test
    public void testLoadWithEnableAllAnchor() {
        testLoad(true);
    }

    @Test
    public void testLoadWithDisableAllAnchor() {
        testLoad(false);
    }

    @Test
    public void testPressEnableAll() {

        testLoad(true);

        testAnchorPress(false);
    }

    @Test
    public void testPressDisableAll() {

        testLoad(false);

        testAnchorPress(true);
    }

    @Test
    public void testExpandAndCollapse() {

        assertFalse(group.isExpanded());

        group.collapse();

        verify(view, never()).collapse();

        group.expand();

        assertTrue(group.isExpanded());
        verify(view).expand();

        group.expand();

        verify(view).expand();

        group.collapse();

        assertFalse(group.isExpanded());
        verify(view).collapse();

        group.notifyExpand();
        verify(view).arrangeCaret();
    }

    @Test
    public void testGroupsSorting() {

        ExperimentalFeaturesGroup generalGroup = new ExperimentalFeaturesGroup(view, translationService, editorInstance);
        generalGroup.init(UberfireExperimentalConstants.experimentalFeaturesGeneralGroupKey, new ArrayList<>(), callback);

        ExperimentalFeaturesGroup secondGroup = new ExperimentalFeaturesGroup(view, translationService, editorInstance);
        secondGroup.init(GROUP_KEY, new ArrayList<>(), callback);

        ExperimentalFeaturesGroup thirdGroup = new ExperimentalFeaturesGroup(view, translationService, editorInstance);
        thirdGroup.init(ANOTHER_GROUP_KEY, new ArrayList<>(), callback);

        ExperimentalFeaturesGroup adminGroup = new ExperimentalFeaturesGroup(view, translationService, editorInstance);
        adminGroup.init(UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey, new ArrayList<>(), callback);

        TreeSet<ExperimentalFeaturesGroup> groups = new TreeSet<>();
        groups.add(secondGroup);
        groups.add(adminGroup);
        groups.add(generalGroup);
        groups.add(thirdGroup);

        Assertions.assertThat(groups)
                .hasSize(4)
                .containsExactly(generalGroup, secondGroup, thirdGroup, adminGroup);
    }

    private void testAnchorPress(boolean enabled) {
        group.doEnableAll();

        editors.forEach(editor -> verify(editor).enable());

        verify(callback, times(3)).execute(any());

        checkSetEnableAllLabel(enabled, 2);
    }

    private void testLoad(boolean enableAll) {

        if (!enableAll) {
            features.forEach(feature -> feature.setEnabled(true));
        }

        group.init(GROUP_KEY, new ArrayList<>(features), callback);

        checkClear();

        verify(view).setLabel(GROUP_KEY);

        checkSetEnableAllLabel(enableAll, 1);
    }

    private void checkSetEnableAllLabel(boolean isEnabled, int times) {
        verify(view, times(times)).setEnableAllLabel(anyString());

        if (isEnabled) {
            verify(translationService).getTranslation(UberfireExperimentalConstants.ExperimentalFeaturesGroupEnableAll);
        } else {
            verify(translationService).getTranslation(UberfireExperimentalConstants.ExperimentalFeaturesGroupDisableAll);
        }
    }

    @Test
    public void testClear() {
        group.clear();

        checkClear();
    }

    private void checkClear() {
        verify(view).clear();
        verify(editorInstance).destroyAll();
    }
}
