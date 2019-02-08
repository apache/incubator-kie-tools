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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DeploymentPopupTest {

    static final String TEMPLATE1_ID = "id1";
    static final String TEMPLATE1_NAME = "name1";

    static final String CONTAINER1 = "container1";

    static final String TEMPLATE2_ID = "id2";
    static final String TEMPLATE2_NAME = "name2";

    static final String CONTAINER2 = "container2";

    static final String CONTAINER3 = "container3";

    private ServerTemplate template1;
    private ServerTemplate template2;

    private List<ServerTemplate> templates;

    @Mock
    private DeploymentPopupView view;

    @Mock
    private Command callback;

    @Mock
    private Command cancelCallback;

    private BuildExecutionContext context;

    private DeploymentPopup popup;

    @Before
    public void init() {
        template1 = new ServerTemplate(TEMPLATE1_ID, TEMPLATE1_NAME);
        template1.addContainerSpec(new ContainerSpec(CONTAINER1, CONTAINER1, null, null, null, null));

        template2 = new ServerTemplate(TEMPLATE2_ID, TEMPLATE2_NAME);
        template2.addContainerSpec(new ContainerSpec(CONTAINER2, CONTAINER2, null, null, null, null));

        templates = Arrays.asList(template1, template2);

        popup = new DeploymentPopup(view);

        verify(view).init(popup);
    }

    @Test
    public void testSingleTemplate() {
        context = new BuildExecutionContext(CONTAINER3, CONTAINER3, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.SINGLE_SERVER, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER3), eq(true));
        verify(view).initContainerAlias(eq(CONTAINER3), eq(true));
        verify(view).initStartContainer(eq(true), eq(true));

        verify(view, never()).initServerTemplates(any(), any());
        verify(view).disableServerTemplates();

        initViewWithValues(context.getContainerId(), context.getContainerAlias(), context.isStartContainer(), template1.getId());

        popup.onOk();

        verify(view).clearValidations();
        verify(view).hide();
        verify(callback).execute();
    }

    @Test
    public void testSingleTemplateWithValidationFailure() {
        context = new BuildExecutionContext(CONTAINER3, CONTAINER3, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.SINGLE_SERVER, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER3), eq(true));
        verify(view).initContainerAlias(eq(CONTAINER3), eq(true));
        verify(view).initStartContainer(eq(true), eq(true));

        verify(view, never()).initServerTemplates(any(), any());
        verify(view).disableServerTemplates();

        popup.onOk();

        verify(view).clearValidations();

        verify(view).invalidateContainerId(any());
        verify(view).invalidateContainerAlias(any());

        verify(view, never()).hide();
        verify(callback, never()).execute();
    }

    @Test
    public void testMultipleTemplate() {
        context = new BuildExecutionContext(CONTAINER3, CONTAINER3, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.MULTIPLE_SERVER, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER3), eq(true));
        verify(view).initContainerAlias(eq(CONTAINER3), eq(true));
        verify(view).initStartContainer(eq(true), eq(true));

        verify(view).initServerTemplates(any(), any());
        verify(view, never()).disableServerTemplates();

        initViewWithValues(context.getContainerId(), context.getContainerAlias(), context.isStartContainer(), template1.getId());

        popup.onOk();

        verify(view).clearValidations();
        verify(view).hide();
        verify(callback).execute();
    }

    @Test
    public void testMultipleTemplateWithValidationFailure() {
        context = new BuildExecutionContext(CONTAINER1, CONTAINER1, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.MULTIPLE_SERVER, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER1), eq(true));
        verify(view).initContainerAlias(eq(CONTAINER1), eq(true));
        verify(view).initStartContainer(eq(true), eq(true));

        verify(view).initServerTemplates(any(), any());
        verify(view, never()).disableServerTemplates();

        initViewWithValues(CONTAINER1, null, context.isStartContainer(), TEMPLATE1_ID);

        popup.onOk();

        verify(view).clearValidations();

        verify(view).invalidateContainerId(any());
        verify(view).invalidateContainerAlias(any());

        verify(view, never()).hide();
        verify(callback, never()).execute();
    }

    @Test
    public void testMultipleForcedTemplate() {
        context = new BuildExecutionContext(CONTAINER3, CONTAINER3, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.MULTIPLE_SERVER_FORCED, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER3), eq(false));
        verify(view).initContainerAlias(eq(CONTAINER3), eq(false));
        verify(view).initStartContainer(eq(true), eq(false));

        verify(view).initServerTemplates(any(), any());
        verify(view, never()).disableServerTemplates();

        initViewWithValues(context.getContainerId(), context.getContainerAlias(), context.isStartContainer(), template1.getId());

        popup.onOk();

        verify(view).clearValidations();
        verify(view).hide();
        verify(callback).execute();
    }

    @Test
    public void testMultipleForcedTemplateWithValidationFailure() {
        context = new BuildExecutionContext(CONTAINER1, CONTAINER1, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.MULTIPLE_SERVER_FORCED, () -> templates, callback, cancelCallback);

        popup.show(driver);

        verify(view).initContainerId(eq(CONTAINER1), eq(false));
        verify(view).initContainerAlias(eq(CONTAINER1), eq(false));
        verify(view).initStartContainer(eq(true), eq(false));

        verify(view).initServerTemplates(any(), any());
        verify(view, never()).disableServerTemplates();

        initViewWithValues(CONTAINER1, CONTAINER1, context.isStartContainer(), null);

        popup.onOk();

        verify(view).clearValidations();

        verify(view).invalidateContainerServerTemplate(any());

        verify(view, never()).hide();
        verify(callback, never()).execute();
    }

    @Test
    public void testOnCancel() {
        context = new BuildExecutionContext(CONTAINER1, CONTAINER1, null);
        context.setServerTemplate(template1);

        DefaultDeploymentPopupDriver driver = new DefaultDeploymentPopupDriver(context, DeploymentPopup.Mode.MULTIPLE_SERVER_FORCED, () -> templates, callback, cancelCallback);

        popup.show(driver);

        popup.onCancel();

        verify(view).hide();

        verify(cancelCallback).execute();
    }

    private void initViewWithValues(String containerId, String containerAlias, boolean isStartContainer, String templateId) {
        when(view.getContainerId()).thenReturn(containerId);
        when(view.getContainerAlias()).thenReturn(containerAlias);
        when(view.isStartContainer()).thenReturn(isStartContainer);
        when(view.getServerTemplate()).thenReturn(templateId);
    }
}
