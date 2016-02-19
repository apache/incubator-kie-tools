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

package org.kie.workbench.common.screens.server.management.client.widget.card.title;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.ReleaseId;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InfoTitlePresenterTest {

    @Mock
    InfoTitlePresenter.View view;

    @InjectMocks
    InfoTitlePresenter presenter;

    @Test
    public void testInit() {
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testSetup() {
        final ReleaseId releaseId = new ReleaseId( "com.company", "artifact-id", "1.0.0" );
        presenter.setup( releaseId );
        verify( view ).setup( releaseId.getGroupId(), releaseId.getArtifactId() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSetup() {
        presenter.setup( null );
    }

}