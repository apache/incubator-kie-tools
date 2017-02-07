/*
 * Copyright 2017 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.PageHeader;
import org.gwtbootstrap3.client.ui.Radio;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KBaseFormViewImplTest {

    KBaseFormViewImpl view;

    @Mock
    PageHeader nameLabel;

    @Mock
    Radio equalsBehaviorIdentity;

    @Mock
    Radio eventProcessingModeStream;

    @Mock
    KSessionsPanel statefulSessionsPanel;

    @Mock
    CRUDListBox includesListBox;

    @Mock
    CRUDListBox packagesListBox;

    @Captor
    ArgumentCaptor<List> listCaptor;

    @Before
    public void setUp() throws Exception {
        view = new KBaseFormViewImpl( statefulSessionsPanel, includesListBox, packagesListBox );
        view.nameLabel = nameLabel;
        view.equalsBehaviorIdentity = equalsBehaviorIdentity;
        view.eventProcessingModeStream = eventProcessingModeStream;
    }

    @Test
    public void testClear() throws Exception {
        view.clear();

        verify( nameLabel).setText( "" );
        verify( nameLabel ).setSubText( "" );
        verify( includesListBox ).clear();
        verify( packagesListBox ).clear();
        verify( equalsBehaviorIdentity ).setValue( true );
        verify( eventProcessingModeStream ).setValue( true );
        verify( statefulSessionsPanel ).setItems( listCaptor.capture() );
        assertEquals( 0, listCaptor.getValue().size() );
    }
}
