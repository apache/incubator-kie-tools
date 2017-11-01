/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SummaryPageViewImplTest {

    @Mock
    private SummaryPage presenter;
    private SummaryPageViewImpl view;

    @Before
    public void setup() {
        view = new SummaryPageViewImpl();
        view.init( presenter );
    }

    @Test
    public void testFileNameChange() {
        view.handleFileNameInputKeyUp();
        verify( presenter ).stateChanged();
    }

    @Test
    public void warningShownOnInvalidFileName() {
        view.setValidBaseFileName( false );
        verify( view.baseFileNameHelp ).setVisible( true );
        verify( view.baseFileNameContainer ).addStyleName( ValidationState.ERROR.getCssName() );
    }

    @Test
    public void warningNotShownOnValidFileName() {
        view.setValidBaseFileName( true );
        verify( view.baseFileNameHelp ).setVisible( false );
        verify( view.baseFileNameContainer ).removeStyleName( ValidationState.ERROR.getCssName() );
    }
}