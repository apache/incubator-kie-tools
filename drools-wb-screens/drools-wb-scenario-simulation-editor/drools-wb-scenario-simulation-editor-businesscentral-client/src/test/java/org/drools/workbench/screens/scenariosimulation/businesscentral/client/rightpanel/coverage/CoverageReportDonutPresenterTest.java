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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CoverageReportDonutPresenterTest {

    @Mock
    DisplayerLocator displayerLocator;

    @Mock
    Elemental2DomUtil elemental2DomUtil;

    @Mock
    DisplayerCoordinator displayerCoordinator;

    @InjectMocks
    CoverageReportDonutPresenter donutPresenter;

    @Mock
    Displayer displayer;

    @Before
    public void setUp() throws Exception {
        when(displayerLocator.lookupDisplayer(any())).thenReturn(displayer);
    }

    @Test
    public void showSuccessFailureDiagramSecondRun() {
        HTMLDivElement container = mock(HTMLDivElement.class);
        donutPresenter.init(container);

        donutPresenter.showCoverageReport(1, 1, "holeLabel");

        verify(elemental2DomUtil, never()).removeAllElementChildren(any());
        verify(displayerCoordinator, never()).removeDisplayer(any());

        donutPresenter.showCoverageReport(2, 2, "holeLabel");

        verify(elemental2DomUtil).removeAllElementChildren(any());
        verify(displayerCoordinator).removeDisplayer(any());
    }
}