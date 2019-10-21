/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.verifier.reporting.client.panel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AnalysisReportScreenViewImplTest {

    @Mock
    private Element progressTooltip;

    @Mock
    private Style style;

    private AnalysisReportScreenViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new AnalysisReportScreenViewImpl();
        view.progressTooltip = progressTooltip;

        when(progressTooltip.getStyle()).thenReturn(style);
    }

    @Test
    public void testHideProgressStatus() {
        view.hideProgressStatus();

        verify(style).setVisibility(Style.Visibility.HIDDEN);
    }

    @Test
    public void testStatusComplete() {
        view.showStatusComplete();

        verify(style).setVisibility(Style.Visibility.VISIBLE);
    }

    @Test
    public void testStatusTitle() {
        view.showStatusTitle(0, 1, 1);

        verify(style).setVisibility(Style.Visibility.VISIBLE);
    }
}
