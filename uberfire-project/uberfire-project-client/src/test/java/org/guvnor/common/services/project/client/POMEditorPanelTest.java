/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.client;

import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class POMEditorPanelTest {

    @Mock
    private POMEditorPanelView view;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private GAVPreferences gavPreferences;

    @Mock
    private ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    private POMEditorPanel panel;
    private POMEditorPanelView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        setChildGAVEdit(false);

        panel = new POMEditorPanel(view,
                                   iocManager,
                                   gavPreferences,
                                   projectScopedResolutionStrategySupplier);
        presenter = panel;

        verify(view,
               times(1)).setPresenter(presenter);

        doAnswer(invocationOnMock -> {
            ((ParameterizedCommand<GAVPreferences>) invocationOnMock.getArguments()[1]).execute(gavPreferences);
            return null;
        }).when(gavPreferences).load(any(PreferenceScopeResolutionStrategyInfo.class),
                                     any(ParameterizedCommand.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void testAddArtifactChangeHandler() {
        ArtifactIdChangeHandler handler = mock(ArtifactIdChangeHandler.class);
        panel.addArtifactIdChangeHandler(handler);

        verify(view,
               times(1)).addArtifactIdChangeHandler(handler);
    }

    @Test
    public void testAddGroupChangeHandler() {
        GroupIdChangeHandler handler = mock(GroupIdChangeHandler.class);
        panel.addGroupIdChangeHandler(handler);

        verify(view,
               times(1)).addGroupIdChangeHandler(handler);
    }

    @Test
    public void testAddVersionChangeHandler() {
        VersionChangeHandler handler = mock(VersionChangeHandler.class);
        panel.addVersionChangeHandler(handler);

        verify(view,
               times(1)).addVersionChangeHandler(handler);
    }

    @Test
    public void testLoadSingleModule() throws Exception {
        POM gavModel = createTestModel("pomName",
                                       "pomDescription",
                                       "pomUrl",
                                       "group",
                                       "artifact",
                                       "1.1.1");
        panel.setPOM(gavModel,
                     false);

        verify(view).setName("pomName");
        verify(view).setDescription("pomDescription");
        verify(view).enableGroupID();
        verify(view).enableArtifactID();
        verify(view).enableVersion();
        verify(view).hideParentGAV();
    }

    @Test
    public void testLoadMultiModule() throws Exception {
        POM gavModel = createTestModelWithParent("group",
                                                 "artifact",
                                                 "1.1.1");

        panel.setPOM(gavModel,
                     false);

        verify(view).setGAV(gavModel.getGav());
        verify(view).setTitleText("artifact");
        verify(view).setParentGAV(gavModel.getParent());
        verify(view).disableGroupID("");
        verify(view).enableArtifactID();
        verify(view).disableVersion("");
        verify(view).showParentGAV();
    }

    @Test
    public void testProjectNameValidation() throws Exception {
        panel.setValidName(true);
        verify(view).setValidName(true);

        panel.setValidName(false);
        verify(view).setValidName(false);
    }

    @Test
    public void testGroupIDValidation() throws Exception {
        panel.setValidGroupID(true);
        verify(view).setValidGroupID(true);

        panel.setValidGroupID(false);
        verify(view).setValidGroupID(false);
    }

    @Test
    public void testArtifactIDValidation() throws Exception {
        panel.setValidArtifactID(true);
        verify(view).setValidArtifactID(true);

        panel.setValidArtifactID(false);
        verify(view).setValidArtifactID(false);
    }

    @Test
    public void testVersionValidation() throws Exception {
        panel.setValidVersion(true);
        verify(view).setValidVersion(true);

        panel.setValidVersion(false);
        verify(view).setValidVersion(false);
    }

    @Test
    public void testOpenProjectContext() throws Exception {
        SyncBeanDef iocBeanDef = mock(SyncBeanDef.class);
        PlaceManager placeManager = mock(PlaceManager.class);
        when(iocBeanDef.getInstance()).thenReturn(placeManager);
        when(iocManager.lookupBean(eq(PlaceManager.class))).thenReturn(iocBeanDef);

        presenter.onOpenProjectContext();
        verify(placeManager).goTo("repositoryStructureScreen");
    }

    @Test
    public void testSetPomWhenItHasParentAndChildGAVEditIsDisabled() {
        POM pom = createTestModelWithParent("group",
                                            "artifact",
                                            "1.1.1");

        panel.setPOM(pom,
                     false);

        verify(view).enableGroupID();
        verify(view).enableArtifactID();
        verify(view).enableVersion();
        verify(view).showParentGAV();
        verify(view).disableGroupID(anyString());
        verify(view).disableVersion(anyString());
    }

    @Test
    public void testSetPomWhenItHasParentAndChildGAVEditIsEnabled() {
        POM pom = createTestModelWithParent("group",
                                            "artifact",
                                            "1.1.1");
        setChildGAVEdit(true);

        panel.setPOM(pom,
                     false);

        verify(view).enableGroupID();
        verify(view).enableArtifactID();
        verify(view).enableVersion();
        verify(view).showParentGAV();
        verify(view,
               never()).disableGroupID(anyString());
        verify(view,
               never()).disableVersion(anyString());
    }

    @Test
    public void testSetPomWhenItDoesNotHaveParent() {
        POM pom = createTestModel("group",
                                  "artifact",
                                  "1.1.1");

        panel.setPOM(pom,
                     false);

        verify(view).enableGroupID();
        verify(view).enableArtifactID();
        verify(view).enableVersion();
        verify(view).hideParentGAV();
        verify(view,
               never()).disableGroupID(anyString());
        verify(view,
               never()).disableVersion(anyString());
    }

    private POM createTestModelWithParent(final String group,
                                          final String artifact,
                                          final String version) {
        POM gavModel = createTestModel(group,
                                       artifact,
                                       version);
        gavModel.setParent(new GAV());
        return gavModel;
    }

    private POM createTestModel(final String group,
                                final String artifact,
                                final String version) {
        return new POM(new GAV(group,
                               artifact,
                               version));
    }

    private POM createTestModel(final String name,
                                final String description,
                                final String url,
                                final String group,
                                final String artifact,
                                final String version) {
        return new POM(name,
                       description,
                       url,
                       new GAV(group,
                               artifact,
                               version));
    }

    private void setChildGAVEdit(final boolean value) {
        doReturn(value).when(gavPreferences).isChildGAVEditEnabled();
    }
}
