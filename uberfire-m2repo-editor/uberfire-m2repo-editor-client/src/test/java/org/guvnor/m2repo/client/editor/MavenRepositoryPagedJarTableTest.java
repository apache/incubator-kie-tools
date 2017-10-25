/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MavenRepositoryPagedJarTableTest {

    @Mock
    private ArtifactListPresenter presenter;

    @Mock
    private ArtifactListView view;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    private MavenRepositoryPagedJarTable table;

    @Before
    public void setup() {
        when(presenter.getView()).thenReturn(view);

        final MavenRepositoryPagedJarTable wrapped = new MavenRepositoryPagedJarTable(presenter,
                                                                                      authorizationManager,
                                                                                      identity);
        table = spy(wrapped);
    }

    @Test
    public void downloadJARButtonIncludedWhenUserHasPermission() {
        when(authorizationManager.authorize(eq(MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD),
                                            eq(identity))).thenReturn(true);

        table.init();

        verify(table,
               times(1)).addDownloadJARButton();
    }

    @Test
    public void downloadJARButtonExcludedWhenUserLacksPermission() {
        when(authorizationManager.authorize(eq(MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD),
                                            eq(identity))).thenReturn(false);

        table.init();

        verify(table,
               never()).addDownloadJARButton();
    }
}
