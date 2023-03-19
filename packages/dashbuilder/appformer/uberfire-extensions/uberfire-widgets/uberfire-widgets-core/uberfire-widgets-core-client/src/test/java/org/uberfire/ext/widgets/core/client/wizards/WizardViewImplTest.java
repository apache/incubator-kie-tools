/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.core.client.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.NavPills;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class WizardViewImplTest {

    @Mock
    NavPills sideBar;

    @Mock
    WizardPopupFooter footer;

    @Mock
    AbstractWizard presenter;

    @Mock
    SyncBeanManager iocBeanManager;

    List<WizardPageTitle> pageTitleWidgets = new ArrayList<>();

    @Mock
    WizardViewImpl view;

    @Before
    public void init() {
        view.sideBar = sideBar;
        view.footer = footer;
        view.iocBeanManager = iocBeanManager;
        view.pageTitleWidgets = pageTitleWidgets;

        doCallRealMethod().when(view).setCompletionStatus(anyBoolean());
        doCallRealMethod().when(view).onUnload();
        doCallRealMethod().when(view).setPageTitles(anyList());
        doCallRealMethod().when(view).hide();
    }

    @Test
    public void testSetCompletionStatusTrue() {
        view.setCompletionStatus(true);

        verify(view.footer,
               times(1)).enableFinishButton(true);
    }

    @Test
    public void testSetCompletionStatusFalse() {
        view.setCompletionStatus(false);

        verify(view.footer,
               times(1)).enableFinishButton(false);
    }

    @Test
    public void testOnUnload() {
        doReturn(presenter).when(view).getPresenter();

        view.onUnload();

        verify(presenter).close();
        verify(view).parentOnUnload();
    }

    @Test
    public void testHide() {
        final WizardPage firstWizardPageMock = mock(WizardPage.class);
        final WizardPage secondWizardPageMock = mock(WizardPage.class);

        final WizardPageTitle firstTitleMock = mock(WizardPageTitle.class);
        final WizardPageTitle secondTitleMock = mock(WizardPageTitle.class);

        doReturn(firstTitleMock).when(view).makeWizardPageTitle(firstWizardPageMock);
        doReturn(secondTitleMock).when(view).makeWizardPageTitle(secondWizardPageMock);

        view.setPageTitles(Arrays.asList(firstWizardPageMock, secondWizardPageMock));

        assertEquals(2, pageTitleWidgets.size());

        view.hide();

        verify(iocBeanManager).destroyBean(firstTitleMock);
        verify(iocBeanManager).destroyBean(secondTitleMock);
        assertEquals(0, pageTitleWidgets.size());
    }
}
