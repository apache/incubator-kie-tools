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

package org.drools.workbench.screens.guided.template.client.editor;

import java.util.function.Supplier;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(CommandDrivenErrorCallback.class)
public class GuidedRuleTemplateEditorPresenterTest {

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private ObservablePath path;

    @Mock
    private KieEditorWrapperView kieView;

    @Mock
    private OverviewWidgetPresenter overviewPresenter;

    @Mock
    private ImportsWidgetPresenter importsWidgetPresenter;

    @Mock
    private GuidedRuleTemplateEditorView view;

    @Mock
    private VersionRecordManager versionRecordManager;

    @Mock
    private GuidedRuleTemplateEditorService service;

    @Mock
    private Overview overview;

    @Mock
    private TemplateModel templateModel;

    @Mock
    private PackageDataModelOracleBaselinePayload payload;

    @Mock
    private GuidedTemplateEditorContent content;

    private Caller<GuidedRuleTemplateEditorService> serviceCaller;

    @Spy
    @InjectMocks
    private GuidedRuleTemplateEditorPresenter presenter = new GuidedRuleTemplateEditorPresenter(view);

    @Before
    public void setUp() throws Exception {
        doReturn(templateModel).when(content).getModel();
        doReturn(overview).when(content).getOverview();
        doReturn(payload).when(content).getDataModel();

        serviceCaller = new CallerMock<>(service);

        doReturn(serviceCaller).when(presenter).getService();

        doReturn(oracle).when(oracleFactory).makeAsyncPackageDataModelOracle(path, templateModel, payload);

        doReturn(path).when(versionRecordManager).getCurrentPath();
    }

    @Test
    public void testLoadContentSuccess() throws Exception {
        doReturn(content).when(service).loadContent(eq(path));
        presenter.loadContent();

        verify(view).setContent(eq(templateModel),
                                eq(oracle),
                                any(),
                                notNull(EventBus.class),
                                eq(false));

        final Imports imports = templateModel.getImports();
        verify(kieView).addImportsTab(eq(importsWidgetPresenter));
        verify(importsWidgetPresenter).setContent(eq(oracle),
                                                  same(imports),
                                                  eq(false));
    }

    @Test
    public void testLoadContentFail() throws Exception {
        doThrow(new RuntimeException()).when(service).loadContent(eq(path));
        presenter.loadContent();

        verify(view, never()).setContent(any(TemplateModel.class),
                                         any(AsyncPackageDataModelOracle.class),
                                         any(Caller.class),
                                         any(EventBus.class),
                                         anyBoolean());
    }

    @Test
    public void testGetContentSupplier() throws Exception {

        final TemplateModel content = mock(TemplateModel.class);

        doReturn(content).when(presenter).getModel();

        final Supplier<TemplateModel> contentSupplier = presenter.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() throws Exception {

        final Caller<? extends SupportsSaveAndRename<TemplateModel, Metadata>> serviceCaller = presenter.getSaveAndRenameServiceCaller();

        assertEquals(this.serviceCaller, serviceCaller);
    }
}
