/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryStructureDataPresenterTest {

    @GwtMock
    private Widget widget;

    @Mock
    private RepositoryStructureDataView view;

    private RepositoryStructureDataPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new RepositoryStructureDataPresenter(view);
    }

    @Test
    public void testSetGav() throws Exception {
        presenter.setGav(new GAV("groupId",
                                 "artifactId",
                                 "1.0.0"));

        verify(view).setGroupId("groupId");
        verify(view).setArtifactId("artifactId");
        verify(view).setVersion("1.0.0");
    }

    @Test
    public void testGetGav() throws Exception {
        when(view.getGroupId()).thenReturn("groupId");
        when(view.getArtifactId()).thenReturn("artifactId");
        when(view.getVersion()).thenReturn("1.0.0");

        final GAV gav = presenter.getGav();

        assertEquals("groupId",
                     gav.getGroupId());
        assertEquals("artifactId",
                     gav.getArtifactId());
        assertEquals("1.0.0",
                     gav.getVersion());
    }

    @Test
    public void testConstructor() throws Exception {
        verify(view).clear();
        verify(view).setCreateStructureText();
        verify(view,
               never()).setEditModuleVisibility(anyBoolean());
    }

    @Test
    public void testMode_CREATE_STRUCTURE() throws Exception {
        reset(view);

        presenter.setMode(RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE);
        verify(view).setCreateStructureText();
        verify(view,
               never()).setEditModuleVisibility(anyBoolean());
    }

    @Test
    public void testMode_EDIT_SINGLE_MODULE_PROJECT() throws Exception {
        presenter.setMode(RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT);
        verify(view).setEditSingleModuleProjectText();
        verify(view).setEditModuleVisibility(true);
    }

    @Test
    public void testMode_EDIT_MULTI_MODULE_PROJECT() throws Exception {
        presenter.setMode(RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT);
        verify(view).setEditMultiModuleProjectText();

        verify(view).setEditModuleVisibility(true);
    }

    @Test
    public void testMode_EDIT_UNMANAGED_REPOSITORY() throws Exception {
        presenter.setMode(RepositoryStructureDataView.ViewMode.EDIT_UNMANAGED_REPOSITORY);
        verify(view).setEditUnmanagedRepositoryText();

        verify(view).setEditModuleVisibility(false);
    }

    @Test
    public void testClear() throws Exception {
        reset(view);

        presenter.clear();
        verify(view).clear();
    }

    @Test
    public void testAsWidget() throws Exception {
        when(view.asWidget()).thenReturn(widget);

        assertEquals(widget,
                     presenter.asWidget());
    }
}