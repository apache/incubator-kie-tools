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

package org.uberfire.client.workbench;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.pmgr.nswe.part.WorkbenchPartPresenterDefault;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartPresenter;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBeanFactoryTest {

    @Mock
    private UnanchoredWorkbenchPartPresenter unanchoredWorkbenchPartPresenter;

    @Mock
    private WorkbenchPartPresenterDefault workbenchPartPresenterDefault;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SyncBeanManager iocManager;

    @InjectMocks
    private DefaultBeanFactory beanFactory;

    @Before
    public void setup() {
        when(iocManager.lookupBean(UnanchoredWorkbenchPartPresenter.class).getInstance()).thenReturn(unanchoredWorkbenchPartPresenter);
        when(iocManager.lookupBean(WorkbenchPartPresenterDefault.class).getInstance()).thenReturn(workbenchPartPresenterDefault);
    }

    @Test
    public void testNewTemplateWorkbenchPartPresenter() {
        beanFactory.newWorkbenchPart(null,
                                     null,
                                     null,
                                     null,
                                     UnanchoredWorkbenchPartPresenter.class);
        verify(iocManager.lookupBean(UnanchoredWorkbenchPartPresenter.class)).getInstance();
        verify(iocManager.lookupBean(WorkbenchPartPresenterDefault.class),
               never()).getInstance();
    }

    @Test
    public void testNewWorkbenchPartPresenterDefault() {
        beanFactory.newWorkbenchPart(null,
                                     null,
                                     null,
                                     null,
                                     WorkbenchPartPresenterDefault.class);
        verify(iocManager.lookupBean(WorkbenchPartPresenterDefault.class)).getInstance();
        verify(iocManager.lookupBean(UnanchoredWorkbenchPartPresenter.class),
               never()).getInstance();
    }
}
