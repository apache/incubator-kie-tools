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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WithClassesToStub({Text.class, BaseModal.class})
@RunWith(GwtMockitoTestRunner.class)
public class GuidedRuleEditorPresenterTest {

    private GuidedEditorContent guidedEditorContent;

    @Mock
    private KieEditorWrapperView kieEditorWrapperView;

    @Mock
    private OverviewWidgetPresenter overviewWidgetPresenter;

    @Mock
    private ManagedInstance<RuleModellerActionPlugin> actionPluginInstance;

    @Mock
    private ImportsWidgetPresenter importsWidgetPresenter;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private GuidedRuleEditorView view;

    @Mock
    private ObservablePath resourcePath;

    @Mock
    private VersionRecordManager versionRecordManager;

    @Mock
    private GuidedRuleEditorService service;

    private CallerMock<GuidedRuleEditorService> serviceCaller;

    @Mock
    private RuleNamesService ruleNamesService;

    private CallerMock<RuleNamesService> ruleNamesServiceCaller;

    @Mock
    private RuleModel ruleModel;

    @Mock
    private Imports imports;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private PackageDataModelOracleBaselinePayload payload;

    @Mock
    private Overview overview;

    @Spy
    @InjectMocks
    private GuidedRuleEditorPresenter presenter = new GuidedRuleEditorPresenter(view);

    @Captor
    private ArgumentCaptor<List<RuleModellerActionPlugin>> pluginsListCaptor;

    @Before
    public void setUp() throws Exception {
        guidedEditorContent = new GuidedEditorContent(ruleModel,
                                                      overview,
                                                      payload);
        serviceCaller = new CallerMock<>(service);
        ruleNamesServiceCaller = new CallerMock<>(ruleNamesService);

        doReturn(imports).when(ruleModel).getImports();

        doReturn(oracle).when(oracleFactory).makeAsyncPackageDataModelOracle(resourcePath, ruleModel, payload);

        doReturn(resourcePath).when(versionRecordManager).getCurrentPath();
        doReturn(resourcePath).when(versionRecordManager).getPathToLatest();

        doReturn(guidedEditorContent).when(service).loadContent(resourcePath);

        doReturn(serviceCaller).when(presenter).getService();
        doReturn(ruleNamesServiceCaller).when(presenter).getRuleNamesService();
        doReturn(versionRecordManager).when(presenter).getVersionRecordManager();
    }

    @Test
    public void testLoadContentSuccess() throws Exception {
        final RuleModellerActionPlugin pluginOne = mock(RuleModellerActionPlugin.class);
        final RuleModellerActionPlugin pluginTwo = mock(RuleModellerActionPlugin.class);

        doAnswer(invocationOnMock -> {
            final Consumer<RuleModellerActionPlugin> consumer = invocationOnMock.getArgument(0, Consumer.class);
            consumer.accept(pluginOne);
            consumer.accept(pluginTwo);
            return null;
        }).when(actionPluginInstance).forEach(any());

        presenter.loadContent();

        verify(kieEditorWrapperView).clear();
        verify(kieEditorWrapperView).addMainEditorPage(view);
        verify(kieEditorWrapperView).addOverviewPage(any(), any());
        verify(kieEditorWrapperView).addImportsTab(eq(importsWidgetPresenter));
        verify(kieEditorWrapperView).addSourcePage(any());
        verify(oracleFactory).makeAsyncPackageDataModelOracle(resourcePath, ruleModel, payload);
        verify(overviewWidgetPresenter).setContent(overview, resourcePath);
        verify(importsWidgetPresenter).setContent(oracle, imports, false);
        verify(view).hideBusyIndicator();
        verify(view).setContent(eq(ruleModel),
                                pluginsListCaptor.capture(),
                                eq(oracle),
                                eq(ruleNamesServiceCaller),
                                eq(false),
                                eq(false));

        Assertions.assertThat(pluginsListCaptor.getValue()).containsExactly(pluginOne, pluginTwo);
    }

    @Test
    public void testLoadContentFail() throws Exception {
        doThrow(new RuntimeException()).when(service).loadContent(resourcePath);

        presenter.loadContent();

        verify(kieEditorWrapperView, never()).clear();
        verify(kieEditorWrapperView, never()).addMainEditorPage(view);
        verify(kieEditorWrapperView, never()).addOverviewPage(any(), any());
        verify(kieEditorWrapperView, never()).addImportsTab(any());
        verify(kieEditorWrapperView, never()).addSourcePage(any());
        verify(oracleFactory, never()).makeAsyncPackageDataModelOracle(resourcePath, ruleModel, payload);
        verify(overviewWidgetPresenter, never()).setContent(overview, resourcePath);
        verify(importsWidgetPresenter, never()).setContent(oracle, imports, false);
        verify(view).hideBusyIndicator();
    }

    @Test
    public void testGetContentSupplier() throws Exception {

        final RuleModel content = mock(RuleModel.class);

        doReturn(content).when(view).getContent();

        final Supplier<RuleModel> contentSupplier = presenter.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() throws Exception {

        final Caller<? extends SupportsSaveAndRename<RuleModel, Metadata>> serviceCaller = presenter.getSaveAndRenameServiceCaller();

        assertEquals(this.serviceCaller, serviceCaller);
    }
}
