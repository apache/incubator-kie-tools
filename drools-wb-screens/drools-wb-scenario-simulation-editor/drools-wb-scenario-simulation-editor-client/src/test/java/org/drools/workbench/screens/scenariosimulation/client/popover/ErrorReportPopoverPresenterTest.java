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

package org.drools.workbench.screens.scenariosimulation.client.popover;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.APPLY_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.ERROR_CONTENT_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.ERROR_TITLE_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.KEEP_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.MX;
import static org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverViewTest.MY;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ErrorReportPopoverPresenterTest {

    private ErrorReportPopoverPresenter errorReportPopupPresenter;

    @Mock
    private ErrorReportPopoverView errorReportPopoverViewMock;

    @Mock
    private Command applyCommandMock;

    @Before
    public void setUp() {
        errorReportPopupPresenter = spy(new ErrorReportPopoverPresenter() {
            {
                this.errorReportPopupView = errorReportPopoverViewMock;
            }
        });
    }

    @Test
    public void setupWithCommand() {
        errorReportPopupPresenter.setup(ERROR_TITLE_TEXT, ERROR_CONTENT_TEXT, KEEP_TEXT, APPLY_TEXT, applyCommandMock, MX, MY, PopoverView.Position.RIGHT);
        verify(errorReportPopoverViewMock, times(1)).setup(eq(ERROR_TITLE_TEXT),
                                                           eq(ERROR_CONTENT_TEXT),
                                                           eq(KEEP_TEXT),
                                                           eq(APPLY_TEXT),
                                                           eq(applyCommandMock),
                                                           eq(MX),
                                                           eq(MY),
                                                           eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void setupWithoutCommand() {
        errorReportPopupPresenter.setup(ERROR_TITLE_TEXT, ERROR_CONTENT_TEXT, KEEP_TEXT, MX, MY, PopoverView.Position.RIGHT);
        verify(errorReportPopoverViewMock, times(1)).setup(eq(ERROR_TITLE_TEXT),
                                                           eq(ERROR_CONTENT_TEXT),
                                                           eq(KEEP_TEXT),
                                                           eq(MX),
                                                           eq(MY),
                                                           eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void show() {
        errorReportPopupPresenter.show();
        verify(errorReportPopoverViewMock, times(1)).show();
    }

    @Test
    public void hide() {
        errorReportPopupPresenter.hide();
        verify(errorReportPopoverViewMock, times(1)).hide();
    }

    @Test
    public void isShown() {
        errorReportPopupPresenter.isShown();
        verify(errorReportPopoverViewMock, times(1)).isShown();
    }

    @Test
    public void getActualHeight() {
        errorReportPopupPresenter.getActualHeight();
        verify(errorReportPopoverViewMock, times(1)).getActualHeight();
    }
}