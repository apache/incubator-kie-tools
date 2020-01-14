/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.verifier.reporting.client.panel;

import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBaseView;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisDockPlaceHolderTest {

    @Mock
    private DockPlaceHolderBaseView view;

    @InjectMocks
    private AnalysisDockPlaceHolder analysisDockPlaceHolder = new AnalysisDockPlaceHolder();

    @Test
    public void presenterIsSet() {
        analysisDockPlaceHolder.init(view);
        verify(view).setPresenter(analysisDockPlaceHolder);
    }

    @Test
    public void viewIsNotNull() {
        assertEquals(view, analysisDockPlaceHolder.getView());
    }

    @Test
    public void setContent() {
        final IsWidget widget = mock(IsWidget.class);

        analysisDockPlaceHolder.setView(widget);

        verify(view).clear();
        verify(view).setWidget(widget);
    }

}